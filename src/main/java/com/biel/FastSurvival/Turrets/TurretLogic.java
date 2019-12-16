package com.biel.FastSurvival.Turrets;

public class TurretLogic {
	public static void doAllLogic(){
		for(TurretData d : TurretUtils.getActiveTurrets()){
			Turret t = new Turret(d);
			t.doTurretLogic();
		}
	}
	
	public enum AttackGroups{ALL, ENEMY_PLAYERS, ENEMY_MOBS, FRIENDLY_MOBS}
	public enum Upgrades{DAMAGE, ATTACK_SPEED, INCREASE_MAX_HP, SHIELD, FIRE, MAGNETIC}
}
