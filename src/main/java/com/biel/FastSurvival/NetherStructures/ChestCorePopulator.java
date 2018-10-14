package com.biel.FastSurvival.NetherStructures;

import com.biel.FastSurvival.SpecialItems.SpecialItemsUtils;
import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class ChestCorePopulator extends BlockPopulator {
    NoiseGenerator ng;
    boolean first = false;
    int number = 0;
    int attempts = 0;
    @Override
    public void populate(World world, Random random, Chunk source) {
//        Debug logic
        //attempts++;
        if((source.getX() + source.getZ()) % 2 == 0){
            return;
        }
        if (!(random.nextInt(30) <= 1)) {
            return;
        }
        ng = new SimplexNoiseGenerator(world.getSeed());
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        int xw = Utils.NombreEntre(11, 16);
        int zw = Utils.NombreEntre(18, 24);
        Location center = getFreeLocationAt(new Location(world, centerX, 0, centerZ));
        if(center == null || center.getY() > 120) return;
        Vector offset = new Vector(0.5, (xw / 2.0) + 0.5, 0.5);
        center.add(offset);
        Location ensureAir = center.clone().add(0, xw + 1, 0);
        if(ensureAir.getBlock().getType() != Material.AIR) return;
        Cuboid cuboidAround = Utils.getCuboidAround(center.clone().add(0, 1, 0), xw, 1, zw);
        List<Block> cuboidAroundBlocks = cuboidAround.getBlocks();
        int filledCount = 0;
        for (int i = 0; i < cuboidAroundBlocks.size(); i++) {
            if(cuboidAroundBlocks.get(i).getType() != Material.AIR){
                filledCount++;
            }
            if(filledCount > 8) return;
        }
        Vector up = new Vector(0, 1, 0);
        Vector front = new Vector(0, 0, 1);
        int num = Utils.NombreEntre(3,5);
        if(Utils.Possibilitat(1)) num = 7;
        List<Vector> locs = new ArrayList<>();
        List<Vector> glocs = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            List<Vector> dRectangleAround = Utils.get2dRectangleAround(center.clone().toVector(), up.clone(), front.clone(), xw, zw);
            int finalI = i;
            locs.addAll(dRectangleAround);
            up = Utils.rotateVectorCC(up.clone(), front.clone(), Math.PI / num);
        }
        int numRoots = Utils.NombreEntre(3, 6);
        if(Utils.Possibilitat(1)) numRoots = 8;
        List<Vector> lowerVectors = locs.stream().filter(v -> v.getY() >= center.getY()).collect(Collectors.toList());
        List<Vector> vectorsWithinDistance = getVectorsWithinDistance(lowerVectors, center.toVector(), 10);
        Collections.shuffle(vectorsWithinDistance);
        for (int i = 0; i < numRoots; i++) {
            if (vectorsWithinDistance.size() < i) break;
            Vector origin = vectorsWithinDistance.get(i).clone();
            Vector spikeVector = origin.subtract(center.toVector()).clone();
            Vector spikeDirection = spikeVector.clone().normalize().multiply(-1);
            List<Vector> line = Utils.getLine(center.toVector(), spikeDirection, (int) spikeVector.length());
            locs.addAll(line);
            glocs.add(origin);
        }
        List<Vector> base = Utils.getCuboidAround(center, 1, 0, 1).getBlocks().stream().map(b -> b.getLocation().toVector()).collect(Collectors.toList());
        locs.addAll(base);
        locs.forEach(v -> {
            if (Utils.Possibilitat(1, 400)) return;
            if (Utils.Possibilitat(1, 150)) v.setY(v.getY() + -1);
            Material material = Material.NETHERRACK;
            if (Utils.Possibilitat(10)) material = Material.QUARTZ_ORE;
            if (Utils.Possibilitat(20)) material = Material.MAGMA;
            v.toLocation(world).getBlock().setType(material);
        });
        glocs.forEach(v -> {
            Material material = Material.GOLD_BLOCK;
            v.toLocation(world).getBlock().setType(material);
        });
        if (Utils.Possibilitat(38)) center.getBlock().setType(Material.TNT);
        generateChest(center);
//        Debug logic
//        if(number < 40)Bukkit.getOnlinePlayers().forEach(p -> {
//            p.teleport(center);
//            first = true;
//        });
//        number++;
//        Bukkit.broadcastMessage(number + " D: " + number*100/(double)attempts);
    }

    void generateChest(Location center) {
        Block chest = center.add(new Vector(0, 1, 0)).getBlock();
        chest.setType(Material.TRAPPED_CHEST);
        ArrayList<ItemStack> it = new ArrayList<>();
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.DIAMOND, Utils.NombreEntre(1, 4)));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.NETHER_WARTS, Utils.NombreEntre(1, 3)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.GOLD_INGOT, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.ROTTEN_FLESH, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(60)) it.add(new ItemStack(Material.QUARTZ, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.GOLD_SWORD));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.GOLD_SPADE));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.BLAZE_POWDER));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.GHAST_TEAR));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.MAGMA_CREAM));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.BEETROOT_SOUP));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.BAKED_POTATO));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.GOLDEN_CARROT));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.REDSTONE, 3));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.EXP_BOTTLE, 3));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.REDSTONE, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.COCOA, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.FLINT, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.QUARTZ_BLOCK, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.SULPHUR, 10));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.SULPHUR, 20));
        if (Utils.Possibilitat(2)) it.add(Utils.getRandomPotion());
        if (Utils.Possibilitat(2)) it.add(Utils.getRandomPotion());
        if (Utils.Possibilitat(1)) it.add(SpecialItemsUtils.getRandomSpecialItem(1));
        if (Utils.Possibilitat(4)) it.add(Utils.getWitherSkull());
        Utils.fillChestRandomly(chest, it);
    }
    private Location getFreeLocationAt(Location loc){
        Location l = loc.clone();
        for (int i = 0; l.getY() < 127; l.setY(l.getY() + 40)) {
            if(l.getBlock().getType() == Material.AIR){
                for (int j = 0; j < 40; j++) {
                    l.setY(l.getY() - 1);
                    if(l.getBlock().getType() == Material.NETHERRACK){
                        l.setY(l.getY() + 1);
                        if(l.getBlock().getType() != Material.AIR) return null;
                        return l;
                    }
                }
                return null;
            }
        }
        return null;
    }
    // ------
    // |
    // |
	/*List<Location> generateWallBlocks(Location center){
		int r = Utils.NombreEntre(4, 6);
		Location middle = center.clone().add(new Vector(0, r / 2, 0));
		Utils.getCuboidAround(middle, r);
		//Cuboid walls = Utils.getOuterCylBlocks(middle, r);

        return
	}*/
    List<Vector> getVectorsWithinDistance(List<Vector> vectors, Vector center, double distance) {
        List<Vector> list = vectors.stream().filter(v -> v.clone().subtract(center).length() < distance).collect(Collectors.toList());
        return list;
    }
}
