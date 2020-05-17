package com.biel.FastSurvival.Utils;

import com.biel.FastSurvival.DebugOptions;
import com.biel.FastSurvival.FastSurvival;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
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
    public Cuboid getCuboid() {
        try {
            return p.ObtenirCuboid("cuboid");
        } catch (Exception e) {
            return null;
        }
    }

    public void setCuboid(Cuboid cuboid) {
        p.EstablirCuboid("cuboid", cuboid);
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

    // public (controller)
    public void create(Location center, int radius) {
        if (getCuboid() != null) destroyFrame();
        Cuboid testAreaCuboid = Utils.getCuboidAround(center, radius);
        setCuboid(testAreaCuboid);
        buildFrame();
        if (isAuto()) clear();
    }

    public void generate(Player p) {
        clear(); // In stack mode no clear, modify stack offset
        String cmd = getAttachedCommand();
        Location center = getCuboid().getCenter();
        String locationArgs = Stream.of(center.getBlockX(), center.getBlockY(), center.getBlockZ())
                .map(integer -> integer.toString())
                .collect(Collectors.joining(" "));
        String commandLine = cmd + " " + locationArgs;
        Bukkit.broadcastMessage(commandLine);
        Bukkit.getServer().dispatchCommand(p, commandLine);
    }

    public void clear() {
        clearCuboid(getCuboid());
        buildFrame();
    }

    public void attach(String command, Player p) { // maze 10 List<String> args
        setAttachedCommand(command);
        generate(p);
    }

    public void auto(boolean newAutoValue, Player p) {
        boolean offToOn = !isAuto() && newAutoValue;
        boolean onToOff = isAuto() && !newAutoValue;
        setAuto(newAutoValue);
        Bukkit.broadcastMessage("[TA] Auto generation mode " + (newAutoValue ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
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

    public void remove() {
        destroyFrame();
        setCuboid(null);
    }

    public void offset(Location loc) {

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
        }else {
            Bukkit.broadcastMessage("Not reloading: No world found");
        }
    }

    // private helpers
    private Cuboid getInnerCuboid() {
        AtomicReference<Cuboid> c = new AtomicReference<>(getCuboid());
        Arrays.stream(Cuboid.CuboidDirection.all()).forEach(f -> c.set(c.get().expand(f, -1)));
        return c.get();
    }

    private List<Vector> getFrameLocations() {
        return getCuboid().edges().stream().flatMap(vectors -> vectors.stream()).collect(Collectors.toList());
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
