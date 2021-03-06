package com.biel.FastSurvival.NetherStructures;

import com.biel.FastSurvival.SpecialItems.SpecialItemsUtils;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
        FindNetherStructureLocation findNetherStructureLocation = new FindNetherStructureLocation(world, random, source, false)
                .invoke(Utils.NombreEntre(11, 16), Utils.NombreEntre(18, 24), 8, true, 12);
        if (findNetherStructureLocation.notValid()) return;
        int xw = findNetherStructureLocation.getXw();
        int zw = findNetherStructureLocation.getZw();
        Location center = findNetherStructureLocation.getCenter();


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
            fillBlockWithNetherMixture(world, v);
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

    public static void fillBlockWithNetherMixture(World world, Vector v) {
        if (Utils.Possibilitat(1, 400)) return;
        if (Utils.Possibilitat(1, 150)) v.setY(v.getY() + -1);
        Material material = Material.NETHERRACK;
        if (Utils.Possibilitat(10)) material = Material.LEGACY_QUARTZ_ORE;
        if (Utils.Possibilitat(20)) material = Material.LEGACY_MAGMA;
        v.toLocation(world).getBlock().setType(material);
    }

    void generateChest(Location center) {
        Block chest = center.add(new Vector(0, 1, 0)).getBlock();
        chest.setType(Material.TRAPPED_CHEST);
        ArrayList<ItemStack> it = new ArrayList<>();
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.DIAMOND, Utils.NombreEntre(1, 4)));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.LEGACY_NETHER_WARTS, Utils.NombreEntre(1, 3)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.GOLD_INGOT, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.ROTTEN_FLESH, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(60)) it.add(new ItemStack(Material.QUARTZ, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.LEGACY_GOLD_SWORD));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.LEGACY_GOLD_SPADE));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.BLAZE_POWDER));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.GHAST_TEAR));
        if (Utils.Possibilitat(3)) it.add(new ItemStack(Material.DIAMOND_PICKAXE));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.MAGMA_CREAM));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.BEETROOT_SOUP));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.BAKED_POTATO));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.GOLDEN_CARROT));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.REDSTONE, 3));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.LEGACY_EXP_BOTTLE, 3));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.REDSTONE, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.COCOA, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.FLINT, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.QUARTZ_BLOCK, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.GUNPOWDER, 10));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.GUNPOWDER, 20));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.GUNPOWDER, 20));
        if (Utils.Possibilitat(2)) it.add(Utils.getRandomPotion());
        if (Utils.Possibilitat(2)) it.add(Utils.getRandomPotion());
        if (Utils.Possibilitat(1)) it.add(SpecialItemsUtils.getRandomSpecialItem(1));
        if (Utils.Possibilitat(4)) it.add(Utils.getWitherSkull());
        if (Utils.Possibilitat(1, 500)) it.add(new ItemStack(Material.ELYTRA));
        Utils.fillChestRandomly(chest, it);
    }
    public static Location getFreeLocationAt(Location loc){
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
