package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Misc.EventBlockBB;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.move.flymode.FlyModule;
import cn.foodtower.module.modules.move.flymode.fly.*;
import cn.foodtower.ui.notifications.user.Notifications;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;

public class Fly extends Module {
    public static final Option lagBackCheck = new Option("LagBackCheck", false);
    public static final Option vulcan_canClipValue = new Option("CanClip", true);
    public static Numbers<Double> speed = new Numbers<>("Speed", 2d, 0.1d, 5d, 0.1d);
    public static Numbers<Double> ncpSpeed = new Numbers<>("Speed", 0.28d, 0.27d, 0.29d, 0.01d);
    public static Numbers<Double> ncpTimer = new Numbers<>("NCPFly-Timer", 1.1d, 1.0d, 1.3d, 0.1d);
    public static Numbers<Double> dcjTimer = new Numbers<>("DCJFly-Timer", 1d, 1.0d, 10d, 0.1d);
    public static Numbers<Double> timer = new Numbers<>("Timer", 1d, 0.1, 10d, 0.1);
    public static Option vanillaFlyAntiKick = new Option("AntiKick", false);
    private final Option bob = new Option("Bobbing", false);
    public Mode mode = new Mode("Mode", FlyMode.values(), FlyMode.Vanilla);

    public Fly() {
        super("Fly", new String[]{"flight"}, ModuleType.Movement);
        addValues(mode, speed, timer, ncpSpeed, ncpTimer, dcjTimer, bob, lagBackCheck, vanillaFlyAntiKick, vulcan_canClipValue);
        setValueDisplayable(vulcan_canClipValue, mode, FlyMode.Vulcan);
        setValueDisplayable(vanillaFlyAntiKick, mode, FlyMode.Vanilla);
        setValueDisplayable(timer, mode, new Enum[]{FlyMode.Vanilla});
        setValueDisplayable(speed, mode, new Enum[]{FlyMode.Vanilla, FlyMode.AAC5});
        setValueDisplayable(new Value<?>[]{ncpSpeed, ncpTimer}, mode, FlyMode.NCPPacket);
        setValueDisplayable(dcjTimer, mode, FlyMode.DCJTest);
    }

    @EventHandler
    public void onStep(EventStep e) {
        ((FlyMode) mode.get()).get().onStep(e);
    }

    @EventHandler
    public void onRender2d(EventRender2D e) {
        setSuffix(mode.get());
    }

    @Override
    public void onEnable() {
        ((FlyMode) mode.get()).get().onEnabled();
    }

    @Override
    public void onDisable() {
        ((FlyMode) mode.get()).get().onDisable();
        mc.timer.timerSpeed = 1f;
    }

    @EventHandler
    public void onMove(EventMove e) {
        ((FlyMode) mode.get()).get().onMove(e);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (this.bob.get()) {
            mc.thePlayer.cameraYaw = 0.0425245214f;
        }
        ((FlyMode) mode.get()).get().onUpdate(e);
    }

    @EventHandler
    public void onPost(EventPostUpdate e) {
        ((FlyMode) mode.get()).get().onPostUpdate(e);
    }

    @EventHandler
    public void onPacketSend(EventPacketSend e) {
        ((FlyMode) mode.get()).get().onPacketSend(e);
    }

    @EventHandler
    public void onMotion(EventMotionUpdate e) {
        ((FlyMode) mode.get()).get().onMotionUpdate(e);
    }

    @EventHandler
    public void onPacketReceive(EventPacketReceive e) {
        ((FlyMode) mode.get()).get().onPacketReceive(e);
        if (lagBackCheck.get()) {
            final Packet<?> packet = e.getPacket();
            if (packet instanceof S08PacketPlayerPosLook) {
                Notifications.getManager().post("Fly", "检测到回弹!自动关闭Fly");
                this.setEnabled(false);
            }
        }
    }

    @EventHandler
    private void onBlockBB(EventBlockBB event) {
        if (mode.get().equals(FlyMode.DCJTest)) {
            if (event.getBlock() instanceof BlockAir && event.getPos().getY() <= mc.thePlayer.posY) {
                event.boundingBox = AxisAlignedBB.fromBounds(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPos().getX() + 1.0, mc.thePlayer.posY, event.getPos().getZ() + 1.0);
            }
        }
    }

    @EventHandler
    private void onJump(EventJump e) {
        if (mode.get().equals(FlyMode.DCJTest)) {
            e.setCancelled(true);
        }
    }


    enum FlyMode {
        Vanilla(new VanillaFly()), NCPPacket(new NCPPacketFly()), AAC5(new AAC5Fly()), Vulcan(new VulcanFly()), DCJTest(new DCJFly());

        final FlyModule flyModule;

        FlyMode(FlyModule moudleIn) {
            this.flyModule = moudleIn;
        }

        FlyModule get() {
            return this.flyModule;
        }
    }
}
