/*
Author:SuMuGod
Date:2022/7/10 5:11
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Blink
        extends Module {
    private EntityOtherPlayerMP blinkEntity;
    private List<Packet> packetList = new ArrayList<Packet>();

    public Blink() {
        super("Blink", "连接", new String[]{"blonk"}, ModuleType.Player);
    }

    @Override
    public void onEnable() {
        this.setColor(new Color(200, 100, 200).getRGB());
        if (this.mc.thePlayer == null) {
            return;
        }
        this.blinkEntity = new EntityOtherPlayerMP(this.mc.theWorld, new GameProfile(new UUID(69L, 96L), "Blink"));
        this.blinkEntity.inventory = this.mc.thePlayer.inventory;
        this.blinkEntity.inventoryContainer = this.mc.thePlayer.inventoryContainer;
        this.blinkEntity.setPositionAndRotation(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.blinkEntity.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.mc.theWorld.addEntityToWorld(this.blinkEntity.getEntityId(), this.blinkEntity);
    }

    @NMSL
    private void onPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            this.packetList.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        for (Packet packet : this.packetList) {
            mc.getNetHandler().addToSendQueue(packet);
        }
        this.packetList.clear();
        mc.theWorld.removeEntityFromWorld(this.blinkEntity.getEntityId());
    }
}


