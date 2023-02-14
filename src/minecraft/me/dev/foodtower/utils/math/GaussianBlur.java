/*
Author:SuMuGod
Date:2022/7/10 6:01
Project:foodtower Reborn
*/
package me.dev.foodtower.utils.math;

import me.dev.foodtower.utils.normal.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL20.glUniform1;

public class GaussianBlur {

    public static ShaderUtil blurShader = new ShaderUtil("foodtower/gaussian.frag");

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);


    public static void setupUniforms(float dir1, float dir2, float radius) {
        blurShader.setUniformi("textureIn", 0);
        blurShader.setUniformf("texelSize", 1.0F / (float) Minecraft.getMinecraft().displayWidth, 1.0F / (float) Minecraft.getMinecraft().displayHeight);
        blurShader.setUniformf("direction", dir1, dir2);
        blurShader.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtils.calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        glUniform1(blurShader.getUniform("weights"), weightBuffer);
    }


    public static void renderBlur(float radius) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);


        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        blurShader.init();
        setupUniforms(1, 0, radius);

        RenderUtil.bindTexture(Minecraft.getMinecraft().getFramebuffer().framebufferTexture);

        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        blurShader.unload();

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        blurShader.init();
        setupUniforms(0, 1, radius);

        RenderUtil.bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        blurShader.unload();

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
    }

}

