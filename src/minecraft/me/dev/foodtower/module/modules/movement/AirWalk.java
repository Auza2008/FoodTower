/*
Author:SuMuGod
Date:2022/7/10 4:25
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.MathUtil;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public class AirWalk extends Module {
    private final Numbers<Double> delay = new Numbers<Double>("Delay", "Delay", 250.0, 0.0, 500.0, 1.0);
    private final Numbers<Double> boost = new Numbers<Double>("Boost", "Boost", 2.0, 0.1, 2.0, 0.1);
    private final Option bob = new Option("Bobbing", "Bobbing", true);
    private final Option particle = new Option("Particle", "Particle", false);
    private final Option hurtcheck = new Option("Hurtcheck", "Hurtcheck", false);
    int counter, level;
    double moveSpeed, lastDist;
    boolean b2;
    boolean b3 = false;
    int sb;
    TimeHelper timer = new TimeHelper();
    TimerUtil timer2 = new TimerUtil();
    private double movementSpeed;
    private int hypixelCounter;
    private int hypixelCounter2;

    public AirWalk() {
        super("AirWalk", "空行", new String[]{"airwalk", "Boostfly", "aw"}, ModuleType.Movement);
    }

    public void damagePlayer(int damage) {
        if (damage < 1)
            damage = 1;
        if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
            damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());
        double offset = 0.0625;
        if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
            for (int i = 0; i <= ((3 + damage) / offset); ++i) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
            }
        }
    }

    @Override
    public void onEnable() {
        timer2.reset();
        if (!((Boolean) this.hurtcheck.getValue()).booleanValue()) {
            damagePlayer(1);
            b2 = true;
        }

        if (((Boolean) this.particle.getValue()).booleanValue()) {
            double x2 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
            double z2 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
            double lul = 1;
            double xOffset = (MovementInput.moveForward * lul * x2) + (MovementInput.moveStrafe * lul * z2);
            double zOffset = (MovementInput.moveForward * lul * z2) + (MovementInput.moveStrafe * lul * x2);
            double x = mc.thePlayer.posX + xOffset;
            double y = mc.thePlayer.posY + 0.42;
            double z = mc.thePlayer.posZ + zOffset;
            mc.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 0.0D, 0.0D, 0.0D);
            mc.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 0.0D, 0.0D, 0.0D);
            mc.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 0.0D, 10.0D, 0.0D);
        }
        this.hypixelCounter = 0;
        this.hypixelCounter2 = 1000;
        mc.thePlayer.motionY = 0.42f;
        level = 1;
        moveSpeed = 0.1D;
        lastDist = 0.0D;
    }

    @Override
    public void onDisable() {
        this.hypixelCounter = 0;
        this.hypixelCounter2 = 100;
        mc.timer.timerSpeed = 1.0f;
        level = 1;
        moveSpeed = 0.1D;
        b2 = false;
        lastDist = 0.0D;
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        if (!b2) {
            if (((Boolean) this.hurtcheck.getValue()).booleanValue()) {
                if (timer2.hasReached(this.delay.getValue().doubleValue())) {
                    damagePlayer(1);
                    b2 = true;
                    b3 = false;
                    timer2.reset();
                } else {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                    mc.thePlayer.jumpMovementFactor = 0;
                    mc.thePlayer.onGround = false;
                }
            }
        }
        if (((Boolean) this.particle.getValue()).booleanValue()) {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY + 0.42;
            double z = mc.thePlayer.posZ;
            mc.theWorld.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, 0.0D, 0.0D, 0.0D);
            mc.theWorld.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, 0.0D, 0.0D, 0.0D);
            mc.theWorld.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        if (((Boolean) this.bob.getValue()).booleanValue()) {
            mc.thePlayer.cameraYaw = (float) (0.09090908616781235 * 1);
        }
        ++counter;
        if (Minecraft.getMinecraft().thePlayer.moveForward == 0
                && Minecraft.getMinecraft().thePlayer.moveStrafing == 0) {
            Minecraft.getMinecraft().thePlayer.setPosition(
                    Minecraft.getMinecraft().thePlayer.posX + 1.0D,
                    Minecraft.getMinecraft().thePlayer.posY + 1.0D,
                    Minecraft.getMinecraft().thePlayer.posZ + 1.0D);
            Minecraft.getMinecraft().thePlayer.setPosition(Minecraft.getMinecraft().thePlayer.prevPosX,
                    Minecraft.getMinecraft().thePlayer.prevPosY,
                    Minecraft.getMinecraft().thePlayer.prevPosZ);
            Minecraft.getMinecraft().thePlayer.motionX = 0.0D;
            Minecraft.getMinecraft().thePlayer.motionZ = 0.0D;
        }
        Minecraft.getMinecraft().thePlayer.motionY = 0.0D;
        if (Minecraft.getMinecraft().gameSettings.keyBindJump.pressed)
            Minecraft.getMinecraft().thePlayer.motionY += 0.5f;
        if (Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed)
            Minecraft.getMinecraft().thePlayer.motionY -= 0.5f;
        if (counter != 1 && counter == 2) {
            Minecraft.getMinecraft().thePlayer.setPosition(Minecraft.getMinecraft().thePlayer.posX,
                    Minecraft.getMinecraft().thePlayer.posY + 1.0E-10D,
                    Minecraft.getMinecraft().thePlayer.posZ);
            counter = 0;
        }
    }

    @NMSL
    public void onPacket(EventPacketRecieve eventPacketRecieve) {
        if (eventPacketRecieve.getPacket() instanceof S08PacketPlayerPosLook)
            mc.thePlayer.onGround = true;
    }

    @NMSL
    public void onPost(EventPostUpdate e) {
        double xDist = Minecraft.getMinecraft().thePlayer.posX
                - Minecraft.getMinecraft().thePlayer.prevPosX;
        double zDist = Minecraft.getMinecraft().thePlayer.posZ
                - Minecraft.getMinecraft().thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @NMSL
    private void onMove(EventMove e) {
        float forward = MovementInput.moveForward;
        float strafe = MovementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        double mx = Math.cos(Math.toRadians(yaw + 90.0F));
        double mz = Math.sin(Math.toRadians(yaw + 90.0F));

        if (forward == 0.0F && strafe == 0.0F) {
            EventMove.x = 0.0D;
            EventMove.z = 0.0D;
        } else if (forward != 0.0F) {
            if (strafe >= 1.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
                strafe = 0.0F;
            } else if (strafe <= -1.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
                strafe = 0.0F;
            }

            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }
        if (b2) {
            timer.reset();
            if (level != 1 || Minecraft.getMinecraft().thePlayer.moveForward == 0.0F
                    && Minecraft.getMinecraft().thePlayer.moveStrafing == 0.0F) {
                if (level == 2) {
                    level = 3;
                    moveSpeed *= 2.1499999D;
                } else if (level == 3) {
                    level = 4;
                    double difference = (mc.thePlayer.ticksExisted % 2 == 0 ? 0.0103D : 0.0123D)
                            * (lastDist - MathUtil.getBaseMovementSpeed());
                    moveSpeed = lastDist - difference;
                } else {
                    if (Minecraft.getMinecraft().theWorld
                            .getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer,
                                    Minecraft.getMinecraft().thePlayer.boundingBox.offset(0.0D,
                                            Minecraft.getMinecraft().thePlayer.motionY, 0.0D))
                            .size() > 0 || Minecraft.getMinecraft().thePlayer.isCollidedVertically) {
                        level = 1;
                    }
                    moveSpeed = lastDist - lastDist / 159.0D;
                }
            } else {
                level = 2;
                int amplifier = Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)
                        ? Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
                        .getAmplifier() + 1
                        : 0;
                double boost = Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed) ? 1.56
                        : this.boost.getValue().floatValue();
                moveSpeed = boost * MathUtil.getBaseMovementSpeed();
            }
            moveSpeed = Math.max(moveSpeed, MathUtil.getBaseMovementSpeed());

            EventMove.x = (double) forward * moveSpeed * mx + (double) strafe * moveSpeed * mz;
            EventMove.z = (double) forward * moveSpeed * mz - (double) strafe * moveSpeed * mx;
            if (forward == 0.0F && strafe == 0.0F) {
                EventMove.x = 0.0D;
                EventMove.z = 0.0D;
            }
        }
    }
}

