package com.babel;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;


import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.Container;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.CraftingManager;

public class ActionBlocker {

	public static void register() {
		ActionBlocker ab = new ActionBlocker();
		MinecraftForge.EVENT_BUS.register(ab);
		FMLCommonHandler.instance().bus().register(ab);

		//block crafting
		List<IRecipe> rec = CraftingManager.getInstance().getRecipeList();
		for(int i = 0; i < rec.size(); i++) {
			rec.set(i, new RecipeWrapper(rec.get(i)));
		}
	}

	public Map<UUID, Container> lastSeen = new HashMap<UUID, Container>();
	private void setTag(UUID pid, ItemStack is) {
		NBTTagCompound nbt;
		if(is.stackTagCompound == null) {
			nbt = new NBTTagCompound();
		} else {
			nbt = is.stackTagCompound;
		}
		nbt.setLong("TowerUser",pid.getLeastSignificantBits());
	}

	@SubscribeEvent
	public void markItem(EntityItemPickupEvent e) {
		setTag(e.entityPlayer.getUniqueID(), e.item.getEntityItem());
	}

	@SubscribeEvent
	public void markItem2(PlayerOpenContainerEvent e) {
		EntityPlayer p = e.entityPlayer;
		UUID pid = p.getUniqueID();
		Container c = p.openContainer;
		if(c != lastSeen.get(pid)) {
			lastSeen.put(pid, c);

			e.entityPlayer.inventory.getSizeInventory();
			for(int slot = 0; slot < c.getInventory().size(); slot++) {
				ItemStack is = ((ItemStack)c.getInventory().get(slot));
				if(is != null) {
					setTag(pid, is);
				}
			}
		}
	}

	@SubscribeEvent
	public void attempt(PlayerInteractEvent e) {
		EntityPlayer p = e.entityPlayer;
		ItemStack i = p.getHeldItem();

		//Place and Use
		if(i != null) {
			if(i.getItem() instanceof ItemBlock) {
				if(shouldCancel(p,Actions.PLACE,i)) {
					e.useItem = Result.DENY;	
				}
			} else {
				if(shouldCancel(p,Actions.USE,i)) {
					e.useItem = Result.DENY;
				}
			}
		}

		//Interact
		if(e.y != 0) {
			ItemStack t = new ItemStack(Item.getItemFromBlock(p.worldObj.getBlock(e.x,e.y,e.z)));
			if(shouldCancel(p,Actions.INTERACT,t)) {
				e.useBlock = Result.DENY;
			}
		}

		//Break
		if(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK.equals(e.action) && shouldCancel(p,Actions.BREAK,i)) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void attemptEntity(EntityInteractEvent e) {
		EntityPlayer p = e.entityPlayer;
		ItemStack i = p.getHeldItem();

		System.out.println(e.target);
		if(shouldCancel(p,Actions.CARRY,i)) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void attack(AttackEntityEvent e) {
		ItemStack i = e.entityPlayer.getHeldItem();
		e.setCanceled(shouldCancel(e.entityPlayer,Actions.ATTACK,i));
	}

	@SubscribeEvent
	public void wear(PlayerTickEvent e) {
		EntityPlayer p = e.player;
		World w = p.worldObj;
		if(w.isRemote) return;

		if(w.getWorldTime() % 20 == 0) {
			//armor
			ItemStack[] armor = p.inventory.armorInventory;
			for(int i = 0; i < armor.length; i++) {
				armor[i] = droppingCheck(w,p,Actions.WEAR,armor[i]);
			}
			//baubles
			//TODO
			//held item
			InventoryPlayer ip = p.inventory;
			ItemStack res = droppingCheck(w,p,Actions.HOLD,p.getHeldItem());
			ip.setInventorySlotContents(ip.currentItem,res);

			//carry
			if(w.getWorldTime() % 13 == 0) {
				for(int slot = 0; slot < p.inventory.getSizeInventory(); slot++) {
					ip.setInventorySlotContents(slot, droppingCheck(w,p,Actions.CARRY,ip.getStackInSlot(slot)));
				}
			}
		}
	}

	private ItemStack droppingCheck(World w, EntityPlayer p, Actions a, ItemStack i) {
		if(i != null && shouldCancel(p, a, i)) {
			EntityItem item = new EntityItem(w,p.posX,p.posY+1,p.posZ,i);
			item.delayBeforeCanPickup = 200;
			w.spawnEntityInWorld(item);
			return null;
		}
		return i;
	}
	
	public boolean shouldCancel(EntityPlayer p, Actions a, ItemStack i) {
		if(i == null || SkillCache.can(p.getUniqueID(),a,i)) {
			return false;
		} else if (p.worldObj.isRemote || a == Actions.BREAK || a == Actions.WEAR || a == Actions.HOLD || a == Actions.CARRY) {
			//clientside messages
			String mes;
			try {
				mes = "Cannot " + a + " " + i.getDisplayName();
			} catch(Exception ex) {
				mes = a + " " + ex.toString();
			}
			p.addChatComponentMessage(new ChatComponentText(mes));
		} else if(p instanceof EntityPlayerMP) {
			//synchronize server and client view (as client can't cancel events)
			//hotbar
			for(int slot = 0; slot < 9; slot++) {
				ItemStack correctItem = p.inventory.getStackInSlot(slot);
				((EntityPlayerMP)p).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(p.inventoryContainer.windowId, slot+36, correctItem));
			}
			//armor
			for(int slot = 0; slot < 4; slot++) {
				ItemStack correctItem = p.getCurrentArmor(slot);
				((EntityPlayerMP)p).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(p.inventoryContainer.windowId, slot+5, correctItem));
			}
			//the rest
			for(int slot = 0; slot < 27; slot++) {
				ItemStack correctItem = p.inventory.getStackInSlot(slot+9);
				((EntityPlayerMP)p).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(p.inventoryContainer.windowId, slot+9, correctItem));
			}
		}
		return true;
	}
}
