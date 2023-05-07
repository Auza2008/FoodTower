package cn.foodtower.util.misc.liquidbounce;

import cn.foodtower.util.math.Rotation;

public class VecRotation {
    Vec3 vec;
    Rotation rotation;

    public VecRotation(Vec3 vec, Rotation rotation) {
        this.vec = vec;
        this.rotation = rotation;
    }
    public Rotation getRotation()
    {
        return this.rotation;
    }
}
