package cn.foodtower.module.modules.move.flymode.fly.server;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.flymode.FlyModule;
import cn.foodtower.util.time.TickTimer;
import net.minecraft.network.play.client.C03PacketPlayer;

public class DCJFly implements FlyModule {
    private final TickTimer timer = new TickTimer();

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventPreUpdate e) {
//        fly.antiDesync = true;
        mc.timer.timerSpeed = Fly.dcjTimer.get().floatValue();
        timer.update();
        if (timer.hasTimePassed(2)) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
            timer.reset();
        }
    }

    @Override
    public void onMotionUpdate(EventMotionUpdate e) {

    }

    @Override
    public void onPostUpdate(EventPostUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            packet.onGround = false;
        }
    }

    @Override
    public void onPacketReceive(EventPacketReceive e) {

    }

    @Override
    public void onStep(EventStep e) {
        e.setStepHeight(0f);
    }
}
