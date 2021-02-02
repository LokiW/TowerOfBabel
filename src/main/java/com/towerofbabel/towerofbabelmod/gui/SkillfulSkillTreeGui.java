package com.towerofbabel.towerofbabelmod.gui;

import com.towerofbabel.towerofbabelmod.TOBPlayerProps;
import com.towerofbabel.towerofbabelmod.TowerOfBabel;
import com.towerofbabel.towerofbabelmod.tower.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
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

    private final ResourceLocation SKILL_BUTTON_IMAGE = new ResourceLocation(TowerOfBabel.MODID, "textures/gui/mining_buttons.png");

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
            boolean isSkillDisabled = false;
            boolean isSkillUnlocked = false; // TODO -  TOBPlayerProps.get(mc.player).unlockedSkills.get(skill.something());

            // Skill images exist in 3-wide clumps of button variants.  The leftmost value is the disabled state,
            // then the 'active' state, and finally, the 'already unlocked' state.  Each state is 20px wide.
            // Hover states (per MC conventions) are located directly below their normal implementations with a 1-px gap.
            int imageXOffset = 20;
            if (isSkillDisabled) {
                imageXOffset = 0;
            } else if (isSkillUnlocked) {
                imageXOffset = 40;
            }

            GuiButtonImage newSkillButton = new GuiButtonImage(
                    i + 101, // Button ID
                    i * 100 + 27, // X of Button on screen
                    10, // Y of button on screen
                    20, // Button (and also image) width
                    18, // Button (and also image) height
                    imageXOffset, // probably x-offset inside the image file
                    0, // probably y-offset inside image file
                    19, // Vertical offset of hover state below the non-hover version.
                    SKILL_BUTTON_IMAGE
            );
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
                // TODO - Figure out how to set the button's X-Offset to 40 upon unlock
//                button = new GuiButtonImage(
//                        101, // Button ID
//                        27, // X of Button on screen
//                        10, // Y of button on screen
//                        20, // Button (and also image) width
//                        18, // Button (and also image) height
//                        40, // probably x-offset inside the image file
//                        0, // probably y-offset inside image file
//                        19, // Is this vertical offset of hover state?
//                        SKILL_BUTTON_IMAGE
//                );
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
