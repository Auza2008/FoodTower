/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck.checks;

import cn.foodtower.util.entity.entitycheck.ICheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public final class ConstantDistanceCheck
        implements ICheck {
    private final float distance;

    public ConstantDistanceCheck(float distance) {
        this.distance = distance;
    }

    @Override
    public boolean validate(Entity entity) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= this.distance;
    }
}

