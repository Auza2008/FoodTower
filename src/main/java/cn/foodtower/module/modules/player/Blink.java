package cn.foodtower.module.modules.player;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.render.Breadcrumbs;
import cn.foodtower.module.modules.render.HUD;
import cn.foodtower.util.render.gl.GLUtils;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;

public class Blink extends Module {

    public final Option pulseValue = new Option("Pulse", false);
    private final LinkedBlockingQueue<Packet> packets = new LinkedBlockingQueue<>();
    private final LinkedList<double[]> positions = new LinkedList<>();
    private final Numbers<Double> pulseDelayValue = new Numbers<>("PulseDelay", 1000d, 100d, 5000d, 100d);
    private final MSTimer pulseTimer = new MSTimer();
    private EntityOtherPlayerMP fakePlayer = null;
    private boolean disableLogger;

    public Blink() {
        super("Blink", new String[]{"blink"}, ModuleType.Player);
        this.addValues(pulseValue, pulseDelayValue);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        if (!pulseValue.get()) {
            fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            fakePlayer.clonePlayer(mc.thePlayer, true);
            fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
            fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.theWorld.addEntityToWorld(-1337, fakePlayer);
        }
        synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (mc.thePlayer.getEyeHeight() / 2), mc.thePlayer.posZ});
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }

        pulseTimer.reset();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null)
            return;

        blink();
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }
    }

    @EventHandler
    public void onPacket(EventPacketSend event) {
        final Packet<?> packet = event.getPacket();

        if (mc.thePlayer == null || disableLogger)
            return;

        if (packet instanceof C03PacketPlayer) // Cancel all movement stuff
            event.setCancelled(true);

        if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook ||
                packet instanceof C08PacketPlayerBlockPlacement ||
                packet instanceof C0APacketAnimation ||
                packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity) {
            event.setCancelled(true);

            packets.add(packet);
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }

        if (pulseValue.get() && pulseTimer.hasTimePassed(pulseDelayValue.get().longValue())) {
            blink();
            pulseTimer.reset();
        }
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        final Breadcrumbs breadcrumbs = (Breadcrumbs) ModuleManager.getModuleByClass(Breadcrumbs.class);
        final Color color = breadcrumbs.colorRainbow.get() ? HUD.RainbowColor : new Color(breadcrumbs.colorRedValue.get().intValue(), breadcrumbs.colorGreenValue.get().intValue(), breadcrumbs.colorBlueValue.get().intValue());

        synchronized (positions) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            mc.entityRenderer.disableLightmap();
            glBegin(GL_LINE_STRIP);
            GLUtils.glColor(color.getRGB());
            final double renderPosX = mc.getRenderManager().viewerPosX;
            final double renderPosY = mc.getRenderManager().viewerPosY;
            final double renderPosZ = mc.getRenderManager().viewerPosZ;

            for (final double[] pos : positions)
                glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);

            glColor4d(1, 1, 1, 1);
            glEnd();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        }
    }

    @EventHandler
    private void onRender2d() {
        this.setSuffix("Packet:" + packets.size());
    }

    private void blink() {
        try {
            disableLogger = true;

            while (!packets.isEmpty()) {
                mc.getNetHandler().getNetworkManager().sendPacket(packets.take());
            }

            disableLogger = false;
        } catch (final Exception e) {
            e.printStackTrace();
            disableLogger = false;
        }

        synchronized (positions) {
            positions.clear();
        }
    }
}
