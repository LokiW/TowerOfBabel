package com.babel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.item.Item;
import java.util.UUID;
import java.util.*;
import java.util.function.Consumer;

public class SkillCache {
	private static Map<Long,Map<Item,Integer>> cache = new HashMap<Long,Map<Item,Integer>>();

	public static boolean can(UUID p, Actions a, ItemStack i) {
		return can(p.getLeastSignificantBits(), a, i);
	}

	public static boolean can(long p, Actions a, ItemStack i) {
		Map<Item,Integer> skills = cache.get(p);
		if(skills == null) {
			skills = new HashMap<Item, Integer>();
			cache.put(p,skills);
		}
		Integer can = skills.get(i.getItem());
		return can != null && (can & (1<<a.ordinal())) != 0;
	}

	public static void addSkill(EntityPlayer p, String regex, int pattern) {
		p.addChatMessage(new ChatComponentText(name + " learned " + regex));

		Map<Item,Integer> skills = cache.get(p.getUniqueID().getLeastSignificantBits());
		if(skills == null) {
			skills = new HashMap<Item,Integer>();
			cache.put(p.getUniqueID().getLeastSignificantBits(), skills);
		}

		Item.itemRegistry.forEach(new Cacher(skills, regex, pattern));
	}

	public static class Cacher implements Consumer<Item> {
		private Map<Item,Integer> skill;
		private String regex;
		private int pattern;

		public Cacher(Map<Item,Integer> skill, String regex, int pattern) {
			this.skill = skill;
			this.regex = regex;
			this.pattern = pattern;
		}

		public void accept(Item item) {
			String name = item.getUnlocalizedName();
			if(name == null || !name.matches(regex))
				return;

			Integer i = skill.get(item);

			if(i == null)
				i = 0;
			
			if(add) {
				i |= pattern;
			}

			skill.put(item, i);
		}

		public Consumer<Item> andThen(Consumer<? super Item> c) {
			throw new UnsupportedOperationException();
		}
	}
}







