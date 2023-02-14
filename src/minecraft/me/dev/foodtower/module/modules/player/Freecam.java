/*
Author:SuMuGod
Date:2022/7/10 4:43
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventCollideWithBlock;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.MoveUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class Freecam
        extends Module {
    private EntityOtherPlayerMP copy;
    private double x;
    private double y;
    private double z;

    public Freecam() {
        super("FreeCam", "纵角自缘", new String[]{"outofbody"}, ModuleType.Player);
        this.setColor(new Color(221, 214, 51).getRGB());
    }

    @Override
    public void onEnable() {
        this.copy = new EntityOtherPlayerMP(this.mc.theWorld, this.mc.thePlayer.getGameProfile());
        this.copy.clonePlayer(this.mc.thePlayer, true);
        this.copy.setLocationAndAngles(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.copy.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.copy.setEntityId(-1337);
        this.copy.setSneaking(this.mc.thePlayer.isSneaking());
        this.mc.theWorld.addEntityToWorld(this.copy.getEntityId(), this.copy);
        this.x = this.mc.thePlayer.posX;
        this.y = this.mc.thePlayer.posY;
        this.z = this.mc.thePlayer.posZ;
    }

    @NMSL
    private void onPreMotion(EventPreUpdate e) {
        this.mc.thePlayer.capabilities.isFlying = true;
        this.mc.thePlayer.noClip = true;
        this.mc.thePlayer.capabilities.setFlySpeed(0.1f);
        e.setCancelled(true);
    }

    @NMSL
    private void onPacketSend(EventPacketRecieve e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            e.setCancelled(true);
        }
    }

    @NMSL
    private void onBB(EventCollideWithBlock e) {
        e.setBoundingBox(null);
    }

    @Override
    public void onDisable() {
        MoveUtils.strafe(0.0);
        this.mc.thePlayer.setLocationAndAngles(this.copy.posX, this.copy.posY, this.copy.posZ, this.copy.rotationYaw, this.copy.rotationPitch);
        this.mc.thePlayer.rotationYawHead = this.copy.rotationYawHead;
        this.mc.theWorld.removeEntityFromWorld(this.copy.getEntityId());
        this.mc.thePlayer.setSneaking(this.copy.isSneaking());
        this.copy = null;
        this.mc.renderGlobal.loadRenderers();
        this.mc.thePlayer.setPosition(this.x, this.y, this.z);
        this.mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.01, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
        this.mc.thePlayer.capabilities.isFlying = false;
        this.mc.thePlayer.noClip = false;
        this.mc.theWorld.removeEntityFromWorld(-1);
    }
}

