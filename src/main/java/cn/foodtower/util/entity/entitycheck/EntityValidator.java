/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck;

import net.minecraft.entity.Entity;

import java.util.HashSet;
import java.util.Set;

public final class EntityValidator {
    private final Set<ICheck> checks = new HashSet<>();

    public boolean validate(Entity entity) {
        for (ICheck check : this.checks) {
            if (check.validate(entity)) continue;
            return false;
        }
        return true;
    }

    public void add(ICheck check) {
        this.checks.add(check);
    }
}

