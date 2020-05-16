package com.biel.FastSurvival.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
       }catch (Exception e){
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
    public void generate(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "");
    }
    public void clear() {
        clearCuboid(getCuboid());
        buildFrame();
    }
    public void attach(String command) {
        setAttachedCommand(command);
    }
    public void auto(boolean auto) {
        setAuto(auto);
    }

    // private helpers
    private Cuboid getInnerCuboid(){
        AtomicReference<Cuboid> c = new AtomicReference<>(getCuboid());
        Arrays.stream(Cuboid.CuboidDirection.all()).forEach(f -> c.set(c.get().expand(f, -1)));
        return c.get();
    }
    private List<Vector> getFrameLocations() {
       return getCuboid().edges().stream().flatMap(vectors -> vectors.stream()).collect(Collectors.toList());
    }
    private void buildFrame() {
        boolean active = isActive();
        getFrameLocations().forEach(vector -> {
            boolean condition = (vector.getBlockX() + vector.getBlockY() + vector.getBlockZ()) % 2 == 0;
            Material m1 = active ? Material.YELLOW_CONCRETE : Material.WHITE_CONCRETE;
            Material m2 = active ? Material.BLACK_CONCRETE : Material.RED_CONCRETE;
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
    private void renderFace(){

    }
}
