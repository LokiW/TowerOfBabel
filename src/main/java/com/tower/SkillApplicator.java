package com.tower;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;

public class SkillApplicator {

	public static void register() {
		SkillApplicator ps = new SkillApplicator();
		MinecraftForge.EVENT_BUS.register(ps);
		FMLCommonHandler.instance().bus().register(ps);
	}

	public static void learnSkill(EntityPlayer p, boolean learn, String name) {
		//add to player's nbt
		TowerSkills t = (TowerSkills)p.getExtendedProperties(TowerSkills.PROP_NAME);
		if(learn) {
			t.skills.add(name);
		} else {
			t.skills.remove(name);
		}
	}

	@SubscribeEvent
	public void joinCommand(EntityJoinWorldEvent e) {
		World w = e.world;
		if(w.isRemote || !(e.entity instanceof EntityPlayer)) return;
		MinecraftServer.getServer().getCommandManager().executeCommand(new Sender(w), "/say join");
	}

	@SubscribeEvent
	public void repeatedCommand(PlayerTickEvent e) {
		EntityPlayer p = e.player;
		World w = p.worldObj;
		if(w.isRemote) return;

		if(w.getWorldTime() % 256 == 0) {
			MinecraftServer.getServer().getCommandManager().executeCommand(new Sender(w), "/say test");
		}
	}

	public static class Sender implements ICommandSender {
		public World w;

		public Sender(World w) {
			this.w = w;
		}

		public void addChatMessage(IChatComponent mes) {
			System.out.println(mes.getFormattedText());
		}

		public boolean canCommandSenderUseCommand(int a, String b) {
			return true;
		}

		public IChatComponent func_145748_c_() {
			return null;
		}

		public String getCommandSenderName() {
			return "Tower of Babel";
		}

		public World getEntityWorld() {
			return w;
		}
		
		public ChunkCoordinates getPlayerCoordinates() {
			return null;
		}
	}
}






