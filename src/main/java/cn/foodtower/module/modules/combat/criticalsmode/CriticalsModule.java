package cn.foodtower.module.modules.combat.criticalsmode;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.Module;
import cn.foodtower.module.modules.combat.Criticals;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public abstract class CriticalsModule {
    public Minecraft mc = Minecraft.getMinecraft();

    public void Crit(Double[] value, Boolean onGround) {
        double curX = mc.thePlayer.posX;
        double curY = mc.thePlayer.posY;
        double curZ = mc.thePlayer.posZ;
        for (double offset : value) {
            if (!Criticals.C06.get()) {
                Module.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(curX, curY + offset, curZ, onGround));
            } else {
                Module.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(curX, curY + offset, curZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, onGround));
            }
        }
    }

    public abstract void onEnabled();

    public abstract void onDisable();

    public abstract void onAttack(EventAttack e);

    public abstract void onPacketSend(EventPacketSend e);

    public abstract void onMove(EventMove e);

    public abstract void onUpdate(EventMotionUpdate e);
}
