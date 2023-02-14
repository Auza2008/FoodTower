/*
Author:SuMuGod
Date:2022/7/10 4:45
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.MoveUtils;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class NoFall extends Module {
    public NoFall() {
        super("NoFall", "无坠地伤", new String[]{"NoFall"}, ModuleType.Player);
    }
    private double getLastTickYDistance() {
        return Math.hypot(mc.thePlayer.posY - mc.thePlayer.prevPosY, mc.thePlayer.posY - mc.thePlayer.prevPosY);
    }

    @NMSL
    public void onPacket(EventPacketSend event) {
        if (mc.thePlayer.posY > 0 && mc.thePlayer.fallDistance >= 2 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0) {
            if (!MoveUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !MoveUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                return;
            }

            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();

                if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    event.setCancelled(true);
                }
            }

            if (event.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                if (packet.isMoving() && packet.rotating) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, packet.isOnGround()));
                    event.setCancelled(true);
                }
            }
        }
    }

    @NMSL
    public void onMotion(EventPreUpdate event) {
        if (mc.thePlayer.posY > 0 && mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0 && mc.thePlayer.motionY != 0 && mc.thePlayer.fallDistance >= 2.5) {
            if (!MoveUtils.isBlockUnder() || mc.thePlayer.fallDistance > 255 || !MoveUtils.isBlockUnder() && mc.thePlayer.fallDistance > 50) {
                return;
            }
            if (mc.thePlayer.fallDistance > 10 || mc.thePlayer.ticksExisted % 2 == 0) {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                mc.timer.timerSpeed = 1.0F;
            }
        }
    }
}
