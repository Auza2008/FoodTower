package cn.foodtower.module.modules.world.dis.disablers;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DCJNetWorkDisabler implements DisablerModule {
    public static boolean isDis = false;
    public static boolean enable = true;
    private final List<C00PacketKeepAlive> c00s = new ArrayList<>();
    private final List<Packet> packetList = new CopyOnWriteArrayList<Packet>();
    private final TimerUtil timer = new TimerUtil();

    @Override
    public void onDisable() {
        timer.reset();
        if (mc.thePlayer == null) return;
        c00s.clear();
        isDis = false;
        enable = true;
    }

    @Override
    public void onEnabled() {
        timer.reset();
        if (mc.thePlayer == null) return;
        c00s.clear();
        isDis = false;
        enable = true;
    }

    @Override
    public void onPacket(EventPacketSend event) {
        if (mc.thePlayer.ticksExisted <= 1) {
            isDis = false;
        }
        if (event.getPacket() instanceof C03PacketPlayer && !isDis) {
            //event.setCancelled(true);
        }
    }

    @Override
    public void onPacket(EventPacketReceive event) {

    }

    @Override
    public void onPacket(EventPacket event) {

    }

    @Override
    public void onUpdate(EventPreUpdate event) {
        if (timer.hasReached(4500)) {
            isDis = true;
            timer.reset();
            enable = false;
        }
        if (!isDis) {
            this.mc.thePlayer.motionX = 0.0;
            this.mc.thePlayer.motionZ = 0.0;
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.11111111122, mc.thePlayer.posZ, false));
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0820525, mc.thePlayer.posZ, true));
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0000525, mc.thePlayer.posZ, true));
            this.mc.getNetHandler().getNetworkManager().sendPacket(new C00PacketKeepAlive(10000));
        }
    }

    @EventHandler
    public void onUpdatepo(EventPostUpdate event) {
        if (!isDis) {
            this.mc.thePlayer.motionX = 0.0;
            this.mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onWorldChange(EventWorldChanged event) {

    }

    @Override
    public void onRender2d(EventRender2D event) {

    }

    @Override
    public void onRender3d(EventRender3D event) {

    }

    @Override
    public void onMotionUpdate(EventMotionUpdate event) {

    }

    @Override
    public void onTick(EventTick event) {

    }
}
