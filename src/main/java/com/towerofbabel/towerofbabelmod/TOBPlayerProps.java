package com.towerofbabel.towerofbabelmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.towerofbabel.towerofbabelmod.babel.Actions;
import com.towerofbabel.towerofbabelmod.babel.SkillCache;
import com.towerofbabel.towerofbabelmod.babel.Bonuses;
import com.towerofbabel.towerofbabelmod.tower.SkillTree;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.UUID;

public class TOBPlayerProps {
	public static Map<Long, TOBPlayerProps> properites = new HashMap<Long, TOBPlayerProps>();

	public Set<String> unlockedSkills = new HashSet<String>();
	public Map<Bonuses.BONUS, Bonuses.BonusTracker> stats = new HashMap<Bonuses.BONUS, Bonuses.BonusTracker>();

	public static final String PROP_NAME = TowerOfBabel.MODID + "_PlayerProps";
	public static final String SKILL_LST = TowerOfBabel.MODID + "_UNLOCKED_SKILLS";
	//TODO add event handlers to recalculate bonuses on conidtionals

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new PlayerJoinHandler());
	}

	public static TOBPlayerProps get(EntityPlayer p) {
		TOBPlayerProps props = TOBPlayerProps.properites.get(p.getUniqueID().getLeastSignificantBits());

		if (props == null) {
			props = new TOBPlayerProps();
			TOBPlayerProps.properites.put(p.getUniqueID().getLeastSignificantBits(), props);
		}

		return props;
	}

	public TOBPlayerProps readFromNBT(NBTTagCompound props) {
		if (props == null) return this;
		
		NBTTagCompound nbtSkills = props.getCompoundTag(SKILL_LST);
		if (nbtSkills == null) {
		}
		if (nbtSkills != null) {
			for (String s : nbtSkills.getKeySet()) {
				unlockedSkills.add(s);				
			}
		}
		return this;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound nbtSkills = new NBTTagCompound();
		for (String s : unlockedSkills) {
			nbtSkills.setString(s,s);
		}

		NBTTagCompound nbtProps = new NBTTagCompound();
		nbtProps.setTag(SKILL_LST, nbtSkills);

		return nbtProps;
	}

	public static boolean isUnlocked(EntityPlayer p, String skillId) {
		TOBPlayerProps props = TOBPlayerProps.get(p);
	
		return props.unlockedSkills.contains(skillId);
	
	}

	public static boolean canLearn(EntityPlayer p, String skillId) {
		TOBPlayerProps props = TOBPlayerProps.get(p);

		// TODO add conditionals
		// TODO add creative vs survival
		SkillTree skill = TowerOfBabel.skills.get(skillId);
		for (String prereq : skill.prereqs.keySet()) {
			if (prereq != null && !prereq.equals("") && !props.unlockedSkills.contains(prereq)) {
				return false;
			}
		}
		return true;
	}

	public static boolean addSkill(EntityPlayer p, String skillId) {
		if (!TOBPlayerProps.canLearn(p, skillId)) {
			return false;
		}
		TOBPlayerProps props = TOBPlayerProps.get(p);
	
		props.unlockedSkills.add(skillId);
		return true;
	}

	public static void updateSkillValues(EntityPlayer p) {
		TOBPlayerProps props = TOBPlayerProps.get(p);
		SkillCache.clearPlayerCache(p.getUniqueID());
		props.stats = new HashMap<Bonuses.BONUS, Bonuses.BonusTracker>();

		for (String s : props.unlockedSkills) {
			SkillTree skill = TowerOfBabel.skills.get(s);

			for (Actions a : skill.permissions.keySet()) {
				SkillCache.allowAction(p.getUniqueID(), a, skill.permissions.get(a));
			}

			for (Bonuses.BONUS b : skill.bonuses.keySet()) {
				Bonuses.BonusTracker bt = props.stats.get(b);
				if (bt == null) {
					bt = new Bonuses.BonusTracker();
					props.stats.put(b, bt);
				}
				bt.combine(skill.bonuses.get(b));
			}
		}

		for (Bonuses.BONUS b : props.stats.keySet()) {
			props.stats.get(b).applyAll(p, b);
		}
	}

		/*
	 * Hooks to Minecraft engine to add Properties to Players
	 */
	public static class PlayerJoinHandler {

		/*
		 * Create properties for player when they loggin
		 */
		@SubscribeEvent
		public void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
			TOBPlayerProps.get(e.player).readFromNBT(e.player.getEntityData().getCompoundTag(PROP_NAME));
			TOBPlayerProps.updateSkillValues(e.player);
		}

		/*
		 * Save properties for data when they logout
		 */
		@SubscribeEvent
		public void playerLogout(PlayerEvent.PlayerLoggedOutEvent e){
			NBTTagCompound nbtProps = TOBPlayerProps.get(e.player).writeToNBT();
			NBTTagCompound base = e.player.getEntityData();
			base.setTag(PROP_NAME, nbtProps);
		}

	}

	public static class updateCacheHandler {}

}
