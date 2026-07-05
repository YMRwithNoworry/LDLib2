package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

public class ScreenEvent {
    public static class Init {
        public static class Pre {
            private final Screen screen;

            public Pre(Screen screen) {
                this.screen = screen;
            }

            public Screen getScreen() {
                return screen;
            }

            public void addListener(GuiEventListener listener) {
            }
        }
    }
}
