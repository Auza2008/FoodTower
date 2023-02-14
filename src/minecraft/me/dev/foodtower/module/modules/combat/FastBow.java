/*
Author:SuMuGod
Date:2022/7/10 4:10
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class FastBow
        extends Module {
    int counter;
    private final Option<Boolean> faithful = new Option<>("Faithful", "faithful", true);

    public FastBow() {
        super("FastBow", "疾骤弓", new String[]{"zoombow", "quickbow"}, ModuleType.Combat);
        this.setColor(new Color(255, 255, 255).getRGB());
        this.counter = 0;
    }

    private boolean canConsume() {
        return mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow;
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        if (this.faithful.getValue()) {
            if (mc.thePlayer.onGround && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && mc.gameSettings.keyBindUseItem.isPressed()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                int index = 0;
                while (index < 16) {
                    if (!mc.thePlayer.isDead) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.09, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                    }
                    ++index;
                }
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            if (mc.thePlayer.onGround && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && mc.gameSettings.keyBindUseItem.isPressed()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 12);
                    ++this.counter;
                    if (this.counter > 0) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0, mc.thePlayer.posZ, mc.thePlayer.onGround));
                        this.counter = 0;
                    }
                    int dist = 20;
                    int index = 0;
                    while (index < dist) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12, mc.thePlayer.posZ, mc.thePlayer.onGround));
                        ++index;
                    }
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    Minecraft.playerController.onStoppedUsingItem(mc.thePlayer);
                }
            }
        } else if (mc.thePlayer.onGround && this.canConsume() && mc.gameSettings.keyBindUseItem.isPressed()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            int i = 0;
            while (i < 20) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-9, mc.thePlayer.posZ, true));
                ++i;
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        } else {
            mc.timer.timerSpeed = 1.0f;
        }
    }

    @NMSL
    public void onRecieve(EventPacketRecieve event) {
        try {
            if (event.getPacket() instanceof S18PacketEntityTeleport) {
                S18PacketEntityTeleport packet = (S18PacketEntityTeleport) event.getPacket();
                if (mc.thePlayer != null) {
                    packet.yaw = (byte) mc.thePlayer.rotationYaw;
                }
                assert mc.thePlayer != null;
                packet.pitch = (byte) mc.thePlayer.rotationPitch;
            }
        } catch (NullPointerException ignored) {

        }
    }
}

