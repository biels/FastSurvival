package com.biel.FastSurvival.Dimensions.Moon;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TeleporterListener implements Listener {

    public static void handlePlayerInteractEvent(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if (b == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (b.getType() == Material.STONE_BUTTON) {
            ArrayList<Player> pls = Utils.getNearbyPlayers(p, 32);
            pls.add(p);
            double energy = detectMoonPortal(b.getLocation());
            double Maxenergy = 730 + pls.size() * 210;
            double ratio = energy / Maxenergy;
            //if(energy != 0){
            //Teleport
            for (Player pl : pls) {
                String pName = "";
                if (!p.getName().equalsIgnoreCase(pl.getName())) {
                    pName = "(" + ChatColor.YELLOW + p.getName() + ChatColor.WHITE + ")";
                }
                pl.sendMessage(Utils.L("D_MOON_TELEPORTER_ENERGY") + ": (" + energy + "/" + Maxenergy + ") " + Integer.toString((int) Math.round(ratio * 100)) + "%" + pName);
            }
            if (ratio >= 1) {

                if (MoonUtils.IsInMoon(p)) {
                    Bukkit.broadcastMessage(Utils.L("D_TOEARTH"));
                    MoonUtils.portalActivateToEarth(pls, b);
                } else {
                    Bukkit.broadcastMessage(Utils.L("D_TOMOON"));
                    MoonUtils.portalActivateToMoon(pls, b);
                }

            }
            //}
        }
        if (Tag.WOODEN_BUTTONS.isTagged(b.getType())) {
            Switch blockData = (Switch) b.getBlockData();
            BlockFace facing = blockData.getFacing();
            Switch.Face face = blockData.getFace();
            Block redstone = null;
            BlockFace blockFace = null;
            if (face == face.WALL) {
                blockFace = facing.getOppositeFace();
            } else {
            	if (face == face.CEILING) blockFace = blockFace.UP;
            	if (face == face.FLOOR) blockFace = blockFace.DOWN;
            }
            redstone = b.getRelative(blockFace);
            if (redstone.getType() == Material.REDSTONE_BLOCK) {
                ArrayList<Player> pls = Utils.getNearbyPlayers(p, 32);
//                pls.add(p);
                pls.get(Utils.NombreEntre(0, pls.size() - 1)).setVelocity(new Vector(Utils.NombreEntre(-1, 1), Utils.NombreEntre(1, 2), Utils.NombreEntre(-1, 1)));
            }
        }
    }

    public ArrayList<Player> detectMoonPortalPlayers(Location l) {
        ArrayList<Player> pls = new ArrayList<Player>();

        return pls;
    }

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
        return e;
    }
}

