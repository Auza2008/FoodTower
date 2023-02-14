/*
Author:SuMuGod
Date:2022/7/10 4:34
Project:foodtower Reborn
*/
package me.dev.foodtower.utils.normal;

import me.dev.foodtower.api.events.EventMove;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final double BUNNY_SLOPE = 0.66;
    public static final double WATCHDOG_BUNNY_SLOPE = BUNNY_SLOPE * 0.96;
    public static final double SPRINTING_MOD = 1.3;
    public static final double ICE_MOD = 2.5;
    public  static final List<Double> frictionValues = new ArrayList<>();
    public  static final double MIN_DIF = 1.0E-4;
    public static final double MAX_DIST = 2.15 - MIN_DIF;
    public  static final double WALK_SPEED = 0.221;
    public  static final double SWIM_MOD = 0.115D / WALK_SPEED;
    public  static final double[] DEPTH_STRIDER_VALUES = {
            1.0,
            0.1645 / SWIM_MOD / WALK_SPEED,
            0.1995 / SWIM_MOD / WALK_SPEED,
            1.0 / SWIM_MOD,
    };
    public  static final double SNEAKING_MOD = 0.13 / WALK_SPEED;
    public  static final double AIR_FRICTION = 0.98;
    public  static final double WATER_FRICTION = 0.89;
    public  static final double LAVA_FRICTION = 0.535;
    public  static final double BUNNY_DIV_FRICTION = 160.0 - MIN_DIF;

    public static double getJumpHeight() {
        double baseJumpHeight = 0.42f;
        if (PlayerUtil.isInLiquid()) {
            return WALK_SPEED * SWIM_MOD + 0.02;
        } else if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + 0.1 * mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
        }
        return baseJumpHeight;
    }
    public static boolean isBlockUnder() {
        if (mc.thePlayer == null) return false;

        if (mc.thePlayer.posY < 0.0) {
            return false;
        }
        for (int off = 0; off < (int)mc.thePlayer.posY + 2; off += 2) {
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, (double)(-off), 0.0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    public static double getBaseSpeed(double v1, double v3) {
        if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int a1 = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 - (mc.thePlayer.isPotionActive(Potion.moveSlowdown)?mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1:0);
            v1 *= 1.0D + v3 * (double)a1;
        }

        return v1;
    }

    public double getBaseSpeed(double[] v2) {
        if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int a1 = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 - (mc.thePlayer.isPotionActive(Potion.moveSlowdown)?mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1:0);
            v2[0] *= 1.0D + v2[1] * (double)a1;
        }

        return v2[0];
    }


    public static double defaultSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            // if(((Options)
            // settings.get(MODE).getValue()).getSelected().equalsIgnoreCase("Hypixel")){
            // baseSpeed *= (1.0D + 0.225D * (amplifier + 1));
            // }else
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }
    public static void setMotion(EventMove event, double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            event.setX(mc.thePlayer.motionX = 0);
            event.setZ(mc.thePlayer.motionZ = 0);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            event.setX(mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setZ(mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }


    public static void setMotion(double speed, float directionInYaw) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = directionInYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static void setMotion(double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static boolean checkTeleport(double x, double y, double z, double distBetweenPackets) {
        double distx = mc.thePlayer.posX - x;
        double disty = mc.thePlayer.posY - y;
        double distz = mc.thePlayer.posZ - z;
        double dist = Math.sqrt(mc.thePlayer.getDistanceSq(x, y, z));
        double distanceEntreLesPackets = distBetweenPackets;
        double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;

        double xtp = mc.thePlayer.posX;
        double ytp = mc.thePlayer.posY;
        double ztp = mc.thePlayer.posZ;
        for (int i = 1; i < nbPackets; i++) {
            double xdi = (x - mc.thePlayer.posX) / (nbPackets);
            xtp += xdi;

            double zdi = (z - mc.thePlayer.posZ) / (nbPackets);
            ztp += zdi;

            double ydi = (y - mc.thePlayer.posY) / (nbPackets);
            ytp += ydi;
            AxisAlignedBB bb = new AxisAlignedBB(xtp - 0.3, ytp, ztp - 0.3, xtp + 0.3, ytp + 1.8, ztp + 0.3);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return false;
            }

        }
        return true;
    }

    private static final Minecraft MC = Minecraft.getMinecraft();

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static int getJumpEffect() {
        if (mc.thePlayer.isPotionActive(Potion.jump))
            return mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
        else
            return 0;
    }

    public static int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        else
            return 0;
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return Minecraft.getMinecraft().theWorld
                .getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }

    public static Block getBlockAtPosC(double x, double y, double z) {
        EntityPlayer inPlayer = Minecraft.getMinecraft().thePlayer;
        return Minecraft.getMinecraft().theWorld
                .getBlockState(new BlockPos(inPlayer.posX + x, inPlayer.posY + y, inPlayer.posZ + z)).getBlock();
    }

    public static float getDistanceToGround(Entity e) {
        if (mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
            return 0.0F;
        }
        for (float a = (float) e.posY; a > 0.0F; a -= 1.0F) {
            int[] stairs = { 53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180 };
            int[] exemptIds = { 6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75,
                    76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157, 171, 175,
                    176, 177 };
            Block block = mc.theWorld.getBlockState(new BlockPos(e.posX, a - 1.0F, e.posZ)).getBlock();
            if (!(block instanceof BlockAir)) {
                if ((Block.getIdFromBlock(block) == 44) || (Block.getIdFromBlock(block) == 126)) {
                    return (float) (e.posY - a - 0.5D) < 0.0F ? 0.0F : (float) (e.posY - a - 0.5D);
                }
                int[] arrayOfInt1;
                int j = (arrayOfInt1 = stairs).length;
                for (int i = 0; i < j; i++) {
                    int id = arrayOfInt1[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return (float) (e.posY - a - 1.0D) < 0.0F ? 0.0F : (float) (e.posY - a - 1.0D);
                    }
                }
                j = (arrayOfInt1 = exemptIds).length;
                for (int i = 0; i < j; i++) {
                    int id = arrayOfInt1[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return (float) (e.posY - a) < 0.0F ? 0.0F : (float) (e.posY - a);
                    }
                }
                return (float) (e.posY - a + block.getBlockBoundsMaxY() - 1.0D);
            }
        }
        return 0.0F;
    }

    public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5 - mc.thePlayer.posX + (double) face.getFrontOffsetX() / 2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ + (double) face.getFrontOffsetZ() / 2;
        double y = (block.getY() + 0.5);
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if (yaw < 0.0F) {
            yaw += 360f;
        }
        return new float[]{yaw, pitch};
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                mc.thePlayer.posZ + 0.3, mc.thePlayer.posX + 0.3, mc.thePlayer.posY + 2.5, mc.thePlayer.posZ - 0.3);
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty();
    }

    public static boolean isOnGround(Entity entity, double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(entity, entity.getEntityBoundingBox().offset(0.0D, -height, 0.0D))
                .isEmpty();
    }

    public static boolean isCollidedH(double dist) {
        AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3, mc.thePlayer.posY + 2, mc.thePlayer.posZ + 0.3,
                mc.thePlayer.posX + 0.3, mc.thePlayer.posY + 3, mc.thePlayer.posZ - 0.3);
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.3 + dist, 0, 0)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(-0.3 - dist, 0, 0)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0, 0, 0.3 + dist)).isEmpty()) {
            return true;
        } else return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0, 0, -0.3 - dist)).isEmpty();
    }

    public static double getArBaseMoveSpeed() {
        double baseSpeed = 0.2875D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0D
                    + 0.2D * (double) (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static float getSpeed() {
        return (float) Math
                .sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isMoving() {
        return mc.thePlayer != null
                && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    public static boolean hasMotion() {
        return mc.thePlayer.motionX != 0D && mc.thePlayer.motionZ != 0D && mc.thePlayer.motionY != 0D;
    }

    public static void strafe(final double d) {
        if (!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * d;
        mc.thePlayer.motionZ = Math.cos(yaw) * d;
    }

    public static void forward(final double length) {
        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY,
                mc.thePlayer.posZ + (Math.cos(yaw) * length));
    }

    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static void setSpeed(final EventMove moveEvent, final double moveSpeed) {
        setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe,
                mc.thePlayer.movementInput.moveForward);
    }

    public static void setSpeed(final EventMove moveEvent, final double moveSpeed, final float pseudoYaw,
                                final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.setZ(0);
            moveEvent.setX(0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));

            moveEvent.setX((forward * moveSpeed * cos + strafe * moveSpeed * sin));
            moveEvent.setZ((forward * moveSpeed * sin - strafe * moveSpeed * cos));
        }
    }

    public static boolean isBlockAbovePlayer() {
        return !(mc.theWorld.getBlockState(
                        new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().maxY + 0.42F, mc.thePlayer.posZ))
                .getBlock() instanceof BlockAir);
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1 + .2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (float) (amplifier + 1) * 0.1F;
        }

        return baseJumpHeight;
    }

    public static boolean isRealCollidedH(double dist) {
        AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ + 0.3,
                mc.thePlayer.posX + 0.3, mc.thePlayer.posY + 1.9, mc.thePlayer.posZ - 0.3);
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0.3 + dist, 0, 0)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(-0.3 - dist, 0, 0)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0, 0, 0.3 + dist)).isEmpty()) {
            return true;
        } else return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb.offset(0, 0, -0.3 - dist)).isEmpty();
    }

    public static final void doStrafe(double speed) {
        if (!isMoving()) return;

        final double yaw = getYaw(true);
        MC.thePlayer.motionX = -Math.sin(yaw) * speed;
        MC.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static final double getYaw(boolean strafing) {
        float rotationYaw = MC.thePlayer.rotationYawHead;
        float forward = 1F;

        final double moveForward = MC.thePlayer.movementInput.moveForward;
        final double moveStrafing = MC.thePlayer.movementInput.moveStrafe;
        final float yaw = MC.thePlayer.rotationYaw;

        if (moveForward < 0) {
            rotationYaw += 180F;
        }

        if (moveForward < 0) {
            forward = -0.5F;
        } else if (moveForward > 0) {
            forward = 0.5F;
        }

        if (moveStrafing > 0) {
            rotationYaw -= 90F * forward;
        } else if (moveStrafing < 0) {
            rotationYaw += 90F * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public final void doStrafe(double speed, double yaw) {
        MC.thePlayer.motionX = -Math.sin(yaw) * speed;
        MC.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public final void doStrafe() {
        doStrafe(getSpeed());
    }

    public final void forward(double length, double y) {
        final double yaw = getYaw(false);
        MC.thePlayer.setPosition(MC.thePlayer.posX + (-Math.sin(yaw) * length), MC.thePlayer.posY + y, MC.thePlayer.posZ + (Math.cos(yaw) * length));
    }

    public final void stop(boolean y) {
        MC.thePlayer.motionX = 0;
        MC.thePlayer.motionZ = 0;
        if (y) MC.thePlayer.motionY = 0;
    }

    public static double getMotion() {
        Minecraft localMinecraft = Minecraft.getMinecraft();
        double d1 = localMinecraft.thePlayer.motionX;
        double d2 = localMinecraft.thePlayer.motionZ;
        return Math.sqrt(d1 * d1 + d2 * d2);
    }
    public static double calculateFriction(double moveSpeed, double lastDist, double baseMoveSpeedRef) {
        frictionValues.clear();
        frictionValues.add(lastDist - (lastDist / BUNNY_DIV_FRICTION));
        frictionValues.add(lastDist - ((moveSpeed - lastDist) / 33.3));
        double materialFriction = mc.thePlayer.isInWater() ? WATER_FRICTION : mc.thePlayer.isInLava() ? LAVA_FRICTION : AIR_FRICTION;
        frictionValues.add(lastDist - (baseMoveSpeedRef * (1.0 - materialFriction)));
        Collections.sort(frictionValues);
        return frictionValues.get(0);
    }
    public static boolean isOnIce() {
        final EntityPlayerSP player = mc.thePlayer;
        final Block blockUnder = mc.theWorld.getBlockState(new BlockPos(player.posX, player.posY - 1, player.posZ)).getBlock();
        return blockUnder instanceof BlockIce || blockUnder instanceof BlockPackedIce;
    }

}

