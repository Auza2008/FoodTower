/*
Author:SuMuGod
Date:2022/7/10 5:21
Project:foodtower Reborn
*/
package me.dev.foodtower.ui;

import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.utils.math.Colors;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.utils.normal.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class Notification {
    private final String message;
    private final TimeHelper timer;
    private final int color;
    private final ResourceLocation image;
    private final long stayTime;
    Minecraft mc = Minecraft.getMinecraft();
    private double lastY;
    private double posY;
    private double width;
    private double height;
    private double animationX;
    private int imageWidth;

    public Notification(final String message, final Type type) {
        this.message = message;
        (this.timer = new TimeHelper()).reset();
        this.width = FontManager.F16.getStringWidth(message) + 20;
        this.height = 20.0;
        this.animationX = this.width;
        this.stayTime = 2000L;
        this.imageWidth = 16;
        this.posY = -1.0;
        this.image = new ResourceLocation("foodtower/Notification/" + type.name() + ".png");
        this.color = Colors.DARKGREY.c;

//		this.color = new Color(255, 255, 255,220).getRGB();
    }

    public static int reAlpha(final int n, final float n2) {
        final Color color = new Color(n);
        return new Color(0.003921569f * color.getRed(), 0.003921569f * color.getGreen(), 0.003921569f * color.getBlue(), n2).getRGB();
    }

    public void draw(final double getY, final double lastY) {
        this.width = FontManager.F16.getStringWidth(this.message) + 45;
        this.height = 22.0D;
        this.imageWidth = 11;
        this.lastY = lastY;
        this.animationX = RenderUtil.getAnimationState(this.animationX, this.isFinished() ? this.width : 0.0D, Math.max(this.isFinished() ? 200 : 30, Math.abs(this.animationX - (this.isFinished() ? this.width : 0.0D)) * 20.0D) * 0.3D);
        if (this.posY == -1.0D) {
            this.posY = getY;
        } else {
            this.posY = RenderUtil.getAnimationState(this.posY, getY, 200.0D);
        }

        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int x1 = (int) ((double) res.getScaledWidth() - this.width + this.animationX);
        int x2 = (int) ((double) res.getScaledWidth() + this.animationX);
        int y1 = (int) this.posY - 22;
        int y2 = (int) ((double) y1 + this.height);
        RenderUtil.drawRect(x1 + 30, y1, x2, y2, new Color(23, 23, 23).getRGB());
        RenderUtil.drawRoundedRect((float) x1, (float) y1, (float) x2, (float) y2, 2, new Color(23, 23, 23).getRGB());
        //BlurUtil.blurAreaBoarder(x1, y1, x1, y1, 170);
        RenderUtil.drawImage(this.image, (int) ((double) x1 + (this.height - (double) this.imageWidth) / 2.0D) - 1, y1 + (int) ((this.height - (double) this.imageWidth) / 2.0D), this.imageWidth, this.imageWidth);
        ++y1;
        if (this.message.contains(" Enabled")) {
            FontManager.F16.drawString(this.message.replace(" Enabled", ""), (float) (x1 + 19), (float) ((double) y1 + this.height / 4.0D), -1);
            FontManager.F16.drawString(" Enabled", (float) (x1 + 20 + FontManager.F16.getStringWidth(this.message.replace(" Enabled", ""))), (float) ((double) y1 + this.height / 4.0D), Colors.GREEN.c);
        } else if (this.message.contains(" Disabled")) {
            FontManager.F16.drawString(this.message.replace(" Disabled", ""), (float) (x1 + 19), (float) ((double) y1 + this.height / 4.0D), -1);
            FontManager.F16.drawString(" Disabled", (float) (x1 + 20 + FontManager.F16.getStringWidth(this.message.replace(" Disabled", ""))), (float) ((double) y1 + this.height / 4.0D), Colors.RED.c);
        } else {
            float var10002 = (float) (x1 + 20);
            float var10003 = (float) ((double) y1 + this.height / 4.0D);
            FontManager.F16.drawString(this.message, var10002, var10003, -1);
        }

    }

    public boolean shouldDelete() {
        return this.isFinished() && this.animationX >= this.width;
    }

    private boolean isFinished() {
        return this.timer.isDelayComplete(this.stayTime) && this.posY == this.lastY;
    }

    public double getHeight() {
        return this.height;
    }

    public enum Type {
        SUCCESS("SUCCESS", 0),
        INFO("INFO", 1),
        WARNING("WARNING", 2),
        ERROR("ERROR", 3);

        Type(final String s, final int n) {
        }
    }
}
