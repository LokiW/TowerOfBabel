package com.tower;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Set;
import java.util.HashSet;
import com.TowerOfBabel;

public class TowerSkills implements IExtendedEntityProperties {
	public static final String PROP_NAME = TowerOfBabel.MODID + "_TowerProps";
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new TowerSkills());
	}

	public Set<String> skills;
	public TowerSkills() {
		skills = new HashSet<String>();
	}

	@Override
	public void saveNBTData(NBTTagCompound tag) {
		NBTTagCompound t = new NBTTagCompound();

		int i = 0;
		for(String s : skills) {
			t.setString("" + i, s);
			i++;
		}

		tag.setTag("TowerSkill",t);
	}

	@Override
	public void loadNBTData(NBTTagCompound tag) {
		NBTTagCompound t = (NBTTagCompound)tag.getTag("TowerSkills");
		
		int i = 0;
		String s = null;
		while(true) {
			s = t.getString("" + i);
			if(s != null) {
				skills.add(s);
			} else {
				break;
			}
		}
	}

	@SubscribeEvent
	public void join(EntityJoinWorldEvent e) {
		if(!(e.entity instanceof EntityPlayer))
			return;

		TowerSkills prop = new TowerSkills();
		e.entity.registerExtendedProperties(PROP_NAME, prop);
	}

	@SubscribeEvent
	public void recreated(PlayerEvent.Clone e) {
		TowerSkills prop = (TowerSkills)e.original.getExtendedProperties(PROP_NAME);
		e.entityPlayer.registerExtendedProperties(PROP_NAME, prop);
	}

	//unused but required
	@Override
	public void init(Entity e, World w) {}
}
