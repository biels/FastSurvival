package com.biel.FastSurvival.Bows;

import com.biel.FastSurvival.Bows.BowUtils.BowType;
import com.biel.FastSurvival.FastSurvival;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Iterator;

public class BowRecipeGenerator {

	public static void addBowRecipes(){
		enderRecipe();
		magneticRecipe();
		explosiveRecipe();
		torchRecipe();
		bouncyRecipe();
		icyRecipe();
		waterRecipe();
		witherRecipe();
		multiRecipe();
		electricRecipe();
		skyExplosiveRecipe();
		skyJetRecipe();
	}
	static void enderRecipe(){

//		ShapelessRecipe r = new ShapelessRecipe(getKey("ender"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_GREEN + Utils.L("BOW_NAME_TELEPORT"), ChatColor.WHITE + Utils.L("BOW_DESC_TELEPORT"), getID(BowType.ENDER)));
//		Bukkit.getServer().addRecipe(r);
		ShapedRecipe r = new ShapedRecipe(getKey("ender"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_GREEN + Utils.L("BOW_NAME_TELEPORT"), ChatColor.WHITE + Utils.L("BOW_DESC_TELEPORT"), getID(BowType.ENDER)));
		r.shape("IEI", "EBE", "GRG");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('G', Material.GOLD_INGOT);
        r.setIngredient('I', Material.IRON_INGOT);
        r.setIngredient('E', Material.ENDER_PEARL);
        Bukkit.getServer().addRecipe(r);
//        Bukkit.getLogger().info("Added Ender recipe");
	}
	static void magneticRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("magnetic"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_BLUE + Utils.L("BOW_NAME_MAGNETIC"), ChatColor.WHITE + Utils.L("BOW_DESC_MAGNETIC"), getID(BowType.MAGNETIC)));
		r.shape(" C ", "RBR", "RIR");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('C', Material.COMPASS);
        r.setIngredient('I', Material.IRON_BLOCK);
        Bukkit.getServer().addRecipe(r);
	}
	static void explosiveRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("explosive"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_RED + Utils.L("BOW_NAME_EXPLOSIVE"), ChatColor.WHITE + Utils.L("BOW_DESC_EXPLOSIVE"), getID(BowType.EXPLOSIVE)));
		r.shape(" T ", "IBI", "XRX");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('X', Material.TNT);
        r.setIngredient('I', Material.IRON_INGOT);
        r.setIngredient('T', Material.REDSTONE_TORCH);
        Bukkit.getServer().addRecipe(r);
	}
	static void torchRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("torch"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_GREEN + Utils.L("BOW_NAME_ILLUMINATOR"), ChatColor.WHITE + Utils.L("BOW_DESC_ILLUMINATOR"), getID(BowType.TORCH)));
		r.shape(" T ", "CBC", "SGS");
		r.setIngredient('B', Material.BOW);
		r.setIngredient('C', Material.COAL_BLOCK);
        r.setIngredient('S', Material.STICK);
        r.setIngredient('T', Material.TORCH);
        r.setIngredient('G', Material.GLASS_PANE);
        Bukkit.getServer().addRecipe(r);
	}
	static void bouncyRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("bouncy"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.GREEN + Utils.L("BOW_NAME_BOUNCY"), ChatColor.WHITE + Utils.L("BOW_DESC_BOUNCY"), getID(BowType.BOUNCY)));
		r.shape(" S ", "SBS", "RGR");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('G', Material.GOLD_INGOT);
        r.setIngredient('S', Material.SLIME_BALL);
        Bukkit.getServer().addRecipe(r);
	}
	static void icyRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("icy"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.AQUA + Utils.L("BOW_NAME_ICY"), ChatColor.WHITE + Utils.L("BOW_DESC_ICY"), getID(BowType.ICY)));
		r.shape("GWG", "RBR", "IDI");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('D', Material.DIAMOND);
        r.setIngredient('G', Material.GOLD_INGOT);
        r.setIngredient('I', Material.ICE);
        r.setIngredient('W', Material.CLOCK);
        Bukkit.getServer().addRecipe(r);
	}
	static void witherRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("wither"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.BLACK + Utils.L("BOW_NAME_WITHER"), ChatColor.WHITE + Utils.L("BOW_DESC_WITHER"), getID(BowType.WITHER)));
		r.shape(" S ", "RBR", "WGW");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE_BLOCK);
        r.setIngredient('W', Material.STONE_SWORD);
        r.setIngredient('G', Material.GOLD_INGOT);
        r.setIngredient('S', Material.WITHER_SKELETON_SKULL);
        Bukkit.getServer().addRecipe(r);
	}
	static void waterRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("water"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.BLUE + Utils.L("BOW_NAME_WATER"), ChatColor.WHITE + Utils.L("BOW_DESC_WATER"), getID(BowType.WATER)));
		r.shape(" W ", "LBL", "RVR");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('V', Material.LEGACY_IRON_FENCE);
        r.setIngredient('W', Material.LEGACY_RAW_FISH);
        r.setIngredient('L', Material.LAPIS_BLOCK);
        Bukkit.getServer().addRecipe(r);
	}
	static void multiRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("multi"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_PURPLE + Utils.L("BOW_NAME_MULTITARGET"), ChatColor.WHITE + Utils.L("BOW_DESC_MULTITARGET"), getID(BowType.MULTI)));
		r.shape("AAA", " B ", "RVR");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('V', Material.LEGACY_IRON_FENCE);
        r.setIngredient('A', Material.ARROW);
        //r.setIngredient('D', Material.DISPENSER);
        Bukkit.getServer().addRecipe(r);
	}
	static void electricRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("electric"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_BLUE + Utils.L("BOW_NAME_ELECTRIC"), ChatColor.WHITE + Utils.L("BOW_DESC_ELECTRIC"), getID(BowType.ELECTRIC)));
		r.shape(" D ", "VBV", "RLR");
		r.setIngredient('B', Material.BOW);
        r.setIngredient('R', Material.REDSTONE);
        r.setIngredient('V', Material.LEGACY_IRON_FENCE);
        r.setIngredient('D', Material.DIAMOND);
        r.setIngredient('L', Material.DIAMOND_BLOCK);
        Bukkit.getServer().addRecipe(r);
	}
	static void skyExplosiveRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("skyexplosive"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.DARK_BLUE + Utils.L("BOW_NAME_SKY_EXPLOSIVE"), ChatColor.WHITE + Utils.L("BOW_DESC_SKY_EXPLOSIVE"), getID(BowType.SKY_EXPLOSIVE)));
		r.shape(" D ", "VBV", "RLR");
		r.setIngredient('B', Material.BOW);
		r.setIngredient('R', Material.REDSTONE);
		r.setIngredient('V', Material.LEGACY_IRON_FENCE);
		r.setIngredient('D', Material.DIAMOND);
		r.setIngredient('L', Material.DIAMOND_BLOCK);
		Bukkit.getServer().addRecipe(r);
	}
	static void skyJetRecipe(){
		ShapedRecipe r = new ShapedRecipe(getKey("skyjet"), Utils.setItemNameAndLore(new ItemStack(Material.BOW), ChatColor.AQUA + Utils.L("BOW_NAME_SKY_JET"), ChatColor.WHITE + Utils.L("BOW_DESC_SKY_JET"), getID(BowType.SKY_JET)));
		r.shape(" D ", "VBV", "RLR");
		r.setIngredient('B', Material.BOW);
		r.setIngredient('R', Material.REDSTONE);
		r.setIngredient('V', Material.LEGACY_IRON_FENCE);
		r.setIngredient('D', Material.DIAMOND);
		r.setIngredient('L', Material.DIAMOND_BLOCK);
		Bukkit.getServer().addRecipe(r);
	}

	private static NamespacedKey getKey(String key) {
		return new NamespacedKey(FastSurvival.getPlugin(), key + "Bow");
	}

	static String getID(BowType t){
		return Integer.toString(t.ordinal());
	}
	public static ArrayList<ItemStack> getAllBows(){
		ArrayList<ItemStack> arr = new ArrayList<ItemStack>();
		Iterator<Recipe> itr = Bukkit.recipeIterator();
		while(itr.hasNext()) {
			Recipe element = itr.next();
			if(element.getResult().getType() == Material.BOW && element.getResult().getItemMeta().hasLore()){
				arr.add(element.getResult());
			}
		}
		return arr;
	}
	public static ItemStack getRandomBow(Boolean electric){
		int id = Utils.NombreEntre(0, BowUtils.BowType.values().length - 1);
		
		BowType t = BowUtils.BowType.values()[id];
		if (!electric && t == BowType.ELECTRIC){return getRandomBow(false);}
		ItemStack i = BowRecipeGenerator.getBow(t);
		return i;
	}
	public static ItemStack getBow(BowType t){
		for (ItemStack i : getAllBows()){
			if (BowUtils.getBowType(i) == t){
				return i;
			}
		}
		return null;
	}
}
