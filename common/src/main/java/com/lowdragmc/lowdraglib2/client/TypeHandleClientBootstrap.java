package com.lowdragmc.lowdraglib2.client;

import com.lowdragmc.lowdraglib2.configurator.IConfigurable;
import com.lowdragmc.lowdraglib2.configurator.ui.ColorConfigurator;
import com.lowdragmc.lowdraglib2.gui.texture.Icons;
import com.lowdragmc.lowdraglib2.nodegraphtookit.api.type.TypeHandleHelpers;
import com.lowdragmc.lowdraglib2.nodegraphtookit.api.type.TypeHandles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class TypeHandleClientBootstrap {
    private TypeHandleClientBootstrap() {
    }

    public static void init() {
        TypeHandleHelpers.setCustomIcon(TypeHandles.MISSING_PORT, Icons.ALERT.copy().setColor(0xFFFF3B30));
        TypeHandleHelpers.setCustomIcon(TypeHandles.BOOL, Icons.BOOL.copy().setColor(0xFF8c85ff));
        TypeHandleHelpers.setCustomIcon(TypeHandles.DOUBLE, Icons.FLOAT.copy().setColor(0xFF10B4C5));
        TypeHandleHelpers.setCustomIcon(TypeHandles.FLOAT, Icons.FLOAT.copy().setColor(0xFF10B4C5));
        TypeHandleHelpers.setCustomIcon(TypeHandles.INT, Icons.INT.copy().setColor(0xFF0C9EFF));
        TypeHandleHelpers.setCustomIcon(TypeHandles.LONG, Icons.LONG.copy().setColor(0xFF0C9EFF));
        TypeHandleHelpers.setCustomIcon(TypeHandles.STRING, Icons.STRING.copy().setColor(0xFFE3890B));
        TypeHandleHelpers.setCustomIcon(TypeHandles.COLOR, Icons.COLOR);
        TypeHandleHelpers.setCustomConfigurable(TypeHandles.COLOR, (valueConfigurable, typeHandle) ->
                IConfigurable.create(group -> group.addConfigurator(new ColorConfigurator("",
                        valueConfigurable::getValue, valueConfigurable::setValue, -1,
                        valueConfigurable.forceUpdate()))));
        TypeHandleHelpers.setCustomIcon(TypeHandles.DIRECTION, Icons.MOVE.copy().setColor(0xFF5BFF94));
    }
}
