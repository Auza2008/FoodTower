/*
Author:SuMuGod
Date:2022/7/10 4:28
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventCollideWithBlock;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class Jesus
        extends Module {
    public Jesus() {
        super("Jesus", "水行", new String[]{"waterwalk", "float"}, ModuleType.Movement);
        this.setColor(new Color(188, 233, 248).getRGB());
    }

    private boolean canJeboos() {
        if (!(this.mc.thePlayer.fallDistance >= 3.0f || this.mc.gameSettings.keyBindJump.isPressed() || mc.thePlayer.isInLiquid() || this.mc.thePlayer.isSneaking())) {
            return true;
        }
        return false;
    }

    @NMSL
    public void onPre(EventPreUpdate e) {
        if (mc.thePlayer.isInLiquid() && !this.mc.thePlayer.isSneaking() && !this.mc.gameSettings.keyBindJump.isPressed()) {
            this.mc.thePlayer.motionY = 0.05;
            this.mc.thePlayer.onGround = true;
        }
    }

    @NMSL
    public void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer && this.canJeboos() && mc.thePlayer.isOnLiquid()) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            packet.y = this.mc.thePlayer.ticksExisted % 2 == 0 ? packet.y + 0.01 : packet.y - 0.01;
        }
    }

    @NMSL
    public void onBB(EventCollideWithBlock e) {
        if (e.getBlock() instanceof BlockLiquid && this.canJeboos()) {
            e.setBoundingBox(new AxisAlignedBB(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), (double) e.getPos().getX() + 1.0, (double) e.getPos().getY() + 1.0, (double) e.getPos().getZ() + 1.0));
        }
    }
}


