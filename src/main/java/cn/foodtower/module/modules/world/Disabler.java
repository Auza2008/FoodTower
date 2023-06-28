package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import cn.foodtower.module.modules.world.dis.disablers.bypass.*;
import cn.foodtower.module.modules.world.dis.disablers.server.CubeCraftDisabler;
import cn.foodtower.module.modules.world.dis.disablers.server.DisablerHypixelDisabler;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Disabler extends Module {
    public static Option lobbycheckvalue = new Option("LobbyCheck", true);
    public static Option lowerTimer = new Option("Lower timer on Lag", false);
    public static Option TimerA = new Option("TimerA", false);
    public static Option TimerB = new Option("TimerB", false);
    public static Option noC03s = new Option("NoC03s", false);
    public static Option blinkvalue = new Option("TestBlink", false);
    public static Option pingspoofvalue = new Option("PingSpoof", true);
    public static Numbers<Number> pingspoofdelay = new Numbers<Number>("PingSpoodDelay", 400, 10, 600, 1);
    public static Option invclickbypass = new Option("InvClickBypass", true);
    public static Option debug = new Option("Debug", false);
    private final Mode mode = new Mode("Mode", Modes.values(), Modes.Basic);
    private final MSTimer lagTimer = new MSTimer();
    //    public static final Numbers<Double> delay = new Numbers<>("Delay",500d, 300d, 2000d, 100d);

    public Disabler() {
        super("Disabler", new String[]{"Bypass", "Patcher"}, ModuleType.World);
        addValues(mode, lowerTimer, lobbycheckvalue, TimerA, TimerB, noC03s, blinkvalue, pingspoofvalue, pingspoofdelay, invclickbypass, debug);
        setValueDisplayable(new Value<?>[]{lobbycheckvalue, TimerA, TimerB, noC03s, blinkvalue, pingspoofvalue, invclickbypass, pingspoofdelay}, mode, Modes.Hypxiel);
    }

    @Override
    public void onEnable() {
        ((Modes) mode.get()).get().onEnabled();
    }

    @Override
    public void onDisable() {
        ((Modes) mode.get()).get().onDisable();
    }

    @EventHandler
    public void onMotionUpdate(EventMotionUpdate e) {
        ((Modes) mode.get()).get().onMotionUpdate(e);
    }

    @EventHandler
    public void onRender2d(EventRender2D e) {
        ((Modes) mode.get()).get().onRender2d(e);

    }


    @EventHandler
    public void onPre(EventPreUpdate e) {
        setSuffix(mode.get());

        ((Modes) mode.get()).get().onUpdate(e);

        if (lowerTimer.get()) {
            if (!lagTimer.hasTimePassed(1000)) {
                mc.timer.timerSpeed = 0.7f;
            } else {
                mc.timer.timerSpeed = 1f;
            }
        }
    }

    @EventHandler
    public void onRender3d(EventRender3D e) {
        ((Modes) mode.get()).get().onRender3d(e);
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        ((Modes) mode.get()).get().onPacket(e);
        if (e.packet instanceof S08PacketPlayerPosLook) {
            lagTimer.reset();
        }
    }

    @EventHandler
    public void onPacket(EventPacketSend event) {
        ((Modes) mode.get()).get().onPacket(event);
    }

    @EventHandler
    public void onPacketRE(EventPacketReceive e) {
        ((Modes) mode.get()).get().onPacket(e);
    }

    @EventHandler
    public void onRespawn(EventWorldChanged e) {
        ((Modes) mode.get()).get().onWorldChange(e);
    }

    @EventHandler
    public void onTick(EventTick e) {
        ((Modes) (mode.get())).get().onTick(e);
    }

    enum Modes {
        Basic(new BasicDisabler()), Hypxiel(new DisablerHypixelDisabler()), CubeCraft(new CubeCraftDisabler()), OldNCP(new OldNCPDisabler()), AAC4LessFlag(new AAC4LessFlagDisabler()), AAC5Test(new AAC5TestDisabler()), VulcanCombat(new VulcanCombatDisabler());

        final DisablerModule disablerModule;

        Modes(DisablerModule disabler) {
            disablerModule = disabler;
        }

        public DisablerModule get() {
            return disablerModule;
        }
    }
}
