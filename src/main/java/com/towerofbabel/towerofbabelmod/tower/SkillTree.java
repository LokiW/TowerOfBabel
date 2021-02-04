package com.towerofbabel.towerofbabelmod.tower;

import com.towerofbabel.towerofbabelmod.babel.Actions;
import com.towerofbabel.towerofbabelmod.babel.Bonuses;
import com.towerofbabel.towerofbabelmod.TowerOfBabel;

import net.minecraft.item.Item;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


public class SkillTree {
	public Map<String, SkillTree> prereqs;
	public Map<String, SkillTree> unlocks;
	private String name;
	private String id;
	private String rootId;
	//TODO how to allow only one unlock

	public Map<Actions, String> permissions;
	public Map<Bonuses.BONUS, Bonuses.BonusTracker> bonuses;
	//TODO conditionals
	//private List<ConditionalActivation> conditionals;

	public SkillTree(String id, String rootId) {
		this.id = id;
		this.rootId = rootId;

		prereqs = new HashMap<String, SkillTree>();
		unlocks = new HashMap<String, SkillTree>();

		permissions = new HashMap<Actions, String>();
		bonuses = new HashMap<Bonuses.BONUS, Bonuses.BonusTracker>();
	}

	public void addName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void addAction(String action, String[] items) throws IllegalArgumentException {
		Actions toAdd = Actions.valueOf(action.toUpperCase());
		String reg = "";
		for (String s : items) {
			reg = TowerOfBabel.constructItemRegex(reg, s);
		}
		permissions.put(toAdd, reg);
	}

	public void addBonus(String bonus, String operator, Double value) throws IllegalArgumentException {
		System.out.println("TowerOfBabel: adding bonus " + bonus + " to skill " + this.id);
		addBonus(Bonuses.BONUS.valueOf(bonus.toUpperCase()), Bonuses.OPERATOR.valueOf(operator.toUpperCase()), value);
	}

	public void addBonus(String bonus, Bonuses.OPERATOR operator, Double value) throws IllegalArgumentException {
		System.out.println("TowerOfBabel: adding bonus " + bonus + " to skill " + this.id);
		addBonus(Bonuses.BONUS.valueOf(bonus.toUpperCase()), operator, value);
	}

	public void addBonus(Bonuses.BONUS bonus, Bonuses.OPERATOR operator, Double value) {
		Bonuses.BonusTracker bt = bonuses.get(bonus);
		if (bt == null) {
			bt = new Bonuses.BonusTracker();
			bonuses.put(bonus, bt);
		}
		bt.values.put(operator, value);
	}

	public String getId() {
		return id;
	}

	public String getRootId() {
		return rootId;
	}
}
