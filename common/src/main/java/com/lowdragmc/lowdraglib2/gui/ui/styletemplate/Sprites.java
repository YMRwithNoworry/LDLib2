package com.lowdragmc.lowdraglib2.gui.ui.styletemplate;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.editor.resource.BuiltinPath;
import com.lowdragmc.lowdraglib2.editor.resource.BuiltinResourceProvider;
import com.lowdragmc.lowdraglib2.editor.resource.ResourceInstance;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.texture.UIResourceTexture;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.lang.reflect.Modifier;

@UtilityClass
public class Sprites {
    public static ResourceLocation GDP = LDLib2.id("textures/gui/gdp_styles.png");

    public static IGuiTexture RECT_RD = sprite(1, 29, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_RD_LIGHT = sprite(1, 15, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_RD_DARK = sprite(1, 43, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_RD_SOLID = sprite(1, 1, 13, 13, 2, 2, 2, 2);

    public static IGuiTexture RECT_RD_T = sprite(15, 29, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_RD_T_LIGHT = sprite(15, 15, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_RD_T_DARK = sprite(15, 43, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_RD_T_SOLID = sprite(15, 1, 13, 13, 4, 4, 4, 4);

    public static IGuiTexture RECT = sprite(29, 29, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_LIGHT = sprite(29, 15, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_DARK = sprite(29, 43, 13, 13, 4, 4, 4, 4);
    public static IGuiTexture RECT_SOLID = sprite(29, 1, 13, 13, 1, 1, 1, 1);

    public static IGuiTexture BORDER = sprite(86, 131, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_DARK = sprite(86, 148, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_TRANSLATE = sprite(86, 165, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER_THICK = sprite(171, 154, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_DARK = sprite(171, 171, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_TRANSLATE = sprite(171, 165, 16, 16, 4, 4, 4, 4);

    public static IGuiTexture BORDER_RT0 = sprite(103, 131, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT0_DARK = sprite(103, 148, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT0_TRANSLATE = sprite(103, 165, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER_THICK_RT0 = sprite(188, 154, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_RT0_DARK = sprite(188, 171, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_RT0_TRANSLATE = sprite(188, 165, 16, 16, 4, 4, 4, 4);

    public static IGuiTexture BORDER_RT1 = sprite(120, 131, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT1_DARK = sprite(120, 148, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT1_TRANSLATE = sprite(120, 165, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER_THICK_RT1 = sprite(205, 154, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_RT1_DARK = sprite(205, 171, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_RT1_TRANSLATE = sprite(205, 165, 16, 16, 4, 4, 4, 4);

    public static IGuiTexture BORDER_RT2 = sprite(137, 131, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT2_DARK = sprite(137, 148, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT2_TRANSLATE = sprite(137, 165, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER_THICK_RT2 = sprite(222, 154, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_RT2_DARK = sprite(222, 171, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER_THICK_RT2_TRANSLATE = sprite(222, 165, 16, 16, 4, 4, 4, 4);

    public static IGuiTexture BORDER_RT3 = sprite(154, 131, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT3_DARK = sprite(154, 148, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER_RT3_TRANSLATE = sprite(154, 165, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER1 = sprite(86, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_DARK = sprite(86, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_TRANSLATE = sprite(86, 239, 16, 16, 3, 5, 3, 3);

    public static IGuiTexture BORDER1_THICK = sprite(171, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_DARK = sprite(171, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_TRANSLATE = sprite(171, 239, 16, 16, 3, 5, 3, 3);

    public static IGuiTexture BORDER1_RT0 = sprite(103, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_RT0_DARK = sprite(103, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_RT0_TRANSLATE = sprite(103, 239, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER1_THICK_RT0 = sprite(188, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_RT0_DARK = sprite(188, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_RT0_TRANSLATE = sprite(188, 239, 16, 16, 3, 5, 3, 3);

    public static IGuiTexture BORDER1_RT1 = sprite(120, 205, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER1_RT1_DARK = sprite(120, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_RT1_TRANSLATE = sprite(120, 165, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER1_THICK_RT1 = sprite(205, 205, 16, 16, 6, 6, 6, 6);
    public static IGuiTexture BORDER1_THICK_RT1_DARK = sprite(205, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_RT1_TRANSLATE = sprite(205, 239, 16, 16, 3, 5, 3, 3);

    public static IGuiTexture BORDER1_RT2 = sprite(137, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_RT2_DARK = sprite(137, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_RT2_TRANSLATE = sprite(137, 239, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER1_THICK_RT2 = sprite(222, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_RT2_DARK = sprite(222, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_RT2_TRANSLATE = sprite(222, 239, 16, 16, 3, 5, 3, 3);

    public static IGuiTexture BORDER1_RT3 = sprite(154, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_RT3_DARK = sprite(154, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_RT3_TRANSLATE = sprite(154, 239, 16, 16, 3, 3, 3, 3);

    public static IGuiTexture BORDER1_THICK_RT3 = sprite(239, 205, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_RT3_DARK = sprite(239, 222, 16, 16, 5, 5, 5, 5);
    public static IGuiTexture BORDER1_THICK_RT3_TRANSLATE = sprite(239, 239, 16, 16, 3, 5, 3, 3);

    public static IGuiTexture SCROLL_CONTAINER_V = sprite(48, 198, 5, 7, 2, 2, 2, 2);
    public static IGuiTexture SCROLL_BAR_V = sprite(48, 174, 5, 7, 2, 2, 2, 2);
    public static IGuiTexture SCROLL_BAR_LIGHT_V = sprite(48, 182, 5, 7, 2, 2, 2, 2);
    public static IGuiTexture SCROLL_BAR_WHITE_V = sprite(48, 190, 5, 7, 2, 2, 2, 2);

    public static IGuiTexture SCROLL_CONTAINER_H = sprite(55, 200, 7, 5, 2, 2, 2, 2);
    public static IGuiTexture SCROLL_BAR_H = sprite(55, 182, 7, 5, 2, 2, 2, 2);
    public static IGuiTexture SCROLL_BAR_LIGHT_H = sprite(55, 188, 7, 5, 2, 2, 2, 2);
    public static IGuiTexture SCROLL_BAR_WHITE_H = sprite(55, 194, 7, 5, 2, 2, 2, 2);

    public static IGuiTexture PROGRESS_CONTAINER = sprite(237, 130, 18, 11, 4, 4, 4, 4);
    public static IGuiTexture PROGRESS_BAR = sprite(241, 164, 10, 3, 1, 1, 1, 1);

    public static IGuiTexture TAB = sprite(242, 85, 13, 13, 3, 3, 3, 3);
    public static IGuiTexture TAB_DARK = sprite(242, 71, 13, 13, 3, 3, 3, 3);
    public static IGuiTexture TAB_WHITE = sprite(242, 113, 13, 13, 3, 3, 3, 3);

    private static IGuiTexture sprite(int x, int y, int width, int height,
                                      int left, int top, int right, int bottom) {
        if (!LDLib2.isClient()) return IGuiTexture.EMPTY;
        return ClientSprites.create(x, y, width, height, left, top, right, bottom);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientSprites {
        private static IGuiTexture create(int x, int y, int width, int height,
                                          int left, int top, int right, int bottom) {
            return SpriteTexture.of(GDP)
                    .setSprite(x, y, width, height)
                    .setBorder(left, top, right, bottom);
        }
    }

    public static void init(ResourceInstance<IGuiTexture> instance) {
        var provider = new BuiltinResourceProvider<>("ui-gdp", instance);
        for (var field : Sprites.class.getDeclaredFields()) {
            if (IGuiTexture.class.isAssignableFrom(field.getType())
                    && Modifier.isStatic(field.getModifiers()) ) {
                try {
                    var texture = (IGuiTexture) field.get(null);
                    provider.addResource(field.getName(), texture);
                    var res = new BuiltinPath("ui-gdp:" + field.getName());
                    texture = new UIResourceTexture(res);
                    field.set(null, texture);
                } catch (Exception ignored) {}
            }
        }
        instance.addBuiltinProvider(provider);
    }
}
