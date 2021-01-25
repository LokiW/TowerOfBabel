package com.babel;

public enum Actions {
	WEAR("wear"),
	HOLD("hold"),
	CARRY("carry"),
	BREAK("break blocks with"),
	ATTACK("attack with"),
	USE("use"),
	INTERACT("interact with"),
	PLACE("place"),
	CRAFT("craft");

	//mes should complete the sentence:
	//Cannot ___ itemname
	String mes;
	private Actions(String mes) {
		this.mes = mes;	
	}
	public String toString() {
		return mes;
	}
}
