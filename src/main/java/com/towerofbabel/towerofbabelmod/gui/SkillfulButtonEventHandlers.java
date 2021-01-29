package com.towerofbabel.towerofbabelmod.gui;

import com.towerofbabel.towerofbabelmod.TowerOfBabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// This class listens for all GUI events, and responds to select ones that relate to the rendering of Skillful buttons
// in the inventory UI (and potentially other places in the future).
public class SkillfulButtonEventHandlers {
    // This ID chosen completely arbitrarily.
    private int SKILLFUL_ID = 983;
    // Worth noting: this icon is a spritemap with both highlighted and default states.
    private ResourceLocation SKILLFUL_ICON = new ResourceLocation(TowerOfBabel.MODID, "textures/gui/gui.png");
    // The actual button object.
    private GuiButtonImage skillfulButton;

    // When a GUI opens, if it's the Inventory, we'll need to render our button.
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void guiOpen(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen guiScreen = event.getGui();
        // Break out if this isn't the inventory screen, since we get GUI events on lots of other screens too.
        if (!(guiScreen instanceof GuiInventory)) {
            return;
        }


        // Get button position
        int[] position = calculateButtonPosition(guiScreen);
        int x = position[0];
        int y = position[1];

        // Create button - despite the class name, this is "a button with an image label" (as opposed to a text label), not "the image on a button"
        skillfulButton = new GuiButtonImage(SKILLFUL_ID, x, y, 20, 18, 14, 0, 19, SKILLFUL_ICON);

        // Add the button to the list of buttons on the inventory screen (basically, get the inventory screen to render it).
        event.getButtonList().add(skillfulButton);
    }

    // If any GUI button is clicked, we need to check if it's our button.
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void guiButtonClick(GuiScreenEvent.ActionPerformedEvent.Post event) {
        // Break out if this isn't the inventory screen, since we get GUI button events on lots of other screens too.
        if (!(event.getGui() instanceof GuiInventory)) {
            return;
        }

        // Other mods might cause 'guiButtonClick' to fire, as well as the recipe book button.
        // We obviously only want to act if our button was the one clicked.
        if (event.getButton().equals(skillfulButton)) {
            // DO STUFF HERE
            // Get data
//            EntityPlayer player = Minecraft.getMinecraft().player;
//            World world = Minecraft.getMinecraft().world;

            // Open GUI
            // player.openGui(Nutrition.instance, ModGuiHandler.NUTRITION_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        } else {
            // Some other button was clicked, but we should recalculate our button position (since new UI might have popped up)
            int[] pos = calculateButtonPosition(event.getGui());
            int xPosition = pos[0];
            int yPosition = pos[1];
            skillfulButton.setPosition(xPosition, yPosition);
        }
    }

    // Returns an array (a poor man's tuple, [x,y]) with the button's position
    @SideOnly(Side.CLIENT)
    private int[] calculateButtonPosition(GuiScreen gui) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        // Get bounding box of origin
//        if (Config.buttonOrigin.equals("screen")) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            width = scaledResolution.getScaledWidth();
            height = scaledResolution.getScaledHeight();
//        } else if (Config.buttonOrigin.equals("gui")) {
//            width = ((GuiInventory) gui).getXSize();
//            height = ((GuiInventory) gui).getYSize();
//        }

        // Calculate anchor position from origin (eg. x/y pixels at right side of gui)
        // The x/y is still relative to the top/left corner of the screen at this point
//        switch(Config.buttonAnchor) {
//            case "top": x = width / 2; y = 0; break;
//            case "right": x = width; y = height / 2; break;
//            case "bottom": x = width / 2; y = height; break;
//            case "left": x = 0; y = height / 2; break;
//            case "top-left": x = 0; y = 0; break;
//            case "top-right": x = width; y = 0; break;
//            case "bottom-right": x = width; y = height; break;
//            case "bottom-left": x = 0; y = height; break;
//            case "center": x = width / 2; y = height / 2; break;
//        }
        x = 10;
        y = 10;

        // If origin=gui, add the offset to the button's position
//        if (Config.buttonOrigin.equals("gui")) {
//            x += ((GuiInventory) gui).getGuiLeft();
//            y += ((GuiInventory) gui).getGuiTop();
//        }

        // Then add the offset as defined in the config file
//        x += Config.buttonXPosition;
//        y += Config.buttonYPosition;

        return new int[]{x, y};
    }
}
