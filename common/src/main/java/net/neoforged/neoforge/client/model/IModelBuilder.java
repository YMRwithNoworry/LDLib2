package net.neoforged.neoforge.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

public interface IModelBuilder<T> {
    void addUnculledFace(BakedQuad quad);

    void addCulledFace(Direction direction, BakedQuad quad);
}
