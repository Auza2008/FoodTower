package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventLoadWorld;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.movement.Scaffold;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.utils.normal.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;

public class Disabler extends Module {
    public Disabler() {
        super("Disabler", "禁用器", new String[]{"dis"}, ModuleType.World);
    }

    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil timer1 = new TimerUtil();
    private final TimerUtil timer2 = new TimerUtil();
    private final ArrayList<Packet> packets = new ArrayList<>();
    private boolean cancel;

    @NMSL
    private void onWorldLoad(EventLoadWorld e) {
        timer.reset();
    }

    @NMSL
    private void onTick(EventTick e) {
        if (timer1.hasTimeElapsed(10000, true)) {
            cancel = true;
            timer2.reset();
        }
    }

    @NMSL
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            if (mc.thePlayer.ticksExisted < 50) {
                e.setCancelled(true);
            }
        }
        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer c03 = (C03PacketPlayer) e.getPacket();
            if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
                e.setCancelled(true);
            }
            if (cancel) {
                if (!timer2.hasTimeElapsed(400, false)) {
                    if(!Client.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
                        e.setCancelled(true);
                        packets.add(e.getPacket());
                    }
                } else {
                    packets.forEach(PacketUtils::sendPacketNoEvent);
                    packets.clear();
                    cancel = false;
                }
            }
        }
    }

    @NMSL
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(packet.getX(), packet.getY(), packet.getZ(), false));
            mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
            mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
            mc.thePlayer.prevPosX = mc.thePlayer.posX;
            mc.thePlayer.prevPosY = mc.thePlayer.posY;
            mc.thePlayer.prevPosZ = mc.thePlayer.posZ;
            mc.displayGuiScreen(null);
            e.setCancelled(true);
        }
    }
}
