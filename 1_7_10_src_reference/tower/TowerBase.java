package com.tower;

import net.minecraft.world.World;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.creativetab.CreativeTabs;

public class TowerBase extends Block implements ITileEntityProvider {
    public TowerBase() {
        super(Material.iron);
				this.setBlockName("TowerBase");
				//this.setBlockTextureName(MODID + ":" + "test.png");
				//this.setStepSound(soundTypeGrass);
				this.setTickRandomly(false);
				//this.setBlockBounds(
				this.setCreativeTab(CreativeTabs.tabMisc);
        this.setHardness(2.0f);
        this.setResistance(6.0f);
        this.setHarvestLevel("pickaxe", 2);
        this.isBlockContainer = true; //XXX what is this
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TowerBaseEntity();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block b, int m) {
        super.breakBlock(world, x, y, z, b, m);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventID, int eventParam) {
        super.onBlockEventReceived(worldIn, x, y, z, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(x, y, z);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }
}
