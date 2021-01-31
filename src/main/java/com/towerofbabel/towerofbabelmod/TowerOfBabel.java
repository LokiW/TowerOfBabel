package com.towerofbabel.towerofbabelmod;

import com.towerofbabel.towerofbabelmod.gui.GuiProxy;
import com.towerofbabel.towerofbabelmod.gui.ModGuiHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import java.util.Map;
import java.util.HashMap;
//import com.tower.SkillApplicator;
//import com.tower.TowerSkills;
//import com.tower.TowerBaseEntity;
//import com.tower.TowerBase;
import com.towerofbabel.towerofbabelmod.babel.ActionBlocker;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import com.towerofbabel.towerofbabelmod.babel.SkillCache;
import com.towerofbabel.towerofbabelmod.babel.Actions;
import com.towerofbabel.towerofbabelmod.babel.ActionItemTracker;
import com.towerofbabel.towerofbabelmod.tower.SkillTree;


/*
 * Root file for mod, that causes all other Components to link up
 */
@Mod(modid = TowerOfBabel.MODID, name = TowerOfBabel.NAME, version = TowerOfBabel.VERSION)
public class TowerOfBabel
{
	public static final String NAME = "Tower of Babel";
	public static final String MODID = "towerofbabel";
	public static final String VERSION = "1.0";
	@Mod.Instance
	public static TowerOfBabel instance;

	/**
	 * Configuration fields, referenced by other parts of the code as constants
	 */
	public static Configuration config;
	public static Map<String, SkillTree> skillTrees;
	public static Map<String, SkillTree> skills;
	// Regex of all allowed items
	public static ActionItemTracker defaultDisallowed = new ActionItemTracker();

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

		// Register the handler that binds GUI IDs to the corresponding GUI instances.
		NetworkRegistry.INSTANCE.registerGuiHandler(TowerOfBabel.instance, new ModGuiHandler());
	}


	/*
	 * Load configuration data from file
	 */
	public static void syncConfig() {
		try {
			config.load();
			System.out.println("TowerOfBabel: loading configs");
			//useful methods
			//config.get("category name","key","default value","comment");
			//config.getCategoryNames();
			//config.hasKey(c,Integer.toString(i)))

			if(config.getCategoryNames().size() < 2) {
				generateConfig();
			}

			if (skillTrees == null) {
				skillTrees = new HashMap<String, SkillTree>();
			}

			if (skills == null) {
				skills = new HashMap<String, SkillTree>();
			}
			
			for (String root : config.getCategoryNames()) {
				if (root.contains(".")) {
					// This is a sub category and will already be handled in parseConfig
					continue;
				}
				SkillTree rootTree = new SkillTree(root, root);
				skillTrees.put(root, rootTree);

				parseConfig(config.getCategory(root), rootTree);
			}

			updateSkillDependencies();
		} catch (Exception e) {
			System.out.println("TowerOfBabel: Error while loading configs");
			e.printStackTrace();
		} finally {
			System.out.println("TowerOfBabel: Completed loading of configs");
			if(config.hasChanged()) {
				config.save();
			}
		}
	}

	private static void parseConfig(ConfigCategory conf, SkillTree cur) {
		// Sub Skills & Objects (numerical bonuses)
		for (ConfigCategory c : conf.getChildren()) {
			if (c.getName().toLowerCase().equals("numericalbonuses")) {
				for (String bonus : c.getValues().keySet()) {
					try {
						cur.addBonus(bonus, c.get(bonus).getDouble());
					} catch (IllegalArgumentException e) {
						System.out.println("TowerOfBabel: Error Reading Config. Unknown numerical bonus " + bonus);
					}
				}
			}	else {
				SkillTree next = new SkillTree(c.getName(), cur.getRootId());
				skills.put(c.getName(), next);
				parseConfig(c, next);
			}
		}
		
		// Properties
		for (String s : conf.getValues().keySet()) {
			if (s.toLowerCase().equals("prereqs")) {
				SkillTree prereq = skills.get(conf.get(s).getString());
				if (prereq != null) {
					prereq.unlocks.put(cur.getId(), cur);
				}
				cur.prereqs.put(conf.get(s).getString(), prereq);
			} else if (s.toLowerCase().equals("unlocks")) {
				SkillTree unlock = skills.get(conf.get(s).getString());
				if (unlock != null) {
					unlock.prereqs.put(cur.getId(), cur);
				}
				cur.unlocks.put(conf.get(s).getString(), unlock);
			} else if (s.toLowerCase().equals("name")) {
				cur.addName(s);
			} else {
				try {
					cur.addAction(s, conf.get(s).getStringList());
					for (String i : conf.get(s).getStringList()) {
						defaultDisallowed.addItem(s, i);
					}
				} catch (IllegalArgumentException e) {
					System.out.println("TowerOfBabel: Error Reading Config. Unknown Skill Option " + s);
				}
			}	
		}
	}

	private static void updateSkillDependencies() {
		System.out.println("TowerOfBabel: updating skill tree dependencies");
		for (String cur : skills.keySet()) {
			for (String prereq : skills.get(cur).prereqs.keySet()) {
				if (skills.get(cur).prereqs.get(prereq) == null) {
					skills.get(cur).prereqs.put(prereq, skills.get(prereq));
				}
				if (skills.get(prereq) != null) {
					skills.get(prereq).unlocks.put(cur, skills.get(cur));
				}
			}

			for (String unlock : skills.get(cur).unlocks.keySet()) {
				if (skills.get(cur).unlocks.get(unlock) == null) {
					skills.get(cur).unlocks.put(unlock, skills.get(unlock));
				}
				if (skills.get(unlock) != null) {
					skills.get(unlock).prereqs.put(cur, skills.get(cur));
				}
			}
		}

		for (String cur: skills.keySet()) {
			if (skills.get(cur).prereqs.isEmpty()) {
				skills.get(skills.get(cur).getRootId()).unlocks.put(cur, skills.get(cur));
			}
		}
	}

	private static void generateConfig() {
		System.out.println("TowerOfBabel: Generating config file");
		/*
		DEFAULT {
			S:prerequisites <
			>
			S:Unlocks <
			>
			S:Name=Skill Friendly Name
			S:Carry <
				a11
			>
			S:Hold <
				Minecraft.a11
			>
			S:Use <
				Minecraft.a11
			>
			S:Wear <
				Minecraft.a11
			>
			S:Place <
				Minecraft.a11
			>
			S:Break <
				Minecraft.a11
			>
			S:Craft <
				Minecraft.a11
			>
			NumericalBonuses {
				D:miningSpeed=0.3
				I:reach=1
				D:stepUp=1.0
			}
		}
		*/
		config.save();
	}
}
