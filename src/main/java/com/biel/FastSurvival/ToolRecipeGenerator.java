package com.biel.FastSurvival;

import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;

public class ToolRecipeGenerator {
	public static void addToolRecipes(){
		pickacxeRecipe();
		//sampleRec();
	}
	static void pickacxeRecipe(){
		ArrayList<Material> ms = new ArrayList<Material>();
		ms.addAll(getFromKb("_AXE"));
		ms.addAll(getFromKb("_PICKAXE"));
		ms.addAll(getFromKb("_SPADE"));
		ms.add(Material.WOODEN_SWORD);

		for (Material m : ms){
			ShapelessRecipe sr= new ShapelessRecipe(getKey("pickaxe" + m.toString()), Utils.setItemLore(new ItemStack(m), ChatColor.YELLOW + "Automatic"));

			sr.addIngredient(1, m);
			sr.addIngredient(1, Material.REDSTONE);
			Bukkit.getServer().addRecipe(sr);
		}		
	}
	private static NamespacedKey getKey(String key) {
		return new NamespacedKey(FastSurvival.getPlugin(), key + "FSTool");
	}
	static void sampleRec(){
		ShapelessRecipe sr= new ShapelessRecipe(getKey("pickaxe"),new ItemStack(Material.DIAMOND));
		sr.addIngredient(1, Material.IRON_INGOT);
		sr.addIngredient(2, Material.REDSTONE);
		
		Bukkit.getServer().addRecipe(sr);
	}
	static ArrayList<Material> getFromKb(String kb){
		ArrayList<Material> ms = new ArrayList<Material>();
		for (Material m : Material.values()){
			if(m.name().contains(kb)){
				ms.add(m);
			}
		}
		return ms;
	}
	
}
