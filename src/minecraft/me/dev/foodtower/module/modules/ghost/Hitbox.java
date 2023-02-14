/*
Author:SuMuGod
Date:2022/7/10 4:25
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.ghost;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.EntitySize;
import me.dev.foodtower.value.Numbers;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;
import java.util.ArrayList;

public class Hitbox extends Module {
    private final Numbers<Double> heights = new Numbers<>("Height", "Height", 2.0, 2.0, 5.0, 0.1);
    private final Numbers<Double> Widths = new Numbers<>("Width", "Width", 1.0, 1.0, 5.0, 0.1);

    public Hitbox() {
        super("Hitbox", "触盒", new String[]{"hb"}, ModuleType.Ghost);
        this.setColor(new Color(0, 219, 255).getRGB());
    }

    public static void setEntityBoundingBoxSize(Entity entity, float width, float height) {
        EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        double d0 = (double) (width) / 2.0D;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0,
                entity.posY + (double) height, entity.posZ + d0));
    }

    public static void setEntityBoundingBoxSize(Entity entity) {
        EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        double d0 = (double) (entity.width) / 2.0D;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0,
                entity.posY + (double) entity.height, entity.posZ + d0));
    }

    public static EntitySize getEntitySize(Entity entity) {
        EntitySize entitySize = new EntitySize(0.6F, 1.8F);
        return entitySize;
    }

    public static ArrayList<EntityPlayer> getPlayersList() {
        return (ArrayList<EntityPlayer>) mc.theWorld.playerEntities;
    }

    public boolean check(EntityLivingBase entity) {
        if (entity instanceof EntityPlayerSP) {
            return false;
        }
        if (entity == mc.thePlayer) {
            return false;
        }
        return !entity.isDead;
    }

    @NMSL
    public void onClientTick(EventTick event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        for (EntityPlayer player : getPlayersList()) {
            if (!check(player)) continue;
            float width = this.Widths.getValue().floatValue();
            float height = this.heights.getValue().floatValue();
            setEntityBoundingBoxSize(player, width, height);
        }
    }

    @Override
    public void onDisable() {
        for (EntityPlayer player : getPlayersList())
            setEntityBoundingBoxSize(player);
    }

}


