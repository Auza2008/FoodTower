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
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.Speed;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.math.MathUtil;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Criticals extends Module {
    private static final Numbers<Double> Delay = new Numbers<>("Delay", "Delay", 400.0, 0.0, 1000.0, 1.0);
    public static Mode mode = new Mode("Mode", "mode", CritMode.values(), CritMode.Hypixel);
    public static Mode hypixelmode = new Mode("HypixelMode", HypixelMode.values(), HypixelMode.Packetor);
    public static Option Always = new Option("Always", "Always", false);
    public static Option C06 = new Option("C06", "C06", false);
    public static Option AutoSet = new Option("AutoSet", "AutoSet", true);
    public static Numbers<Double> motionYvalue = new Numbers<Double>("MotionY", 0.42, 0.01, 1.0, 0.01);
    private final Option speedCheck = new Option("SpeedCheck", true);
    static double[] y1 = {0.104080378093037, 0.105454222033912, 0.102888018147468, 0.099634532004642};
    private final TimerUtil timer = new TimerUtil();
    private final Numbers<Double> HurtTime = new Numbers<>("HurtTime", "HurtTime", 20.0D, 1.0D, 20.0D, 1.0D);
    double randomoffset = MathUtil.getRandomInRange(1.0E-12D, 1.0E-5D);
    double random = new Random().nextDouble() / 1000000;
    boolean readycrit = false;
    int ncpcrit = 0;
    int leftTicks = 0;
    boolean first = false;

    public Criticals() {
        super("Criticals", new String[]{"Criticals", "crit"}, ModuleType.Combat);
        this.addValues(mode, hypixelmode, motionYvalue, HurtTime, Delay, Always, C06, AutoSet, speedCheck);
        setValueDisplayable(new Value<?>[]{motionYvalue}, mode, CritMode.Motion);
        setValueDisplayable(hypixelmode, mode, CritMode.Hypixel);
    }

    public static void Crit2(Double[] value) {
        NetworkManager var1 = mc.thePlayer.sendQueue.getNetworkManager();
        double curX = mc.thePlayer.posX;
        double curY = mc.thePlayer.posY;
        double curZ = mc.thePlayer.posZ;
        for (double offset : value) {
            if (!C06.getValue()) {
                var1.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(curX, curY + offset, curZ, false));
            } else {
                var1.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(curX, curY + offset, curZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            }
        }
    }

    public static void Crit2(double[] value) {
        NetworkManager var1 = mc.thePlayer.sendQueue.getNetworkManager();
        double curX = mc.thePlayer.posX;
        double curY = mc.thePlayer.posY;
        double curZ = mc.thePlayer.posZ;
        for (double offset : value) {
            if (!C06.getValue()) {
                var1.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(curX, curY + offset, curZ, false));
            } else {
                var1.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(curX, curY + offset, curZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            }
        }
    }

    private boolean canCrit(EntityLivingBase e) {
        return (!ModuleManager.getModuleByClass(Scaffold.class).isEnabled() && (AutoSet.getValue() ? e.hurtTime <= MathUtil.randomNumber(20, 12) : e.hurtTime <= HurtTime.getValue()) && (mc.thePlayer.onGround || MoveUtils.isOnGround(0.01)) && (!speedCheck.getValue() || !ModuleManager.getModuleByClass(Speed.class).isEnabled()) && !ModuleManager.getModuleByClass(Fly.class).isEnabled()) || Always.getValue();
    }

    @EventHandler
    public void onPacketsend(EventPacketSend e) {
        if (mode.getValue().equals(CritMode.AAC440NoG)) {
            if (leftTicks > 490) {
                if (e.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
                    packet.onGround = false;
                }
            }
        }
        if (mode.getValue().equals(CritMode.NoGround) && e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            packet.onGround = false;
        }
        if (mode.getValue().equals(CritMode.Edit) && e.getPacket() instanceof C03PacketPlayer && readycrit) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            packet.onGround = false;
            readycrit = false;
        }
    }

    @Override
    public void onEnable() {
        if (mode.getValue().equals(CritMode.AAC440NoG)) {
            if (leftTicks > 0) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    leftTicks = 299;
                }
            }
        }
        if (mode.getValue().equals(CritMode.NoGround)) {
            mc.thePlayer.jump();
        }
        ncpcrit = 0;
        timer.reset();
    }

    @EventHandler
    private void onAttack(EventAttack e10) {
        if (e10.isPreAttack()) {
            autoCrit(e10.getEntity());
        }
        if (mode.getValue().equals(CritMode.AAC440NoG)) {
            if (leftTicks < 299) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    leftTicks = 500;
                }
            } else {
                leftTicks = 500;
            }
        }
    }

    @EventHandler
    public void onMotion(EventMove e) {
        if (mc.thePlayer.fallDistance >= 2.0 || ModuleManager.getModuleByClass(Speed.class).isEnabled()) leftTicks = 0;
    }

    @EventHandler
    private void onUpdate(EventMotionUpdate e10) {
        if (mode.getValue().equals(CritMode.Hypixel)) {
            setSuffix("Hyp-" + hypixelmode.getValue());
        } else {
            setSuffix(mode.getValue());
        }
        random = new Random().nextDouble() / 1000000;
        randomoffset = MathUtil.getRandomInRange(1.0E-12D, 1.0E-5D);
        if (mode.getValue().equals(CritMode.AAC440NoG)) {
            if (leftTicks > 0) {
                leftTicks--;
            } else {
                first = true;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    leftTicks = 299;
                }
            }
        }
    }

    public boolean autoCrit(Entity e) {
        if (!this.isEnabled()) return false;
        if (canCrit((EntityLivingBase) e)) {
            if (this.timer.hasReached(AutoSet.getValue() ? MathUtil.randomNumber(375, 50) : Delay.getValue())) {
                timer.reset();
                mc.thePlayer.onCriticalHit(e);
                switch ((CritMode) mode.getValue()) {
                    case Motion:
                        mc.thePlayer.motionY = motionYvalue.get().floatValue();
                        break;
                    case Hypixel:
                        switch (((HypixelMode) hypixelmode.getValue())) {
                            case Minimum:
                                Crit2(new double[]{0, new Random().nextBoolean() ? 0.102888018147468 : 0.105888018147468 * (new Random().nextBoolean() ? 0.98 : 0.99) + mc.thePlayer.ticksExisted % 0.0215 * 0.94, (new Random().nextBoolean() ? 0.01063469198817 : 0.013999999) * (new Random().nextBoolean() ? 0.98 : 0.99) * y1[new Random().nextInt(y1.length)] * 10});
                                break;
                            case Packetor:
                                Crit2(new double[]{new Random().nextBoolean() ? 0.082888018147468 * y1[new Random().nextInt(y1.length)] * 10 * (new Random().nextBoolean() ? 0.98 : 0.99) + mc.thePlayer.ticksExisted % 0.0215 * 0.94 : 0.09634532004642 * y1[new Random().nextInt(y1.length)] * 10 + mc.thePlayer.ticksExisted % 0.0115 * 0.94, (new Random().nextBoolean() ? 0.03125 : 0.01125) * (new Random().nextBoolean() ? 0.98 : 0.99) * y1[new Random().nextInt(y1.length)] * 10 + mc.thePlayer.ticksExisted % 0.0215 - mc.thePlayer.ticksExisted % 0.0115});
                                break;
                            case Complex:
                                Crit2(new double[]{y1[new Random().nextInt(y1.length)] * (y1[new Random().nextInt(y1.length)] - 0.003) * 10 + mc.thePlayer.ticksExisted % 0.0215 * 0.94, 0.01125 * (y1[new Random().nextInt(y1.length)] - 0.003) * 10, (0.03125 + ThreadLocalRandom.current().nextDouble(0.03, 0.06)) * (new Random().nextBoolean() ? 0.98 : 0.99) * y1[new Random().nextInt(y1.length)] * 10, (new Random().nextBoolean() ? 0.01063469198817 : 0.013999999) * (new Random().nextBoolean() ? 0.98 : 0.99) * y1[new Random().nextInt(y1.length)] * 10 + mc.thePlayer.ticksExisted % 0.0215 - mc.thePlayer.ticksExisted % 0.0115});
                                break;
                            case GetDown:
                                Crit2(new double[]{-0.0091165721 * y1[new Random().nextInt(y1.length)] * 10, 0.0176063469198817 * y1[new Random().nextInt(y1.length)] * 10});
                                break;
                        }
                        break;
                    case Packet:
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, true));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.1E-5, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                        break;
                    case Edit:
                        readycrit = true;
                    case OldNCPacket:
                        Crit2(new Double[]{0.11, 0.1100013579, 0.0000013579});
                        break;
                    case AAC440Packet:
                        Crit2(new Double[]{0.05250000001304, 0.00150000001304, 0.01400000001304, 0.00150000001304});
                        break;
                    case Jump:
                        mc.thePlayer.jump();
                        break;
                    case AACV5: {
                        mc.thePlayer.motionY = 0.104514886;
                        break;

                    }
                    case NCP: {
                        if (ncpcrit >= 3) {
                            Crit2(new Double[]{0.00001100134977413, 0.00000000013487744, 0.00000571003114589, 0.00000001578887744});
                            ncpcrit = 0;
                        } else {
                            ++ncpcrit;
                        }
                        break;
                    }
                }

                return true;
            }
        }
        return false;
    }

    public enum HypixelMode {
        Minimum, Packetor, Complex, GetDown
    }

    public enum CritMode {
        AAC440Packet, NCP, AAC440NoG, Jump, Packet, NoGround, OldNCPacket, Hypixel, AACV5, Edit, Motion
    }

}
