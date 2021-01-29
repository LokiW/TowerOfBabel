package com.towerofbabel.towerofbabelmod.gui;

import net.minecraftforge.common.MinecraftForge;

// A Client-side-only proxy (servers don't care about GUI) which handles Forge events without burdening server perf.
public class GuiProxy {
    public void registerEventHandlers() {
        // Register the listeners needed to render Skillful button(s) in the inventory menu.
        MinecraftForge.EVENT_BUS.register((new SkillfulButtonEventHandlers()));
    }
}
