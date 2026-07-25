package com.lowdragmc.lowdraglib2.core.mixins.accessor;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;


/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote AbstractContainerScreenMixin
 */
@Mixin(Slot.class)
public interface SlotAccessor {
    @Accessor(value = "x", remap = false) int getX();
    @Accessor(value = "y", remap = false) int getY();
    @Accessor(value = "x", remap = false) @Mutable void setX(int x);
    @Accessor(value = "y", remap = false) @Mutable void setY(int y);
}
