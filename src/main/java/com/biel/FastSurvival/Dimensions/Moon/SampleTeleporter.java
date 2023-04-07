package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;


public class SampleTeleporter {
    // Generate a sample teleporter at the given location

    // A Teleporter is an y structure that fulfills the following requirements:

    /*
    public static double detectMoonPortal(Location l) {
        List<Block> blocks = MoonUtils.detectMoonPortalBlocks(l);
        int wButtons = 0;
        int iBlocks = 0;
        int iBars = 0;
        int dBlocks = 0;
        int gBlocks = 0;
        int glowBlocks = 0;
        int iPlate = 0;
        int nFence = 0;
        int oFurnance = 0;
        int tTorch = 0;
        int tTorchOff = 0;
        int gBlock = 0;
        int rBlock = 0;
        int cBlock = 0;
        for (Block b : blocks) {
            Material t = b.getType();
            if (Tag.WOODEN_BUTTONS.isTagged(t)) {
                wButtons++;
            }
            if (t == Material.IRON_BLOCK) {
                iBlocks++;
            }
            if (t == Material.IRON_BARS) {
                iBars++;
            }
            if (t == Material.DIAMOND_BLOCK) {
                dBlocks++;
            }
            if (t == Material.GLASS) {
                gBlocks++;
            }
            if (t == Material.REDSTONE_LAMP) {
                glowBlocks++;
            }
            if (t == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                iPlate++;
            }
            if (t == Material.NETHER_BRICK_FENCE) {
                nFence++;
            }
            if (t == Material.FURNACE) {
                oFurnance++;
            }
            if (t == Material.REDSTONE_TORCH) {
                tTorch++;
            }
            if (t == Material.REDSTONE_WALL_TORCH) {
                tTorchOff++;
            }
            if (t == Material.REDSTONE_BLOCK) {
                rBlock++;
            }
            if (t == Material.COAL_BLOCK) {
                cBlock++;
            }
            if (t == Material.GOLD_BLOCK) {
                gBlock++;
            }
        }
        double e = 0;

        if (iBlocks < 8 || iBars < 4 || wButtons < 2) {
            return 0;
        }
        e = e + iBlocks * 38;
        //e = e + gBlocks * (5 + oFurnance);
        if (dBlocks >= 1) {
            e = e + 75;
        }
        if (dBlocks >= 2) {
            e = e + 18;
        }
        e = e + dBlocks * 150;
        e = e + rBlock * 8; //30% loss
        e = e + cBlock * 12; //70% loss
        if (cBlock != 0) {
            e = e + 45;
        }
        ;
        e = e + tTorchOff * 1;
        if (wButtons != 0) {
            e = e + (wButtons * 2 + 15) + 10;
        }
        ;
        if (iPlate != 0) {
            e = e + (iPlate * 5 + 12);
        }
        ;
        if (iBars != 0) {
            e = e + Math.max(iBars * 5, 40);
        }
        ;
        if (glowBlocks == 1) {
            e = e + 50;
        }
        if (nFence != 0) {
            e = e + 5 * nFence - (tTorch * 2);
        }
        //if (oFurnance != 0){e = e + 25 + 2 * oFurnance;}
        if (nFence == 4) {
            e = e + 18;
        }
        if (nFence == 1) {
            e = e + 60;
        }
        if (nFence == 2) {
            e = e + 30;
        }
        if (nFence == 3) {
            e = e + 20;
        }
        if (gBlock != 0) {
            e = e + 100;
        }
        return e;
    }
     */

    // Use         Utils.getCuboidAround(l, x, y, z);
    public static void generate(Location center) {
        // Create the cuboid area around the center
        Cuboid cuboid = Utils.getCuboidAround(center.clone().add(0, 2, 0), 4, 5, 4);
        // Loop through each block in the cuboid and set the appropriate material
        for (Block block : cuboid) {
            int x = block.getX() - center.getBlockX();
            int y = block.getY() - center.getBlockY();
            int z = block.getZ() - center.getBlockZ();
            // Place the iron blocks in the frame
            if (Math.abs(x) == 4 || Math.abs(z) == 4) {
                block.setType(Material.IRON_BLOCK);
            }
            // Place the iron bars in the frame
            else if ((Math.abs(x) == 3 && Math.abs(z) == 2) || (Math.abs(x) == 2 && Math.abs(z) == 3)) {
                block.setType(Material.IRON_BARS);
            }
            // Place the wooden buttons on the frame
            else if ((Math.abs(x) == 3 && z == -4) || (x == -4 && Math.abs(z) == 3)) {
                block.setType(Material.OAK_BUTTON);
            }
            // Place the diamond block in the center
            else if (x == 0 && y == 2 && z == 0) {
                block.setType(Material.DIAMOND_BLOCK);
            }
            // Place the glass blocks in the portal
            else if ((Math.abs(x) <= 2 && Math.abs(z) <= 2 && y >= 0 && y <= 3) || (x == 0 && y == 4 && z == 0)) {
                block.setType(Material.GLASS);
            }
            // Place the redstone lamps around the portal
            else if ((Math.abs(x) == 2 && z == -3) || (x == -3 && Math.abs(z) == 2)) {
                block.setType(Material.REDSTONE_LAMP);
            }
            // Place the redstone torches around the portal
            else if ((Math.abs(x) == 2 && z == 4) || (x == 4 && Math.abs(z) == 2)) {
                block.setType(Material.REDSTONE_TORCH);
            }
            // Place the coal blocks in the corners
            else if ((x == 4 && z == 4) || (x == 4 && z == -4) || (x == -4 && z == 4) || (x == -4 && z == -4)) {
                block.setType(Material.COAL_BLOCK);
            }
        }
    }

}
