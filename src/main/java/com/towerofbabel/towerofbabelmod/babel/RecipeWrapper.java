package com.towerofbabel.towerofbabelmod.babel;

public class RecipeWrapper {}

/*
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import java.util.UUID;

import java.util.List;

import java.lang.reflect.Field;
import net.minecraft.inventory.Container;

public class RecipeWrapper implements IRecipe {
	private IRecipe recipe;
	private ItemStack out;
	public static UUID localPlayer;

	public RecipeWrapper(IRecipe rec) {
		recipe = rec;
		out = recipe.getRecipeOutput();
	}

	public ItemStack getCraftingResult(InventoryCrafting ic) {
		return recipe.getCraftingResult(ic);
	}

	public ItemStack getRecipeOutput() {
		return recipe.getRecipeOutput();
	}

	public int getRecipeSize() {
		return recipe.getRecipeSize();
	}

	public boolean matches(InventoryCrafting ic, World w) {
		return recipe.matches(ic, w) && check(ic,w);
	}

	private boolean check(InventoryCrafting ic, World w) {
		for(int slot = 0; slot < ic.getSizeInventory(); slot++) {
			ItemStack is = ic.getStackInSlot(slot);
			if(is != null) {
				NBTTagCompound nbt = is.stackTagCompound;
				if(nbt != null) {
					Long val = nbt.getLong("TowerUser");
					if(val != null) {
						if(SkillCache.can(val, Actions.CRAFT, recipe.getRecipeOutput())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static Field containerField; 
	private boolean check2(InventoryCrafting ic, World w) {
		if(containerField == null) {
			try {
				containerField = InventoryCrafting.class.getDeclaredField("eventHandler");
				containerField.setAccessible(true);
			} catch (Exception ex) {
					throw new UnsupportedOperationException("unsupported forge version for Tower of Babel");
			}
		}

		try {
			Container cont = (Container)containerField.get(ic);

			List<EntityPlayer> ps = w.playerEntities;
			for(int i = 0; i < ps.size(); i++) {
				
				if(!cont.isPlayerNotUsingContainer(ps.get(i))) {
					if(SkillCache.can(ps.get(i).getUniqueID(),Actions.CRAFT,out)) {
						return true;
					}
				}
			}
		} catch (IllegalAccessException ex) {
			//we already set accessible, so should never happen
		}
		return false;
	}
}*/
