package cn.foodtower.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class RotationUtils {
    private static final Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();

    public static float[] getCustomRotation(Vec3 vec) {
        Vec3 playerVector = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        double y = vec.yCoord - playerVector.yCoord;
        double x = vec.xCoord - playerVector.xCoord;
        double z = vec.zCoord - playerVector.zCoord;
        double dff = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(y, dff)));
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static boolean isFaced(EntityLivingBase targetEntity, double blockReachDistance) {
        return RayCastUtil.raycastEntity(blockReachDistance, entity -> entity == targetEntity) != null;
    }

    public static cn.foodtower.util.misc.scaffold.VecRotation searchCenter(AxisAlignedBB bb, boolean predict) {
        cn.foodtower.util.misc.scaffold.VecRotation vecRotation = null;
        for (double xSearch = 0.15; xSearch < 0.85; xSearch += 0.1) {
            for (double ySearch = 0.15; ySearch < 1.0; ySearch += 0.1) {
                for (double zSearch = 0.15; zSearch < 0.85; zSearch += 0.1) {
                    Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    Rotation rotation = toRotationMisc(vec3, predict);
                    cn.foodtower.util.misc.scaffold.VecRotation currentVec = new cn.foodtower.util.misc.scaffold.VecRotation(vec3, rotation);
                    if (vecRotation != null && !(cn.foodtower.util.misc.scaffold.RotationUtil.getRotationDifference(currentVec.getRotation()) < cn.foodtower.util.misc.scaffold.RotationUtil.getRotationDifference(vecRotation.getRotation())))
                        continue;
                    vecRotation = currentVec;
                }
            }
        }
        return vecRotation;
    }

    public static Rotation toRotationMisc(Vec3 vec, boolean predict) {
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        if (predict) {
            eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
        }
        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;
        return new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapAngleTo180_float((float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
    }

    public static Vec3 getLocation(AxisAlignedBB bb) {
        double yaw = 0.5;
        double pitch = 0.5;
        cn.foodtower.util.misc.scaffold.VecRotation rotation = searchCenter(bb, true);
        return rotation != null ? (Vec3) rotation.getVec() : new Vec3(bb.minX + (bb.maxX - bb.minX) * yaw, bb.minY + (bb.maxY - bb.minY) * pitch, bb.minZ + (bb.maxZ - bb.minZ) * yaw);
    }

    public static float[] getBlockRotations(int x2, int y2, int z2, EnumFacing facing) {
        Minecraft mc2 = Minecraft.getMinecraft();
        EntitySnowball temp = new EntitySnowball(mc.theWorld);
        temp.posX = (double) x2 + 0.5;
        temp.posY = (double) y2 + 0.5;
        temp.posZ = (double) z2 + 0.5;
        return RotationUtils.getAngles(temp);
    }

    public static boolean canEntityBeSeen(Entity e) {
        Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + (double) (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.25D;
        double maxx = e.posX + 0.25D;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.25D;
        double maxz = e.posZ + 0.25D;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see) {
            return true;
        } else {
            vec2 = new Vec3(maxx, miny, minz);
            see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
            if (see) {
                return true;
            } else {
                vec2 = new Vec3(minx, miny, minz);
                see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                if (see) {
                    return true;
                } else {
                    vec2 = new Vec3(minx, miny, maxz);
                    see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                    if (see) {
                        return true;
                    } else {
                        vec2 = new Vec3(maxx, miny, maxz);
                        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                        if (see) {
                            return true;
                        } else {
                            vec2 = new Vec3(maxx, maxy, minz);
                            see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                            if (see) {
                                return true;
                            } else {
                                vec2 = new Vec3(minx, maxy, minz);
                                see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                                if (see) {
                                    return true;
                                } else {
                                    vec2 = new Vec3(minx, maxy, maxz - 0.1D);
                                    see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                                    if (see) {
                                        return true;
                                    } else {
                                        vec2 = new Vec3(maxx, maxy, maxz);
                                        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
                                        return see;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static float[] getAngles(Entity e2) {
        return new float[]{RotationUtils.getYawChangeToEntity(e2) + mc.thePlayer.rotationYaw, RotationUtils.getPitchChangeToEntity(e2) + mc.thePlayer.rotationPitch};
    }

    public static float[] getRotationsEntity(final EntityLivingBase ent) {
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = ent.posY + ent.getEyeHeight() / 2.0f;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getRotationsEntityEye(final EntityLivingBase ent) {
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = ent.posY + ent.getEyeHeight() - 0.5;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getPredictedRotations(EntityLivingBase ent) {
        double x = ent.posX + (ent.posX - ent.lastTickPosX);
        double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double yawToEntity = deltaZ < 0.0 && deltaX < 0.0 ? 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)) : (deltaZ < 0.0 && deltaX > 0.0 ? -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)) : Math.toDegrees(-Math.atan(deltaX / deltaZ)));
        return MathHelper.wrapAngleTo180_float(-mc.thePlayer.rotationYaw - (float) yawToEntity);
    }

    public static float getPitchChange(Entity entity, double posY) {
        double var10000 = entity.posX;
        double deltaX = var10000 - mc.thePlayer.posX;
        var10000 = entity.posZ;
        double deltaZ = var10000 - mc.thePlayer.posZ;
        var10000 = posY - 2.2D + (double) entity.getEyeHeight();
        double deltaY = var10000 - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) pitchToEntity) - 2.5F;
    }

    public static float getPitchChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double deltaY = entity.posY - 1.6 + (double) entity.getEyeHeight() - 0.4 - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) pitchToEntity);
    }

    public static float[] getRotationFromPosition(final double x, final double z, final double y) {
        final double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        final double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        final double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;
        final double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[]{yaw, pitch};
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static float[] getRotations(Vec3 position) {
        return RotationUtils.getRotations((Vec3) mc.thePlayer.getPositionVector().addVector(0.0, mc.thePlayer.getEyeHeight(), 0.0), position);
    }

    public static float[] getRotations(Vec3 origin, Vec3 position) {
        Vec3 difference = (Vec3) position.subtract(origin);
        double distance = difference.flat().lengthVector();
        float yaw = (float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(BlockPos pos) {
        return RotationUtils.getRotations((Vec3) mc.thePlayer.getPositionVector().addVector(0.0, mc.thePlayer.getEyeHeight(), 0.0), new Vec3((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5));
    }

    public static float[] getBowAngles(Entity entity) {
        double xDelta = entity.posX - entity.lastTickPosX;
        double zDelta = entity.posZ - entity.lastTickPosZ;
        double d2 = mc.thePlayer.getDistanceToEntity(entity);
        d2 -= d2 % 0.8;
        double xMulti = 1.0;
        double zMulti = 1.0;
        boolean sprint = entity.isSprinting();
        xMulti = d2 / 0.8 * xDelta * (sprint ? 1.25 : 1.0);
        zMulti = d2 / 0.8 * zDelta * (sprint ? 1.25 : 1.0);
        double x2 = entity.posX + xMulti - mc.thePlayer.posX;
        double z2 = entity.posZ + zMulti - mc.thePlayer.posZ;
        double y2 = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - (entity.posY + (double) entity.getEyeHeight());
        double dist = mc.thePlayer.getDistanceToEntity(entity);
        float yaw = (float) Math.toDegrees(Math.atan2(z2, x2)) - 90.0f;
        float pitch = (float) Math.toDegrees(Math.atan2(y2, dist));
        return new float[]{yaw, pitch};
    }

    public static float normalizeAngle(float angle) {
        return (angle + 360.0f) % 360.0f;
    }

    public static float getTrajAngleSolutionLow(float d3, float d1, float velocity) {
        float g2 = 0.006f;
        float sqrt = velocity * velocity * velocity * velocity - g2 * (g2 * (d3 * d3) + 2.0f * d1 * (velocity * velocity));
        return (float) Math.toDegrees(Math.atan(((double) (velocity * velocity) - Math.sqrt(sqrt)) / (double) (g2 * d3)));
    }

    public static Vec3 getEyesPos() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static float[] getRotationsBlock(BlockPos pos) {
        double d0 = (double) pos.getX() - mc.thePlayer.posX;
        double d1 = (double) pos.getY() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double d2 = (double) pos.getZ() - mc.thePlayer.posZ;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f2 = (float) (MathHelper.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0f;
        float f1 = (float) (-Math.toDegrees(Math.atan2(d3, d1)));
        return new float[]{f1};
    }

    public static float[] getNeededRotations(Vec3 vec) {
        Vec3 eyesPos = RotationUtils.getEyesPos();
        double diffX = vec.xCoord - eyesPos.xCoord + 0.5;
        double diffY = vec.yCoord - eyesPos.yCoord + 0.5;
        double diffZ = vec.zCoord - eyesPos.zCoord + 0.5;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) ((-Math.atan2(diffY, diffXZ)) * 180.0 / 3.141592653589793);
        float[] arrf = new float[]{MathHelper.wrapAngleTo180_float(yaw), Minecraft.getMinecraft().gameSettings.keyBindJump.pressed ? 90.0f : MathHelper.wrapAngleTo180_float(pitch)};
        return arrf;
    }

    public static void faceVectorPacketInstant(Vec3 vec) {
        float[] rotations = RotationUtils.getNeededRotations(vec);
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], mc.thePlayer.onGround));
    }

    public static float getYawChange(float yaw, double posX, double posZ) {
        double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity = 0;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if (deltaZ != 0) yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(yaw - (float) yawToEntity));
    }

    public static float getYawChange(double posX, double posZ) {
        double deltaX = posX - mc.thePlayer.posX;
        double deltaZ = posZ - mc.thePlayer.posZ;
        double yawToEntity;
        if (deltaZ < 0.0D && deltaX < 0.0D) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaZ < 0.0D && deltaX > 0.0D) {
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float) yawToEntity));
    }

    public static boolean isVisibleFOV(final Entity e, final float fov) {
        return ((Math.abs(getRotationsEntity((EntityLivingBase) e)[0] - mc.thePlayer.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(getRotationsEntity((EntityLivingBase) e)[0] - mc.thePlayer.rotationYaw) % 360.0f) : (Math.abs(getRotationsEntity((EntityLivingBase) e)[0] - mc.thePlayer.rotationYaw) % 360.0f)) <= fov;
    }
}
