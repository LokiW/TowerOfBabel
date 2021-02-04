package com.towerofbabel.towerofbabelmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImageUpdatable extends GuiButtonImage {
    private final ResourceLocation resourceLocation;
    private int xTexStart;
    private int yTexStart;
    private int yDiffText;

    public GuiButtonImageUpdatable(int p_i47392_1_, int p_i47392_2_, int p_i47392_3_, int p_i47392_4_, int p_i47392_5_, int p_i47392_6_, int p_i47392_7_, int p_i47392_8_, ResourceLocation p_i47392_9_) {
        super(p_i47392_1_, p_i47392_2_, p_i47392_3_, p_i47392_4_, p_i47392_5_, p_i47392_6_, p_i47392_7_, p_i47392_8_, p_i47392_9_);
        this.xTexStart = p_i47392_6_;
        this.yTexStart = p_i47392_7_;
        this.yDiffText = p_i47392_8_;
        this.resourceLocation = p_i47392_9_;
    }

    public void setPosition(int p_191746_1_, int p_191746_2_)
    {
        this.x = p_191746_1_;
        this.y = p_191746_2_;
    }

    public void setTexStart(int xTexStart, int yTexStart) {
        this.xTexStart = xTexStart;
        this.yTexStart = this.yTexStart;
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
