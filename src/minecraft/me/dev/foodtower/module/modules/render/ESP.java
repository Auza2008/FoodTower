/*
Author:SuMuGod
Date:2022/7/10 5:03
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.world.Teams;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Mode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.client.renderer.RenderHelper.glColor;

public class ESP extends Module {
    public Mode<Enum> mode = new Mode("Mode", "mode", ESPMode.values(), ESPMode.CSGO);

    public ESP() {
        super("ESP", "超觉之", new String[]{"outline", "wallhack"}, ModuleType.Render);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    }

    public static void entityESPBox(Entity e, Color color, EventRender3D event) {
        double posX = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks() - RenderManager.renderPosX;
        double posY = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks() - RenderManager.renderPosY;
        double posZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks() - RenderManager.renderPosZ;
        AxisAlignedBB box = AxisAlignedBB.fromBounds(posX - (double) e.width, posY, posZ - (double) e.width, posX + (double) e.width, posY + (double) e.height + 0.2, posZ + (double) e.width);
        if (e instanceof EntityLivingBase) {
            box = AxisAlignedBB.fromBounds(posX - (double) e.width + 0.2, posY, posZ - (double) e.width + 0.2, posX + (double) e.width - 0.2, posY + (double) e.height + (e.isSneaking() ? 0.02 : 0.2), posZ + (double) e.width - 0.2);
        }
        GL11.glLineWidth(4.0f);
        GL11.glColor4f(0f, 0f, 0f, 1f);
        RenderUtil.drawOutlinedBoundingBox(box);
        GL11.glLineWidth(1.5f);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, 1f);
        RenderUtil.drawOutlinedBoundingBox(box);

    }

    public static void entityESPBox2(Entity e, Color color, EventRender3D event) {
        double posX = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks() - RenderManager.renderPosX;
        double posY = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks() - RenderManager.renderPosY;
        double posZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks() - RenderManager.renderPosZ;
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        RenderUtil.drawBoundingBox(new AxisAlignedBB(posX + 0.5, posY, posZ - 0.5, posX - 0.5, posY + 1.9, posZ + 0.5));
    }

    public static void drawBorderedRect(float x2, float y2, float x22, float y22, float l1, int col1, int col2) {
        ESP.drawRect(x2, y2, x22, y22, col2);
        float f2 = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f22 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f22, f3, f2);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y22);
        GL11.glVertex2d(x22, y22);
        GL11.glVertex2d(x22, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x22, y2);
        GL11.glVertex2d(x2, y22);
        GL11.glVertex2d(x22, y22);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawRect(float g2, float h2, float i2, float j2, int col1) {
        float f2 = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f22 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f22, f3, f2);
        GL11.glBegin(7);
        GL11.glVertex2d(i2, h2);
        GL11.glVertex2d(g2, h2);
        GL11.glVertex2d(g2, j2);
        GL11.glVertex2d(i2, j2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        glColor(topColor);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        glColor(bottomColor);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        disableGL2D();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @NMSL
    public void onRender(EventRender3D event) {
        this.setSuffix(mode.getValue());
        if (this.mode.getValue().toString().equals("Box")) {
            this.doBoxESP2(event);
        }
        if (this.mode.getValue().toString().equals("Box2")) {
            this.doBoxESP(event);
        }
        if (this.mode.getValue().toString().equals("CSGO")) {
            this.doOther2DESP();
        }

    }

    private void doBoxESP(EventRender3D event) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        for (Object o2 : mc.theWorld.loadedEntityList) {
            if (!(o2 instanceof EntityPlayer) || o2 == mc.thePlayer) continue;
            EntityPlayer ent = (EntityPlayer) o2;
            if (Teams.isOnSameTeam(ent)) {
                entityESPBox2(ent, new Color(0, 255, 0, 100), event);
                continue;
            }
            if (ent.hurtTime > 0) {
                //RenderUtil.drawBoundingBox((AxisAlignedBB)new AxisAlignedBB(ent.motionX, ent.motionY, ent.motionZ, ent.motionX-1, ent.motionY-1, ent.motionZ-1));
                entityESPBox2(ent, new Color(255, 0, 0, 100), event);
                continue;
            }
            if (ent.isInvisible()) {
                //RenderUtil.drawBoundingBox((AxisAlignedBB)new AxisAlignedBB(ent.motionX, ent.motionY, ent.motionZ, ent.motionX-1, ent.motionY-1, ent.motionZ-1));
                //entityESPBox2(ent, new Color(155, 155, 255, 100), event);
                continue;
            }

            entityESPBox2(ent, new Color(255, 255, 255, 100), event);

        }
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }

    private void doBoxESP2(EventRender3D event) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        for (Object o2 : mc.theWorld.loadedEntityList) {
            if (!(o2 instanceof EntityPlayer) || o2 == mc.thePlayer) continue;
            EntityPlayer ent = (EntityPlayer) o2;
            if (Teams.isOnSameTeam(ent)) {
                entityESPBox(ent, new Color(0, 255, 0), event);
                continue;
            }
            if (ent.hurtTime > 0) {
                entityESPBox(ent, new Color(255, 0, 0), event);
                continue;
            }
            if (ent.isInvisible()) {
                entityESPBox(ent, new Color(255, 255, 0), event);
                continue;
            }
            entityESPBox(ent, new Color(255, 255, 255), event);
        }
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }

    private boolean isValid(EntityLivingBase entity) {
        return entity instanceof EntityPlayer && entity.getHealth() >= 0.0f && entity != mc.thePlayer;
    }

    private void doOther2DESP() {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (!this.isValid(entity)) continue;
            if (entity.isInvisible()) continue;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            float partialTicks = mc.timer.renderPartialTicks;
            mc.getRenderManager();
            double x2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - RenderManager.renderPosX;
            mc.getRenderManager();
            double y2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - RenderManager.renderPosY;
            mc.getRenderManager();
            double z2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - RenderManager.renderPosZ;
            float DISTANCE = mc.thePlayer.getDistanceToEntity(entity);
            float DISTANCE_SCALE = Math.min(DISTANCE * 0.15f, 0.15f);
            float SCALE = 0.035f;
            float xMid = (float) x2;
            float yMid = (float) y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f);
            float zMid = (float) z2;
            GlStateManager.translate((float) x2, (float) y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float) z2);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            mc.getRenderManager();
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(-SCALE, -SCALE, -(SCALE /= 2.0f));
            Tessellator tesselator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tesselator.getWorldRenderer();
            float HEALTH = entity.getHealth();
            int COLOR = -1;
            COLOR = (double) HEALTH > 20.0 ? -65292 : ((double) HEALTH >= 10.0 ? -16711936 : ((double) HEALTH >= 3.0 ? -23296 : -65536));
            Color gray = new Color(0, 0, 0);
            double thickness = 1 + DISTANCE * 0.01f;
            double xLeft = -15.0;
            double xRight = 15.0;
            double yUp = 10.0;
            double yDown = 70.0;
            double size = 10.0;
            Color color = new Color(255, 255, 255);
            if (entity.hurtTime > 0) {
                color = new Color(255, 0, 0);
            } else if (Teams.isOnSameTeam(entity)) {
                color = new Color(0, 255, 0);
            } else if (entity.isInvisible()) {
                color = new Color(255, 255, 0);
            }
            drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness + 0.2f, Color.BLACK.getRGB(), 0);
            drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness, color.getRGB(), 0);
            drawBorderedRect((float) xLeft - 2.0f - DISTANCE * 0.1f, (float) yDown - (float) (yDown - yUp), (float) xLeft - 2.0f, (float) yDown, 0.15f, new Color(100, 100, 100).getRGB(), new Color(100, 100, 100).getRGB());
            Color c1 = new Color(Color.HSBtoRGB(mc.thePlayer.ticksExisted / 25f, 0.7f, 1));
            Color c2 = new Color(Color.HSBtoRGB(mc.thePlayer.ticksExisted / 50f, 0.7f, 1));
            drawGradientRect((float) xLeft - 2.0f - DISTANCE * 0.1f, (float) yDown - (float) (yDown - yUp) * Math.min(1.0f, entity.getHealth() / 20.0f), (float) xLeft - 2.0f, (float) yDown, c1.getRGB(), c2.getRGB());
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }

    public void pre() {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }

    public void post() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0, 1.0, 1.0);
    }

    enum ESPMode {
        CSGO, Box, Box2
    }
}