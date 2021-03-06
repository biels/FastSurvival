package com.biel.FastSurvival.SpecialItems.Items;

import com.biel.FastSurvival.SpecialItems.SpecialItem;
import com.biel.FastSurvival.SpecialItems.SpecialItemData;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

public class RawLifeStealItem extends SpecialItem{
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RawDamageAmplifierItem";
	}
	@Override
	public String getDescription(SpecialItemData d) {
		// TODO Auto-generated method stub
		long percentage = Math.round(getLifeStealMultiplier(d) * 100);
		long percentage2 = Math.round(getUndeadReducingMultiplier() * 100);
		return "Heals yourself for a " + ChatColor.GREEN + percentage + ChatColor.WHITE + "% " + " of the total damage dealt. This effect is reduced by " + ChatColor.YELLOW + percentage2 + ChatColor.WHITE + "% " +"against undead creatures.";
	}
	@Override
	public void initializeData(SpecialItemData d) {
		// TODO Auto-generated method stub
		super.initializeData(d);
	}
	public Double getLifeStealMultiplier(SpecialItemData d){
		return 1D;
	}
	public double getUndeadReducingMultiplier() {
		return 0.5D;
	}
	public Boolean isUndead(LivingEntity e){
		return (e instanceof Skeleton || e instanceof Zombie);
	}
	//Handlers
	@EventHandler
	public void onEntDmg(EntityDamageByEntityEvent evt) {
		if (!(evt.getEntity() instanceof LivingEntity)){return;}
		LivingEntity damaged = (LivingEntity) evt.getEntity();
		Entity rawDamager = evt.getDamager();
		if (rawDamager instanceof Projectile){
			ProjectileSource shooter = ((Projectile) rawDamager).getShooter();
			if(shooter instanceof Entity){
				rawDamager = (Entity) shooter;
			}			
		}

		if (!(rawDamager instanceof LivingEntity)){return;}
		LivingEntity damager = (LivingEntity) rawDamager;
		DamageCause c = evt.getCause();
		if (damager instanceof Player){ //apply
			Player p = (Player) damager;
			if (!hasItem(p)){return;} //CHECK!
			SpecialItemData d = getData(p); //DATA
			double dmg = evt.getDamage();
			double healing = dmg * getLifeStealMultiplier(d);
			if (isUndead(damaged)){healing = healing - (healing * getUndeadReducingMultiplier());}
			Utils.healDamageable(p, healing);
			//replaceItem(p, d); //REPLACE
		}
	}


}
