package com.biel.FastSurvival.SpecialItems.Items;

import com.biel.FastSurvival.SpecialItems.SpecialItemData;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Material;

public class AdvancedAssaultEquipmentItem extends RawAssaultEquipmentItem{
	@Override
	public int getClassID() {
		// TODO Auto-generated method stub
		return 14;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Advanced assault equipment";
	}
	@Override
	public Material getMaterial() {
		// TODO Auto-generated method stub
		return Material.DIAMOND_BOOTS;
	}
	@Override
	public int getTier() {
		// TODO Auto-generated method stub
		return 2;
	}
	@Override
	public void initializeData(SpecialItemData d) {
		// TODO Auto-generated method stub
		super.initializeData(d);
		d.setModifier(8D + (Utils.NombreEntre(1, 10) / 2D));
	}
	@Override
	public Double getDamageMultiplier(SpecialItemData d) {
		// TODO Auto-generated method stub
		return 0.3D + ((d.getModifier() / 100) / 6);
	}
	
}
