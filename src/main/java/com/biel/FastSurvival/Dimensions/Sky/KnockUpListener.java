package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;


public class KnockUpListener implements Listener {
	@EventHandler
    public void onClick(PlayerInteractEvent event){
        final Player p = event.getPlayer();
       
		if(p.getInventory().getItemInMainHand().getType() == Material.FEATHER){
			ArrayList<Player> pls = Utils.getNearbyPlayers(p, 32);
			pls.add(p);
        	if(isInKnockUp(p)){
        		if (SkyUtils.IsInSky(p)){
        				Bukkit.broadcastMessage("A la terra!");
        				//SkyUtils.portalActivateToEarth(pls, b);
        			}else{
        				Bukkit.broadcastMessage("Al cel!");
        				SkyUtils.makePlayerKnockUp(p);
        			}
        			
        		}
			//}
        }
	}
	public boolean isInKnockUp(Player ply){
		ArrayList<BlockFace> arr = new ArrayList<BlockFace>();
		arr.add(BlockFace.NORTH);
		arr.add(BlockFace.SOUTH);
		arr.add(BlockFace.EAST);
		arr.add(BlockFace.WEST);
		arr.add(BlockFace.NORTH_EAST);
		arr.add(BlockFace.NORTH_WEST);
		arr.add(BlockFace.SOUTH_EAST);
		arr.add(BlockFace.SOUTH_WEST);
		for(BlockFace f : arr){
			Block bc = ply.getLocation().getBlock().getRelative(f);
			if (bc.getType() != Material.SPONGE){
				return false;
			}
		}
		return true;
	}
	
}

