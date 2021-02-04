package com.towerofbabel.towerofbabelmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.LinkedList;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class SkillButton extends GuiButtonImage {
    private final ResourceLocation resourceLocation;
    private int xTexStart;
    private int yTexStart;
    private int yDiffText;

    public SkillButton(int buttonId, int x, int y, int width, int height, int xTexStart, int yTexStart, int yHoverTexStart, ResourceLocation resourceLocation) {
        super(buttonId, x, y, width, height, xTexStart, yTexStart, yHoverTexStart, resourceLocation);
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffText = yHoverTexStart;
        this.resourceLocation = resourceLocation;
    }

    public void setPosition(int p_191746_1_, int p_191746_2_)
    {
        this.x = p_191746_1_;
        this.y = p_191746_2_;
    }

    public void setTexStart(int xTexStart, int yTexStart) {
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(this.resourceLocation);
            GlStateManager.disableDepth();
            int i = this.xTexStart;
            int j = this.yTexStart;

            if (this.hovered)
            {
                j += this.yDiffText;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
            GlStateManager.enableDepth();
        }
    }
}
