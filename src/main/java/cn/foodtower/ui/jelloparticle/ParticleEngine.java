package cn.foodtower.ui.jelloparticle;

import cn.foodtower.util.anim.AnimationUtil;
import cn.foodtower.util.render.RenderingUtil;
import cn.foodtower.util.render.gl.GLUtils;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticleEngine {

    public Color cc = new Color(255, 255, 255);
    public CopyOnWriteArrayList<Particle> particles = Lists.newCopyOnWriteArrayList();
    public float lastMouseX;
    public float lastMouseY;

    public ParticleEngine(Color c) {
        cc = c;
    }

    public ParticleEngine() {

    }

    public void render(float mouseX, float mouseY) {
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.color(1, 1, 1, 1);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float xOffset = ScaledResolution.getScaledWidth() / 2f - mouseX;
        float yOffset = ScaledResolution.getScaledHeight() / 2f - mouseY;
        for (particles.size(); particles.size() < (int) (ScaledResolution.getScaledWidth() / 19.2f); particles.add(new Particle(sr, new Random().nextFloat() * 2 + 2, new Random().nextFloat() * 5 + 5)))
            ;
        List<Particle> toremove = Lists.newArrayList();
        for (Particle p : particles) {
            p.opacity = AnimationUtil.moveUD(p.opacity, 32, 10f / Minecraft.getDebugFPS(), 3f / Minecraft.getDebugFPS());
            Color c = new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), (int) p.opacity);
            GLUtils.startSmooth();
            RenderingUtil.drawBorderedCircle(p.x + MathHelper.sin(p.ticks / 2) * 50 + -xOffset / 5, ScaledResolution.getScaledHeight() - ((p.ticks * p.speed) * p.ticks / 10 + yOffset / 5), p.radius * (p.opacity / 32), c.getRGB(), c.getRGB());
            GLUtils.endSmooth();
            p.ticks += (0.9f / (float) Minecraft.getDebugFPS());// +(0.005*1.777*(GLUtils.getMouseX()-lastMouseX) + 0.005*(GLUtils.getMouseY()-lastMouseY));
            if (((p.ticks * p.speed) * p.ticks / 10 + yOffset / 5) > ScaledResolution.getScaledHeight() || ((p.ticks * p.speed) * p.ticks / 10 + yOffset / 5) < 0 || (p.x + MathHelper.sin(p.ticks / 2) * 50 + -xOffset / 5) > ScaledResolution.getScaledWidth() || (p.x + MathHelper.sin(p.ticks / 2) * 50 + -xOffset / 5) < 0) {
                toremove.add(p);
            }
        }

        particles.removeAll(toremove);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        lastMouseX = GLUtils.getMouseX();
        lastMouseY = GLUtils.getMouseY();
    }
}
