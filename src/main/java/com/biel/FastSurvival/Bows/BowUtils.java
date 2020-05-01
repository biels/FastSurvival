package com.biel.FastSurvival.Bows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BowUtils {
	public enum BowType{TORCH, ENDER, MAGNETIC, EXPLOSIVE, BOUNCY, ICY, WITHER, WATER, MULTI, ELECTRIC, SKY_EXPLOSIVE, SKY_JET}
	static BowType getTypeFromIdString(String s){
		return BowType.values()[Integer.parseInt(s)];
	}
	public static BowType getBowType(ItemStack b){
		String loreid;
		int id;
		if(b.getType() != Material.BOW){return null;}
		try {
			ItemMeta itemMeta = b.getItemMeta();
			if(itemMeta == null) return null;
			loreid = itemMeta.getLore().get(1);
		} catch (Exception e1) {
			//Bukkit.broadcastMessage("No 1 lore");
			return null;
		}
		return getTypeFromIdString(loreid);
	}
}
