package com.towerofbabel.towerofbabelmod.gui;

import com.towerofbabel.towerofbabelmod.TOBPlayerProps;
import com.towerofbabel.towerofbabelmod.TowerOfBabel;
import com.towerofbabel.towerofbabelmod.tower.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class SkillfulSkillTreeGui extends GuiScreen {
    private GuiButton closeButton;
    private Map<GuiButton, String> skillButtons = new HashMap<GuiButton, String>();
    private GuiLabel label;

    ///////////////////
    // Magic Numbers //
    ///////////////////

    // Close button position
    private final int CLOSE_BUTTON_WIDTH = 10;
    private final int CLOSE_BUTTON_HEIGHT = 10;
    private final int CLOSE_BUTTON_OFFSET = 7;
    // Container info
    private final ResourceLocation GUI_BORDERS = new ResourceLocation(TowerOfBabel.MODID, "textures/gui/gui.png");

    // Offsets
    public int top = 0;
    public int left = 0;
    public int right = 0;
    public int bottom = 0;

    // Called when GUI is opened or resized
    @Override
    public void initGui() {
//        // Sync Nutrition info from server to client
//        Sync.clientRequest();

        // Calculate label offset for long nutrition names
//        for (Nutrient nutrient : NutrientList.getVisible()) {
//            int nutrientWidth = fontRenderer.getStringWidth(I18n.format("nutrient." + Nutrition.MODID + ":" + nutrient.name)); // Get width of localized string
//            nutrientWidth = (nutrientWidth / 4) * 4; // Round to nearest multiple of 4
//            if (nutrientWidth > labelCharacterPadding)
//                labelCharacterPadding = nutrientWidth;
//        }

        // Update dynamic GUI size
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        // These widths and heights are the width of the GUI "canvas" - meaning the darkened background, not just
        // the opaque UI overlaid atop it.  Therefore, we do want the full screen width and height no matter what size
        // the contained UI actually (factoring in borders/padding, etc).
        this.width = scaledResolution.getScaledWidth();
        this.height = scaledResolution.getScaledHeight();

        // Add Close button
        buttonList.add(closeButton = new GuiButton(
                0,
                CLOSE_BUTTON_OFFSET,
                 CLOSE_BUTTON_OFFSET,
                CLOSE_BUTTON_WIDTH,
                CLOSE_BUTTON_HEIGHT,
                I18n.format("gui." + TowerOfBabel.MODID + ":close")
        ));
//        buttonList.add(closeButton = new GuiButton(
//                0,
//                0,
//                0,
//                CLOSE_BUTTON_WIDTH,
//                CLOSE_BUTTON_HEIGHT,
//                I18n.format("gui." + TowerOfBabel.MODID + ":close")
//        ));

        // Draw labels
        // redrawLabels();
        int i = 0;
        System.out.println("HERE");
        for (SkillTree skill : TowerOfBabel.skills.values()) {
            System.out.println("Adding Skill: " + skill.getName());
            // Clever algorithm go brr
            GuiButton newSkillButton = new GuiButton(i + 101, i * 100 + 27, 10, 80, 20, skill.getName());
            this.skillButtons.put(newSkillButton, skill.getId());
            buttonList.add(newSkillButton);
            i++;
        }

    }

    // Called when needing to propagate the window with new information
    public void redrawLabels() {
        // Clear existing labels for nutrition value or screen changes
        labelList.clear();

        // Draw title
        String skillTreeTitle = I18n.format("gui." + TowerOfBabel.MODID + ":skill_tree_title");
        // 10 = Title Top Padding/Margin (no concept of a border box, so no difference between the two)
        labelList.add(label = new GuiLabel(fontRenderer, 0, (width / 2) - (fontRenderer.getStringWidth(skillTreeTitle) / 2),  10, 0, 0, 0xffffffff));
        label.addLine(skillTreeTitle);
    }

    // Called when button/element is clicked
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == closeButton) {
            // Close GUI
            mc.player.closeScreen();
            if (mc.currentScreen == null)
                mc.setIngameFocus();
        } else {
            String skillId = skillButtons.get(button);
            if (skillId != null) {
                TOBPlayerProps.addSkill(mc.player, skillId);
                TOBPlayerProps.updateSkillValues(mc.player);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // The 'default background' is the partially transparent dark layer behind most screens.
        drawDefaultBackground();

        // Then draw *our* background - meaning the opaque background for the skills menu.
        drawBackground();

        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, 0);
        }
    }

    private void drawBackground() {
        // Init
//        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f); // Reset color
        mc.getTextureManager().bindTexture(GUI_BORDERS); // Fetch texture
//
//        // Top left corner
//        drawTexturedModalRect(left, top, 0, 0, 4, 4);
//
//        // Bottom left corner
//        drawTexturedModalRect(left, bottom - 4, 0, 8, 4, 4);
//
//        // Top right corner
//        drawTexturedModalRect(right - 4, top, 8, 0, 4, 4);
//
//        // Bottom right corner
//        drawTexturedModalRect(right - 4, bottom - 4, 8, 8, 4, 4);
//
//        // Left side
//        for (int i = 0; i < this.height - 8; i += 4)
//            drawTexturedModalRect(left, top + 4 + i, 0, 4, 4, 4);
//
//        // Top side
//        for (int i = 0; i < this.width - 8; i += 4)
//            drawTexturedModalRect(left + 4 + i, top, 4, 0, 4, 4);
//
//        // Right side
//        for (int i = 0; i < this.height - 8; i += 4)
//            drawTexturedModalRect(right - 4, top + 4 + i, 8, 4, 4, 4);
//
//        // Bottom side
//        for (int i = 0; i < this.width - 8; i += 4)
//            drawTexturedModalRect(left + 4 + i, bottom - 4, 4, 8, 4, 4);

        // Draw center tiles
        for (int i = 0; i < this.width - 8; i += 4)
            for (int j = 0; j < this.height - 8; j += 4)
                drawTexturedModalRect(left + 4 + i, top + 4 + j, 4, 4, 4, 4);
    }
}
