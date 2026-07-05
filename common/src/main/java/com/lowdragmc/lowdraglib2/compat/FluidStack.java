package com.lowdragmc.lowdraglib2.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private Fluid fluid;
    private long amount;
    @Nullable
    private CompoundTag tag;

    public FluidStack(Fluid fluid, long amount) {
        this(fluid, amount, null);
    }

    public FluidStack(Fluid fluid, long amount, @Nullable CompoundTag tag) {
        this.fluid = fluid == null ? Fluids.EMPTY : fluid;
        this.amount = amount;
        this.tag = tag == null ? null : tag.copy();
    }

    public Fluid getFluid() {
        return isEmpty() ? Fluids.EMPTY : fluid;
    }

    public Component getHoverName() {
        return Component.translatable(getFluid().defaultFluidState().createLegacyBlock().getBlock().getDescriptionId());
    }

    public long getAmount() {
        return isEmpty() ? 0 : amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void grow(long amount) {
        setAmount(getAmount() + amount);
    }

    public void shrink(long amount) {
        setAmount(getAmount() - amount);
    }

    public boolean isEmpty() {
        return fluid == Fluids.EMPTY || amount <= 0;
    }

    public FluidStack copy() {
        return isEmpty() ? EMPTY : new FluidStack(fluid, amount, tag);
    }

    public FluidStack copyWithAmount(long amount) {
        return isEmpty() ? EMPTY : new FluidStack(fluid, amount, tag);
    }

    @Nullable
    public CompoundTag getTag() {
        return tag;
    }

    @Nullable
    public CompoundTag getComponentsPatch() {
        return getTag();
    }

    public void setTag(@Nullable CompoundTag tag) {
        this.tag = tag == null ? null : tag.copy();
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        var nbt = new CompoundTag();
        nbt.putString("FluidName", net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid).toString());
        nbt.putLong("Amount", amount);
        if (tag != null) nbt.put("Tag", tag.copy());
        return nbt;
    }

    public static FluidStack parseOptional(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) return EMPTY;
        var id = net.minecraft.resources.ResourceLocation.tryParse(nbt.getString("FluidName"));
        if (id == null) return EMPTY;
        var fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.get(id);
        if (fluid == Fluids.EMPTY) return EMPTY;
        var stack = new FluidStack(fluid, nbt.getLong("Amount"));
        if (nbt.contains("Tag")) stack.setTag(nbt.getCompound("Tag"));
        return stack;
    }

    public static boolean matches(FluidStack a, FluidStack b) {
        return a == b || (a != null && b != null && a.getFluid() == b.getFluid() && a.getAmount() == b.getAmount() && Objects.equals(a.getTag(), b.getTag()));
    }

    public static FluidStack read(FriendlyByteBuf buf) {
        var fluid = buf.readById(net.minecraft.core.registries.BuiltInRegistries.FLUID);
        var amount = buf.readVarLong();
        var tag = buf.readNbt();
        return new FluidStack(fluid, amount, tag);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeId(net.minecraft.core.registries.BuiltInRegistries.FLUID, getFluid());
        buf.writeVarLong(getAmount());
        buf.writeNbt(tag);
    }
}
