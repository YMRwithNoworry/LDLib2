package com.lowdragmc.lowdraglib2.forge.core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.lowdragmc.lowdraglib2.forge.core.mixins.accessor.ForgeObjModelAccessor;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Mixin(targets = "net.minecraftforge.client.model.obj.ObjModel$ModelMesh", remap = false)
public abstract class ForgeObjModelMixin {

    @Shadow @Final ObjModel this$0;

    @Shadow @Nullable public ObjMaterialLibrary.@Nullable Material mat;

    @Inject(method = "bake", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/client/model/obj/ObjModel;makeQuad([[IILorg/joml/Vector4f;Lorg/joml/Vector4f;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/math/Transformation;)Lorg/apache/commons/lang3/tuple/Pair;"))
    private void ldlib2$bake(CompositeRenderable.PartBuilder<?> builder,
                             IGeometryBakingContext configuration,
                             CallbackInfo ci,
                             @Local List<BakedQuad> quads,
                             @Local int[][] face) {
        if (this$0 instanceof ForgeObjModelAccessor model && mat != null) {
            var left = ldlib2$getLeftFaces(face);
            if (left.length >= 3) {
                var tintIndex = mat.diffuseTintIndex;
                var colorTint = mat.diffuseColor;
                for (int[][] splitFaces : ldlib2$splitFaces(left)) {
                    var quad = model.invokeMakeQuad(splitFaces, tintIndex, colorTint, mat.ambientColor, UnitTextureAtlasSprite.INSTANCE, Transformation.identity());
                    quads.add(quad.getLeft());
                }
            }
        }
    }

    @Inject(method = "addQuads", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/client/model/obj/ObjModel;makeQuad([[IILorg/joml/Vector4f;Lorg/joml/Vector4f;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/math/Transformation;)Lorg/apache/commons/lang3/tuple/Pair;"))
    private void ldlib2$addQuads(IGeometryBakingContext owner,
                                 IModelBuilder<?> modelBuilder,
                                 Function<Material, TextureAtlasSprite> spriteGetter,
                                 ModelState modelTransform,
                                 CallbackInfo ci,
                                 @Local int[][] face,
                                 @Local TextureAtlasSprite texture,
                                 @Local(name = "transform") Transformation transform) {
        if (this$0 instanceof ForgeObjModelAccessor model && mat != null) {
            var left = ldlib2$getLeftFaces(face);
            if (left.length >= 3) {
                var tintIndex = mat.diffuseTintIndex;
                var colorTint = mat.diffuseColor;
                for (int[][] splitFaces : ldlib2$splitFaces(left)) {
                    var quad = model.invokeMakeQuad(splitFaces, tintIndex, colorTint, mat.ambientColor, texture, transform);
                    if (quad.getRight() == null) {
                        modelBuilder.addUnculledFace(quad.getLeft());
                    } else {
                        modelBuilder.addCulledFace(quad.getRight(), quad.getLeft());
                    }
                }
            }
        }
    }

    @Unique
    private int[][] ldlib2$getLeftFaces(int[][] face) {
        if (face.length <= 4) {
            return new int[0][];
        }
        var left = new int[face.length - 2][];
        left[0] = face[3];
        System.arraycopy(face, 4, left, 1, face.length - 4);
        left[left.length - 1] = face[0];
        return left;
    }

    @Unique
    private List<int[][]> ldlib2$splitFaces(int[][] face) {
        int n = face.length;
        List<int[][]> parts = new ArrayList<>();

        if (n <= 4) {
            parts.add(Arrays.copyOf(face, n));
            return parts;
        }

        int remainder = n % 4;
        int limit = n - remainder;

        for (int i = 0; i < limit; i += 4) {
            parts.add(Arrays.copyOfRange(face, i, i + 4));
        }

        switch (remainder) {
            case 3 -> parts.add(Arrays.copyOfRange(face, limit, n));
            case 2 -> parts.add(new int[][] {
                    face[limit - 1], face[limit], face[limit + 1], face[0]
            });
            case 1 -> parts.add(new int[][] {
                    face[limit - 1], face[limit], face[0]
            });
            default -> { }
        }

        return parts;
    }
}
