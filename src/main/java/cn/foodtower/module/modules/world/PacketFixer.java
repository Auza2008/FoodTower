/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPacket;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.player.Blink;
import cn.foodtower.module.modules.render.Freecam;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class PacketFixer extends Module {
    // settings
    private final Option fixBlinkAndFreecam = new Option("BlinkFreeCam3Y", true);
    private final Option fixPacketPlayer = new Option("Timer3A", true);
    private final Option fixItemSwap = new Option("Scaffold14D", true);
    private final Option fixInvalidPlace = new Option("Scaffold14E", true);
    private final Option fixGround = new Option("Fly4I", false);
    private final Option fixIdleFly = new Option("Fly4C", false);
    private double x = 0.0;
    private double y = 0.0;
    private double z = 0.0;
    private float yaw = 0.0F;
    private float pitch = 0.0F;
    private int jam = 0;
    private int packetCount = 0;
    private int prevSlot = -1;

    public PacketFixer() {
        super("PacketFixer", new String[]{"ViaFix"}, ModuleType.World);
        addValues(fixBlinkAndFreecam, fixPacketPlayer, fixItemSwap, fixInvalidPlace, fixGround, fixIdleFly);
    }

    @Override
    public void onEnable() {
        jam = 0;
        packetCount = 0;
        prevSlot = -1;

        if (mc.thePlayer == null) return;
        x = mc.thePlayer.posX;
        y = mc.thePlayer.posY;
        z = mc.thePlayer.posZ;
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;
    }

    @EventHandler(priority = 1)
    private void onPacket(EventPacket event) {
        if (mc.thePlayer == null || mc.theWorld == null || event.isCancelled()) return;

        Packet packet = event.packet;

        // fix ground check (4I)
        if (fixGround.get() && packet instanceof C03PacketPlayer && !(packet instanceof C04PacketPlayerPosition) && !(packet instanceof C06PacketPlayerPosLook)) {
            if ((mc.thePlayer.motionY == 0.0 || (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically)) && !((C03PacketPlayer) packet).onGround)
                ((C03PacketPlayer) packet).onGround = true;
        }

        if (packet instanceof C04PacketPlayerPosition) {
            x = ((C04PacketPlayerPosition) packet).x;
            y = ((C04PacketPlayerPosition) packet).y;
            z = ((C04PacketPlayerPosition) packet).z;
            jam = 0;
        }

        if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
            yaw = ((C03PacketPlayer.C05PacketPlayerLook) packet).yaw;
            pitch = ((C03PacketPlayer.C05PacketPlayerLook) packet).pitch;
        }

        if (packet instanceof C06PacketPlayerPosLook) {
            x = ((C06PacketPlayerPosLook) packet).x;
            y = ((C06PacketPlayerPosLook) packet).y;
            z = ((C06PacketPlayerPosLook) packet).z;
            jam = 0;

            yaw = ((C06PacketPlayerPosLook) packet).yaw;
            pitch = ((C06PacketPlayerPosLook) packet).pitch;
        }

        // fix bad packets, caused by timer or fast use
        if (fixPacketPlayer.get() && packet instanceof C03PacketPlayer && !(packet instanceof C04PacketPlayerPosition) && !(packet instanceof C06PacketPlayerPosLook)) {
            jam++;
            if (jam > 20) {
                jam = 0;
                event.setCancelled(true);
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C06PacketPlayerPosLook(x, y, z, yaw, pitch, ((C03PacketPlayer) packet).onGround));
            }
        }

        // fix scaffold duplicated hotbar switch
        if (!mc.isSingleplayer() && fixItemSwap.get() && packet instanceof C09PacketHeldItemChange) {
            if (((C09PacketHeldItemChange) packet).getSlotId() == prevSlot) {
                event.setCancelled(true);
            } else {
                prevSlot = ((C09PacketHeldItemChange) packet).getSlotId();
            }
        }

        if (fixInvalidPlace.get() && packet instanceof C08PacketPlayerBlockPlacement) {
            if (((C08PacketPlayerBlockPlacement) packet).facingX > 1.00000F) {
                ((C08PacketPlayerBlockPlacement) packet).facingX = 1.00000F;
            } else if (((C08PacketPlayerBlockPlacement) packet).facingX < -1.00000F) {
                ((C08PacketPlayerBlockPlacement) packet).facingX = -1.00000F;
            }
            if (((C08PacketPlayerBlockPlacement) packet).facingY > 1.00000F) {
                ((C08PacketPlayerBlockPlacement) packet).facingY = 1.00000F;
            } else if (((C08PacketPlayerBlockPlacement) packet).facingY < -1.00000F) {
                ((C08PacketPlayerBlockPlacement) packet).facingY = -1.00000F;
            }
            if (((C08PacketPlayerBlockPlacement) packet).facingZ > 1.00000F) {
                ((C08PacketPlayerBlockPlacement) packet).facingZ = 1.00000F;
            } else if (((C08PacketPlayerBlockPlacement) packet).facingZ < -1.00000F) {
                ((C08PacketPlayerBlockPlacement) packet).facingZ = -1.00000F;
            }
        }

        // fix blink and freecam cancelling c03s while sending c00
        Blink blink = (Blink) ModuleManager.getModuleByClass(Blink.class);
        Freecam freeCam = (Freecam) ModuleManager.getModuleByClass(Freecam.class);
        if (fixBlinkAndFreecam.get() && ((blink.isEnabled() && !blink.pulseValue.get())) && packet instanceof C00PacketKeepAlive)
            event.setCancelled(true);

        // fix fly while not moving, reduce some checks (4C)
        if (fixIdleFly.get() && packet instanceof C03PacketPlayer && !((C03PacketPlayer) packet).onGround) {
            if (!(packet instanceof C04PacketPlayerPosition) && !(packet instanceof C03PacketPlayer.C05PacketPlayerLook) && !(packet instanceof C06PacketPlayerPosLook)) {
                packetCount++;
                if (packetCount >= 2) event.setCancelled(true);
            } else {
                packetCount = 0;
            }
        }
    }

}