package net.neoforged.neoforge.client.event;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import java.util.function.Consumer;

public class ModelEvent {
    public static class RegisterGeometryLoaders {
        public void register(ResourceLocation id, IGeometryLoader<?> loader) {
        }
    }

    public static class RegisterAdditional implements Consumer<ModelResourceLocation> {
        public void register(ModelResourceLocation location) {
        }

        @Override
        public void accept(ModelResourceLocation location) {
            register(location);
        }
    }
}
