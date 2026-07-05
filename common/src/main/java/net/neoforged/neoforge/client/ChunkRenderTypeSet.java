package net.neoforged.neoforge.client;

import net.minecraft.client.renderer.RenderType;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ChunkRenderTypeSet {
    private static final ChunkRenderTypeSet EMPTY = new ChunkRenderTypeSet(Collections.emptySet());
    private final Set<RenderType> renderTypes;

    private ChunkRenderTypeSet(Set<RenderType> renderTypes) {
        this.renderTypes = renderTypes;
    }

    public static ChunkRenderTypeSet none() {
        return EMPTY;
    }

    public static ChunkRenderTypeSet of(RenderType renderType) {
        if (renderType == null) {
            return none();
        }
        return new ChunkRenderTypeSet(Set.of(renderType));
    }

    public static ChunkRenderTypeSet of(Collection<RenderType> renderTypes) {
        return new ChunkRenderTypeSet(new LinkedHashSet<>(renderTypes));
    }

    public static ChunkRenderTypeSet union(Collection<ChunkRenderTypeSet> sets) {
        var renderTypes = new LinkedHashSet<RenderType>();
        for (var set : sets) {
            if (set != null) {
                renderTypes.addAll(set.renderTypes);
            }
        }
        return new ChunkRenderTypeSet(renderTypes);
    }

    public Set<RenderType> asSet() {
        return Collections.unmodifiableSet(renderTypes);
    }
}
