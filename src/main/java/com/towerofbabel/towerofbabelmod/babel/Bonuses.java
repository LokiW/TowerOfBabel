package com.towerofbabel.towerofbabelmod.babel;

public enum Bonuses {
	STEP_HEIGHT("step height"),
	MINING_SPEED("mining speed"),
	JUMP_HEIGHT("jump height"),
	REACH("reach"),
	ATTACK_DAMAGE("attack damage");

	String mes;
	private Bonuses(String mes) {
		this.mes = mes;	
	}
	public String toString() {
		return mes;
	}

	public static Double resolveBonus(Bonuses bonus, Double value1, Double value2) {
		// TODO switch over Bonuses
		return 1.0;
	} 
}
