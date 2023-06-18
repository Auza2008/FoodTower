package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketReceive;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.misc.Helper;
import cn.foodtower.util.time.Timer;
import cn.foodtower.util.world.PacketUtils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;

public class AntiFall extends Module {
    public static Numbers<Double> distance = new Numbers<>("Distance", 5.0, 0.0, 10.0, 1.0);
    public static Option Void = new Option("Void", "Void", true);
    public static Option scaffoldvalue = new Option("ToggleScaffold", true);
    private final Timer timer = new Timer();
    private final Mode mode = new Mode("Mode", "Mode", AntiMode.values(), AntiMode.Hypixel);
    boolean needBlink;
    private boolean saveMe;
    private double[] lastGroundPos = new double[3];
    private static ArrayList<C03PacketPlayer> packets = new ArrayList<>();

    public AntiFall() {
        super("AntiFall", new String[]{"AntiVoid"}, ModuleType.Movement);
        this.addValues(this.mode, distance, Void, scaffoldvalue);
    }

    @EventHandler
    private void onUpdate(EventMove e) {
        setSuffix(mode.get());
        NetworkManager networkManager = mc.thePlayer.sendQueue.getNetworkManager();
        if ((saveMe && timer.delay(150)) || mc.thePlayer.isCollidedVertically) {
            saveMe = false;
            needBlink = false;
            timer.reset();
        }
        int dist = distance.get().intValue();
        if (mc.thePlayer.fallDistance >= dist && !mc.thePlayer.capabilities.allowFlying) {
            if (!((Boolean) Void.get()) || !isBlockUnder()) {
                if (!saveMe) {
                    saveMe = true;
                    needBlink = true;
                    switch ((AntiMode) mode.get()) {
                        case Basic:
                            PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                                    mc.thePlayer.posY + 11, mc.thePlayer.posZ, false));
                            break;
                        case FlyFlag:
                            mc.thePlayer.motionY += 0.1;
                            mc.thePlayer.fallDistance = 0F;
                            break;
                    }
                    if (scaffoldvalue.get()) {
                        ModuleManager.getModByClass(Scaffold.class).setEnabled(true);
                    }
                    timer.reset();
                }
            }
        }
    }

    @EventHandler
    public void onPacketSend(EventPacketSend e) {
        if (mode.get().equals(AntiMode.Hypixel)) {
            if (e.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = ((C03PacketPlayer) e.getPacket());
                if (isInVoid()) {
                    e.setCancelled(true);
                    packets.add(packet);
                    if (mc.thePlayer.fallDistance >= distance.get()) {
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1] - 1, lastGroundPos[2], true));
                    }
                } else {
                    lastGroundPos[0] = mc.thePlayer.posX;
                    lastGroundPos[1] = mc.thePlayer.posY;
                    lastGroundPos[2] = mc.thePlayer.posZ;

                    if (!packets.isEmpty()) {
                        Helper.sendMessage("Release Packets - " + packets.size());
                        for (Packet p : packets)
                            PacketUtils.sendPacketNoEvent(p);
                        packets.clear();
                    }
                    timer.reset();
                }
            }
        }
    }

    public static boolean isInVoid() {
        for (int i = 0; i <= 128; i++) {
            if (MoveUtils.isOnGround(i)) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onPacketRe(EventPacketReceive ep) {
        final Packet<?> packet = ep.getPacket();
    }

    @EventHandler
    public void onMove(EventMove e) {
    }

    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0)
            return false;
        for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB bb = mc.thePlayer.boundingBox.offset(0, -off, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    enum AntiMode {
        Hypixel, FlyFlag, Basic
    }

}
