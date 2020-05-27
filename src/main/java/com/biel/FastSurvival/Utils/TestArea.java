package com.biel.FastSurvival.Utils;

import com.biel.FastSurvival.DebugOptions;
import com.biel.FastSurvival.FastSurvival;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestArea {
    GestorPropietats p;
    World world;

    public TestArea(World world) {
        this.world = world;
        p = new GestorPropietats(world.getName() + "/testarea.txt");
    }

    // public (data)
    public Location getCenter() {
        return p.ObtenirLocation("center");
    }

    public int getRadius() {
        return Integer.parseInt(p.ObtenirPropietat("radius"));
    }

    public int getSize() {
        return Integer.parseInt(p.ObtenirPropietat("radius"));
    }

    public void setCenter(Location center) {
        p.EstablirLocation("center", center);
    }

    public void setRadius(int radius) {
        p.EstablirPropietat("radius", radius);
    }

    public boolean isActive() {
        return p.ObtenirPropietatBoolean("isActive");
    }

    public void setActive(boolean active) {
        p.EstablirPropietat("isActive", active);
    }

    public String getAttachedCommand() {
        return p.ObtenirPropietat("attachedCommand");
    }

    public void setAttachedCommand(String command) {
        p.EstablirPropietat("attachedCommand", command);
    }

    public boolean isAuto() {
        return p.ObtenirPropietatBoolean("isAuto");
    }

    public void setAuto(boolean auto) {
        p.EstablirPropietat("isAuto", auto);
    }

    public boolean isFloorActive() {
        return p.ObtenirPropietatBoolean("floor");
    }

    public boolean isFillActive() {
        return p.ObtenirPropietatBoolean("fill");
    }

    public void setFloorActive(boolean newFloorValue) {
        p.EstablirPropietat("floor", newFloorValue);
    }

    public void setFillActive(boolean newFillValue) {
        p.EstablirPropietat("fill", newFillValue);
    }

    public int getFloorHeight() {
        return p.ObtenirPropietatInt("floorHeight");
    }

    public void setFloorHeight(int height) {
        p.EstablirPropietat("floorHeight", height);
    }

    public Material getFloorMaterial() {
        String floorMaterial = p.ObtenirPropietat("floorMaterial");
        if (floorMaterial.equalsIgnoreCase("null")) return null;
        return Material.getMaterial(floorMaterial);
    }

    public Material getFillMaterial() {
        String fillMaterial = p.ObtenirPropietat("fillMaterial");
        if (fillMaterial.equalsIgnoreCase("null")) return null;
        return Material.getMaterial(fillMaterial);
    }

    public void setFloorMaterial(Material material) {
        String name;
        if (material == null) name = "null";
        else
            name = material.name();
        p.EstablirPropietat("floorMaterial", name);
    }

    public void setFillMaterial(Material material) {
        String name;
        if (material == null) name = "null";
        else
            name = material.name();
        p.EstablirPropietat("fillMaterial", name);
    }

    public Location getOffset() {
        Location location = p.ObtenirLocation("offset");
        if (location != null) return location;
        return new Location(world, 0, 0, 0);
    }

    public boolean isCompassActive() {
        return p.ObtenirPropietatBoolean("compass");
    }

    public void setCompassActive(boolean newCompassValue) {
        p.EstablirPropietat("compass", newCompassValue);
    }

    public boolean isAxisActive() {
        return p.ObtenirPropietatBoolean("axis");
    }

    public void setAxisActive(boolean newAxisValue) {
        p.EstablirPropietat("axis", newAxisValue);
    }

    // public (controller)
    public void create(Location center, Integer radius, Player p, boolean move) {
        remove();
        if (!move) reset();
        if (getCenter() != null) destroyFrame();
        setCenter(center);
        if (radius != null) setRadius(radius);
        buildFrame();
        if (isAuto()) clear(true);
        if (isAttached()) generate(p);
        setActive(true);
        updateStatus();
    }

    public void size(int size, Player player) {
        Location center = getCenter();
        if (isFloorActive()) buildFloor();
        if (isCompassActive()) buildCompassFaces();
        create(center, size, player, true);
    }

    public void offset(int x, int y, int z) {
        Location loc = new Location(world, x, y, z);
        p.EstablirLocation("offset", loc);
    }

    public void generate(Player p) {
        if (!isAttached()) return;
        clear(true); // In stack mode no clear, modify stack offset
        updateStatus();
        Location center = getCenter();
        Bukkit.broadcastMessage(center.getWorld().getName() + " " + getOffset().getWorld().getName());
        String cmd = getAttachedCommand();
        center.add(getOffset());
        String locationArgs = Stream.of(center.getBlockX(), center.getBlockY(), center.getBlockZ())
                .map(integer -> integer.toString())
                .collect(Collectors.joining(" "));
        String commandLine = cmd + " " + locationArgs;
        Bukkit.broadcastMessage(commandLine);
        Bukkit.getServer().dispatchCommand(p, commandLine);
    }

    public void clear(boolean rebuild) {
        clearCuboid(Utils.getCuboidAround(getCenter(), getRadius()));
        buildFrame();
        if (rebuild) updateStatus();
    }

    public void overClear(int r) {
        clearCuboid(Utils.getCuboidAround(getCenter(), getRadius() + r));
        buildFrame();
        updateStatus();
    }

    public void attach(String command, Player p) { // maze 10 List<String> args
        setAttachedCommand(command);
        generate(p);
    }

    public void detach() {
        setAttachedCommand("");
        clear(true);
    }

    public boolean isAttached() {
        if (p.ObtenirPropietat("attachedCommand").equals("")) return false;
        else return true;
    }

    public void auto(boolean newAutoValue, Player p) {
        boolean offToOn = !isAuto() && newAutoValue;
        boolean onToOff = isAuto() && !newAutoValue;
        setAuto(newAutoValue);
        Bukkit.broadcastMessage("[TA] Auto generation mode: " + (newAutoValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        if (offToOn) {
            generate(p);
            Bukkit.broadcastMessage("[TA] Using command: " + getAttachedCommand());
        }
        if (onToOff) {
            clear(true);
            buildFrame();
            updateStatus();
        }
    }

    public void toggleAuto(Player p) {
        if (isAuto()) auto(false, p);
        else if (!isAuto()) auto(true, p);
    }

    public void floorHeight(int height, Player p) {
        setFloorActive(true);
        clearFloor();
        if (height > getSize() * 2) height = getSize() * 2;
        setFloorHeight(height);
        buildFloor();
        Bukkit.broadcastMessage("[TA] Floor height: " + ChatColor.GREEN + height);
        if (isAxisActive()) buildAxis();
        if (isFillActive()) {
            clear(true);
        }
    }

    public void floorMaterial(String newMaterialStr, Player p) {
        Material mat = Material.matchMaterial(newMaterialStr);
        setFloorMaterial(mat);
        buildFloor();
    }

    public void fillMaterial(String newMaterialStr, Player p) {
        Material mat = Material.matchMaterial(newMaterialStr);
        setFillMaterial(mat);
        buildFill();
    }

    public void floor(boolean newFloorValue, Player p) {
        boolean offToOn = !isFloorActive() && newFloorValue;
        boolean onToOff = isFloorActive() && !newFloorValue;

        Bukkit.broadcastMessage("[TA] Floor: " + (newFloorValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        setFloorActive(newFloorValue);
        if (offToOn) buildFloor();
        if (onToOff) {
            clearFloor();
            fill(false, p);
            if (isAxisActive()) {
                clearAxis();
                setFloorHeight(0);
                buildAxis();
            }
        }
        if (isFillActive()) buildFill();
        updateStatus();
    }

    public void fill(boolean newFillValue, Player p) {
        boolean offToOn = !isFillActive() && newFillValue;
        boolean onToOff = isFillActive() && !newFillValue;

        Bukkit.broadcastMessage("[TA] Fill: " + (newFillValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        setFillActive(newFillValue);
        if (offToOn) buildFill();
        if (onToOff) clearFill();
        updateStatus();
    }

    public void clearFloor() {
        getFloorCuboid()
                .forEach(block -> block.setType(Material.AIR));
//        clear(false);
//        buildFrame();
    }

    public void clearFill() {
        getFillCuboid()
                .forEach(block -> block.setType(Material.AIR));
//        clear(true);
//        buildFrame();
    }

    public void buildFloor() {
        int patternSize = (int) Math.ceil(getSize() / 12.5);
        Material floorMaterial = getFloorMaterial();

        getFloorCuboid()
                .forEach(block -> {
                    if (floorMaterial != null) {
                        block.setType(floorMaterial);
                        return;
                    }
                    block.setType((((block.getX() / patternSize) + (block.getZ() / patternSize))) % 2 == 0 ? Material.WHITE_CONCRETE : Material.LIGHT_GRAY_CONCRETE);
                });
    }

    public void buildFill() {
        int patternSize = (int) Math.ceil(getSize() / 12.5);
        Material fillMaterial = getFillMaterial();

        getFillCuboid()
                .forEach(block -> {
                    if (fillMaterial != null) {
                        block.setType(fillMaterial);
                        return;
                    }
                    block.setType((((block.getX() / patternSize) + (block.getY() / patternSize) + (block.getZ() / patternSize))) % 2 == 0 ? Material.WHITE_CONCRETE : Material.LIGHT_GRAY_CONCRETE);
                });
    }

    public Cuboid getFloorCuboid() {
        return getInnerCuboid()
                .getFace(Cuboid.CuboidDirection.Down)
                .shift((getFloorHeight() - 1) > 0 ? Cuboid.CuboidDirection.Up : Cuboid.CuboidDirection.Down, Math.abs(getFloorHeight() - 1));
    }

    public Cuboid getFillCuboid() {
        return getInnerCuboid()
                .getFace(Cuboid.CuboidDirection.Down).shift(Cuboid.CuboidDirection.Down, 1)
                .expand(Cuboid.CuboidDirection.Up, ((getFloorHeight() - 1) < 0) ? 0 : (getFloorHeight() - 1));
    }

    public void compass(boolean newCompassValue, Player p) {
        boolean offToOn = !isCompassActive() && newCompassValue;
        boolean onToOff = isCompassActive() && !newCompassValue;

        Bukkit.broadcastMessage("[TA] Compass: " + (newCompassValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        if (offToOn) buildCompassFaces();
        if (onToOff) clearCompassFaces();
        setCompassActive(newCompassValue);
    }

    public void axis(boolean newAxisValue, Player p) {
        boolean offToOn = !isAxisActive() && newAxisValue;
        boolean onToOff = isAxisActive() && !newAxisValue;

        Bukkit.broadcastMessage("[TA] Axis old: " + (isAxisActive() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        Bukkit.broadcastMessage("[TA] Axis: " + (newAxisValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        if (offToOn) buildAxis();
        if (onToOff) clearAxis();
        setAxisActive(newAxisValue);
    }

    private void clearAxis() {
        getFloorCuboid().forEach(block -> block.setType(Material.AIR));
        if (isFloorActive()) buildFloor();
    }

    public void clearCompassFaces() {
        getTestAreaFace(Cuboid.CuboidDirection.North).forEach(block -> block.setType(Material.AIR));
        getTestAreaFace(Cuboid.CuboidDirection.East).forEach(block -> block.setType(Material.AIR));
        getTestAreaFace(Cuboid.CuboidDirection.South).forEach(block -> block.setType(Material.AIR));
        getTestAreaFace(Cuboid.CuboidDirection.West).forEach(block -> block.setType(Material.AIR));
    }

    public void buildCompassFaces() {
        int fontSize = (int) Math.ceil(getSize() / 2.0);
        if (getSize() < 30) fontSize = 10;
        if (getSize() < 5) return;
        String West = "West";
        String North = "North";
        String East = "East";
        String South = "South";
        if (getSize() < 40) {
            fontSize = (int) Math.ceil(getSize() / 1.5);
            West = "W";
            North = "N";
            East = "E";
            South = "S";
        }
        FontRenderer.renderText(West, getTestAreaFace(Cuboid.CuboidDirection.North).getCenter(), new Vector(0, 0, -1), new Vector(1, 0, 0), fontSize, Material.BLACK_CONCRETE);
        FontRenderer.renderText(North, getTestAreaFace(Cuboid.CuboidDirection.East).getCenter(), new Vector(1, 0, 0), new Vector(0, 0, 1), fontSize, Material.BLACK_CONCRETE);
        FontRenderer.renderText(East, getTestAreaFace(Cuboid.CuboidDirection.South).getCenter(), new Vector(0, 0, 1), new Vector(-1, 0, 0), fontSize, Material.BLACK_CONCRETE);
        FontRenderer.renderText(South, getTestAreaFace(Cuboid.CuboidDirection.West).getCenter(), new Vector(-1, 0, 0), new Vector(0, 0, -1), fontSize, Material.BLACK_CONCRETE);
    }

    public void buildAxis() {
        int fontSize = (int) Math.ceil(getSize() / 2.0);
        if (getSize() < 20) fontSize = 7;
        if (getSize() < 5) return;
        Location initialLoc = getTestAreaFace(Cuboid.CuboidDirection.Down).getCenter().add(Math.ceil(getSize() / -1.2), 0, Math.ceil(getSize() / -1.2));
        initialLoc.add(0, getFloorHeight(), 0);
        Utils.getLine(initialLoc.toVector(), new Vector(1, 0, 0), getSize()).forEach(block -> block.toLocation(Objects.requireNonNull(initialLoc.getWorld())).getBlock().setType(Material.BLACK_CONCRETE));
        Utils.getLine(initialLoc.toVector(), new Vector(0, 0, 1), getSize()).forEach(block -> block.toLocation(Objects.requireNonNull(initialLoc.getWorld())).getBlock().setType(Material.BLACK_CONCRETE));
        Location xTextLoc = initialLoc.clone().add(getSize() + 0.3 * getSize(), 0, 0);
        Location zTextLoc = initialLoc.clone().add(0, 0, (getSize() + 0.3 * getSize()));
        String hex = "2192";
        int intValue = Integer.parseInt(hex, 16);
        String s = 'A' + String.valueOf(intValue);
        FontRenderer.renderText("X", xTextLoc, new Vector(0, 0, -1), new Vector(0, 1, 0), fontSize, Material.BLACK_CONCRETE);
        FontRenderer.renderText("Z", zTextLoc, new Vector(1, 0, 0), new Vector(0, 1, 0), fontSize, Material.BLACK_CONCRETE);
    }

    public void toggleFloor(Player player) {
        if (isFloorActive()) floor(false, player);
        else if (!isFloorActive()) floor(true, player);
    }

    public void toggleFill(Player player) {
        if (isFillActive()) fill(false, player);
        else if (!isFillActive()) fill(true, player);
    }

    public void toggleCenter(Player p) {
        if (isCenterActive()) activateCenter(false, p);
        else if (!isCenterActive()) activateCenter(true, p);
    }

    public void toggleCompass(Player player) {
        if (isCompassActive()) compass(false, player);
        else if (!isCompassActive()) compass(true, player);
    }

    public void toggleAxis(Player player) {
        if (isAxisActive()) axis(false, player);
        else if (!isAxisActive()) axis(true, player);
    }

    public void activateCenter(boolean newCenterValue, Player player) {
        boolean offToOn = !isCenterActive() && newCenterValue;
        boolean onToOff = isCenterActive() && !newCenterValue;
        p.EstablirPropietat("centerActive", newCenterValue);
        Bukkit.broadcastMessage("[TA] Center: " + (newCenterValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        if (offToOn) getCenter().getBlock().setType(Material.GOLD_BLOCK);
        if (onToOff) getCenter().getBlock().setType(Material.AIR);
    }

    public boolean isCenterActive() {
        return p.ObtenirPropietatBoolean("center");
    }

    public void remove() {
        if (!isActive()) return;
        destroyFrame();
        clear(false);
//        clearFloor();
        setActive(false);
    }

    public void reset() {
        setFloorMaterial(null);
        setFloorHeight(0);
        setFloorActive(false);
        setFillActive(false);
        setAttachedCommand("");
        setAuto(false);
        setCompassActive(false);
    }


    public static void onReload() {
        AtomicReference<World> world = new AtomicReference<>(Bukkit.getServer().getWorld(DebugOptions.getSelectedTestAreaWorldName()));
        if (world.get() != null && world.get().getPlayers().size() == 0 || world.get() == null) {
            Bukkit.getServer().getWorlds().stream().filter(w -> w.getPlayers().size() > 0).findFirst().ifPresent(w -> world.set(w));
        }
        if (world.get() != null) {
            Bukkit.getScheduler().runTask(FastSurvival.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    DebugOptions.getTestArea(world.get()).generate(world.get().getPlayers().get(0));
                }
            });
        } else {
            Bukkit.broadcastMessage("Not reloading: No world found");
        }
    }

    // private helpers
    private Cuboid getInnerCuboid() {
        AtomicReference<Cuboid> c = new AtomicReference<>(Utils.getCuboidAround(getCenter(), getRadius()));
        Arrays.stream(Cuboid.CuboidDirection.all()).forEach(f -> c.set(c.get().expand(f, -1)));
        return c.get();
    }

    private Cuboid getTestAreaFace(Cuboid.CuboidDirection direction) {
        return getInnerCuboid().getFace(direction).shift(direction, 1);
    }

    private List<Vector> getFrameLocations() {
        return Utils.getCuboidAround(getCenter(), getRadius()).edges().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private void buildFrame() {
        boolean auto = isAuto();
        getFrameLocations().forEach(vector -> {
            boolean condition = (vector.getBlockX() + vector.getBlockY() + vector.getBlockZ()) % 2 == 0;
            Material m1 = auto ? Material.YELLOW_CONCRETE : Material.WHITE_CONCRETE;
            Material m2 = auto ? Material.BLACK_CONCRETE : Material.RED_CONCRETE;
            vector.toLocation(world).getBlock().setType(condition ? m1 : m2);
        });
    }

    private void destroyFrame() {
        getFrameLocations().forEach(vector -> {
            vector.toLocation(world).getBlock().setType(Material.AIR);
        });
    }

    private void clearCuboid(Cuboid cuboid) {
        cuboid.forEach(block -> {
            if (block.isEmpty()) return;
            block.setType(Material.AIR);
        });
    }

    private void updateStatus() {
        if (!isActive()) return;
        if (isFloorActive()) buildFloor();
        if (isFillActive()) buildFill();
        if (isAxisActive()) buildAxis();
        if (isCompassActive()) buildCompassFaces();
        if (isCenterActive()) getCenter().getBlock().setType(Material.GOLD_BLOCK);
    }

    private void renderFace() {

    }
}
