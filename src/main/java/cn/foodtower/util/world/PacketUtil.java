package cn.foodtower.util.world;


import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPacketReceive;
import cn.foodtower.api.events.World.EventTick;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

import java.util.ArrayList;

public class PacketUtil {

    private static final ArrayList<Packet<?>> packets = new ArrayList<Packet<?>>();
    private static final MSTimer packetTimer = new MSTimer();
    private static final MSTimer wdTimer = new MSTimer();
    public static int inBound, outBound = 0;
    public static int avgInBound, avgOutBound = 0;
    static Minecraft mc = Minecraft.getMinecraft();
    private static int transCount = 0;
    private static int wdVL = 0;

    private static boolean isInventoryAction(short action) {
        return action > 0 && action < 100;
    }

    public static boolean isWatchdogActive() {
        return wdVL >= 8;
    }

    private static void handlePacket(Packet<?> packet) {
        if (packet.getClass().getSimpleName().startsWith("C")) outBound++;
        else if (packet.getClass().getSimpleName().startsWith("S")) inBound++;

        if (packet instanceof S32PacketConfirmTransaction) {
            if (!isInventoryAction(((S32PacketConfirmTransaction) packet).getActionNumber()))
                transCount++;
        }
    }

    public static void sendPacketNoEvent(final Packet<?> packet) {
        packets.add(packet);
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static boolean handleSendPacket(Packet<?> packet) {
        if (packets.contains(packet)) {
            packets.remove(packet);
            handlePacket(packet);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPacket(EventPacketReceive event) {
        handlePacket(event.getPacket());
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (packetTimer.hasTimePassed(1000L)) {
            avgInBound = inBound;
            avgOutBound = outBound;
            inBound = outBound = 0;
            packetTimer.reset();
        }
        if (mc.thePlayer == null || mc.theWorld == null) {
            //reset all checks
            wdVL = 0;
            transCount = 0;
            wdTimer.reset();
        } else if (wdTimer.hasTimePassed(100L)) {
            wdVL += (transCount > 0) ? 1 : -1;
            transCount = 0;
            if (wdVL > 10) wdVL = 10;
            if (wdVL < 0) wdVL = 0;
            wdTimer.reset();
        }
    }

    /**
     * @return wow
     */

    public boolean handleEvents() {
        return true;
    }

}