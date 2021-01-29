package com.towerofbabel.towerofbabelmod;

import com.towerofbabel.towerofbabelmod.gui.GuiProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.common.config.Configuration;
//import com.tower.SkillApplicator;
//import com.tower.TowerSkills;
//import com.tower.TowerBaseEntity;
//import com.tower.TowerBase;
import com.towerofbabel.towerofbabelmod.babel.ActionBlocker;
import com.towerofbabel.towerofbabelmod.babel.SkillCache;

import java.util.Map;


/*
 * Root file for mod, that causes all other Components to link up
 */
@Mod(modid = TowerOfBabel.MODID, name = TowerOfBabel.NAME, version = TowerOfBabel.VERSION)
public class TowerOfBabel
{
	public static final String NAME = "Tower of Babel";
	public static final String MODID = "towerofbabel";
	public static final String VERSION = "1.0";

	/**
	 * Configuration fields, referenced by other parts of the code as constants
	 */
	public static Configuration config;
	public static boolean dimensionSpecific;
	public static class SkillDesc {}
	public static Map<String, SkillDesc> skills;

	@SidedProxy(clientSide = "com.towerofbabel.towerofbabelmod.gui.GuiProxy")
	public static GuiProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//Read in configuration values
		config = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();

		proxy.registerEventHandlers();

		//add blocks and items
		/*
		 * Example Item
		Item skillpoint = new Item() {
		public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p) {
			return is;
		}
		}.setUnlocalizedName("skill_point").setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerItem(skillpoint, "skill_point");
		 */

		/*
		 * Armor bundle WIP
		Item armor_bundle = new ItemArmorBundle().setUnlocalizedName("armor_bundle").setCreativeTab(CreativeTabs.tabMisc);
		armor_bundle.setTextureName(ExampleMod.MODID + ":" + "ArmorBag");

		GameRegistry.registerItem(armor_bundle, "armor_bundle");
		 */

		/*
		Block towerbase = new TowerBase();
		GameRegistry.registerBlock(towerbase, "TowerBase");
    GameRegistry.registerTileEntity(TowerBaseEntity.class, "TowerBaseE");
		*/

		//Setup Skill Cache Based on Config
		SkillCache.init(config);
	}

	/*
	 * Stage 2 initialization, after all items and entities exist
	 */
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		//add crafting recipes and event handlers
		ActionBlocker.register();
		/*
		SkillApplicator.register();
		TowerSkills.register();
		*/
	}


	/*
	 * Load configuration data from file
	 */
	public static void syncConfig() {
		try {
			config.load();
			//useful methods
			//config.get("category name","key","default value","comment");
			//config.getCategoryNames();
			//config.hasKey(c,Integer.toString(i)))
			
			if(config.getCategoryNames().size() < 2) {
				//generateConfig();
			}
			/*
			for(String s : config.getCategoryNames()) {
				if(s.equals(MODID)) {
					//general config values
					Property p;
					p = config.get(MODID, "dimensionSpecific", false);
					dimensionSpecific = p.getBoolean();
				} else {
					//skill definition
					Property p;
					SkillDesc skill = new SkillDesc();

					p = config.get(s, "prereqs", "");
					//skill.setPrereqs(p.getStringList());
					p = config.get(s, "heights", "1");
					//skill.setHeights(p.getIntList());
					p = config.get(s, "bonuses", "");
					//skill.setBonuses(p.getStringList());

					skills.put(p.getString(), skill);
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(config.hasChanged()) {
				config.save();
			}
		}
	}

	private static void generateConfig() {
		config.save();
	}
}
