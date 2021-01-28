package com.towerofbabel.towerofbabelmod.babel;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

//import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.Container;
import net.minecraft.util.NonNullList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.play.server.SPacketSetSlot;

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
		/*
		List<IRecipe> rec = CraftingManager.getInstance().getRecipeList();
		for(int i = 0; i < rec.size(); i++) {
			rec.set(i, new RecipeWrapper(rec.get(i)));
		}*/
	}

	public Map<UUID, Container> lastSeen = new HashMap<UUID, Container>();
	private void setTag(UUID pid, ItemStack is) {
		NBTTagCompound nbt = is.getTagCompound();
		if(nbt == null) {
			nbt = new NBTTagCompound();
		}
	
		nbt.setLong("TowerUser",pid.getLeastSignificantBits());
		is.setTagCompound(nbt);
	}

	@SubscribeEvent
	public void markItem(EntityItemPickupEvent e) {
		setTag(e.getEntityPlayer().getUniqueID(), e.getItem().getItem());
	}

	@SubscribeEvent
	public void markItem2(Open e) {
		EntityPlayer p = e.getEntityPlayer();
		UUID pid = p.getUniqueID();
		Container c = p.openContainer;
		if(c != lastSeen.get(pid)) {
			lastSeen.put(pid, c);

			e.getEntityPlayer().inventory.getSizeInventory();
			for(int slot = 0; slot < c.getInventory().size(); slot++) {
				ItemStack is = ((ItemStack)c.getInventory().get(slot));
				if(is != null) {
					setTag(pid, is);
				}
			}
		}
	}

	@SubscribeEvent
	public void attemptUse(PlayerInteractEvent.RightClickItem e) {
		EntityPlayer p = e.getEntityPlayer();
		ItemStack i = p.getActiveItemStack();

		System.out.println("TowerOfBabel:  Player Interacting With " + i.getItem());
		//Place and Use
		if(i != null) {
			if(shouldCancel(p,Actions.USE,i)) {
				//e.useItem = Result.DENY;
				e.setResult(Result.DENY);	
				e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void attemtInteract(PlayerInteractEvent.RightClickBlock e) {
		EntityPlayer p = e.getEntityPlayer();
		ItemStack i = p.getActiveItemStack();
		System.out.println("TowerOfBabel:  Player Interacting On Block With " + i.getItem());
		//Place
		if(i != null) {
			if(i.getItem() instanceof ItemBlock) {
				if(shouldCancel(p,Actions.PLACE,i)) {
					//e.useItem = Result.DENY;
					e.setUseItem(Result.DENY);
				}
			}
		}
		//Interact
		if(e.getPos().getY() != 0) {
			TileEntity interactable = p.getEntityWorld().getTileEntity(e.getPos());
			if (interactable != null) {
				ItemStack t = new ItemStack(interactable.getBlockType());
				if(shouldCancel(p,Actions.INTERACT,t)) {
					//e.useBlock = Result.DENY;
					e.setUseBlock(Result.DENY);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void attemptBreak(PlayerInteractEvent.LeftClickBlock e) {
		EntityPlayer p = e.getEntityPlayer();
		ItemStack i = new ItemStack(p.getEntityWorld().getBlockState(e.getPos()).getBlock());
		//Break
		if(shouldCancel(p,Actions.BREAK,i)) {
			e.setUseBlock(Result.DENY);
			e.setCanceled(true);
		}

		ItemStack h = p.getActiveItemStack();
		if(h != null) {
			if(shouldCancel(p,Actions.USE,i)) {
				e.setUseItem(Result.DENY);
			}	
		}
		System.out.println("TowerOfBabel:  Player Left Clicking With " + i.getItem());
	}

	@SubscribeEvent
	public void attemptEntity(EntityInteract e) {
		EntityPlayer p = e.getEntityPlayer();
		ItemStack i = p.getActiveItemStack();

		System.out.println(e.getTarget());
		if(shouldCancel(p,Actions.CARRY,i)) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void attack(AttackEntityEvent e) {
		ItemStack i = e.getEntityPlayer().getActiveItemStack();
		e.setCanceled(shouldCancel(e.getEntityPlayer(),Actions.ATTACK,i));
	}

	@SubscribeEvent
	public void wear(PlayerTickEvent e) {
		EntityPlayer p = e.player;
		World w = p.getEntityWorld();
		if(w.isRemote) return;

		if(w.getWorldTime() % 20 == 0) {
			//armor
			NonNullList<ItemStack> armor = p.inventory.armorInventory;
			for(int i = 0; i < armor.size(); i++) {
				armor.set(i, droppingCheck(w,p,Actions.WEAR,armor.get(i)));
			}
			//baubles
			//TODO
			//held item
			InventoryPlayer ip = p.inventory;
			ItemStack res = droppingCheck(w,p,Actions.HOLD,p.getActiveItemStack());
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
			item.setPickupDelay(200);
			w.spawnEntity(item);
			return null;
		}
		return i;
	}
	
	public boolean shouldCancel(EntityPlayer p, Actions a, ItemStack i) {
		if(i == null || SkillCache.can(p.getUniqueID(),a,i)) {
			return false;
		} else if (p.getEntityWorld().isRemote || a == Actions.BREAK || a == Actions.WEAR || a == Actions.HOLD || a == Actions.CARRY) {
			//clientside messages
			String mes;
			try {
				mes = "Cannot " + a + " " + i.getDisplayName();
			} catch(Exception ex) {
				mes = a + " " + ex.toString();
			}
			//p.addChatComponentMessage(new ChatComponentText(mes));
		} else if(p instanceof EntityPlayerMP) {
			//synchronize server and client view (as client can't cancel events)
			//hotbar
			for(int slot = 0; slot < 9; slot++) {
				ItemStack correctItem = p.inventory.getStackInSlot(slot);
				((EntityPlayerMP)p).connection.sendPacket(new SPacketSetSlot(p.inventoryContainer.windowId, slot+36, correctItem));
			}
			//armor
			for(int slot = 0; slot < 4; slot++) {
				ItemStack correctItem = p.inventory.armorInventory.get(slot);
				((EntityPlayerMP)p).connection.sendPacket(new SPacketSetSlot(p.inventoryContainer.windowId, slot+5, correctItem));
			}
			//the rest
			for(int slot = 0; slot < 27; slot++) {
				ItemStack correctItem = p.inventory.getStackInSlot(slot+9);
				((EntityPlayerMP)p).connection.sendPacket(new SPacketSetSlot(p.inventoryContainer.windowId, slot+9, correctItem));
			}
		}
		return true;
	}
}
