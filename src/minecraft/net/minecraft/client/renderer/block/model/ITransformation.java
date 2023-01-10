package net.minecraft.client.renderer.block.model;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.vecmath.Matrix4f;

public interface ITransformation
{
    Matrix4f getMatrix();

    EnumFacing rotate(EnumFacing facing);

    int rotate(EnumFacing facing, int var);
}
