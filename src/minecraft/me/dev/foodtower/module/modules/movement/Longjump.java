/*
Author:SuMuGod
Date:2022/7/10 4:29
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.MathUtils;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.utils.normal.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Random;

public class Longjump
        extends Module {
    public Longjump() {
        super("LongJump", "长跃而起", new String[]{"lj", "jumpman", "jump"}, ModuleType.Movement);
        this.setColor(new Color(76, 67, 216).getRGB());
        setKey(Keyboard.KEY_H);
    }

    boolean hasHurt = false;
    TimeHelper timer = new TimeHelper();
    private int stage;
    private double moveSpeed, lastDist, boost = 4.0;
    private boolean jumped;
    int i, slotId, ticks;
    int check;

    public static void NovolineDamage() {
        double offset = 0.060100000351667404;
        NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double x = player.posX;
        double y = player.posY;
        double z2 = player.posZ;
        int i = 0;
        while ((double)i < (double)getMaxFallDist() / 0.05510000046342611 + 1.0) {
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404, z2, false));
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4, z2, false));
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.004999999888241291 + 6.01000003516674E-8, z2, false));
            ++i;
        }
        netHandler.addToSendQueue(new C03PacketPlayer(true));
    }

    public static float getMaxFallDist() {
        PotionEffect potioneffect = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump);
        int f = (potioneffect != null) ? (potioneffect.getAmplifier() + 1) : 0;
        return (Minecraft.getMinecraft().thePlayer.getMaxFallHeight() + f);
    }


    public static double getRandomInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    @Override
    public void onEnable() {
        timer.reset();
        check = 1;
        stage = 1;
        moveSpeed = 0.1873;
        hasHurt = false;
        ItemStack itemStack = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        timer.reset();
        check = 1;
        mc.timer.timerSpeed = 1;
        lastDist = 0;
        stage = 4;
        jumped = false;
        super.onDisable();
    }

    @NMSL
    public void onUpdate(EventPostUpdate event) {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    boolean can;
    @NMSL
    private void onTick(EventTick e) {
        if (!hasHurt) {
            if (timer.hasReached(3000)) {
                can = true;
                timer.reset();
            }
        }
        if (can) {
            if (check == 1) {
                System.out.println("Damage!");
                NovolineDamage();
                timer.reset();
                check = 0;
            }
        }
    }

    @NMSL
    private void onPacket(EventPacketSend ep) {
        if (!can) {
            if (ep.getPacket() instanceof C03PacketPlayer) {
                ep.setCancelled(true);
            }
        }
    }

    @NMSL
    private void onMove(EventMove e) {
        if (mc.thePlayer.hurtTime > 0) {
            hasHurt = true;
            timer.reset();
        }
        if (!hasHurt) {
            e.setX(0);
            if (e.getY() > 0)
                e.setY(0);
            e.setZ(0);
        } else {
            timer.reset();
            if (MathUtils.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtils.roundToPlace(0.41, 3)) {
                mc.thePlayer.motionY = 0;
            }
            if (mc.thePlayer.moveStrafing < 0 && mc.thePlayer.moveForward < 0) {
                stage = 1;
            }
            if (MathUtils.round(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtils.round(0.943, 3)) {
                mc.thePlayer.motionY = 0;
            }

            if (stage == 1 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) && mc.thePlayer.isCollidedVertically) {
                //start jump
                stage = 2;
                moveSpeed = (boost) * PlayerUtil.getBaseMoveSpeed() - 0.01;
            } else if (stage == 2) {
                stage = 3;
                mc.thePlayer.motionY = 0.64;
                e.y = 0.64;
                moveSpeed *= (0.549802);

                jumped = true;
            } else if (stage == 3) {
                stage = 4;
                final double difference = 0.66 * (lastDist - PlayerUtil.getBaseMoveSpeed());
                moveSpeed = lastDist - difference;
            } else if (stage == 4) {
                // jumping...
                moveSpeed = lastDist - lastDist / 55.0;

                // if onground
                if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically) {
                    // end jump
                    stage = 1;

                    if (jumped) {
                        this.setEnabled(false);
                    }
                }
            }

            // Smart Moving
            moveSpeed = Math.max(moveSpeed, PlayerUtil.getBaseMoveSpeed());
            float forward = mc.thePlayer.movementInput.moveForward;
            float strafe = mc.thePlayer.movementInput.moveStrafe;
            float yaw = mc.thePlayer.rotationYaw;

            if (forward == 0.0f && strafe == 0.0f) {
                e.x = 0.0;
                e.z = 0.0;
            } else if (forward != 0.0f) {
                if (strafe >= 1.0f) {
                    yaw += ((forward > 0.0f) ? -45 : 45);
                    strafe = 0.0f;
                } else if (strafe <= -1.0f) {
                    yaw += ((forward > 0.0f) ? 45 : -45);
                    strafe = 0.0f;
                }
                if (forward > 0.0f) {
                    forward = 1.0f;
                } else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            double mx = Math.cos(Math.toRadians(yaw + 90.0f));
            double mz = Math.sin(Math.toRadians(yaw + 90.0f));
            e.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
            e.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
            if (forward == 0.0f && strafe == 0.0f) {
                e.x = 0;
                e.z = 0;
            }
        }
    }
}

