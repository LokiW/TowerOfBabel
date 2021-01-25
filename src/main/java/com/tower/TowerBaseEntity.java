package com.tower;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;

public class TowerBaseEntity extends TileEntity implements IUpdatePlayerListBox {
	@Override
	public void update() {
		System.out.println("got an update");
	}
}
