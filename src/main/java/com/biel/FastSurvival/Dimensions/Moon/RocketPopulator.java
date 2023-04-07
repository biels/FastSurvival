package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Bows.BowRecipeGenerator;
import com.biel.FastSurvival.SpecialItems.SpecialItemsUtils;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RocketPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(1250) <= 1)) {
            return;
        }
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int centerY = world.getHighestBlockYAt(centerX, centerZ) - 1;
        Location start = new Location(world, centerX, centerY, centerZ);
        if(start.getBlock().getType() == Material.LIME_STAINED_GLASS)  return;

        generate(start, random);
    }

    double toRadians = 180 / Math.PI;
    Vector up = new Vector(0, 1, 0).normalize();
    Vector side = Utils.getVectorInPlaneY(up, new Vector(0, 0, 1));
    int height = 50;
    int roofHeight = 12;
    int BodyBottom = 10;
    int cutBodyPart = 4;
    int legNum = 3;

    public double getWidthAt(int y) {
        return Math.sin(y / (height / Math.PI)) * 6.9;
    }

    public Material getBodyColorAt(Location location, Location cylCenter) {
        Vector vector = Utils.CrearVector(cylCenter, location);
        int angle = (int) (side.angle(vector) * toRadians);
        int y = (int) location.getY();
//        Bukkit.broadcastMessage("center: " + cylCenter + "location: " + location);
        return (((y / 2) + (angle / 24)) % 2 == 1) ? Material.WHITE_CONCRETE : Material.RED_CONCRETE;
    }

    public void generateMid(Location center, Random random) {
        Location cylCenter = center.clone();
        List<Location> floorCenters = new ArrayList<>();
        List<Integer> floorRadiuses = new ArrayList<>();
        // Generate structure
        Material floorMaterial = Material.BIRCH_PLANKS;
        for (int i = 0; i < height - roofHeight; i++) {
            cylCenter.add(up);
            Vector r = side.clone().normalize().multiply(getWidthAt(i));
            if (i >= cutBodyPart + BodyBottom) {
                Utils.getCylBlocks(cylCenter, (int) r.length(), 1, false, up).forEach(block -> {
                    block.setType(getBodyColorAt(block.getLocation(), cylCenter));
                });
            } else if (i > cutBodyPart) {
                Utils.getCylBlocks(cylCenter, (int) r.length(), 1, false, up).forEach(block -> {
                    block.setType(Material.RED_CONCRETE);
                });
            }
            if (i % 5 == 0 && i > 8) {
                int floorRadius = (int) r.length() - 1;
                Utils.getCylBlocks(cylCenter, floorRadius, 1, true, up).forEach(block -> {
                    block.setType(floorMaterial);
                });
                floorCenters.add(cylCenter.clone());
                floorRadiuses.add(floorRadius);
            }
        }

        // Generate details

        for (int i = 0; i < floorCenters.size(); i++) {
            Location floorCenter = floorCenters.get(i);
            Integer floorRadius = floorRadiuses.get(i);
            // Generate stair holes
            List<Block> ladderRingBlocks = Utils.getCylBlocks(floorCenter, floorRadius - 2, 1, false, up);
            Block ladderBlock = ladderRingBlocks.get(random.nextInt(ladderRingBlocks.size()));
            int levelHeight = 4;
            for (int j = 0; j < levelHeight; j++) {
                Block b = ladderBlock.getRelative(0, -j, 0);
                if (b.isEmpty() || b.getType().equals(floorMaterial)) b.setType(Material.LADDER);
            }

            List<Block> chestRingBlocks = Utils.getCylBlocks(floorCenter.clone().add(0, 1, 0), floorRadius - 1, 1, false, up);
            int chestRingIndex = random.nextInt(chestRingBlocks.size() - 1) + 1;
            int lampIndex = random.nextInt(chestRingBlocks.size() - 1) + 1;
            int extraElementIndex = random.nextInt(chestRingBlocks.size() - 1) + 1;
            Block lampBlock = chestRingBlocks.get(lampIndex);
            lampBlock.setType(Material.SEA_LANTERN);

            Block chestBlock = chestRingBlocks.get(chestRingIndex);
            if (Utils.Possibilitat(80) && chestBlock.getRelative(0, -1, 0).getType().isSolid()) {
                chestBlock.setType(Material.CHEST);
                Utils.fillChestRandomly(chestBlock, getItemsForLevel());
            }
            Block furnanceBlock = chestRingBlocks.get(chestRingIndex - 1);
            if (Utils.Possibilitat(70) && furnanceBlock.getRelative(0, -1, 0).getType().isSolid()) {
                Material applianceMat = Material.BLAST_FURNACE;
                if(Utils.Possibilitat(10)) applianceMat = Material.DROPPER;
                if(Utils.Possibilitat(10)) applianceMat = Material.FURNACE;
                if(Utils.Possibilitat(10)) applianceMat = Material.TNT;
                furnanceBlock.setType(applianceMat);

            }
            // Add a brewing stand on the ring blocks
            if (Utils.Possibilitat(20)) {
                Block brewingStandBlock = chestRingBlocks.get(extraElementIndex);
                if (brewingStandBlock.getRelative(0, -1, 0).getType().isSolid()) {
                    brewingStandBlock.setType(Material.BREWING_STAND);
                }
            }


            floorCenter.getBlock().setType(Material.CRAFTING_TABLE);





            // Bukkit.broadcastMessage("centers: " + i);
        }

    }

    public ArrayList<ItemStack> getItemsForLevel() {
        ArrayList<ItemStack> i = new ArrayList<ItemStack>();

        if (Utils.Possibilitat(8)){i.add(BowRecipeGenerator.getRandomBow(false));}
        if (Utils.Possibilitat(4)){i.add(BowRecipeGenerator.getRandomBow(false));}
        if (Utils.Possibilitat(60)){i.add(new ItemStack(Material.DIAMOND_HELMET, 1));}
        if (Utils.Possibilitat(60)){i.add(new ItemStack(Material.IRON_HELMET, 1));}
        if (Utils.Possibilitat(30)){i.add(new ItemStack(Material.IRON_SWORD, 1));}
        if (Utils.Possibilitat(40)){i.add(new ItemStack(Material.ARROW, 1));}
        if (Utils.Possibilitat(40)){i.add(new ItemStack(Material.FLINT_AND_STEEL, 1));}
        if (Utils.Possibilitat(10)){i.add(new ItemStack(Material.IRON_DOOR, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_CHESTPLATE, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_LEGGINGS, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_BOOTS, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_HELMET, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_SWORD, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_PICKAXE, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_AXE, 1));}
        if (Utils.Possibilitat(20)){i.add(new ItemStack(Material.IRON_SHOVEL, 1));}
        if (Utils.Possibilitat(50)){i.add(new ItemStack(Material.TNT, Utils.NombreEntre(1,  6)));}
        if (Utils.Possibilitat(25)){i.add(new ItemStack(Material.TNT, Utils.NombreEntre(1,  6)));}
        if (Utils.Possibilitat(15)){i.add(new ItemStack(Material.TNT, Utils.NombreEntre(1,  6)));}
        if (Utils.Possibilitat(14)){i.add(new ItemStack(Material.TNT, Utils.NombreEntre(1,  6)));}
        if (Utils.Possibilitat(38)){i.add(Utils.getRandomPotion());}
        if (Utils.Possibilitat(35)){i.add(Utils.getRandomPotion());}
        if (Utils.Possibilitat(1)){new ItemStack(SpecialItemsUtils.getRandomSpecialItem(3));}

        if (Utils.Possibilitat(1)){i.add(new ItemStack(Material.DIAMOND, 1));}
        if (Utils.Possibilitat(1)){i.add(new ItemStack(Material.DIAMOND, 1));}


        return i;
    }


    public void generateRoof(Location center) {
        Location cylCenter = center.clone();
        for (int i = height - roofHeight; i < height; i++) {
            cylCenter.add(up);
            Vector r = side.clone().multiply(getWidthAt(i));
            Utils.getCylBlocks(cylCenter, (int) r.length(), 1, false, up).forEach(block ->
                    block.setType(Material.RED_CONCRETE));
        }
    }

    public void generateLegs(Location center) {
        Location loc = center.clone().add(up.clone().multiply(cutBodyPart));
        int p1Y = 4;
        int p2Y = 8;
        int p3Y = -10;
        int p3X = 12;
//        Utils.drawTriangle(loc.clone(), loc.clone().add(0, 10, 5), loc.clone().add(0, -10, 5)).forEach(l -> {
//            l.getBlock().setType(Material.REDSTONE_BLOCK);});
        Vector sideVector = side.clone();
        for (int i = 0; i < legNum; i++) {
            Utils.drawTriangle(loc.clone().add(up.clone().multiply(p1Y)).add(sideVector.clone().multiply(getWidthAt(p1Y)))
                    , loc.clone().add(up.clone().multiply(p2Y)).add(sideVector.clone().multiply(getWidthAt(p2Y)))
                    , loc.clone().add(up.clone().multiply(p3Y)).add(sideVector.clone().multiply(p3X))).forEach(l -> {
                l.getBlock().setType(Material.RED_CONCRETE);
            });
            sideVector.rotateAroundAxis(up, (360 / legNum) / toRadians);
//            Utils.getLineBetween(loc.clone().add(up.clone().multiply(p1Y)).add(sideVector.clone().multiply(getWidthAt(p1Y)))
//                    , loc.clone().add(up.clone().multiply(p3Y)).add(sideVector.clone().multiply(10))).forEach(v -> {
//                        Utils.getSphereLocations(v.toLocation(Objects.requireNonNull(loc.getWorld())), 2.0, false)
//                        .forEach(l -> l.getBlock().setType(Material.RED_CONCRETE));
//            });

        }
    }

    public void generate(Location location, Random random) {
        location = location.toVector().toBlockVector().toLocation(location.getWorld());
//        location.getBlock().setType(Material.DIAMOND_BLOCK);
        Location center = location.clone().add(up.clone().multiply(10));
//        center.getBlock().setType(Material.GOLD_BLOCK);
        generateMid(center, random);
        generateRoof(center.clone().add(up.clone().multiply(height - roofHeight)));
        generateLegs(center);
//        Utils.getCylBlocks(location, 3,1,false, up).forEach(block -> block.setType(Material.GOLD_BLOCK));
    }
}
