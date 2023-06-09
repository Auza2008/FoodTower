/*
 * Decompiled with CFR 0.150.
 */
package cn.foodtower.util.entity.entitycheck.checks;

import cn.foodtower.api.value.Option;
import cn.foodtower.manager.FriendManager;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.modules.combat.AntiBot;
import cn.foodtower.module.modules.combat.HypixelAntibot;
import cn.foodtower.module.modules.player.Blink;
import cn.foodtower.util.entity.entitycheck.ICheck;
import cn.foodtower.util.math.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public final class EntityCheck implements ICheck {
    private final Option players;
    private final Option animals;
    private final Option monsters;
    private final Option invisibles;
    private final Option wall;

    public EntityCheck(Option players, Option animals, Option monsters, Option invisibles, Option wall) {
        this.players = players;
        this.animals = animals;
        this.monsters = monsters;
        this.invisibles = invisibles;
        this.wall = wall;
    }

    @Override
    public boolean validate(Entity entity) {
        if (entity instanceof EntityPlayerSP) {
            return false;
        }
        if (ModuleManager.getModuleByClass(Blink.class).isEnabled() && entity.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
            return false;
        }
        if (!this.wall.get() && !RotationUtils.canEntityBeSeen(entity)) {
            return false;
        }
        if (ModuleManager.getModuleByClass(AntiBot.class).isEnabled() && AntiBot.isServerBot(entity)) {
            return false;
        }
        if (ModuleManager.getModuleByClass(HypixelAntibot.class).isEnabled() && HypixelAntibot.isServerBot(entity)) {
            return false;
        }
        if (!this.invisibles.get() && entity.isInvisible()) {
            return false;
        }
        if (this.animals.get() && entity instanceof EntityAnimal) {
            return true;
        }
        if (this.players.get() && entity instanceof EntityPlayer) {
            return !FriendManager.isFriend(entity.getName());
        }
        return (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityDragon || entity instanceof EntityGolem);
    }
}

