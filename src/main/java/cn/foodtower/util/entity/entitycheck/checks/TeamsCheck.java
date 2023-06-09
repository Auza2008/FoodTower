/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck.checks;

import cn.foodtower.api.value.Option;
import cn.foodtower.module.modules.world.Teams;
import cn.foodtower.util.entity.entitycheck.ICheck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public final class TeamsCheck
implements ICheck {
    private final Option teams;

    public TeamsCheck(Option teams) {
        this.teams = teams;
    }

    @Override
    public boolean validate(Entity entity) {
        return !(entity instanceof EntityPlayer) || !Teams.isOnSameTeam((EntityPlayer)entity) || this.teams.get() == false;
    }
}

