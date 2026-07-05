package com.lowdragmc.lowdraglib2.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib2.client.renderer.IItemRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2022/05/28
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow abstract UnbakedModel getModel(ResourceLocation modelPath);

    @Shadow
    private java.util.Map<ResourceLocation, UnbakedModel> topLevelModels;

    @WrapOperation(method = "getModel",
              at = @At(value = "INVOKE",
                       target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    protected void injectStateToModelLocation(Logger instance, String s, Object[] objects, Operation<Void> original) {
        ResourceLocation id = objects[0] instanceof ResourceLocation rl ? rl : null;
        if (id != null) {
            if (id.getPath().startsWith("block/")) {
                id = id.withPath(id.getPath().substring("block/".length()));
            } else if (id.getPath().startsWith("item/")) {
                id = id.withPath(id.getPath().substring("item/".length()));
            }
            if (BuiltInRegistries.ITEM.get(id) instanceof IItemRendererProvider) {
                return;
            }
        }
        original.call(instance, s, objects);
    }

    @Inject(method = "loadTopLevel", at = @At("RETURN"))
    protected void ldlib2$changeLoadedModel(ModelResourceLocation modelResourceLocation, CallbackInfo ci) {
        if (!modelResourceLocation.getVariant().equals("inventory")) {
            ResourceLocation resourceLocation = new ResourceLocation(modelResourceLocation.getNamespace(), modelResourceLocation.getPath());
            var block = BuiltInRegistries.BLOCK.get(resourceLocation);
            if (block instanceof IBlockRendererProvider) {
                UnbakedModel newModel = getModel(LDLib2.id("block/renderer_model"));
                topLevelModels.put(modelResourceLocation, newModel);
            }
        }
    }
}
