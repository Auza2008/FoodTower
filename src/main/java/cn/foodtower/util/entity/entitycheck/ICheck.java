/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck;

import net.minecraft.entity.Entity;

@FunctionalInterface
public interface ICheck {
    public boolean validate(Entity var1);
}

