package com.biel.FastSurvival.SpecialItems.Items;

import com.biel.FastSurvival.SpecialItems.SpecialItemData;
import org.bukkit.Material;

public class SmallDamageBoosterItem extends RawDamageAmplifierItem{
	@Override
	public int getClassID() {
		// TODO Auto-generated method stub
		return 15;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Small damage booster";
	}
	@Override
	public int getTier() {
		// TODO Auto-generated method stub
		return 1;
	}
	@Override
	public Material getMaterial() {
		// TODO Auto-generated method stub
		return Material.LEGACY_INK_SACK;
	}
	@Override
	public Byte getData() {
		// TODO Auto-generated method stub
		return 1; //Rose red
	}
	@Override
	public void initializeData(SpecialItemData d) {
		// TODO Auto-generated method stub
		super.initializeData(d);
	}
	@Override
	public Double getDamageAmplifierMultiplier(SpecialItemData d) {
		// TODO Auto-generated method stub
		return 0.08D;
	}
}
