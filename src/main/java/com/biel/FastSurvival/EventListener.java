package com.biel.FastSurvival;

import com.biel.FastSurvival.Dimensions.Moon.*;
import com.biel.FastSurvival.Dimensions.Sky.SkyListener;
import com.biel.FastSurvival.Dimensions.Sky.SkyNexus;
import com.biel.FastSurvival.Dimensions.Sky.SkyTreePopulator;
import com.biel.FastSurvival.Dimensions.Sky.IcyArchPopulator;
import com.biel.FastSurvival.Dimensions.Sky.SkyUtils;
import com.biel.FastSurvival.NetherStructures.ChestCorePopulator;
import com.biel.FastSurvival.NetherStructures.NetherHutPopulator;
import com.biel.FastSurvival.OverworldStructures.*;
import com.biel.FastSurvival.Utils.Utils;
import com.biel.FastSurvival.Utils.WGUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getLogger;

public class EventListener implements Listener {
    @EventHandler
    public void onWorldInit(WorldInitEvent evt) {
        World world = evt.getWorld();
        world.setAutoSave(true);

        if (MoonUtils.IsEarth(world)) {
            world.setGameRuleValue("doFireTick", "false");
            //evt.getWorld().getPopulators().add(new SlimeBossPopulator());
           // world.getPopulators().add(new NetherPopulator());
            world.getPopulators().add(new LogPopulator());
            evt.getWorld().getPopulators().add(new MoonMagicTreePopulator());
            world.getPopulators().add(new GraveyardPopulator());
            world.getPopulators().add(new EarthMagicTreePopulator());
            world.getPopulators().add(new HotAirBalloonPopulator());
//            world.getPopulators().add(new TreasurePopulator());
            world.setMonsterSpawnLimit(80);
        }

        if (SkyUtils.IsSky(world)) {
            world.getPopulators().add(new SkyTreePopulator());
//            world.getPopulators().add(new IcyArchPopulator());
        }
        if (MoonUtils.IsMoon(world)) {
//            world.setMonsterSpawnLimit(80);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setDifficulty(Difficulty.HARD);
            world.setTime(114000); //15000
//            Bukkit.broadcastMessage("getViewDistance: "+ world.getViewDistance());
        }

        if (Bukkit.getWorlds().size() > 1 && Bukkit.getWorlds().get(1).equals(world)) {
            world.getPopulators().add(new ChestCorePopulator());
            world.getPopulators().add(new NetherHutPopulator());

        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {

        if (evt.getPlayer() != null) {
            Player p = evt.getPlayer();
            //WG
            Block blk = evt.getBlock();
            if (p != null) {
                if (!WGUtils.canBuild((Player) p, blk)) {
                    return;
                }
            }
            //--
            ItemStack i = p.getInventory().getItemInMainHand();
            if (blk.getType() == Material.SPONGE) {
                evt.setCancelled(true);
                evt.getBlock().setType(Material.AIR);
                p.getWorld().dropItemNaturally(evt.getBlock().getLocation(), Utils.setItemNameAndLore(new ItemStack(Material.SPONGE), "Fragment of sky launcher", "Fragment to crete the Knock Up current"));
            }
            if (blk.getType() == Material.GRAVEL) {
                if (Utils.Possibilitat(35)) {
                    evt.getBlock().getDrops().clear();
                }
                if (Utils.Possibilitat(1)) {
                    p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.IRON_INGOT));
                }
                if (Utils.Possibilitat(4)) {
                    p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.IRON_BARS));
                }
                if (Utils.Possibilitat(3)) {
                    p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.GOLD_NUGGET, 1));
                }
            }
            if (blk.getType() == Material.STONE) {
                if (Utils.Possibilitat(1)) {
                    if (Utils.Possibilitat(1)) {
                        p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.DIAMOND));
                    }
                    if (Utils.Possibilitat(2)) {
                        p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.IRON_ORE));
                    }
                    if (Utils.Possibilitat(1)) {
                        p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.GOLD_ORE));
                    }
                }
            }
            if (blk.getType() == Material.SPAWNER) {
                p.damage(1);
                if (Utils.Possibilitat(80)) {
                    if (Utils.Possibilitat(62)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * Utils.NombreEntre(1, 25), Utils.NombreEntre(0, 1)));
                    } else {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * Utils.NombreEntre(1, 12), 0));
                    }
                }
                p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.IRON_INGOT, Utils.NombreEntre(1, 10)));
                p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.IRON_BARS, 6));
                if (Utils.Possibilitat(1)) {
                    p.getWorld().dropItemNaturally(blk.getLocation(), new ItemStack(Material.DIAMOND));
                    //p.setFireTicks(20 * 24);
                }
                if (Utils.Possibilitat(85)) {
                    //p.setFireTicks(20 * 5);
                    p.damage(1);
                    CreatureSpawner spawner = (CreatureSpawner) blk.getState();
                    EntityType spawnedType = spawner.getSpawnedType();
                    for (Player pl : Utils.getNearbyPlayers(blk.getLocation(), 35)) {
                        pl.damage(2.8, p);
                        p.getWorld().spawnEntity(pl.getLocation(), spawnedType);
                    }

                    p.getWorld().spawnEntity(blk.getLocation(), spawnedType);
                    p.getWorld().spawnEntity(blk.getLocation().add(0, 2, 0), spawnedType).setVelocity(Vector.getRandom());
                    p.getWorld().spawnEntity(blk.getLocation().add(0, 3, 0), spawnedType).setVelocity(Vector.getRandom());
                }
            }
            //Bukkit.broadcastMessage("Msg0");
            if (FastSurvival.Config().ObtenirPropietatBoolean("AutomaticToolsRequireRedstone")) {
                if (i.getItemMeta() == null) return;
                if (!i.getItemMeta().hasLore()) return;
            }
            if (MoonUtils.IsInMoon(p)) {
                return;
            }
            //Bukkit.broadcastMessage("Msg2");
            if (isAxe(i)) {
                CutTree(blk, p, true);
            }
            if (isPickaxeOrShovel(i)) {
                if (isOre(blk.getType())) {
                    HarvestVein(blk, p);
                    for (Entity e : p.getNearbyEntities(15, 15, 15)) {
                        if (e instanceof Item) {
                            Item it = (Item) e;
                            //if(isOre(it.getItemStack().getType())){
                            Vector vec = Utils.CrearVector(it.getLocation(), p.getEyeLocation());
                            vec.multiply(0.1);
                            it.setVelocity(vec);
                            //}
                        }
                    }
                    return;
                }
                Boolean dins = true; //true=dins, false=fora
                Boolean amunt = false;

                if (blk.getY() < p.getLocation().getBlockY()) {
                    dins = false;
                    amunt = false;
                }
                if (blk.getY() > (p.getLocation().getBlockY() + 2)) {
                    dins = false;
                    amunt = true;
                }
//				if (dins){
//					Bukkit.broadcastMessage("dins");
//				}else{
//					Bukkit.broadcastMessage("fora");
//				}
                ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
                if (!dins) {
                    //Sota terra
                    faces.add(BlockFace.NORTH);
                    faces.add(BlockFace.SOUTH);
                    faces.add(BlockFace.EAST);
                    faces.add(BlockFace.WEST);
                    faces.add(BlockFace.NORTH_EAST);
                    faces.add(BlockFace.NORTH_WEST);
                    faces.add(BlockFace.SOUTH_EAST);
                    faces.add(BlockFace.SOUTH_WEST);

                } else {
                    //Sobre terra
                    faces.add(BlockFace.UP);
                    faces.add(BlockFace.DOWN);
                }


                for (BlockFace f : faces) {
                    Block b = blk.getRelative(f);
                    if (dins == false) {

                        //sota
                        Block relative = b.getRelative(BlockFace.UP);
                        if (!amunt) {
                            relative = b.getRelative(BlockFace.UP);
                        } else {
                            relative = b.getRelative(BlockFace.DOWN);
                        }
                        if (relative.isLiquid()) {
                            continue;
                        }
                        if (!(relative.getPistonMoveReaction() == PistonMoveReaction.BREAK || relative.isEmpty())) {
                            continue;
                        }
                        if (p.getLocation().getY() > 40) {

                        }

                    } else {

                        //sobre
                        if (b.getY() < p.getLocation().getBlockY()) {
                            continue;
                        }
                        if (b.getY() > (p.getLocation().getBlockY() + 2)) {
                            continue;
                        }

                    }

                    if (!(b.getType() == Material.STONE
                            || b.getType() == Material.DIORITE
                            || b.getType() == Material.GRANITE
                            || b.getType() == Material.ANDESITE
                            || b.getType() == Material.GRASS
                            || b.getType() == Material.DIRT
                            || b.getType() == Material.GRAVEL
                            || b.getType() == Material.CLAY
                            || b.getType() == Material.LEGACY_STAINED_CLAY
                            || b.getType() == Material.GLASS
                            || b.getType() == Material.SOUL_SAND
                            || b.getType() == Material.SAND
                            || b.getType() == Material.NETHERRACK
                            || b.getType() == Material.SANDSTONE)) {

                        continue;
                    }
                    if (p.getFoodLevel() < 5) {
                        if (Utils.Possibilitat(30)) {
                            p.sendMessage("You need more food for automatic tool effect");
                        }
                        continue;
                    }
                    //Bukkit.broadcastMessage("done");
                    b.breakNaturally(i);
                    p.setExhaustion((float) (p.getExhaustion() + 0.025));
//					if (Utils.Possibilitat(50)){
//						i.setDurability((short) (i.getDurability() + 1));
//					}

                }
            }
//			if(i.getType().name().contains("SPADE")){
//				if (!(blk.getType() == Material.STONE || blk.getType() == Material.DIRT)){
//					return;
//				}
//				ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
//				faces.add(BlockFace.UP);	      	      	
//				faces.add(BlockFace.DOWN);
//				for (BlockFace f : faces){
//					Block b = blk.getRelative(f);
//					if (b.getY() < p.getLocation().getBlockY()){
//						continue;
//					}
//					if (b.getY() > (p.getLocation().getBlockY() + 2)){
//						continue;
//					}
//					if (b.getType() != Material.STONE){
//						continue;
//					}
//					if (p.getFoodLevel() < 6){
//						continue;
//					}
//
//					b.breakNaturally(i);
//					p.setExhaustion((float) (p.getExhaustion() + 0.075));
//					i.setDurability((short) (i.getDurability() + 1));
//				}  
//			}


        }

        Block b = evt.getBlock();
        World w = b.getWorld();
        if (MoonUtils.IsEarth(w)) {
            //--
            if (b.getType() == Material.LEGACY_LEAVES && b.getLocation().getBlockY() > 90) {
                evt.setCancelled(true);
            }


        }
    }

    public boolean isPickaxeOrShovel(ItemStack i) {
        return i.getType().name().contains("PICKAXE") || i.getType().name().contains("SPADE");
    }

    public boolean isAxe(ItemStack i) {
        return i.getType().name().contains("AXE") && (!i.getType().name().contains("PICKAXE"));
    }

    public boolean isOre(Material b) {
        return b == Material.COAL_ORE
                || b == Material.IRON_ORE
                || b == Material.GOLD_ORE
                || b == Material.GLOWSTONE
                || b == Material.REDSTONE_ORE
                || b == Material.LEGACY_GLOWING_REDSTONE_ORE
                || b == Material.DIAMOND_ORE
                || b == Material.LAPIS_ORE
                || b == Material.NETHER_QUARTZ_ORE
                || b == Material.EMERALD_ORE;
    }

    public void HarvestVein(Block b, Player p) {
        CutTree(b, p, false);
    }

    public void CutTree(Block b, Player p, Boolean tree) {
        World w = b.getWorld();
        Location l = b.getLocation();
        if (w.getHighestBlockAt(l).getY() > 80 && b.getType() == Material.LEGACY_LEAVES) {
            return;
        }
        ItemStack i = p.getInventory().getItemInMainHand();
        Material m = b.getType();
        if (!(m == Material.LEGACY_LOG || m == Material.LEGACY_LOG_2 || m == Material.AIR) && tree) {
            return;
        }
        if (!isOre(m) && !tree) {
            return;
        }
        b.breakNaturally(i);
        p.setExhaustion((float) (p.getExhaustion() + 0.075));
        i.setDurability((short) (i.getDurability() + 1));
        // Recursivity
        BlockFace[] faces;
        faces = BlockFace.values();
        for (BlockFace f : faces) {
            Block nb = b.getRelative(f);
            if (nb.getType() == Material.AIR) {
                continue;
            }
            CutTree(nb, p, tree);
            //FastSurvival.getWorld().createExplosion(nb.getLocation(), 2F);

        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent evt) {
        SkyListener.handlePlayerInteractEvent(evt);
        MoonListener.handlePlayerInteractEvent(evt);
        Player p = evt.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        World world = FastSurvival.getPlugin().getServer().getWorlds().get(0);
        Inventory inv = p.getInventory();

        if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockFace blockFace = evt.getBlockFace();
            Block blk = evt.getClickedBlock().getRelative(blockFace);
            if (isPickaxeOrShovel(i)) {
                if (!MoonUtils.IsInMoon(p)) {
                    if (WGUtils.canBuild(p, blk)
                            && (blk.isEmpty() || blk.isLiquid())) {
                        if (!evt.getClickedBlock().isPassable()) {
                            if (Utils.trySpendItem(
                                    new ItemStack(Material.TORCH, 1), p)) {
                                if (blockFace == BlockFace.UP) blk.setType(Material.TORCH);
                                if (Utils.getFacesNSEW().contains(blockFace)) {
                                    blk.setType(Material.WALL_TORCH);
                                    Directional directional = (Directional) blk.getBlockData();
                                    directional.setFacing(blockFace);
                                    blk.setBlockData(directional, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent evt) {

        Entity e = evt.getEntity();
        Block b = evt.getBlock();
        if (e instanceof Enderman) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDies(EntityDeathEvent evt) {
        Entity e = evt.getEntity();
        if (e instanceof Ghast && Utils.NombreEntre(0, 100) <= 50) {
            e.getLocation().getWorld().dropItemNaturally(e.getLocation(), SkyUtils.getSkyCrystal());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {

        Player p = evt.getPlayer();
        String msg = FastSurvival.Config().ObtenirPropietat("join-message");
        if (FastSurvival.getPlugin().config.getBoolean("youAreAwesome")) {
//            p.sendMessage("You are awesome!");
        }
        else {
//            p.sendMessage("You are not awesome...");
        }
        if (msg.length() > 0) {
            p.sendMessage(msg);
        }
        //p.teleport(new Location(Bukkit.getWorlds().get(1), 50, 50 , 50));
        if (DebugOptions.skyGenerationMode()) {
            SkyUtils.teleportPlayerToSky(p);
        }
        if (DebugOptions.moonGenerationMode()) {
            MoonUtils.teleportPlayerToMoon(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDmg(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player p = (Player) evt.getEntity();
            if (evt.getDamage() < 0) {
                evt.setDamage(0);
                evt.setCancelled(true);
            }
            //p.sendMessage(Double.toString(evt.getDamage()));
        }
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent evt) {
        if (MoonUtils.IsEarth(evt.getBlock().getWorld()) && evt.getBlock().getLocation().getBlockY() > 90) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent evt) {
        SkyNexus.loadAll();
        getLogger().info(" SkyNexus.loadAll()");

    }


}
