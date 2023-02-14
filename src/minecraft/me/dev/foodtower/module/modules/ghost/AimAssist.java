/*
Author:SuMuGod
Date:2022/7/10 4:22
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.ghost;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.AntiBot;
import me.dev.foodtower.module.modules.world.Teams;
import me.dev.foodtower.other.FriendManager;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.realms.RealmsMth;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class AimAssist extends Module {
    private final Numbers<Double> Reach = new Numbers<>("Reach", "Reach", 4.5, 3.0, 6.0, 0.1);
    private final Numbers<Double> yaw = new Numbers<>("Yaw", "Yaw", 15.0, 1.0, 50.0, 0.1);
    private final Numbers<Double> pitch = new Numbers<>("Pitch", "Pitch", 15.0, 1.0, 50.0, 0.1);
    private final Option<Boolean> magnetism = new Option<>("OnClick", "OnClick", true);

    public EntityLivingBase target;

    public AimAssist() {
        super("AimAssist", "仪以佐", new String[]{"aim", "aimbot"}, ModuleType.Ghost);
        this.setColor(new Color(255, 149, 113).getRGB());
    }

    public static void assistFaceEntity(Entity entity, float yaw, float pitch) {
        double yDifference;
        if (entity == null) {
            return;
        }
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            yDifference = entityLivingBase.posY + (double) entityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        } else {
            yDifference = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        }
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float rotationYaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float rotationPitch = (float) (-(Math.atan2(yDifference, dist) * 180.0 / Math.PI));
        if (yaw > 0.0f) {
            mc.thePlayer.rotationYaw = updateRotation(mc.thePlayer.rotationYaw, rotationYaw, yaw / 4.0f);
        }
        if (pitch > 0.0f) {
            mc.thePlayer.rotationPitch = updateRotation(mc.thePlayer.rotationPitch, rotationPitch, pitch / 4.0f);
        }
    }

    public static float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
        float var4 = RealmsMth.wrapDegrees(p_70663_2_ - p_70663_1_);
        if (var4 > p_70663_3_) {
            var4 = p_70663_3_;
        }
        if (var4 < -p_70663_3_) {
            var4 = -p_70663_3_;
        }
        return p_70663_1_ + var4;
    }

    public static ArrayList<Entity> getEntityList() {
        return (ArrayList<Entity>) mc.theWorld.getLoadedEntityList();
    }

    @Override
    public void onDisable() {
        this.target = null;
    }

    @NMSL
    public void onTick(EventTick event) {
        if (!this.isEnabled())
            return;
        this.setSuffix(this.yaw.getValue().intValue() + "F");
        if (this.magnetism.getValue()) {
            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                this.updateTarget();
                assistFaceEntity(this.target, this.yaw.getValue().floatValue(), this.pitch.getValue().floatValue());
                this.target = null;
            }
        } else {
            this.updateTarget();
            assistFaceEntity(this.target, this.yaw.getValue().floatValue(), this.pitch.getValue().floatValue());
            this.target = null;
        }
    }

    void updateTarget() {
        try {
            for (Entity object : (ArrayList<Entity>) getEntityList()) {
                EntityLivingBase entity;
                if (!(object instanceof EntityLivingBase) || !this.check(entity = (EntityLivingBase) object)) continue;
                this.target = entity;
            }
        } catch (NullPointerException | ConcurrentModificationException Ex) {
//            Helper.sendMessage("傻逼吧你");
        }
    }

    public boolean check(EntityLivingBase entity) {
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (entity == mc.thePlayer) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (entity.getHealth() < 0)
            return false;
        if (AntiBot.isServerBot(entity)) {
            return false;
        }
        if (Teams.isOnSameTeam(target))
            return false;
        if (entity.getDistanceToEntity(mc.thePlayer) > this.Reach.getValue()) {
            return false;
        }
        if (FriendManager.isFriend(entity.getName()))
            return false;
        return mc.thePlayer.canEntityBeSeen(entity);
    }
}

