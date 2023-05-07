/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck.checks;

import java.util.function.Supplier;

import cn.foodtower.api.value.Option;
import cn.foodtower.manager.FriendManager;
import cn.foodtower.util.entity.entitycheck.ICheck;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public final class EntityCheck
implements ICheck {
    private final Option players;
    private final Option animals;
    private final Option monsters;
    private final Option invisibles;

    public EntityCheck(Option players, Option animals, Option monsters, Option invisibles) {
        this.players = players;
        this.animals = animals;
        this.monsters = monsters;
        this.invisibles = invisibles;
    }

    @Override
    public boolean validate(Entity entity) {
        if (entity instanceof EntityPlayerSP) {
            return false;
        }
        if (!this.invisibles.getValue().booleanValue() && entity.isInvisible()) {
            return false;
        }
        if (this.animals.getValue().booleanValue() && entity instanceof EntityAnimal) {
            return true;
        }
        if (this.players.getValue().booleanValue() && entity instanceof EntityPlayer) {
            return !FriendManager.isFriend(entity.getName());
        }
        return this.monsters.getValue() != false && (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityDragon || entity instanceof EntityGolem);
    }
}

