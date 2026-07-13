package com.lowdragmc.lowdraglib2.gui.ui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModularUIClientAccess {
    private ModularUIClientAccess() {
    }

    public static void focusScreen(ModularUI modularUI) {
        var screen = modularUI.getScreen();
        if (screen != null) {
            screen.setFocused(modularUI.getWidget());
        }
    }
}
