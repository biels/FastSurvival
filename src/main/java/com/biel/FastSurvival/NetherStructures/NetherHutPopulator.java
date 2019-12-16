package com.biel.FastSurvival.NetherStructures;

import com.biel.FastSurvival.Bows.BowRecipeGenerator;
import com.biel.FastSurvival.SpecialItems.SpecialItemsUtils;
import com.biel.FastSurvival.Utils.Cuboid;
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
import java.util.List;
import java.util.Random;

public class NetherHutPopulator extends BlockPopulator {
    NoiseGenerator ng;
    boolean first = false;
    int number = 0;
    int attempts = 0;

    @Override
    public void populate(World world, Random random, Chunk source) {


        FindNetherStructureLocation findNetherStructureLocation = new FindNetherStructureLocation(world, random, source, false)
                .invoke(Utils.NombreEntre(6, 8), Utils.NombreEntre(7, 12), 8, false, 2);
        if (findNetherStructureLocation.notValid()) return;
        int xw = findNetherStructureLocation.getXw();
        int zw = findNetherStructureLocation.getZw();
        Location center = findNetherStructureLocation.getCenter();
        Location floorCenter = center.clone().add(0, -1, 0);
       // if(center.getBlock().getType() != Material.AIR) return;
       // if(floorCenter.getBlock().getType() == Material.AIR) return;
        List<Vector> baseLocs = Utils.get2dRectangleAround(center.toVector().clone(), new Vector(0, 1, 0), new Vector(0, 0, 1), xw, zw);
        Vector zVar = new Vector(0, 0, Math.floor(zw / 2.0));
        Vector xVar = new Vector(Math.floor(xw / 2.0), 0, 0);
        if (Utils.Possibilitat(50)) zVar.multiply(-1);
        if (Utils.Possibilitat(50)) xVar.multiply(-1);
        Vector cornerOffset = xVar.clone().add(zVar);
        Vector corner = cornerOffset.clone().add(center.toVector());
        baseLocs
                .forEach(v -> {
                    Vector vFromCorner = v.clone().subtract(corner);
                    double length = vFromCorner.length();
                    double height = Math.max(5 - Math.sqrt(length) * 1.5, 0);
                    for (int i = 0; i < height; i++) {
                        Vector blockLoc = v.clone().add(new Vector(0, i, 0));
                        ChestCorePopulator.fillBlockWithNetherMixture(world, blockLoc);
                    }
                });


        Cuboid floorCuboid = new Cuboid(floorCenter.clone().add(cornerOffset), floorCenter.clone().add(cornerOffset.clone().multiply(-1)));
        floorCuboid.getBlocks().forEach(b -> ChestCorePopulator.fillBlockWithNetherMixture(world, b.getLocation().toVector()));
        Vector chestLoc = corner.clone().add(cornerOffset.clone().normalize().multiply(-1.3).add(new Vector(0, -1, 0)));
        generateChest(chestLoc.toLocation(world));

        //        Debug logic
//        if(number < 40) Bukkit.getOnlinePlayers().forEach(p -> {
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
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(80)) it.add(new ItemStack(Material.GOLD_NUGGET, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.GOLD_INGOT, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.COAL, Utils.NombreEntre(1, 12)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.COAL, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.IRON_INGOT, Utils.NombreEntre(1, 5)));
        if (Utils.Possibilitat(15)) it.add(new ItemStack(Material.LAVA_BUCKET));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.BEETROOT_SEEDS));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.LADDER, Utils.NombreEntre(1, 60)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.ROTTEN_FLESH, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.ROTTEN_FLESH, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.BONE, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(60)) it.add(new ItemStack(Material.QUARTZ, Utils.NombreEntre(1, 8)));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.LEGACY_GOLD_SWORD));
        if (Utils.Possibilitat(70)) it.add(new ItemStack(Material.ARROW, Utils.NombreEntre(20, 30)));
        if (Utils.Possibilitat(30)) it.add(new ItemStack(Material.ARROW, Utils.NombreEntre(20, 30)));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.LEGACY_GOLD_SPADE));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.BOOK));
        if (Utils.Possibilitat(10)) it.add(new ItemStack(Material.FLINT_AND_STEEL, 1));
        if (Utils.Possibilitat(6)) it.add(new ItemStack(Material.LEGACY_GOLD_PICKAXE));
        if (Utils.Possibilitat(3)) it.add(new ItemStack(Material.IRON_PICKAXE));
        if (Utils.Possibilitat(5)) it.add(new ItemStack(Material.BEETROOT_SOUP));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.BEETROOT_SOUP, 2));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.BAKED_POTATO));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.GOLDEN_CARROT));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.REDSTONE, 3));
        if (Utils.Possibilitat(2)) it.add(new ItemStack(Material.LEGACY_EXP_BOTTLE, 3));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.FLINT, 2));
        if (Utils.Possibilitat(1)) it.add(new ItemStack(Material.QUARTZ_BLOCK, 2));
        if (Utils.Possibilitat(2)) it.add(Utils.getRandomPotion());
        if (Utils.Possibilitat(1, 300)) it.add(SpecialItemsUtils.getRandomSpecialItem(1));
        if (Utils.Possibilitat(1, 150)) it.add(BowRecipeGenerator.getRandomBow(false));
        if (Utils.Possibilitat(1, 200)) it.add(Utils.getWitherSkull());
        Utils.fillChestRandomly(chest, it);
    }

}
