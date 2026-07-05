package com.lowdragmc.lowdraglib2.compat;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InvWrapper implements IItemHandlerModifiable {
    private final Container inv;

    public InvWrapper(Container inv) {
        this.inv = inv;
    }

    protected Container getInv() {
        return inv;
    }

    @Override
    public int getSlots() {
        return inv.getContainerSize();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return inv.getItem(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        var existing = inv.getItem(slot);
        if (!existing.isEmpty() && !ItemStack.isSameItemSameTags(existing, stack)) return stack;
        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        int room = existing.isEmpty() ? limit : limit - existing.getCount();
        if (room <= 0) return stack;
        int inserted = Math.min(room, stack.getCount());
        if (!simulate) {
            if (existing.isEmpty()) inv.setItem(slot, stack.copyWithCount(inserted));
            else existing.grow(inserted);
            inv.setChanged();
        }
        return inserted == stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - inserted);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        var existing = inv.getItem(slot);
        if (existing.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        int extracted = Math.min(amount, existing.getCount());
        var result = existing.copyWithCount(extracted);
        if (!simulate) {
            existing.shrink(extracted);
            if (existing.isEmpty()) inv.setItem(slot, ItemStack.EMPTY);
            inv.setChanged();
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv instanceof Inventory ? 64 : inv.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return inv.canPlaceItem(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        inv.setItem(slot, stack);
        inv.setChanged();
    }
}
