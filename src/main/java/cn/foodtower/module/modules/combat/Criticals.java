package cn.foodtower.module.modules.combat;


import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;
import cn.foodtower.module.modules.combat.criticalsmode.criticals.aac.AAC440NoGroundCriit;
import cn.foodtower.module.modules.combat.criticalsmode.criticals.aac.AAC440PacketCrit;
import cn.foodtower.module.modules.combat.criticalsmode.criticals.aac.AAC5Crit;
import cn.foodtower.module.modules.combat.criticalsmode.criticals.bypass.DCJHopCrit;
import cn.foodtower.module.modules.combat.criticalsmode.criticals.ncp.NCPCrit;
import cn.foodtower.module.modules.combat.criticalsmode.criticals.ncp.OldNCPacketCrit;
import cn.foodtower.module.modules.combat.criticalsmode.criticals.normal.*;
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.Speed;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.entity.EntityLivingBase;


public class Criticals extends Module {
    private static final Numbers<Double> Delay = new Numbers<>("Delay", "Delay", 0.0, 0.0, 1000.0, 1.0);
    public static Mode mode = new Mode("Mode", "mode", CritMode.values(), CritMode.Packet);
    public static Option Always = new Option("Always", "Always", false);
    public static Option C06 = new Option("C06", "C06", false);
    public static Numbers<Double> motionYvalue = new Numbers<>("MotionY", 0.42, 0.01, 1.0, 0.01);
    private final Option speedCheck = new Option("SpeedCheck", true);
    private final MSTimer timer = new MSTimer();
    private final Numbers<Double> HurtTime = new Numbers<>("HurtTime", "HurtTime", 20.0D, 1.0D, 20.0D, 1.0D);
    public static boolean dcj = false;

    public Criticals() {
        super("Criticals", new String[]{"Criticals", "crit"}, ModuleType.Combat);
        this.addValues(mode, motionYvalue, HurtTime, Delay, Always, C06, speedCheck);
        setValueDisplayable(new Value<?>[]{motionYvalue}, mode, new Enum[]{CritMode.Motion});
        setValueDisplayable(C06, mode, new Enum[]{CritMode.Packet, CritMode.AAC440Packet, CritMode.NCP, CritMode.OldNCPacket});
    }

    private boolean canCrit(EntityLivingBase e) {
        return (!ModuleManager.getModuleByClass(Scaffold.class).isEnabled() && (HurtTime.getValue().equals(20.0) || e.hurtTime <= HurtTime.getValue()) && mc.thePlayer.onGround && (!speedCheck.getValue() || !ModuleManager.getModuleByClass(Speed.class).isEnabled()) && !ModuleManager.getModuleByClass(Fly.class).isEnabled()) || Always.getValue();
    }

    @EventHandler
    public void onPacketsend(EventPacketSend e) {
        ((Criticals.CritMode) mode.getValue()).getModule().onPacketSend(e);
    }

    @Override
    public void onEnable() {
        ((Criticals.CritMode) (mode.getValue())).getModule().onEnabled();
        timer.reset();
        if (mode.getValue().equals(CritMode.DCJHop)) {
            dcj = false;
        }
    }

    @EventHandler
    public void onMotion(EventMove e) {
        ((Criticals.CritMode) (mode.getValue())).getModule().onMove(e);
    }

    @EventHandler
    private void onUpdate(EventMotionUpdate e) {
        setSuffix(mode.getValue());
        ((Criticals.CritMode) (mode.getValue())).getModule().onUpdate(e);
    }

    @EventHandler
    public void onAttack(EventAttack e) {
        if (!this.isEnabled()) return;
        if (mode.getValue().equals(CritMode.AAC440NG)) {
            ((Criticals.CritMode) (mode.getValue())).getModule().onAttack(e);
        }
        if (canCrit((EntityLivingBase) e.getEntity())) {
            if (Delay.getValue().equals(0.0) || this.timer.hasTimePassed(Delay.getValue().longValue())) {
                mc.thePlayer.onCriticalHit(e.getEntity());
                ((CritMode) (mode.getValue())).getModule().onAttack(e);
                timer.reset();
            }
        }
    }

    @EventHandler
    private void onWorld(EventWorldChanged e) {
        if (mode.getValue().equals(CritMode.DCJHop)) {
            dcj = false;
        }
    }

    public enum CritMode {
        Packet(new PacketCrit()), NoGround(new NoGroundCrit()), Edit(new EditCrit()), Motion(new MotionCrit()), TpHop(new TpHopCrit()), OldNCPacket(new OldNCPacketCrit()), NCP(new NCPCrit()), AAC440Packet(new AAC440PacketCrit()), AAC440NG(new AAC440NoGroundCriit()), AACV5(new AAC5Crit()), DCJHop(new DCJHopCrit());
        final CriticalsModule module;

        CritMode(CriticalsModule criticalsModule) {
            module = criticalsModule;
        }

        public CriticalsModule getModule() {
            return module;
        }
    }
}
