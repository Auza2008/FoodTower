package cn.foodtower.module.modules.move.flymode.fly.anticheat;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.flymode.FlyModule;
import cn.foodtower.util.entity.MoveUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.ArrayList;

public class AAC5Fly implements FlyModule {
    private final ArrayList<C03PacketPlayer> cacheList = new ArrayList<>();
    private boolean blockC03 = false;

    @Override
    public void onEnabled() {
        blockC03 = true;
    }

    @Override
    public void onDisable() {
        sendC03();
        blockC03 = false;
    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventPreUpdate e) {
        double vanillaSpeed = Fly.speed.get(); // idk why called it timer

        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) mc.thePlayer.motionY += vanillaSpeed;
        if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.thePlayer.motionY -= vanillaSpeed;
        MoveUtils.strafe(vanillaSpeed);
    }

    @Override
    public void onMotionUpdate(EventMotionUpdate e) {

    }

    @Override
    public void onPostUpdate(EventPostUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        Packet packet = e.getPacket();
        if (blockC03 && packet instanceof C03PacketPlayer) {

        }
    }

    @Override
    public void onPacketReceive(EventPacketReceive e) {

    }

    @Override
    public void onStep(EventStep e) {

    }

    private void sendC03() {
        blockC03 = false;
        for (C03PacketPlayer packet : cacheList) {
            mc.getNetHandler().addToSendQueue(packet);
            if (packet.isMoving()) {
                mc.getNetHandler().addToSendQueue((new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(), 1e+159, packet.getPositionZ(), true)));
                mc.getNetHandler().addToSendQueue((new C03PacketPlayer.C04PacketPlayerPosition(packet.getPositionX(), packet.getPositionY(), packet.getPositionZ(), true)));
            }
        }
        cacheList.clear();
        blockC03 = true;
    }
}
