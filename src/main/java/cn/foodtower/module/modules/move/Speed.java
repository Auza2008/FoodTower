package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.module.modules.move.speedmode.speed.aac.AAC440Speed;
import cn.foodtower.module.modules.move.speedmode.speed.aac.AACTimer;
import cn.foodtower.module.modules.move.speedmode.speed.ncp.NCPLatestSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.normal.AutoJumpSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.ncp.BhopSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.normal.GudHopSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.normal.OnGroundSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.bypass.DCJBhopSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.bypass.HiveSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.bypass.HypixelSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.vulcan.VulcanFastHopSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.vulcan.VulcanHopSpeed;
import cn.foodtower.module.modules.move.speedmode.speed.vulcan.VulcanLowHopSpeed;
import cn.foodtower.ui.notifications.user.Notifications;
import cn.foodtower.util.entity.MovementUtils;
import cn.foodtower.util.entity.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;


public class Speed extends Module {
    public static Mode mode = new Mode("Mode", SpeedMode.values(), SpeedMode.Hypixel);
    public static Option lagcheck = new Option("LagBackCheck", true);
    public static Option groundspoof = new Option("GroundSpoof", false);
    public static Option fastfall = new Option("FastFall", false);
    public static Option sprint = new Option("Sprint", true);
    public static Option aireagle = new Option("Aireagle", false);
    public static Option jumpnobob = new Option("JumpNoBob", false);
    public static Numbers<Number> dmspeed = new Numbers<Number>("DamageBoostSpeed", 0.2, 0.0, 1.0, 0.05);
    public static Numbers<Number> timer = new Numbers<Number>("Timer", 1.0, 0.5, 3.0, 0.1);
    public static Numbers<Number> fastfallticks = new Numbers<Number>("Fastfallticks", 4.0, 1.0, 20.0, 1.0);
    public static Numbers<Number> fastfallmotion = new Numbers<Number>("FastfallMotionY", 0.1, 0.01, 2.00, 0.01);
    int airticks = 0;

    public Speed() {
        super("Speed", new String[]{"zoom"}, ModuleType.Movement);
        addValues(mode, lagcheck, sprint, aireagle, groundspoof, jumpnobob, fastfall, fastfallticks, fastfallmotion, timer, dmspeed);
        setValueDisplayable(dmspeed, mode, SpeedMode.AutoJump);
        setValueDisplayable(new Value[]{fastfallticks, fastfallmotion}, fastfall, fastfall.get());
    }

    public void onEnable() {
        ((SpeedMode) mode.get()).getModule().onEnabled();
    }

    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        ((SpeedMode) mode.get()).getModule().onDisabled();

        if (aireagle.get()) {
            mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        }

    }

    @EventHandler
    public void onPacketReceive(EventPacketReceive e) {
        Packet<?> packet = e.getPacket();
        if (packet instanceof S08PacketPlayerPosLook && lagcheck.get()) {
            Notifications.getManager().post("Speed", "Speed拉回!已自动关闭Speed");
            this.setEnabled(false);
        }
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        ((SpeedMode) mode.get()).getModule().onPacket(e);
    }

    @EventHandler
    public void onSteps(EventStep e) {
        ((SpeedMode) mode.get()).getModule().onStep(e);
    }

    @EventHandler
    public void onPost(EventPostUpdate e) {
        ((SpeedMode) mode.get()).getModule().onPost(e);
    }

    @EventHandler
    public void onMotion(EventMotionUpdate e) {
        ((SpeedMode) mode.get()).getModule().onMotion(e);
    }

    @EventHandler
    public void onPacket(EventPacketSend e) {
        if (groundspoof.get()) {
            if (e.getPacket() instanceof C03PacketPlayer) {
                ((C03PacketPlayer) e.getPacket()).onGround = false;
            }
        }
        ((SpeedMode) mode.get()).getModule().onPacketSend(e);
    }

    @EventHandler
    private void onMove(EventMove event) {
        ((SpeedMode) mode.get()).getModule().onMove(event);
    }

    @EventHandler
    public void onStrafe(EventStrafe e) {
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isInWeb()) return;
        if (mode.get() == SpeedMode.Hypixel && !mc.gameSettings.keyBindJump.isKeyDown()) {
            double d = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
            double d5 = mc.thePlayer.hurtTime > 1 && mc.thePlayer.fallDistance < 3.0f && !mc.thePlayer.isPotionActive(Potion.poison) && !mc.thePlayer.isBurning() ? 1.0 : 0.0;
            double d6 = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) * d5;
            double d7 = mc.thePlayer.motionX * (1.0 - d5);
            double d8 = mc.thePlayer.motionZ * (1.0 - d5);
            mc.thePlayer.motionX = d6 * -Math.sin(MovementUtils.getDirection()) + d7;
            mc.thePlayer.motionZ = d6 * Math.cos(MovementUtils.getDirection()) + d8;

        }
    }

    @EventHandler
    public void OnTick(EventMotionUpdate e) {

        mc.timer.timerSpeed = Speed.timer.get().floatValue();

//      跳跃时无视角摇晃
        if (jumpnobob.get()) {
            mc.thePlayer.cameraYaw = -0;
        }

//      疾跑控制
        if (!sprint.get()) {
            mc.thePlayer.setSprinting(false);
        }

//      空中蹲
        if (aireagle.get() && !mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
            mc.gameSettings.keyBindSneak.pressed = PlayerUtil.isAirUnder(mc.thePlayer);
        }

        if (e.isPre()) {
            if (mc.thePlayer.onGround && fastfall.get()) {
                airticks = 0;
            }
            if (!mc.thePlayer.onGround && fastfall.get()) {
                airticks++;
                if (airticks == fastfallticks.get().intValue()) {
                    mc.thePlayer.motionY = -fastfallmotion.get().floatValue();
                }
            }
        }

    }

    @EventHandler
    private void onPreUpdate(EventPreUpdate e) {
        ((SpeedMode) mode.get()).getModule().onPre(e);
        this.setSuffix(mode.get());
    }

    public enum SpeedMode {
        Hypixel(new HypixelSpeed()), AutoJump(new AutoJumpSpeed()), DCJBhop(new DCJBhopSpeed()), Hive(new HiveSpeed()), AAC440(new AAC440Speed()), Bhop(new BhopSpeed()), NCPLatest(new NCPLatestSpeed()), GudHop(new GudHopSpeed()), OnGround(new OnGroundSpeed()), AACTimer(new AACTimer()), VulcanHop(new VulcanHopSpeed()), VulcanFastHop(new VulcanFastHopSpeed()), VulcanLowHop(new VulcanLowHopSpeed());


        final SpeedModule module;

        SpeedMode(SpeedModule speedModule) {
            this.module = speedModule;
        }

        public SpeedModule getModule() {
            return module;
        }
    }
}
