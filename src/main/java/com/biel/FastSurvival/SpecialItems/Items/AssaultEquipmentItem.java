package com.biel.FastSurvival.SpecialItems.Items;

import com.biel.FastSurvival.SpecialItems.SpecialItemData;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Material;

public class AssaultEquipmentItem extends RawAssaultEquipmentItem{
	@Override
	public int getClassID() {
		// TODO Auto-generated method stub
		return 13;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Assault equipment";
	}
	@Override
	public Material getMaterial() {
		// TODO Auto-generated method stub
		return Material.LEGACY_GOLD_BOOTS;
	}
	@Override
	public int getTier() {
		// TODO Auto-generated method stub
		return 1;
	}
	@Override
	public void initializeData(SpecialItemData d) {
		// TODO Auto-generated method stub
		super.initializeData(d);
		d.setModifier(5D + Utils.NombreEntre(1, 2));
	}

	@Override
	public Double getDamageMultiplier(SpecialItemData d) {
		return super.getDamageMultiplier(d) / 2;
	}
}
