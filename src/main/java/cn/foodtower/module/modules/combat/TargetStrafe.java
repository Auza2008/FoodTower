package cn.foodtower.module.modules.combat;

import cn.foodtower.Client;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.entity.MovementUtils;
import cn.foodtower.util.entity.entitycheck.EntityValidator;
import cn.foodtower.util.entity.entitycheck.checks.VoidCheck;
import cn.foodtower.util.entity.entitycheck.checks.WallCheck;
import cn.foodtower.util.render.RenderUtil;
import cn.foodtower.util.render.gl.GLUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TargetStrafe extends Module {
    public static Mode Esp = new Mode("ESP", EspMode.values(), EspMode.Round);
    private final Numbers<Double> radius = new Numbers<>("Radius", 2.0, 0.1, 4.0, 0.1);
    private final Option directionKeys = new Option("DirectionKeys", true);
    private final Option space = new Option("OnJump", false);
    private final EntityValidator targetValidator;
    private KillAura aura;
    private int direction = -1;

    public TargetStrafe() {
        super("TargetStrafe", new String[]{"TargetStrafe"}, ModuleType.Combat);
        this.addValues(radius, Esp, directionKeys, space);
        this.targetValidator = new EntityValidator();
        this.targetValidator.add(new VoidCheck());
        this.targetValidator.add(new WallCheck());
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsEntity(EntityLivingBase entity) {
        return getRotations(entity.posX, entity.posY + (double) entity.getEyeHeight() - 0.4, entity.posZ);
    }

    @Override
    public void onEnable() {
        if (this.aura == null) {
            this.aura = (KillAura) ModuleManager.getModuleByClass(KillAura.class);
        }
    }

    @EventHandler
    public final void onUpdate(EventMotionUpdate event) {
        if (event.isPre()) {
            if (mc.thePlayer.isCollidedHorizontally) {
                this.switchDirection();
            }
            if (mc.gameSettings.keyBindLeft.isPressed() && directionKeys.getValue()) {
                this.direction = 1;
            }
            if (mc.gameSettings.keyBindRight.isPressed() && directionKeys.getValue()) {
                this.direction = -1;
            }
        }
    }

    @EventHandler(priority = 2)
    private void onMove(EventMove eventMove) {
        double speed = MoveUtils.getSpeed();
        if (!canStrafe()) return;
        strafe(eventMove, speed);
    }

    private void switchDirection() {
        this.direction = this.direction == 1 ? -1 : 1;
    }

    public void strafe(EventMove event, double moveSpeed) {
        EntityLivingBase target = KillAura.curTarget;
        float[] rotations = getRotationsEntity(target);
        if ((double) mc.thePlayer.getDistanceToEntity(target) <= this.radius.getValue()) {
            MovementUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 0.0);
        } else {
            MovementUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 1.0);
        }
    }

    public boolean canStrafe() {
        if (this.aura == null) {
            this.aura = (KillAura) ModuleManager.getModuleByClass(KillAura.class);
        }
        return this.aura.isEnabled() && KillAura.curTarget != null && this.isEnabled() && this.targetValidator.validate(KillAura.curTarget) && (!this.space.getValue() || mc.gameSettings.keyBindJump.isKeyDown());
    }

    @EventHandler
    public final void onRender3D(EventRender3D event) {
        switch ((EspMode) Esp.getValue()) {
            case Round: {
                if (canStrafe()) {
                    drawCircle(KillAura.curTarget, event.getPartialTicks(), radius.getValue());
                }
                break;
            }
            case Polygon: {
                if (canStrafe()) {
                    drawPolygon(KillAura.curTarget, event.getPartialTicks(), radius.getValue(), 2.0f, new Color(255, 255, 255));
                    //drawPolygon(KillAura.target, event.getPartialTicks(), Radius.getValue(),2.5f,new Color(0,0,0));
                }
                break;
            }
        }
    }

    private void drawCircle(Entity entity, float partialTicks, double rad) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        RenderUtil.startDrawing();
        GLUtils.startSmooth();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(3);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY;
        Color color = Color.WHITE;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        if (entity == KillAura.curTarget && ModuleManager.getModuleByName("Speed").isEnabled()) {
            color = Client.getBlueColor(1);
        }

        float r = 0.003921569F * (float) color.getRed();
        float g = 0.003921569F * (float) color.getGreen();
        float b = 0.003921569F * (float) color.getBlue();
        double pix2 = 6.283185307179586D;

        for (int i = 0; i <= 90; ++i) {
            GL11.glColor3f(r, g, b);
            GL11.glVertex3d(x + rad * Math.cos((double) i * 6.283185307179586D / 45.0D), y, z + rad * Math.sin((double) i * 6.283185307179586D / 45.0D));
        }

        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        RenderUtil.stopDrawing();
        GLUtils.endSmooth();
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }

    private void drawPolygon(Entity entity, float partialTicks, double rad, float Line, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        RenderUtil.startDrawing();
        GLUtils.startSmooth();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(Line);
        GL11.glBegin(3);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY;
        //Color color = Color.WHITE;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;

        float r = 0.004921569F * (float) color.getRed();
        float g = 0.003921569F * (float) color.getGreen();
        float b = 0.003921569F * (float) color.getBlue();

        for (int i = 0; i <= 90; ++i) {
            GL11.glColor3f(r, g, b);
            GL11.glVertex3d(x + rad * Math.cos((double) i * 35.283185307179586D / 90.0D), y, z + rad * Math.sin((double) i * 35.283185307179586D / 90.0D));
        }

        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        RenderUtil.stopDrawing();
        GLUtils.endSmooth();
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }

    enum EspMode {
        Round, Polygon
    }
}
