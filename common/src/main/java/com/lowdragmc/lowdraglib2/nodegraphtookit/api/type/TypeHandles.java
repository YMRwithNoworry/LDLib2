package com.lowdragmc.lowdraglib2.nodegraphtookit.api.type;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import com.lowdragmc.lowdraglib2.compat.FluidStack;

public class TypeHandles {
    public static final class Unknown { private Unknown() {} }
    public static final class ExecutionFlow { private ExecutionFlow() {} }
    public static final class Subgraph { private Subgraph() {} }
    public static final class MissingPort { private MissingPort() {} }

    public static final TypeHandle AUTOMATIC;
    public static final TypeHandle MISSING;
    public static final TypeHandle UNKNOWN;
    public static final TypeHandle EXECUTION_FLOW;
    public static final TypeHandle SUBGRAPH;

    public static final TypeHandle MISSING_PORT;

    public static final TypeHandle BOOL;
    public static final TypeHandle VOID;
    public static final TypeHandle CHAR;
    public static final TypeHandle DOUBLE;
    public static final TypeHandle FLOAT;
    public static final TypeHandle INT;
    public static final TypeHandle LONG;
    public static final TypeHandle OBJECT;
    public static final TypeHandle STRING;

    public static final TypeHandle COLOR;

    // Minecraft
    public static final TypeHandle DIRECTION;
    public static final TypeHandle BLOCK;
    public static final TypeHandle ITEM;
    public static final TypeHandle FLUID;
    public static final TypeHandle ENTITY_TYPE;
    public static final TypeHandle ITEM_STACK;
    public static final TypeHandle FLUID_STACK;


    static {
        // Normal type handles
        MISSING_PORT = TypeHandleHelpers.fromType(MissingPort.class);
        VOID = TypeHandleHelpers.fromType(Void.class);
        AUTOMATIC = TypeHandleHelpers.customType("AUTOMATIC", "Automatic");
        MISSING = TypeHandleHelpers.customType("MISSING_TYPE", null);
        UNKNOWN = TypeHandleHelpers.customType(Unknown.class, "UNKNOWN");
        EXECUTION_FLOW = TypeHandleHelpers.customType(ExecutionFlow.class, "EXECUTION_FLOW");
        SUBGRAPH = TypeHandleHelpers.customType(Subgraph.class, "SUBGRAPH");

        BOOL = TypeHandleHelpers.fromType(Boolean.class);
        TypeHandleHelpers.setCustomColor(BOOL, 0xFF8c85ff);
        TypeHandleHelpers.setCustomDefaultValue(BOOL, () -> false);
        CHAR = TypeHandleHelpers.fromType(Character.class);
        TypeHandleHelpers.setCustomDefaultValue(CHAR, () -> '\0');
        DOUBLE = TypeHandleHelpers.fromType(Double.class);
        TypeHandleHelpers.setCustomColor(DOUBLE, 0xFF10B4C5);
        TypeHandleHelpers.setCustomDefaultValue(DOUBLE, () -> 0.0);
        FLOAT = TypeHandleHelpers.fromType(Float.class);
        TypeHandleHelpers.setCustomColor(FLOAT, 0xFF10B4C5);
        TypeHandleHelpers.setCustomDefaultValue(FLOAT, () -> 0.0f);
        INT = TypeHandleHelpers.fromType(Integer.class);
        TypeHandleHelpers.setCustomColor(INT, 0xFF0C9EFF);
        TypeHandleHelpers.setCustomDefaultValue(INT, () -> 0);
        LONG = TypeHandleHelpers.fromType(Long.class);
        TypeHandleHelpers.setCustomColor(LONG, 0xFF0C9EFF);
        TypeHandleHelpers.setCustomDefaultValue(LONG, () -> 0L);
        STRING = TypeHandleHelpers.fromType(String.class);
        TypeHandleHelpers.setCustomColor(STRING, 0xFFE3890B);
        TypeHandleHelpers.setCustomDefaultValue(STRING, () -> "");

        OBJECT = TypeHandleHelpers.fromType(Object.class);

        COLOR = TypeHandleHelpers.customType(Integer.class, "COLOR", "Color");
        TypeHandleHelpers.setCustomDefaultValue(COLOR, () -> -1);

        DIRECTION = TypeHandleHelpers.fromType(Direction.class);
        TypeHandleHelpers.setCustomColor(DIRECTION, 0xFF5BFF94);
        BLOCK = TypeHandleHelpers.fromType(Block.class);
        TypeHandleHelpers.setCustomDefaultValue(BLOCK, () -> Blocks.STONE);
        ITEM = TypeHandleHelpers.fromType(Item.class);
        TypeHandleHelpers.setCustomDefaultValue(ITEM, () -> Items.AIR);
        FLUID = TypeHandleHelpers.fromType(Fluid.class);
        TypeHandleHelpers.setCustomDefaultValue(FLUID, () -> Fluids.EMPTY);
        ENTITY_TYPE = TypeHandleHelpers.fromType(EntityType.class);
        TypeHandleHelpers.setCustomDefaultValue(ENTITY_TYPE, () -> EntityType.PIG);
        ITEM_STACK = TypeHandleHelpers.fromType(ItemStack.class);
        TypeHandleHelpers.setCustomDefaultValue(ITEM_STACK, () -> ItemStack.EMPTY);
        FLUID_STACK = TypeHandleHelpers.fromType(FluidStack.class);
        TypeHandleHelpers.setCustomDefaultValue(FLUID_STACK, () -> FluidStack.EMPTY);
    }

    public static void init() {}
}
