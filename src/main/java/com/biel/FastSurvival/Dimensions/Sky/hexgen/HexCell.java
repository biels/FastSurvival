package com.biel.FastSurvival.Dimensions.Sky.hexgen;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Represents a cell in the grid, equivalent to a chunk
 */
public class HexCell {
    HexCoordinates coordinates;
    public int type = 0;
    public Biome biome;
    public Material material;
    boolean populated = false;

    /**
     * Creating a new cell is equivalent to loading or generating it
     *
     * @param coordinates
     */
    public HexCell(HexCoordinates coordinates) {
        this.coordinates = coordinates;
        type = -1;
        material = Material.AIR;
        if(Utils.Possibilitat(50)) material = Material.SNOW_BLOCK;
        if(Utils.Possibilitat(5)) material = Material.NETHERRACK;
        if(Utils.Possibilitat(5)) material = Material.STONE;
        if(Utils.Possibilitat(5)) material = Material.DIORITE;
        if(Utils.Possibilitat(5)) material = Material.ANDESITE;
        if(Utils.Possibilitat(1)) material = Material.POLISHED_DIORITE;
        if(Utils.Possibilitat(4)) material = Material.COBBLESTONE;
        if(Utils.Possibilitat(1)) material = Material.POLISHED_ANDESITE;
        if(Utils.Possibilitat(1)) material = Material.GRASS_BLOCK;
        if(Utils.Possibilitat(1)) material = Material.OAK_PLANKS;

    }

    public void populate(World world) {
        if(populated) return;
//        System.out.println("Populated " + coordinates.toString());
        int height = 80 - 4 + type;
//        coordinates.getCorners().forEach(c -> {
//            Utils.getCuboidAround(c.toLocation(world).add(0, height - 3, 0), 1, 1, 1).getBlocks()
//                    .stream().filter(b -> !b.isEmpty())
//                    .forEach(b -> b.setType(Material.DIAMOND_BLOCK));
//        });
        if (Utils.Possibilitat(1)) {
            Utils.getCuboidAround(coordinates.getCenter(height).toLocation(world), 4).getBlocks().forEach(b -> b.setType(Material.OAK_PLANKS));
        }
        populated = true;
    }
}
