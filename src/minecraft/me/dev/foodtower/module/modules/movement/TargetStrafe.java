/*
Author:SuMuGod
Date:2022/7/10 4:33
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.Killaura;
import me.dev.foodtower.utils.math.RotationUtil;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.utils.normal.PlayerUtil;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.optifine.vecmath.Vector3d;

public class TargetStrafe extends Module {
    public TargetStrafe() {
        super("TargetStrafe", "转圈一圈", new String[]{"TargetStrafe"}, ModuleType.Movement);
    }

    public static boolean direction = true;
    private final Mode mode = new Mode("Mode", "mode", TargetStrafeMode.values(), TargetStrafeMode.Adaptive);
    private Numbers<Double> range = new Numbers<Double>("Range", "range", 2.0, 0.0, 6.0, 0.1);
    private Option<Boolean> render = new Option<>("Render", "render", true);
    public static final Option<Boolean> onlyspeed = new Option<>("OnlySpeed", "only Speed", true);
    public static final Option<Boolean> jumpkey = new Option<>("OnlyJump", "OnlyJump", true);
    private final Option<Boolean> lockPersonView = new Option<>("LockPersonView", "lockPersonView", true);

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        if (lockPersonView.getValue() && Client.instance.getModuleManager().getModuleByClass(Killaura.class).isEnabled()) {
            if ((Client.instance.getModuleManager().getModuleByClass(Speed.class).isEnabled() || Client.instance.getModuleManager().getModuleByClass(Flight.class).isEnabled())) {
                if (Killaura.target != null && !Killaura.target.isDead) {
                    mc.gameSettings.thirdPersonView = 1;
                } else if ((Killaura.target != null && Killaura.target.isDead) || !Client.instance.getModuleManager().getModuleByClass(Killaura.class).isEnabled()) {
                    mc.gameSettings.thirdPersonView = 0;
                }
            }
        }
    }

    @NMSL
    private void onMove(EventMove em) {
        if (PlayerUtil.isMoving2()) {
            if (Killaura.target != null && !Killaura.target.isDead) {
                if (onlyspeed.getValue() && Client.instance.getModuleManager().getModuleByClass(Speed.class).isEnabled()) {
                    if (jumpkey.getValue() && mc.gameSettings.keyBindJump.pressed) {
                        move(em, MoveUtils.defaultSpeed(), Killaura.target);
                    } else if (!jumpkey.getValue()) {
                        move(em, MoveUtils.defaultSpeed(), Killaura.target);
                    }
                } else if (!onlyspeed.getValue()) {
                    if (jumpkey.getValue() && mc.gameSettings.keyBindJump.pressed) {
                        move(em, MoveUtils.defaultSpeed(), Killaura.target);
                    } else if (!jumpkey.getValue()) {
                        move(em, MoveUtils.defaultSpeed(), Killaura.target);
                    }
                }
            }
        }
    }

    public void move(EventMove event, double speed, Entity entity) {
        if (isBlockUnder(entity) && mode.getValue() == TargetStrafeMode.Adaptive) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
            if (event != null) {
                event.setX(0);
                event.setZ(0);
            }
            return;
        }
        if (isBlockUnder(mc.thePlayer) && mode.getValue() == TargetStrafeMode.Adaptive && !Client.instance.getModuleManager().getModuleByClass(Flight.class).isEnabled())
            direction = !direction;

        if (mc.thePlayer.isCollidedHorizontally && mode.getValue() == TargetStrafeMode.Adaptive)
            direction = !direction;

        float strafe = direction ? 1 : -1;
        float diff = (float) (speed / (range.getValue() * Math.PI * 2)) * 360 * strafe;
        float[] rotation = RotationUtil.getNeededRotations(new Vector3d(entity.posX, entity.posY, entity.posZ), new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));

        rotation[0] += diff;
        float dir = rotation[0] * (float) (Math.PI / 180F);

        double x = entity.posX - Math.sin(dir) * range.getValue();
        double z = entity.posZ + Math.cos(dir) * range.getValue();

        float yaw = RotationUtil.getNeededRotations(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3d(x, entity.posY, z))[0] * (float) (Math.PI / 180F);

        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
        if (event != null) {
            event.setX(mc.thePlayer.motionX);
            event.setZ(mc.thePlayer.motionZ);
        }
    }

    private static boolean isBlockUnder(Entity entity) {
        for (int i = (int) (entity.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(entity.posX,
                    i, entity.posZ);
            if (Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            return false;
        }
        return true;
    }

    @NMSL
    private void onRender(EventRender3D e) {
        if (Killaura.target != null && !Killaura.target.isDead && render.getValue()) {
            RenderUtil.drawCircle(Killaura.target, e.getPartialTicks(), range.getValue());
        }
    }

    enum TargetStrafeMode {
        Simple,
        Adaptive
    }
}

