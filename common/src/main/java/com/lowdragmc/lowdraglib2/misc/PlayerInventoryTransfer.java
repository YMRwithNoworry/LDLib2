package com.lowdragmc.lowdraglib2.misc;

import net.minecraft.world.entity.player.Inventory;
import com.lowdragmc.lowdraglib2.compat.InvWrapper;

public class PlayerInventoryTransfer extends InvWrapper {
    public PlayerInventoryTransfer(Inventory inv) {
        super(inv);
    }

    @Override
    public int getSlots() {
        return ((Inventory) getInv()).items.size();
    }
}
