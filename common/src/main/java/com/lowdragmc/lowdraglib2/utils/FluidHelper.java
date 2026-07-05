package com.lowdragmc.lowdraglib2.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.lowdragmc.lowdraglib2.compat.FluidStack;

import org.jetbrains.annotations.Nullable;

/**
 * @author KilaBash
 * @date 2023/2/10
 * @implNote FluidHelper
 */
@UtilityClass
public final class FluidHelper {

    public static int getBucket() {
        return 1000;
    }

    public static int getColor(FluidStack fluidStack) {
        return 0xffffffff;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static TextureAtlasSprite getStillTexture(FluidStack fluidStack) {
        if (fluidStack.getFluid() == Fluids.EMPTY) return null;
        var texture = fluidStack.getFluid().defaultFluidState().createLegacyBlock().getBlock().getDescriptionId();
        if (texture == null) return null;
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(InventoryMenu.BLOCK_ATLAS);
    }

    public static Component getDisplayName(FluidStack fluidStack) {
        return fluidStack.getHoverName();
    }

    public static int getTemperature(FluidStack fluidStack) {
        return 300;
    }

    public static boolean isLighterThanAir(FluidStack fluidStack) {
        return false;
    }

    public static boolean canBePlacedInWorld(FluidStack fluidStack, BlockAndTintGetter level, BlockPos pos) {
        return true;
    }

    public static boolean doesVaporize(FluidStack fluidStack, Level level, BlockPos pos) {
        return false;
    }

    public static SoundEvent getEmptySound(FluidStack fluidStack) {
        return null;
    }

    public static SoundEvent getFillSound(FluidStack fluidStack) {
        return null;
    }

    public static Object toRealFluidStack(FluidStack fluidStack) {
        return fluidStack;
    }

    public static String getUnit() {
        return "mB";
    }
}
