/*
Author:SuMuGod
Date:2022/7/10 5:22
Project:foam Reborn
*/
package me.dev.foam.ui.clickGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class R2DUtils {
    public static void enableGL2D() {
        GL11.glDisable((int) 2929);
        GL11.glEnable((int) 3042);
        GL11.glDisable((int) 3553);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glDepthMask((boolean) true);
        GL11.glEnable((int) 2848);
        GL11.glHint((int) 3154, (int) 4354);
        GL11.glHint((int) 3155, (int) 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable((int) 3553);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 2929);
        GL11.glDisable((int) 2848);
        GL11.glHint((int) 3154, (int) 4352);
        GL11.glHint((int) 3155, (int) 4352);
    }

    public static void draw2DCorner(Entity e, double posX, double posY, double posZ, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        GL11.glNormal3f((float) 0.0f, (float) 0.0f, (float) 0.0f);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(-0.1, -0.1, 0.1);
        GL11.glDisable((int) 2896);
        GL11.glDisable((int) 2929);
        GL11.glEnable((int) 3042);
        GL11.glBlendFunc((int) 770, (int) 771);
        GlStateManager.depthMask(true);
        R2DUtils.drawRect(7.0, -20.0, 7.300000190734863, -17.5, color);
        R2DUtils.drawRect(-7.300000190734863, -20.0, -7.0, -17.5, color);
        R2DUtils.drawRect(4.0, -20.299999237060547, 7.300000190734863, -20.0, color);
        R2DUtils.drawRect(-7.300000190734863, -20.299999237060547, -4.0, -20.0, color);
        R2DUtils.drawRect(-7.0, 3.0, -4.0, 3.299999952316284, color);
        R2DUtils.drawRect(4.0, 3.0, 7.0, 3.299999952316284, color);
        R2DUtils.drawRect(-7.300000190734863, 0.8, -7.0, 3.299999952316284, color);
        R2DUtils.drawRect(7.0, 0.5, 7.300000190734863, 3.299999952316284, color);
        R2DUtils.drawRect(7.0, -20.0, 7.300000190734863, -17.5, color);
        R2DUtils.drawRect(-7.300000190734863, -20.0, -7.0, -17.5, color);
        R2DUtils.drawRect(4.0, -20.299999237060547, 7.300000190734863, -20.0, color);
        R2DUtils.drawRect(-7.300000190734863, -20.299999237060547, -4.0, -20.0, color);
        R2DUtils.drawRect(-7.0, 3.0, -4.0, 3.299999952316284, color);
        R2DUtils.drawRect(4.0, 3.0, 7.0, 3.299999952316284, color);
        R2DUtils.drawRect(-7.300000190734863, 0.8, -7.0, 3.299999952316284, color);
        R2DUtils.drawRect(7.0, 0.5, 7.300000190734863, 3.299999952316284, color);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 2929);
        GlStateManager.popMatrix();
    }

    public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
        R2DUtils.enableGL2D();
        GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
        R2DUtils.drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
        R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
        R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
        R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
        R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
        R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
        R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
        R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
        R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
        R2DUtils.disableGL2D();
        Gui.drawRect(0, 0, 0, 0, 0);
    }

    public static void drawRect(double x2, double y2, double x1, double y1, int color) {
        R2DUtils.enableGL2D();
        R2DUtils.glColor(color);
        R2DUtils.drawRect(x2, y2, x1, y1);
        R2DUtils.disableGL2D();
    }

    private static void drawRect(double x2, double y2, double x1, double y1) {
        GL11.glBegin((int) 7);
        GL11.glVertex2d((double) x2, (double) y1);
        GL11.glVertex2d((double) x1, (double) y1);
        GL11.glVertex2d((double) x1, (double) y2);
        GL11.glVertex2d((double) x2, (double) y2);
        GL11.glEnd();
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 255) / 255.0f;
        float red = (float) (hex >> 16 & 255) / 255.0f;
        float green = (float) (hex >> 8 & 255) / 255.0f;
        float blue = (float) (hex & 255) / 255.0f;
        GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
    }

    public static void drawRect(float x, float y, float x1, float y1, int color) {
        R2DUtils.enableGL2D();
        glColor(color);
        R2DUtils.drawRect(x, y, x1, y1);
        R2DUtils.disableGL2D();
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor) {
        R2DUtils.enableGL2D();
        glColor(borderColor);
        R2DUtils.drawRect(x + width, y, x1 - width, y + width);
        R2DUtils.drawRect(x, y, x + width, y1);
        R2DUtils.drawRect(x1 - width, y, x1, y1);
        R2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
        R2DUtils.disableGL2D();
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
        R2DUtils.enableGL2D();
        GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
        R2DUtils.drawVLine(x *= 2.0f, y *= 2.0f, y1 *= 2.0f, borderC);
        R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y, y1, borderC);
        R2DUtils.drawHLine(x, x1 - 1.0f, y, borderC);
        R2DUtils.drawHLine(x, x1 - 2.0f, y1 - 1.0f, borderC);
        R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
        R2DUtils.disableGL2D();
    }

    public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        R2DUtils.enableGL2D();
        GL11.glShadeModel((int) 7425);
        GL11.glBegin((int) 7);
        glColor(topColor);
        GL11.glVertex2f((float) x, (float) y1);
        GL11.glVertex2f((float) x1, (float) y1);
        glColor(bottomColor);
        GL11.glVertex2f((float) x1, (float) y);
        GL11.glVertex2f((float) x, (float) y);
        GL11.glEnd();
        GL11.glShadeModel((int) 7424);
        R2DUtils.disableGL2D();
    }

    public static void drawHLine(float x, float y, float x1, int y1) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        R2DUtils.drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
    }

    public static void drawVLine(float x, float y, float x1, int y1) {
        if (x1 < y) {
            float var5 = y;
            y = x1;
            x1 = var5;
        }
        R2DUtils.drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
    }

    public static void drawHLine(float x, float y, float x1, int y1, int y2) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        R2DUtils.drawGradientRect(x, x1, y + 1.0f, x1 + 1.0f, y1, y2);
    }

    public static void drawRect(float x, float y, float x1, float y1) {
        GL11.glBegin((int) 7);
        GL11.glVertex2f((float) x, (float) y1);
        GL11.glVertex2f((float) x1, (float) y1);
        GL11.glVertex2f((float) x1, (float) y);
        GL11.glVertex2f((float) x, (float) y);
        GL11.glEnd();
    }
}
