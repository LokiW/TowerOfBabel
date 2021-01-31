package com.towerofbabel.towerofbabelmod.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {
    // GUI IDs
    public static final int SKILL_TREE_GUI_ID = 0;
    public static SkillfulSkillTreeGui skillTreeGui;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == SKILL_TREE_GUI_ID)
            return skillTreeGui = new SkillfulSkillTreeGui();
        return null;
    }
}