package cn.foodtower.module.modules.combat;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.World.EventPacket;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.notifications.user.Notifications;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.math.MathUtil;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;

import java.util.stream.IntStream;

public class Velocity extends Module {
    private final Mode mode;
    private final Numbers<Double> vertical;
    private final Numbers<Double> horizontal;
    private final Numbers<Double> velocitytickvalue;
    private final Numbers<Double> reverseStrengthValue;
    private final Option legitDisableInAirValue = new Option("DisableInAir", true);
    private final Numbers<Integer> legitChanceValue = new Numbers<>("Chance", 100, 0, 100, 1);


    /**
     * VALUES
     */
    private final MSTimer velocityTimer = new MSTimer();
    private final MSTimer timer = new MSTimer();
    private final int velocitytick = 0;
    private boolean velocityInput = false;
    private int templateX = 0;
    private int templateY = 0;
    private int templateZ = 0;
    private boolean velocityinput;

    public Velocity() {
        super("Velocity", new String[]{"AntiKB"}, ModuleType.Combat);
        mode = new Mode("Velocity", modes.values(), modes.Simple);
        this.vertical = new Numbers<>("Vertical", 0.0, 0.0, 1.0, 0.01);
        this.horizontal = new Numbers<>("Horizontal", 0.0, 0.0, 1.0, 0.01);
        this.velocitytickvalue = new Numbers<Double>("VelocityTick", 0.0, 0.0, 24.0, 1.0);
        this.reverseStrengthValue = new Numbers<Double>("ReverseStrengthValue", 1.0, 0.0, 1.0, 0.1);
        this.addValues(mode, vertical, horizontal, velocitytickvalue, reverseStrengthValue, legitDisableInAirValue, legitChanceValue);
        velocityTimer.reset();
        setValueDisplayable(velocitytickvalue, mode, modes.Tick);
        setValueDisplayable(reverseStrengthValue, mode, modes.Reverse);
        setValueDisplayable(legitDisableInAirValue, mode, modes.Legit);
        setValueDisplayable(legitChanceValue, mode, modes.Legit);
    }

    @EventHandler
    public void onEvent(EventPreUpdate event) {
        switch ((modes) mode.getValue()) {
            case Tick:
                if (velocitytick > velocitytickvalue.get()) {
                    if (mc.thePlayer.motionY > 0) mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                    mc.thePlayer.jumpMovementFactor = -0.00001f;
                    velocityinput = false;
                }
                if (mc.thePlayer.onGround && velocitytick > 1) {
                    velocityinput = false;
                }
                break;
            case Legit:
                if (legitDisableInAirValue.getValue() && !MoveUtils.isOnGround(0.5)) return;
                if (mc.thePlayer.maxHurtResistantTime != mc.thePlayer.hurtResistantTime || mc.thePlayer.maxHurtResistantTime == 0) return;
                if (MathUtil.randomNumber(100, 1) < legitChanceValue.get()) {
                    mc.thePlayer.motionX *= horizontal.getValue();
                    mc.thePlayer.motionZ *= horizontal.getValue();
                    mc.thePlayer.motionY *= vertical.get();
                }
                break;
            case Reverse:
                if (!mc.thePlayer.onGround && velocityInput) {
                    MoveUtils.strafe((float) (MoveUtils.getSpeed() * reverseStrengthValue.get()));
                } else if (velocityTimer.hasTimePassed(80L) && velocityInput) {
                    velocityInput = false;
                    velocityTimer.reset();
                }
                break;
            case AAC:
                if (velocityInput && velocityTimer.hasTimePassed(80L)) {
                    mc.thePlayer.motionX *= horizontal.getValue();
                    mc.thePlayer.motionZ *= horizontal.getValue();
                    velocityInput = false;
                }
                break;
            case AAC1:
                if (mc.thePlayer.hurtTime == 1 || mc.thePlayer.hurtTime == 2 || mc.thePlayer.hurtTime == 3 || mc.thePlayer.hurtTime == 4 || mc.thePlayer.hurtTime == 5 || mc.thePlayer.hurtTime == 6 || mc.thePlayer.hurtTime == 7 || mc.thePlayer.hurtTime == 8) {
                    if (mc.thePlayer.onGround) {
                        break;
                    }
                    double yaw = mc.thePlayer.rotationYawHead;
                    yaw = Math.toRadians(yaw);
                    double dX = (-MathHelper.sin(yaw)) * 0.08;
                    double dZ = MathHelper.cos((float) yaw) * 0.08;
                    if (mc.thePlayer.getHealth() >= 6.0f) {
                        mc.thePlayer.motionX = dX;
                        mc.thePlayer.motionZ = dZ;
                    }
                }
                break;
            case AAC5Reduce:
                if (mc.thePlayer.hurtTime > 1 && velocityInput) {
                    mc.thePlayer.motionX *= 0.81;
                    mc.thePlayer.motionZ *= 0.81;
                }
                if (velocityInput && (mc.thePlayer.hurtTime < 5 || mc.thePlayer.onGround) && velocityTimer.hasTimePassed(120L)) {
                    velocityInput = false;
                }
                break;
            case AAC5Combat:
                if (mc.thePlayer.hurtTime > 0 && velocityInput) {
                    velocityInput = false;
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.jumpMovementFactor = -0.002f;
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
                }
                if (velocityTimer.hasTimePassed(80L) && velocityInput) {
                    velocityInput = false;
                    mc.thePlayer.motionX = templateX / 8000.0;
                    mc.thePlayer.motionZ = templateZ / 8000.0;
                    mc.thePlayer.motionY = templateY / 8000.0;
                    mc.thePlayer.jumpMovementFactor = -0.002f;
                }
                break;

        }
    }


    @EventHandler
    public void onEvent(EventPacket event) {
        if (mc.thePlayer == null) {
            return;
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                velocityInput = true;
                velocityinput = true;
                velocityTimer.reset();
            }
        }
        if (event.getPacket() instanceof S27PacketExplosion) {
            velocityInput = true;
            velocityinput = true;
            velocityTimer.reset();
        }

        switch ((modes) mode.getValue()) {
            case Tick:
                if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                    float ve = this.vertical.getValue().floatValue();
                    float ho = this.horizontal.getValue().floatValue();
                    if (ve == 0.0 && ho == 0.0) {
                        event.setCancelled(true);
                    }
                    ((S12PacketEntityVelocity) event.getPacket()).motionX = (int) (((S12PacketEntityVelocity) event.getPacket()).motionX * ho);
                    ((S12PacketEntityVelocity) event.getPacket()).motionY = (int) (((S12PacketEntityVelocity) event.getPacket()).motionY * ve);
                    ((S12PacketEntityVelocity) event.getPacket()).motionZ = (int) (((S12PacketEntityVelocity) event.getPacket()).motionZ * ho);
                }
                break;
            case AAC5Combat:
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    event.setCancelled(true);
                    templateX = packet.motionX;
                    templateZ = packet.motionZ;
                    templateY = packet.motionY;
                }
                break;
            case Simple:
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        if (this.vertical.getValue() == 0.0D && this.horizontal.getValue() == 0.0D) {
                            event.setCancelled(true);
                        } else {
                            packet.motionX = (int) (packet.motionX * this.horizontal.getValue());
                            packet.motionZ = (int) (packet.motionZ * this.horizontal.getValue());
                            packet.motionY = (int) (packet.motionY * this.vertical.getValue());
                        }
                    }
                }


                if (event.getPacket() instanceof S27PacketExplosion) {
                    S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
                    if (this.vertical.getValue() == 0.0D && this.horizontal.getValue() == 0.0D) {
                        event.setCancelled(true);
                    } else {
                        packet.field_149152_f = (float) (packet.field_149152_f * this.horizontal.getValue());
                        packet.field_149153_g = (float) (packet.field_149153_g * this.horizontal.getValue());
                        packet.field_149159_h = (float) (packet.field_149159_h * this.vertical.getValue());
                    }
                }
                break;
            case AAC520:
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (mc.thePlayer == null || mc.theWorld.getEntityByID(packet.getEntityID()) != mc.thePlayer) {
                        return;
                    }
                    event.setCancelled(true);
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
                }
                break;
            case Hypixel:
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (mc.thePlayer == null || mc.theWorld.getEntityByID(packet.getEntityID()) != mc.thePlayer) {
                        return;
                    }
                    if (mc.thePlayer.onGround) {
                        packet.motionX = 0;
                        packet.motionZ = 0;
                    } else {
                        event.setCancelled(true);
                    }
                }
                if (event.getPacket() instanceof S27PacketExplosion) {
                    if (mc.thePlayer.onGround) return;
                    event.setCancelled(true);
                }
                break;
        }
    }

    @EventHandler
    public void onRender2d(EventRender2D e) {
        if (mode.getValue() == modes.Simple || mode.getValue() == modes.Tick) {
            this.setSuffix((horizontal.getValue() * 100) + "% " + (vertical.getValue() * 100) + "%");
        } else {
            this.setSuffix(mode.getValue());
        }

    }

    @EventHandler
    public void onEnable() {
        if (mode.getValue() == modes.Hypixel) {
            Notifications.getManager().post("Velocity", "You ' d better try Hypixel Mode On HighPing!");
        }
    }

    enum modes {
        Simple, Legit, Tick, Reverse, AAC, AAC1, AAC520, AAC5Reduce, AAC5Combat, Hypixel
    }

}


