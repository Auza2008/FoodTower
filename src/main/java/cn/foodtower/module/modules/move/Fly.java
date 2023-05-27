package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.move.flymode.FlyModule;
import cn.foodtower.module.modules.move.flymode.fly.HytFly;
import cn.foodtower.module.modules.move.flymode.fly.NCPPacketFly;
import cn.foodtower.module.modules.move.flymode.fly.VanillaFly;
import cn.foodtower.module.modules.move.flymode.fly.VulcanFly;
import cn.foodtower.ui.notifications.user.Notifications;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Fly extends Module {
    public Mode mode = new Mode("Mode", FlyMode.values(), FlyMode.Vanilla);
    public static final Option lagBackCheck = new Option("LagBackCheck", false);
    public static Numbers<Double> speed = new Numbers<>("Speed", 2d, 0.1d, 5d, 0.1d);
    public static Numbers<Double> ncpSpeed = new Numbers<>("Speed", 0.28d, 0.27d, 0.29d, 0.01d);
    public static Numbers<Double> timer = new Numbers<>("Timer", 1.1d, 1.0d, 1.3d, 0.1d);
    private final Option bob = new Option("Bobbing", false);
    public static Option vanillaFlyAntiKick = new Option("AntiKick", false);
    public static Numbers<Integer> aac520Purse = new Numbers<>("Purse", 7, 3, 20, 1);
    public static Option aac520UseC04 = new Option("UseC04", false);

    public static final Option vulcan_canClipValue = new Option("CanClip", true);

    public Fly() {
        super("Fly", new String[]{"flight"}, ModuleType.Movement);
        addValues(mode, speed, ncpSpeed, timer, bob, lagBackCheck, vanillaFlyAntiKick, aac520Purse, aac520UseC04,vulcan_canClipValue);
        setValueDisplayable(vulcan_canClipValue, mode, FlyMode.Vulcan);
        setValueDisplayable(vanillaFlyAntiKick, mode, FlyMode.Vanilla);
        setValueDisplayable(speed, mode, new Enum<?>[]{FlyMode.Vanilla, FlyMode.HuaYuTing});
        setValueDisplayable(new Value<?>[] {ncpSpeed, timer}, mode, FlyMode.NCPPacket);
        setValueDisplayable(new Value<?>[]{aac520Purse, aac520UseC04}, mode, FlyMode.HuaYuTing);
    }

    @EventHandler
    public void onStep( EventStep e) {
        ((FlyMode) mode.getValue()).get().onStep(e);
    }

    @EventHandler
    public void onRender2d( EventRender2D e) {
        setSuffix(mode.getValue());
    }

    @Override
    public void onEnable() {
        ((FlyMode) mode.getValue()).get().onEnabled();
    }

    @Override
    public void onDisable() {
        ((FlyMode) mode.getValue()).get().onDisable();
    }

    @EventHandler
    public void onMove( EventMove e) {
        ((FlyMode) mode.getValue()).get().onMove(e);
    }

    @EventHandler
    public void onUpdate( EventPreUpdate e) {
        if (this.bob.getValue()) {
            mc.thePlayer.cameraYaw = 0.0425245214f;;
        }
        ((FlyMode) mode.getValue()).get().onUpdate(e);
    }

    @EventHandler
    public void onPost( EventPostUpdate e){
        ((FlyMode) mode.getValue()).get().onPostUpdate(e);
    }

    @EventHandler
    public void onPacketSend( EventPacketSend e) {
        ((FlyMode) mode.getValue()).get().onPacketSend(e);
    }

    @EventHandler
    public void onMotion(EventMotionUpdate e){
        ((FlyMode) mode.getValue()).get().onMotionUpdate(e);
    }

    @EventHandler
    public void onPacketReceive(EventPacketReceive e) {
        ((FlyMode) mode.getValue()).get().onPacketReceive(e);
        if (lagBackCheck.get()) {
            final Packet<?> packet = e.getPacket();
            if (packet instanceof S08PacketPlayerPosLook) {
                Notifications.getManager().post("Fly", "检测到回弹!自动关闭Fly");
                this.setEnabled(false);
            }
        }
    }


    enum FlyMode {
        Vanilla(new VanillaFly()),
        NCPPacket(new NCPPacketFly()),
        HuaYuTing(new HytFly()),
        Vulcan(new VulcanFly());

        final FlyModule flyModule;

        FlyMode(FlyModule moudleIn) {
            this.flyModule = moudleIn;
        }

        FlyModule get() {
            return this.flyModule;
        }
    }
}
