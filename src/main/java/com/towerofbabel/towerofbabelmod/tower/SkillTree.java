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
	public Map<Bonuses, Double> bonuses;
	//TODO conditionals
	//private List<ConditionalActivation> conditionals;

	public SkillTree(String id, String rootId) {
		this.id = id;
		this.rootId = rootId;

		prereqs = new HashMap<String, SkillTree>();
		unlocks = new HashMap<String, SkillTree>();

		permissions = new HashMap<Actions, String>();
		bonuses = new HashMap<Bonuses, Double>();
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

	public void addBonus(String bonus, Double value) throws IllegalArgumentException {
		Bonuses toAdd = Bonuses.valueOf(bonus.toUpperCase());
		bonuses.put(toAdd, value);
	}

	public String getId() {
		return id;
	}

	public String getRootId() {
		return rootId;
	}
}
