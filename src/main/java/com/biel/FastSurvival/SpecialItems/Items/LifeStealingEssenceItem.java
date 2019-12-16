package com.biel.FastSurvival.SpecialItems.Items;

import com.biel.FastSurvival.SpecialItems.SpecialItemData;
import org.bukkit.Material;

public class LifeStealingEssenceItem extends RawLifeStealItem{
	@Override
	public int getClassID() {
		// TODO Auto-generated method stub
		return 22;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Life stealing essence";
	}
	@Override
	public int getTier() {
		// TODO Auto-generated method stub
		return 1;
	}
	@Override
	public Material getMaterial() {
		// TODO Auto-generated method stub
		return Material.REDSTONE;
	}
	@Override
	public Double getLifeStealMultiplier(SpecialItemData d) {
		// TODO Auto-generated method stub
		return 0.08D;
	}
	@Override
	public double getUndeadReducingMultiplier() {
		// TODO Auto-generated method stub
		return 0.5D;
	}
}
