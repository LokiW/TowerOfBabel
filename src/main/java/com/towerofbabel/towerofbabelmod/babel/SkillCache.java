package com.towerofbabel.towerofbabelmod.babel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
//import net.minecraft.util.ChatComponentText;
import net.minecraft.item.Item;
import java.util.UUID;
import java.util.*;
import java.util.function.Consumer;
import com.towerofbabel.towerofbabelmod.babel.ActionItemTracker;

import com.towerofbabel.towerofbabelmod.TowerOfBabel;

public class SkillCache {
	// Map from player id to pairs of actions and a regex for items allowed for that action
	private static Map<Long,ActionItemTracker> playerRegex = new HashMap<Long,ActionItemTracker>();

	/*
	 * Checks if player p can do action a with item i
	 */
	public static boolean can(UUID p, Actions a, ItemStack i) {
		return can(p.getLeastSignificantBits(), a, i);
	}

	/*
	 * Checks if player p can do action a with item i
	 */
	public static boolean can(long p, Actions a, ItemStack i) {
		ActionItemTracker allowed = playerRegex.get(p);
		if(allowed == null) {
			allowed = new ActionItemTracker();
			playerRegex.put(p, allowed);
		}
		return ((!TowerOfBabel.defaultDisallowed.itemAllowed(a, i)) || allowed.itemAllowed(a, i));
	}

	public static void clearPlayerCache(UUID p) {
		clearPlayerCache(p.getLeastSignificantBits());
	}

	public static void clearPlayerCache(long p) {
		playerRegex.put(p, new ActionItemTracker());
	}

	public static void allowAction(UUID p, Actions a, ItemStack i) {
		allowAction(p.getLeastSignificantBits(), a, i.getUnlocalizedName());	
	}

	public static void allowAction(long p, Actions a, ItemStack i) {
		allowAction(p, a, i.getUnlocalizedName());	
	}

	public static void allowAction(UUID p, Actions a, String regex) {
		allowAction(p.getLeastSignificantBits(), a, regex);	
	}

	public static void allowAction(long p, Actions a, String regex) {
		ActionItemTracker allowed = playerRegex.get(p);
		if (allowed == null) {
			allowed = new ActionItemTracker();
			playerRegex.put(p, allowed);
		}

		allowed.addItem(a, regex);
	}
}
