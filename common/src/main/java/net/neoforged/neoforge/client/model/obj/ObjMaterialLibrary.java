package net.neoforged.neoforge.client.model.obj;

import org.joml.Vector4f;

public final class ObjMaterialLibrary {
    private ObjMaterialLibrary() {
    }

    public static class Material {
        public int diffuseTintIndex;
        public Vector4f diffuseColor = new Vector4f(1, 1, 1, 1);
        public Vector4f ambientColor = new Vector4f(1, 1, 1, 1);
    }
}
