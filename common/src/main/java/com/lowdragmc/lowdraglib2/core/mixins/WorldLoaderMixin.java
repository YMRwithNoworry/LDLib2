package com.lowdragmc.lowdraglib2.core.mixins;

import com.mojang.datafixers.util.Pair;
import com.lowdragmc.lowdraglib2.Platform;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldLoader.class)
public abstract class WorldLoaderMixin {
    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/WorldLoader$PackConfig;createResourceManager()Lcom/mojang/datafixers/util/Pair;"))
    private static Pair<WorldDataConfiguration, CloseableResourceManager> ldlib2$loadResourceManager(WorldLoader.PackConfig packConfig) {
        var pair = packConfig.createResourceManager();
        Platform.RESOURCE_MANAGER = pair.getSecond();
        return pair;
    }

    @Inject(method = "method_42096", at = @At(value = "HEAD"))
    private static void ldlib2$closeResourceManager(CloseableResourceManager closeableresourcemanager, ReloadableServerResources p_214370_, Throwable p_214371_, CallbackInfo ci) {
        Platform.RESOURCE_MANAGER = null;
    }
}
