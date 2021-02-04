package com.towerofbabel.towerofbabelmod.gui;

import com.towerofbabel.towerofbabelmod.TOBPlayerProps;
import com.towerofbabel.towerofbabelmod.TowerOfBabel;
import com.towerofbabel.towerofbabelmod.babel.Bonuses;
import com.towerofbabel.towerofbabelmod.tower.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;


public class SkillfulSkillTreeGui extends GuiScreen {
    public class SkillIndex {
        public String skillId;
        public int index;

        public SkillIndex(String skillId, int index) {
            this.skillId = skillId;
            this.index = index;
        }
    }

    private GuiButton closeButton;
    private Map<GuiButton, SkillIndex> skillButtons = new HashMap<GuiButton, SkillIndex>();

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

        redrawSkills();

    }

    // Called when needing to propagate the window with new information
    public void redrawSkills() {
        // Draw skills
        int skillIndex = 0;
        for (SkillTree skill : TowerOfBabel.skills.values()) {
            drawSkill(skill, skillIndex);
            skillIndex++;
        }
    }

    private void drawSkill(SkillTree skill, int skillIndex) {
        drawSkill(skill, skillIndex, null);
    }

    private void drawSkill(SkillTree skill, int skillIndex, SkillButton button) {
        // Clever algorithm go brr
        boolean isSkillDisabled = !TOBPlayerProps.canLearn(mc.player, skill.getId());
        boolean isSkillUnlocked = TOBPlayerProps.isUnlocked(mc.player, skill.getId());
        
        // Skill images exist in 3-wide clumps of button variants.  The leftmost value is the disabled state,
        // then the 'active' state, and finally, the 'already unlocked' state.  Each state is 20px wide.
        // Hover states (per MC conventions) are located directly below their normal implementations with a 1-px gap.
        int imageXOffset = 20;
        if (isSkillDisabled) {
            imageXOffset = 0;
        } else if (isSkillUnlocked) {
            imageXOffset = 40;
        }
        if (button != null)
        {
            button.setTexStart(imageXOffset, 0);
        } else {

            int buttonId = skillIndex + 101;

            SkillButton newSkillButton = new SkillButton(
                buttonId, // Button ID
                skillIndex * 100 + 27, // X of Button on screen
                10, // Y of button on screen
                20, // Button (and also image) width
                18, // Button (and also image) height
                imageXOffset, // probably x-offset inside the image file
                0,  // probably y-offset inside image file
                19, // Vertical offset of hover state below the non-hover version.
                SKILL_BUTTON_IMAGE
            );
            this.skillButtons.put(newSkillButton, new SkillIndex(skill.getId(), skillIndex));

            // Button ID != Index of Button in ButtonList

            buttonList.add(newSkillButton);
        }
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
            SkillIndex skillIndex = skillButtons.get(button);
            System.out.println("Adding Skill");
            if (skillIndex != null) {
                TOBPlayerProps.addSkill(mc.player, skillIndex.skillId);
                TOBPlayerProps.updateSkillValues(mc.player);
                // TODO - Figure out how to set the button's X-Offset to 40 upon unlock
                drawSkill(TowerOfBabel.skills.get(skillIndex.skillId), skillIndex.index, (SkillButton) button);
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

        // Draw buttons, and as we do, check each button to see if any one is hovered.
        GuiButton hoveredButton = null;
        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, 0);
            if (mouseX >= button.x && mouseX <= button.x + button.width && mouseY >= button.y && mouseY <= button.y + button.height) {
                hoveredButton = button;
            }
        }

        // If we are hovering a button, and it's a skill button (and thus has an associated skill) we need to give it a tooltip.
        // Because Z-index of GUI elements is determined by draw order, we can't draw this tooltip inside the 'foreach' loop above.
        if (hoveredButton != null) {
            SkillIndex skillIdentifierForHoveredButton = this.skillButtons.get(hoveredButton);
            if (skillIdentifierForHoveredButton != null) {
                SkillTree skill = TowerOfBabel.skills.get(skillIdentifierForHoveredButton.skillId);
                if (skill != null) {
                    List<String> tooltipText = new LinkedList();
                    tooltipText.add(skill.getName());
                    for (Bonuses.BONUS bonus : skill.bonuses.keySet()) {
                        tooltipText.add(WordUtils.capitalize(bonus.toString() + " +" + skill.bonuses.get(bonus)));
                    }

                    GuiUtils.drawHoveringText(tooltipText, mouseX, mouseY, mc.currentScreen.width, mc.currentScreen.height, 100, mc.fontRenderer);
                }
            }
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
