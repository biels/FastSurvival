package com.biel.FastSurvival.Utils;

import com.biel.FastSurvival.DebugOptions;
import com.biel.FastSurvival.FastSurvival;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

    public void setFloorActive(boolean newFloorValue) {
        p.EstablirPropietat("floor", newFloorValue);
    }
    public int getFloorHeight(){
        return p.ObtenirPropietatInt("floorHeight");
    }
    public void setFloorHeight(int height){
        p.EstablirPropietat("floorHeight", height);

    }

    public Location getOffset() {
        Location location = p.ObtenirLocation("offset");
        if (location != null) return location;
        return new Location(world, 0, 0, 0);
    }

    // public (controller)
    public void create(Location center, int radius, Player p) {
        if (getCenter() != null) destroyFrame();
        setCenter(center);
        setRadius(radius);
        buildFrame();
        if (isAuto()) clear();
        if (isAttached()) generate(p);
    }

    public void size(int size, Player player) {
        Location center = getCenter();
        create(center, size, player);
    }

    public void offset(int x, int y, int z) {
        Location loc = new Location(world, x, y, z);
        p.EstablirLocation("offset", loc);
    }

    public void generate(Player p) {
        if (!isAttached()) return;
        clear(); // In stack mode no clear, modify stack offset
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

    public void clear() {
        clearCuboid(Utils.getCuboidAround(getCenter(), getRadius()));
        buildFrame();
    }

    public void attach(String command, Player p) { // maze 10 List<String> args
        setAttachedCommand(command);
        generate(p);
    }

    public void detach() {
        setAttachedCommand("");
        clear();
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
            clear();
            buildFrame();
        }
    }
    public void toggleAuto(Player p) {
        if (isAuto()) auto(false, p);
        else if (!isAuto()) auto(true, p);
    }

    public void floor(boolean newFloorValue, Player p) {
        boolean offToOn = !isFloorActive() && newFloorValue;
        boolean onToOff = isFloorActive() && !newFloorValue;

        Bukkit.broadcastMessage("[TA] Floor old: " + (isFloorActive() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        Bukkit.broadcastMessage("[TA] Floor: " + (newFloorValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        int patternSize = (int) Math.ceil(getSize() / 12.5);
        if (offToOn) {
            Bukkit.broadcastMessage("offToOn");
            getInnerCuboid().getFace(Cuboid.CuboidDirection.Down).shift(Cuboid.CuboidDirection.Up, getFloorHeight()-1)
                    .forEach(block -> block.setType((((block.getX() / patternSize) + (block.getZ() / patternSize))) % 2 == 0 ? Material.WHITE_CONCRETE : Material.LIGHT_GRAY_CONCRETE));
        }
        if (onToOff) {
            Bukkit.broadcastMessage("onToOff");
            getInnerCuboid().getFace(Cuboid.CuboidDirection.Down).shift(Cuboid.CuboidDirection.Up, getFloorHeight()-1)
                    .forEach(block -> block.setType(Material.AIR));
            clear();
            buildFrame();
        }
        setFloorActive(newFloorValue);
    }

    public void toggleFloor(Player player) {
        if (isFloorActive()) floor(false, player);
        else if (!isFloorActive()) floor(true, player);
    }

    public void toggleCenter(Player p) {
        if (isCenterActive()) activateCenter(false, p);
        else if (!isCenterActive()) activateCenter(true, p);
    }

    public void activateCenter(boolean newCenterValue, Player player) {
        boolean offToOn = !isCenterActive() && newCenterValue;
        boolean onToOff = isCenterActive() && !newCenterValue;
        p.EstablirPropietat("center", newCenterValue);
        Bukkit.broadcastMessage("[TA] Center: " + (newCenterValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        if (offToOn) getCenter().getBlock().setType(Material.GOLD_BLOCK);
        if (onToOff) getCenter().getBlock().setType(Material.AIR);
    }

    public boolean isCenterActive() {
        return p.ObtenirPropietatBoolean("center");
    }

    public void remove() {
        destroyFrame();
        setActive(false);
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

    private void renderFace() {

    }
}
