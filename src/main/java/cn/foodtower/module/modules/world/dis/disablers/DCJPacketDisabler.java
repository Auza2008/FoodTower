package cn.foodtower.module.modules.world.dis.disablers;

import cn.foodtower.Client;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.Speed;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import cn.foodtower.util.misc.Helper;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DCJPacketDisabler implements DisablerModule {
    private final List<Packet> listTransactions = new CopyOnWriteArrayList<>();
    private final TimerUtil timer = new TimerUtil();
    private int packetcount;
    private short id;
    private int key;
    private int transactions;
    private float shouldYaw;
    private float shouldPitch;
    private float lastYaw;
    private float lastPitch;

    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static float getyaw() {
        float wrapAngleTo180_float = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        MovementInput movementInput = mc.thePlayer.movementInput;
        float a = movementInput.moveStrafe;
        float b = movementInput.moveForward;
        if (a != 0.0f) {
            if (b < 0.0f) {
                wrapAngleTo180_float += a < 0.0f ? 135.0f : 45.0f;
            } else if (b > 0.0f) {
                wrapAngleTo180_float -= a < 0.0f ? 135.0f : 45.0f;
            } else if (b == 0.0f && a < 0.0f) {
                wrapAngleTo180_float -= 180.0f;
            }
        } else if (b < 0.0f) {
            wrapAngleTo180_float += 90.0f;
        } else if (b > 0.0f) {
            wrapAngleTo180_float -= 90.0f;
        }
        return MathHelper.wrapAngleTo180_float(wrapAngleTo180_float);
    }

    public static float[] getRotate(float needYaw, float needPitch, float yaw, float pitch, float minturnspeed, float maxturnspeed) {
        EventRotation smoothAngle = smoothAngle(new EventRotation(needYaw, needPitch), new EventRotation(yaw, pitch), RandomUtils.nextFloat(minturnspeed, maxturnspeed));
        float[] fArray = new float[2];
        fArray[0] = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(smoothAngle.getYaw() - mc.thePlayer.rotationYaw);
        fArray[1] = smoothAngle.getPitch();
        return fArray;
    }

    private static EventRotation smoothAngle(EventRotation e, EventRotation e2, float turnSpeed) {
        EventRotation angle = new EventRotation(e2.getYaw() - e.getYaw(), e2.getPitch() - e.getPitch()).getAngle();
        angle.setYaw(e2.getYaw() - angle.getYaw() / 180.0f * turnSpeed);
        angle.setPitch(e2.getPitch() - angle.getPitch() / 180.0f * turnSpeed);
        return angle.getAngle();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {
    }

    @Override
    public void onPacket(EventPacketReceive event) {

    }

    @Override
    public void onPacket(EventPacket event) {
        Packet<INetHandlerPlayServer> packet;
        if (event.getPacket() instanceof C0FPacketConfirmTransaction && ((C0FPacketConfirmTransaction) (packet = (C0FPacketConfirmTransaction) event.getPacket())).getUid() < 0) {
            ++this.transactions;
            if (this.transactions > 5) {
                this.id = ((C0FPacketConfirmTransaction) packet).getUid();
                this.listTransactions.add(packet);
                event.setCancelled(true);
            }
        }
        if (event.getPacket() instanceof C00PacketKeepAlive) {
            packet = (C00PacketKeepAlive) event.getPacket();
            this.key = ((C00PacketKeepAlive) packet).getKey();
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof C03PacketPlayer) {
            ++this.packetcount;
        }
    }

    @Override
    public void onUpdate(EventPreUpdate event) {
        if (timer.hasReached(1000)) {
            if (packetcount > 22) {
                Helper.sendMessage("[Disabler] You are sending too many C03 Packet, less sending now. Packetcount: " + packetcount);
            }
            timer.reset();
            packetcount = 0;
        }
        if (mc.gameSettings.keyBindAttack.isKeyDown() || mc.gameSettings.keyBindDrop.isKeyDown() || mc.gameSettings.keyBindUseItem.isKeyDown()) {
            return;
        }
        this.shouldYaw = ModuleManager.getModuleByClass(Speed.class).isEnabled() || ModuleManager.getModByClass(Fly.class).isEnabled() ? (float) Math.toDegrees(getDirection()) : getyaw();
        this.lastYaw = getRotate(this.shouldYaw, this.shouldPitch, this.lastYaw, this.lastPitch, 80.0f, 120.0f)[0];
        this.lastPitch = getRotate(this.shouldYaw, this.shouldPitch, this.lastYaw, this.lastPitch, 80.0f, 120.0f)[1];
        event.setYaw(this.lastYaw);
        event.setPitch(this.lastPitch);
        Client.RenderRotate(lastYaw, lastPitch);
    }

    @Override
    public void onWorldChange(EventWorldChanged event) {
        this.transactions = 0;
        this.id = 0;
        this.key = 0;
        this.listTransactions.clear();
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
        if (mc.thePlayer.ticksExisted % 15 == 0 && !this.listTransactions.isEmpty()) {
            for (Packet i : listTransactions) {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(i);
            }
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C00PacketKeepAlive(this.key));
            this.listTransactions.clear();
        }
    }
}
