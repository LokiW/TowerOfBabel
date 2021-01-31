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

public class TOBPlayerProps {
	
	public static final String PROP_NAME = TowerOfBabel.MODID + "_PlayerProps";
	
	public static final String SKILL_LST = TowerOfBabel.MODID + "_UNLOCKED_SKILLS";
	public static final String PLAYER_STATS = TowerOfBabel.MODID + "_PLAYER_STATS";
	//public List<String> unlockedSkills;
	//public Map<Bonuses, Double> stats;
	//TODO add event handlers to recalculate bonuses on conidtionals

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new PlayerJoinHandler());
	}

	public static NBTTagCompound get(EntityPlayer e) {
		NBTTagCompound props = e.getEntityData().getCompoundTag(PROP_NAME);
		if (props == null) {
			props = new NBTTagCompound();
			e.getEntityData().setTag(PROP_NAME, props);
		}

		return props;
	}

	public static void addSkill(EntityPlayer p, String skillId) {
		NBTTagCompound props = TOBPlayerProps.get(p);
		NBTTagCompound unlockedSkills = props.getCompoundTag(SKILL_LST);
		if (unlockedSkills == null) {
			unlockedSkills = new NBTTagCompound();
			props.setTag(SKILL_LST, unlockedSkills);
		}
		
		unlockedSkills.setString(skillId, skillId);
	}

	public static void updateSkillValues(EntityPlayer p) {
		NBTTagCompound props = TOBPlayerProps.get(p);
		SkillCache.clearPlayerCache(p.getUniqueID());

		NBTTagCompound unlockedSkills = props.getCompoundTag(SKILL_LST);
		if (unlockedSkills == null) {
			unlockedSkills = new NBTTagCompound();
			props.setTag(SKILL_LST, unlockedSkills);
		}

		NBTTagCompound stats = props.getCompoundTag(PLAYER_STATS);
		if (stats == null) {
			stats = new NBTTagCompound();
			props.setTag(PLAYER_STATS, stats);
		}
		for (String s : unlockedSkills.getKeySet()) {
			SkillTree skill = TowerOfBabel.skills.get(s);

			for (Actions a : skill.permissions.keySet()) {
				SkillCache.allowAction(p.getUniqueID(), a, skill.permissions.get(a));
			}

			for (Bonuses b : skill.bonuses.keySet()) {
				stats.setDouble(b.toString(), Bonuses.resolveBonus(b, skill.bonuses.get(b), stats.getDouble(b.toString())));
			}
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
			TOBPlayerProps.updateSkillValues(e.player);
		}

	}

	public static class updateCacheHandler {}

}
