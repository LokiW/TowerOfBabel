package com.towerofbabel.towerofbabelmod.babel;

import com.towerofbabel.towerofbabelmod.babel.Actions;
import com.towerofbabel.towerofbabelmod.TowerOfBabel;

import net.minecraft.item.ItemStack;
import java.util.Map;
import java.util.HashMap;

public class ActionItemTracker {
	public Map<Actions,String> tracker;

	public ActionItemTracker() {
		tracker = new HashMap<Actions, String>();
	}

	public void addItem(String actionName, String itemName) {
		addItem(Actions.valueOf(actionName.toUpperCase()), itemName);
	}

	public void addItem(String actionName, ItemStack item) {
		if (item == null) return;
		addItem(Actions.valueOf(actionName.toUpperCase()), item.getUnlocalizedName());
	}

	public void addItem(Actions a, ItemStack item) {
		if (item == null) return;
		addItem(a, item.getUnlocalizedName());	
	}

	public void addItem(Actions a, String itemName) {
		tracker.put(a, TowerOfBabel.constructItemRegex(tracker.get(a), itemName));
	}

	public boolean itemAllowed(Actions a, ItemStack item) {
		return itemAllowed(a, item.getUnlocalizedName());
	}

	public boolean itemAllowed(Actions a, String itemName) {
		String reg = tracker.get(a);
		return reg != null && ( itemName == null || itemName.matches(reg));
	}
}

