/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck.checks;

import cn.foodtower.api.value.Numbers;
import cn.foodtower.util.entity.entitycheck.ICheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public final class DistanceCheck
implements ICheck {
    private final Numbers distance;

    public DistanceCheck(Numbers distance) {
        this.distance = distance;
    }

    @Override
    public boolean validate(Entity entity) {
        return (double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= (Double)this.distance.get();
    }
}

