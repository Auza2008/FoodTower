/*
Author:SuMuGod
Date:2022/7/10 5:08
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.AntiBot;
import me.dev.foodtower.other.FriendManager;
import me.dev.foodtower.utils.math.MathUtil;
import me.dev.foodtower.utils.normal.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers
        extends Module {
    public Tracers() {
        super("Tracers", "线", new String[]{"lines", "tracer"}, ModuleType.Render);
        this.setColor(new Color(60, 136, 166).getRGB());
    }

    @NMSL
    private void on3DRender(EventRender3D e) {
        for (Object o : this.mc.theWorld.loadedEntityList) {
            double[] arrd;
            Entity entity = (Entity) o;
            if (!entity.isEntityAlive() || !(entity instanceof EntityPlayer) || entity == this.mc.thePlayer || entity.isInvisible() || AntiBot.isServerBot(entity))
                continue;
            double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) e.getPartialTicks() - RenderManager.renderPosX;
            double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) e.getPartialTicks() - RenderManager.renderPosY;
            double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) e.getPartialTicks() - RenderManager.renderPosZ;
            boolean old = this.mc.gameSettings.viewBobbing;
            RenderUtil.startDrawing();
            this.mc.gameSettings.viewBobbing = false;
            this.mc.entityRenderer.setupCameraTransform(this.mc.timer.renderPartialTicks, 2);
            this.mc.gameSettings.viewBobbing = old;
            float color = (float) Math.round(255.0 - this.mc.thePlayer.getDistanceSqToEntity(entity) * 255.0 / MathUtil.square((double) this.mc.gameSettings.renderDistanceChunks * 2.5)) / 255.0f;
            if (FriendManager.isFriend(entity.getName())) {
                double[] arrd2 = new double[3];
                arrd2[0] = 0.0;
                arrd2[1] = 1.0;
                arrd = arrd2;
                arrd2[2] = 1.0;
            } else {
                double[] arrd3 = new double[3];
                arrd3[0] = color;
                arrd3[1] = 1.0f - color;
                arrd = arrd3;
                arrd3[2] = 0.0;
            }
            this.drawLine(entity, arrd, posX, posY, posZ);
            RenderUtil.stopDrawing();
        }
    }

    public static void drawLine(Entity entity, double[] color, double x, double y, double z) {
        double distance = mc.thePlayer.getDistanceToEntity(entity);
        double xD = distance / 48.0f;
        if (xD >= 1.0f) {
            xD = 1.0f;
        }
        boolean entityesp = false;
        GL11.glEnable((int) 2848);
        if (color.length >= 4) {
            if (color[3] <= 0.1) {
                return;
            }
            GL11.glColor4d((double) color[0], (double) color[1], (double) color[2], (double) color[3]);
        } else {
            GL11.glColor3d((double) color[0], (double) color[1], (double) color[2]);
        }
        GL11.glLineWidth((float) 1.0f);
        GL11.glBegin((int) 1);
        GL11.glVertex3d((double) 0.0, (double) mc.thePlayer.getEyeHeight(), (double) 0.0);
        GL11.glVertex3d((double) x, (double) y, (double) z);
        GL11.glEnd();
        GL11.glDisable((int) 2848);
    }
}


