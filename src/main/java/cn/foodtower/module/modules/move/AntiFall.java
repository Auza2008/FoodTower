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
import cn.foodtower.util.time.Timer;
import cn.foodtower.util.world.PacketUtils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.apache.commons.lang3.RandomUtils;

public class AntiFall extends Module {
    public static Numbers<Double> distance = new Numbers<>("Distance", 5.0, 0.0, 10.0, 1.0);
    public static Option Void = new Option("Void", "Void", true);
    public static Option scaffoldvalue = new Option("ToggleScaffold", true);
    private final Timer timer = new Timer();
    private final Mode mode = new Mode("Mode", "Mode", AntiMode.values(), AntiMode.Hypixel);
    boolean needBlink;
    private boolean saveMe;

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
                        case Hypixel:
                            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + distance.get().intValue() + 3 + RandomUtils.nextDouble(0.07, 0.09), mc.thePlayer.posZ, true));
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
