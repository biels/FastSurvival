package com.biel.FastSurvival.Dimensions.Sky.hexgen;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Represents a cell in the grid, equivalent to a chunk
 */
public class HexCell {
    HexCoordinates coordinates;
    public int type = 0;
    boolean populated = false;

    /**
     * Creating a new cell is equivalent to loading or generating it
     *
     * @param coordinates
     */
    public HexCell(HexCoordinates coordinates) {
        this.coordinates = coordinates;
        type = Utils.NombreEntre(0, 4);
    }

    public void populate(World world) {
        if(populated) return;
//        System.out.println("Populated " + coordinates.toString());
        int height = 60 + 12 + type;
        coordinates.getCorners().forEach(c -> {
            Utils.getCuboidAround(c.toLocation(world).add(0, height - 3, 0), 1, 1, 1).getBlocks()
                    .stream().filter(b -> !b.isEmpty())
                    .forEach(b -> b.setType(Material.DIAMOND_BLOCK));
        });
        if (Utils.Possibilitat(10)) {
            Utils.getCuboidAround(coordinates.getCenter(height).toLocation(world), 4).getBlocks().forEach(b -> b.setType(Material.OAK_PLANKS));
        }
        populated = true;
    }
}
