package com.biel.FastSurvival;

import com.biel.FastSurvival.Bows.BowRecipeGenerator;
import com.biel.FastSurvival.Bows.CustomBowsListener;
import com.biel.FastSurvival.BuilderWand.BuilderWandListener;
import com.biel.FastSurvival.BuilderWand.BuilderWandUtils;
import com.biel.FastSurvival.Dimensions.Moon.*;
import com.biel.FastSurvival.Dimensions.Sky.*;
import com.biel.FastSurvival.MobIntelligence.MobListener;
import com.biel.FastSurvival.Recall.RecallListener;
import com.biel.FastSurvival.Recall.RecallUtils;
import com.biel.FastSurvival.SpecialItems.SpecialItem;
import com.biel.FastSurvival.SpecialItems.SpecialItemsUtils;
import com.biel.FastSurvival.Turrets.TurretListener;
import com.biel.FastSurvival.Turrets.TurretUtils;
import com.biel.FastSurvival.Utils.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FastSurvival extends JavaPlugin {
    public GestorPropietats pTemp = new GestorPropietats("pTemp.txt");
    FileConfiguration config = getConfig();

    public void onEnable() {
        config.addDefault("youAreAwesome", true);
        config.addDefault("obtainableBows.teleport.craftable", true);
        config.addDefault("obtainableBows.teleport.droppable", true);

        config.addDefault("obtainableBows.magnetic.craftable", true);
        config.addDefault("obtainableBows.magnetic.droppable", true);

        config.addDefault("obtainableBows.explosive.craftable", true);
        config.addDefault("obtainableBows.explosive.droppable", true);

        config.addDefault("obtainableBows.illuminator.craftable", true);
        config.addDefault("obtainableBows.illuminator.droppable", true);

        config.addDefault("obtainableBows.bouncy.craftable", true);
        config.addDefault("obtainableBows.bouncy.droppable", true);

        config.addDefault("obtainableBows.icy.craftable", true);
        config.addDefault("obtainableBows.icy.droppable", true);

        config.addDefault("obtainableBows.water.craftable", true);
        config.addDefault("obtainableBows.water.droppable", true);

        config.addDefault("obtainableBows.whiter.craftable", true);
        config.addDefault("obtainableBows.whiter.droppable", true);

        config.addDefault("obtainableBows.multiTarget.craftable", true);
        config.addDefault("obtainableBows.multiTarget.droppable", true);

        config.addDefault("obtainableBows.skyExplosive.craftable", false);
        config.addDefault("obtainableBows.skyExplosive.droppable", true);

        config.addDefault("obtainableBows.skyJet.craftable", false);
        config.addDefault("obtainableBows.skyJet.droppable", true);

        config.addDefault("obtainableBows.electric.craftable", false);
        config.addDefault("obtainableBows.electric.droppable", false);

        config.options().copyDefaults(true);
        saveConfig();
        getLogger().info("FastSurvival onEnable has been invoked!");
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
            System.out.println("Metrics fail :-(");
        }
        if (!FastSurvival.getPlugin().getDataFolder().exists()) FastSurvival.getPlugin().getDataFolder().mkdirs();
        File nexusesFolderPath = new File(SkyNexus.getSkyNexusFolderPath());
        if (!nexusesFolderPath.exists()) nexusesFolderPath.mkdirs();

        FileExtraction.extractResourcesFolder("Lang", false);
//
        System.out.println("Fast Survival Enabled");

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new CustomBowsListener(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new TeleporterListener(), this);
        getServer().getPluginManager().registerEvents(new MoonListener(), this);
//		getServer().getPluginManager().registerEvents(new KnockUpListener(), this);
        getServer().getPluginManager().registerEvents(new SkyListener(), this);
        getServer().getPluginManager().registerEvents(new RecallListener(), this);
        getServer().getPluginManager().registerEvents(new BuilderWandListener(), this);
        getServer().getPluginManager().registerEvents(new TurretListener(), this);
        SpecialItemsUtils.registerItemListeners();
//		//
        BukkitTask loadWorldsTask = getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
//				MoonUtils.loadMoon();
//				SkyUtils.loadSky();
//				Bukkit.broadcastMessage("Addtitional worlds loaded!");
//				TurretUtils.startTurretLogicTask();
//				Bukkit.broadcastMessage("Turret logic started!");
            }
        }, 1);

//		getServer().getPluginManager().registerEvents(new MobTracker(), this);
        // TODO Enable recipes

        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                BowRecipeGenerator.addBowRecipes();
                ToolRecipeGenerator.addToolRecipes();
                RecallUtils.addRecallRecipe();
                MoonUtils.spaceGlassRecipe();
                BuilderWandUtils.addWandRecipe();
                TurretUtils.addRecipes();

                getLogger().info("Added all recipes to server");

                // DEBUG (Load worlds)
                if (DebugOptions.skyGenerationMode()) {
                    SkyUtils.loadSkyIfNecessary();
                } if (DebugOptions.moonGenerationMode()) {
                    MoonUtils.loadMoonIfNecessary();
                }
            }
        }, 20 * 5);
//		MoonUtils.loadMoon();
//		Turret.loadInstances();

        getLogger().info("FastSurvival Finished enabled");
        if (DebugOptions.isDebugEnabled()) {
            HotReload.startWatching();
            TestArea.onReload();
            Bukkit.broadcastMessage(ChatColor.GREEN + "[FS] RELOADED main");
        }

    }

    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
        if (DebugOptions.isDebugEnabled()) {
            HotReload.stopWatching();
        }
        //Turret.saveInstances();

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("save")) { // If the player typed /basic then do the following...
            if (sender instanceof Player) {
                Player p = (Player) sender;
                //Turret.saveInstances();
                getLogger().info("Turrets saved!");

            }
            return true;
        } //If this has happened the function will return true.
        if (cmd.getName().equalsIgnoreCase("load")) { // If the player typed /basic then do the following...
            if (sender instanceof Player) {
                Player p = (Player) sender;
                //Turret.loadInstances();
                getLogger().info("Turrets loaded!");

            }
            return true;
        } //If this has happened the function will return true.
        if (cmd.getName().equalsIgnoreCase("moon")) { // If the player typed /basic then do the following...
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)) {
                    p.sendMessage("You must be in creative to use this command. Build a teleporter to go there in survival.");
                    return true;
                }
                if (MoonUtils.IsInMoon(p)) {
                    MoonUtils.teleportPlayerToEarth(p);
                    getLogger().info("Teleported to earth!");
                    return true;
                }
                MoonUtils.teleportPlayerToMoon(p);
                getLogger().info("Teleported to moon!");

            }
            return true;
        } //If this has happened the function will return true.
        if (cmd.getName().equalsIgnoreCase("sky")) { // If the player typed /basic then do the following...
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)) {
                    p.sendMessage("Has d'estar en creatiu per per això! Construeix un corrent d'aigua (Knock Up) per fer-ho legalment en supervivència!");
                    return true;
                }
                if (SkyUtils.IsInSky(p)) {
                    SkyUtils.teleportPlayerToEarth(p);
                    getLogger().info("Teleported to earth!");
                    return true;
                }
                SkyUtils.teleportPlayerToSky(p);
                getLogger().info("Teleported to Sky!");

            }
            return true;
        } //If this has happened the function will return true.
        if (cmd.getName().equalsIgnoreCase("e")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command!");
                    return true;
                }
                Inventory i = p.getInventory();
                ItemStack itemStacke = new ItemStack(Material.BOW);
                itemStacke.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
                i.addItem(itemStacke);
                ItemStack itemStack3 = new ItemStack(Material.DIAMOND_SWORD);
                itemStack3.addEnchantment(Enchantment.DAMAGE_ALL, 3);
                i.addItem(itemStack3);

                i.addItem(new ItemStack(Material.DIAMOND_AXE));
                ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
                itemStack.addEnchantment(Enchantment.DIG_SPEED, 3);
                i.addItem(itemStack);
                i.addItem(new ItemStack(Material.GOLD_ORE, 64));
                i.addItem(new ItemStack(Material.GOLD_ORE, 64));
                i.addItem(new ItemStack(Material.COOKED_BEEF, 64));
                p.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                p.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                p.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                p.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                i.addItem(new ItemStack(Material.ARROW, 64));
                i.addItem(new ItemStack(Material.ARROW, 64));

            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("t")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command!");
                    return true;
                }
                Inventory i = p.getInventory();
                int count = 0;
                while (count < 2) {
                    i.addItem(TurretUtils.createNewItemStack(Utils.NombreEntre(1, 3)));
                    count++;
                }


            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("moonmats")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command!");
                    return true;
                }
                Inventory i = p.getInventory();
                i.addItem(new ItemStack(Material.IRON_BLOCK, 8));
                i.addItem(new ItemStack(Material.LEGACY_IRON_FENCE, 4));
                i.addItem(new ItemStack(Material.LEGACY_WOOD_BUTTON, 2));
                i.addItem(new ItemStack(Material.STONE_BUTTON, 1));


            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("recall")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command!");
                    return true;
                }
                Inventory i = p.getInventory();
                i.addItem(RecallUtils.getRecallItem(null));


            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("wand")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command!");
                    return true;
                }
                Inventory i = p.getInventory();
                i.addItem(BuilderWandUtils.getWandItem());


            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("it")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to do this!");
                    return true;
                }
                ArrayList<SpecialItem> registeredSpecialItems = SpecialItemsUtils.getRegisteredSpecialItems();
                Inventory i;
                if (args.length == 0) {
                    int rows = 1;
                    while (rows * 9 < registeredSpecialItems.size()) {
                        rows++;
                    }
                    i = Bukkit.createInventory(p, 9 * rows, ChatColor.RED + "Special items - Creative mode");
                } else {
                    i = p.getInventory();
                }
                boolean fet = false;
//				if(args.length == 1){
//					try {
//						int parseInt = Integer.parseInt(args[1]);
//						if(parseInt <= 3 && parseInt >= 1){
//							int rows = 1;
//							while(rows * 9 < registeredSpecialItems.size()){
//								rows++;
//							}
//							i = Bukkit.createInventory(p, 9 * rows, ChatColor.RED + "Tier "+ parseInt +" Special items - Creative mode");
//
//							int count = 0;
//							while (count <= rows * 9){
//								i.addItem(SpecialItemsUtils.getRandomSpecialItem(parseInt));
//							}
//							fet = true;
//						}
//					} catch (NumberFormatException e) {
//					} catch (IllegalArgumentException e) {
//					}
//				}
                if (fet == false) {
                    for (SpecialItem s : registeredSpecialItems) {
                        i.addItem(s.createNewItemStack());
                    }
                }

                if (args.length == 0 || fet == true) {
                    p.openInventory(i);
                }

                //p.setGameMode(GameMode.SURVIVAL);
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("deploy")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.isOp())) {
                    p.sendMessage("You must be an operator to use this command.");
                    return true;
                }
//                String op = args[0];
                HotReload.compileAndDeploy();
            }
        }
        if (cmd.getName().equalsIgnoreCase("vp")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.isOp())) {
                    p.sendMessage("You must be an operator to use this command.");
                    return true;
                }
                MoonChunkGenerator generator = (MoonChunkGenerator) MoonUtils.getMoon().getGenerator();
                if (generator != null) {
                    generator.vpCommand(p, args);
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("testarea")) { // If the player typed /basic then do the following...
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                String op = args[0];
                TestArea testArea = DebugOptions.getTestArea(p.getWorld());
                int defaultRadius = 10;
                if (op.equalsIgnoreCase("create")) {
                    int radius = args.length >= 2 ? Integer.parseInt(args[1]) : defaultRadius;
                    Bukkit.broadcastMessage(testArea.toString());
                    Bukkit.broadcastMessage("center: " + testArea.getCenter() + " radius: " + testArea.getRadius());
                    testArea.create(p.getLocation(), radius, p, false);
                }
                if (op.equalsIgnoreCase("move")) {
                    if (!testArea.isActive()) {
                        testArea.create(p.getLocation(), defaultRadius, p, false);
                    } else
                        testArea.create(p.getLocation(), null, p, true);
                }
                if (op.equalsIgnoreCase("attach")) {
                    if (args.length == 1) {
                        if (testArea.getAttachedCommand().equals(""))
                            Bukkit.broadcastMessage(ChatColor.RED + "[TA] There is no attached command");
                        else
                            Bukkit.broadcastMessage("[TA] Attached command: " + ChatColor.GREEN + testArea.getAttachedCommand());
                    }
                    String command = Stream.of(args).skip(1)
                            .map(integer -> integer.toString())
                            .collect(Collectors.joining(" "));
                    testArea.attach(command, p);
                }
                if (op.equalsIgnoreCase("detach")) {
                    testArea.detach();
                }
                if (op.equalsIgnoreCase("clear")) {
                    testArea.clear(true);
                }
                if (op.equalsIgnoreCase("overclear")) {
                    if (args.length == 1) testArea.overClear(10);
                    else testArea.overClear(Integer.parseInt(args[1]));
                }
                if (op.startsWith("gen")) {
                    testArea.generate(p);
                }
                if (op.equalsIgnoreCase("auto")) {
                    if (args.length == 1) testArea.toggleAuto(p);
                    else if (args[1].equalsIgnoreCase("on")) testArea.auto(true, p);
                    else if (args[1].equalsIgnoreCase("off")) testArea.auto(false, p);
                }
                if (op.equalsIgnoreCase("remove")) {
                    testArea.remove();
                }
                if (op.equalsIgnoreCase("offset")) {
                    if (args.length == 4) {
                        int x = Integer.parseInt(args[1]);
                        int y = Integer.parseInt(args[2]);
                        int z = Integer.parseInt(args[3]);
                        testArea.offset(x, y, z);
                    }
                    if (args.length >= 2 && args[1].equals("clear")) testArea.offset(0, 0, 0);
                    if (args.length >= 2 && args[1].equals("floor"))
                        testArea.offset((int) testArea.getOffset().getX(), -testArea.getRadius() + 1, (int) testArea.getOffset().getZ());
                    if (args.length >= 3 && args[1].equals("move"))
                        testArea.offset((int) testArea.getOffset().getX(), Integer.parseInt(args[2]), (int) testArea.getOffset().getZ());
                    if (args.length == 2 && args[1].equals("here")) {
                        testArea.offset(
                                ((int) p.getLocation().getX() - (int) testArea.getCenter().getX()),
                                ((int) p.getLocation().getY() - (int) testArea.getCenter().getY()),
                                ((int) p.getLocation().getZ()) - (int) testArea.getCenter().getZ());
                    }
                    int xOffset = (int) testArea.getOffset().getX();
                    int yOffset = (int) testArea.getOffset().getY();
                    int zOffset = (int) testArea.getOffset().getZ();
                    ChatColor xChatColor = (Math.abs(xOffset) < testArea.getRadius()) ? ChatColor.GREEN : ChatColor.RED;
                    ChatColor yChatColor = (Math.abs(yOffset) < testArea.getRadius()) ? ChatColor.GREEN : ChatColor.RED;
                    ChatColor zChatColor = (Math.abs(zOffset) < testArea.getRadius()) ? ChatColor.GREEN : ChatColor.RED;
                    Bukkit.broadcastMessage("[TA] Current offset: " +
                            xChatColor + (int) testArea.getOffset().getX() + " " +
                            yChatColor + (int) testArea.getOffset().getY() + " " +
                            zChatColor + (int) testArea.getOffset().getZ());
                    if (xChatColor == ChatColor.RED || yChatColor == ChatColor.RED || zChatColor == ChatColor.RED) {
                        Bukkit.broadcastMessage(ChatColor.RED + "[TA] Offset is out of the test area");
                    }
                }
                if (op.equalsIgnoreCase("tp")) {
                    p.teleport(testArea.getCenter());
                }
                if (op.equalsIgnoreCase("size")) {
                    if (args.length == 2) {
                        int size = Integer.parseInt(args[1]);
                        testArea.size(size, p);
                    } else Bukkit.broadcastMessage("[TA] Current size: " + ChatColor.GREEN + testArea.getSize());
                }
                if (op.equalsIgnoreCase("expand")) {
                    if (args.length == 2) {
                        int expandSize = Integer.parseInt(args[1]);
                        testArea.size(testArea.getSize() + expandSize, p);
                    }
                    Bukkit.broadcastMessage("[TA] Current size: " + ChatColor.GREEN + testArea.getSize());
                }
                if (op.equalsIgnoreCase("center")) {
                    if (args.length == 1) testArea.toggleCenter(p);
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("on")) testArea.activateCenter(true, p);
                        if (args[1].equalsIgnoreCase("off")) testArea.activateCenter(false, p);
                    }
                }
                if (op.equalsIgnoreCase("floor")) {
                    if (args.length == 1) testArea.toggleFloor(p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("on")) testArea.floor(true, p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("off")) testArea.floor(false, p);
                    if (args.length >= 3 && args[1].equalsIgnoreCase("height"))
                        testArea.floorHeight(Integer.parseInt(args[2]), p);
                    if (args.length >= 3 && args[1].startsWith("mat")) testArea.floorMaterial(args[2], p);
                }
                if (op.equalsIgnoreCase("fill")) {
                    if (args.length == 1) testArea.toggleFill(p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("on")) testArea.fill(true, p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("off")) testArea.fill(false, p);
                    if (args.length >= 3 && args[1].startsWith("mat")) testArea.fillMaterial(args[2], p);
                }
                if (op.equalsIgnoreCase("compass")) {
                    if (args.length == 1) testArea.toggleCompass(p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("on")) testArea.compass(true, p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("off")) testArea.compass(false, p);
                }
                if (op.equalsIgnoreCase("axis")) {
                    if (args.length == 1) testArea.toggleAxis(p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("on")) testArea.axis(true, p);
                    if (args.length >= 2 && args[1].equalsIgnoreCase("off")) testArea.axis(false, p);
                }
            }

        }
        if (cmd.getName().equalsIgnoreCase("text")) { // If the player typed /basic then do the following...
            Location l;
            int argsIndex = 1;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                String arg = "Hola";
                int size = 12;
                if (args.length >= 1) {
                    arg = args[0];
                    argsIndex++;
                }
                if (args.length >= 2) {
                    size = Integer.parseInt(args[1]);
                    argsIndex++;
                }
                l = p.getEyeLocation().add(p.getLocation().getDirection().multiply(16));
                if (args.length >= 3) {
                    Location locationFromArgs = Utils.getLocationFromArgs(args, argsIndex, p.getWorld());
                    if (locationFromArgs != null) l = locationFromArgs;
                }

                Vector lateralAxis = l.getDirection().getCrossProduct(new Vector(0, 1, 0));
                FontRenderer.renderText(arg, l, lateralAxis.multiply(1), l.getDirection().multiply(-1), size, Material.DIAMOND_BLOCK);
            }

        }
        if (cmd.getName().equalsIgnoreCase("maze")) { // If the player typed /basic then do the following...
            Location l;
            int argsIndex = 1;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                l = p.getLocation().add(0, -1, 0);
                if (args.length >= 3) {
                    Location locationFromArgs = Utils.getLocationFromArgs(args, argsIndex, p.getWorld());
                    if (locationFromArgs != null) l = locationFromArgs;
                }
                int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 8;
                int y = args.length == 2 ? (Integer.parseInt(args[0])) : 8;
//				MazeGenerator maze = new MazeGenerator(x, y);
//				maze.display(p.getLocation());
                SimpleMazeGenerator generator = new SimpleMazeGenerator();
                generator.generateMaze(x);
                generator.build(l, 1, 1, Material.LIGHT_GRAY_CONCRETE, Material.WHITE_CONCRETE, Material.YELLOW_CONCRETE, Material.GREEN_CONCRETE);
            }

        }
        if (cmd.getName().equalsIgnoreCase("temple")) { // If the player typed /basic then do the following...
            Location l;
            int argsIndex = 0;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                l = p.getLocation().add(0, -1, 0);
                if (args.length >= 3) {
                    Location locationFromArgs = Utils.getLocationFromArgs(args, argsIndex, p.getWorld());
                    if (locationFromArgs != null) l = locationFromArgs;
                }
                int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 8;
                int y = args.length == 2 ? (Integer.parseInt(args[0])) : 8;
                SkyTemplePopulator generator = new SkyTemplePopulator();
                generator.generate(l);
            }

        }
        if (cmd.getName().equalsIgnoreCase("theater")) { // If the player typed /basic then do the following...
            Location l;
            int argsIndex = 0;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                l = p.getLocation().add(0, -1, 0);
                if (args.length >= 3) {
                    Location locationFromArgs = Utils.getLocationFromArgs(args, argsIndex, p.getWorld());
                    if (locationFromArgs != null) l = locationFromArgs;
                }
                SkyTheaterPopulator generator = new SkyTheaterPopulator();
                generator.generate(l);
            }

        }
        if (cmd.getName().equalsIgnoreCase("rocket")) { // If the player typed /basic then do the following...
            Location l;
            int argsIndex = 0;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                l = p.getLocation().add(0, -1, 0);
                if (args.length >= 3) {
                    Location locationFromArgs = Utils.getLocationFromArgs(args, argsIndex, p.getWorld());
                    if (locationFromArgs != null) l = locationFromArgs;
                }
                RocketPopulator generator = new RocketPopulator();
                generator.generate(l, new Random());
            }

        }
        if (cmd.getName().equalsIgnoreCase("moonbase")) { // If the player typed /basic then do the following...
            Location l;
            int argsIndex = 0;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                l = p.getLocation().add(0, -1, 0);
                if (args.length >= 3) {
                    Location locationFromArgs = Utils.getLocationFromArgs(args, argsIndex, p.getWorld());
                    if (locationFromArgs != null) l = locationFromArgs;
                }
                MoonBasePopulator generator = new MoonBasePopulator();
                generator.generate(l, l.getWorld());
            }

        }
        if (cmd.getName().equalsIgnoreCase("b")) { // If the player typed /basic then do the following...
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                Inventory i = p.getInventory();
                Iterator<Recipe> itr = Bukkit.recipeIterator();
                while (itr.hasNext()) {
                    Recipe element = itr.next();
                    if (element.getResult().getType() == Material.BOW && element.getResult().getItemMeta().hasLore()) {
                        i.addItem(element.getResult());
                    }
                }
                i.addItem(new ItemStack(Material.ARROW, 64));

            }
            return true;
        } //If this has happened the function will return true.
        if (cmd.getName().equalsIgnoreCase("skycrystals")) { // If the player typed /basic then do the following...
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!(p.getGameMode() == GameMode.CREATIVE)) {
                    p.sendMessage("You must be in creative to use this command.");
                    return true;
                }
                Inventory i = p.getInventory();
                ItemStack skyCrystals = SkyUtils.getSkyCrystal();
                skyCrystals.setAmount(64);
                i.addItem(skyCrystals);
            }
            return true;
        } //If this has happened the function will return true.
        // If this hasn't happened the a value of false will be returned.
        return false;
    }

    //	@Override
    //	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    //		// TODO Auto-generated method stub
    //		ChunkGenerator c = super.getDefaultWorldGenerator(worldName, id);
    //
    //		return c;
    //	}
    static public World getOverworld() {
        return Bukkit.getWorlds().get(0);
    }

    static public GestorPropietats Config() {
        File f = new File(FastSurvival.getPlugin().getDataFolder().getAbsolutePath() + ((String) File.separator) + "Config.txt");
        if (!f.exists()) {
            //Default settings
            GestorPropietats g = new GestorPropietats(f.getAbsolutePath());
            g.EstablirPropietat("LanguageFileName", "EN");
            g.EstablirPropietat("AutomaticToolsRequireRedstone", false);
        }

        return new GestorPropietats(f.getAbsolutePath());
    }

    static public FastSurvival getPlugin() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("FastSurvival");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof FastSurvival)) {
            throw new RuntimeException("Plugin not found (should never happen)");
        }

        return (FastSurvival) plugin;
    }

    static public NamespacedKey getKey(String key) {
        return new NamespacedKey(getPlugin(), key + "FS");
    }
    //	@Override
    //    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    //        return new MoonChunkGenerator();
    //    }
}
