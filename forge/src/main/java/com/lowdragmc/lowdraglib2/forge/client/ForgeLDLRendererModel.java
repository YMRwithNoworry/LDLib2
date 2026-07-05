package com.lowdragmc.lowdraglib2.forge.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public final class ForgeLDLRendererModel implements IUnbakedGeometry<ForgeLDLRendererModel> {
    public static final ForgeLDLRendererModel INSTANCE = new ForgeLDLRendererModel();

    private ForgeLDLRendererModel() {
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides,
                           ResourceLocation modelLocation) {
        return new com.lowdragmc.lowdraglib2.client.model.forge.LDLRendererModel.RendererBakedModel();
    }

    public static final class Loader implements IGeometryLoader<ForgeLDLRendererModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public ForgeLDLRendererModel read(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
            return ForgeLDLRendererModel.INSTANCE;
        }
    }
}
