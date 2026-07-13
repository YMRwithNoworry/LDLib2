package com.lowdragmc.lowdraglib2.client.renderer.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

// used to present a renderer block
public class RendererBlock extends Block implements EntityBlock {

    public static final RendererBlock BLOCK = new RendererBlock();

    private RendererBlock() {
        super(Properties.of().noOcclusion().destroyTime(2));
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RendererBlockEntity(pPos, pState);
    }
}
