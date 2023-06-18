/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck;

import net.minecraft.entity.Entity;

@FunctionalInterface
public interface ICheck {
    boolean validate(Entity var1);
}

