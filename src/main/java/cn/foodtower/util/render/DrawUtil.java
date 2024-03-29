package cn.foodtower.util.render;


import cn.foodtower.util.render.gl.GLShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.optifine.util.MathUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static cn.foodtower.util.render.ClientPhysic.mc;
import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL20.*;

public final class DrawUtil {

    public static final String VERTEX_SHADER = "#version 120 \n" + "\n" + "void main() {\n" + "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" + "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" + "}";
    private static final FloatBuffer WND_POS_BUFFER = GLAllocation.createDirectFloatBuffer(4);
    private static final IntBuffer VIEWPORT_BUFFER = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer MODEL_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer PROJECTION_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    private static final IntBuffer SCISSOR_BUFFER = GLAllocation.createDirectIntBuffer(16);
    private static final String CIRCLE_FRAG_SHADER = "#version 120\n" + "\n" + "uniform float innerRadius;\n" + "uniform vec4 colour;\n" + "\n" + "void main() {\n" + "   vec2 pixel = gl_TexCoord[0].st;\n" + "   vec2 centre = vec2(0.5, 0.5);\n" + "   float d = length(pixel - centre);\n" + "   float c = smoothstep(d+innerRadius, d+innerRadius+0.01, 0.5-innerRadius);\n" + "   float a = smoothstep(0.0, 1.0, c) * colour.a;\n" + "   gl_FragColor = vec4(colour.rgb, a);\n" + "}\n";
    private static final GLShader CIRCLE_SHADER = new GLShader(VERTEX_SHADER, CIRCLE_FRAG_SHADER) {
        @Override
        public void setupUniforms() {
            this.setupUniform("colour");
            this.setupUniform("innerRadius");
        }
    };
    private static final String ROUNDED_QUAD_FRAG_SHADER = "#version 120\n" + "uniform float width;\n" + "uniform float height;\n" + "uniform float radius;\n" + "uniform vec4 colour;\n" + "\n" + "float SDRoundedRect(vec2 p, vec2 b, float r) {\n" + "    vec2 q = abs(p) - b + r;\n" + "    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;\n" + "}\n" + "\n" + "void main() {\n" + "    vec2 size = vec2(width, height);\n" + "    vec2 pixel = gl_TexCoord[0].st * size;\n" + "    vec2 centre = 0.5 * size;\n" + "    float b = SDRoundedRect(pixel - centre, centre, radius);\n" + "    float a = 1.0 - smoothstep(0, 1.0, b);\n" + "    gl_FragColor = vec4(colour.rgb, colour.a * a);\n" + "}";
    private static final GLShader ROUNDED_QUAD_SHADER = new GLShader(VERTEX_SHADER, ROUNDED_QUAD_FRAG_SHADER) {
        @Override
        public void setupUniforms() {
            this.setupUniform("width");
            this.setupUniform("height");
            this.setupUniform("colour");
            this.setupUniform("radius");
        }
    };
    private static final String RAINBOW_FRAG_SHADER = "#version 120\n" + "uniform float width;\n" + "uniform float height;\n" + "uniform float radius;\n" + "uniform float u_time;\n" + "\n" + "float SDRoundedRect(vec2 p, vec2 b, float r) {\n" + "    vec2 q = abs(p) - b + r;\n" + "    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;\n" + "}\n" + "\n" + "void main() {\n" + "    vec2 size = vec2(width, height);\n" + "    vec2 pixel = gl_TexCoord[0].st * size;\n" + "    vec2 centre = 0.5 * size;\n" + "    float b = SDRoundedRect(pixel - centre, centre, radius);\n" + "    float a = 1.0 - smoothstep(0, 1.0, b);\n" + "    vec3 colour = 0.5 + 0.5*cos(u_time+gl_TexCoord[0].st.x+vec3(0,2,4));\n" + "    gl_FragColor = vec4(colour, a);\n" + "}";
    private static final GLShader GL_COLOUR_SHADER = new GLShader(VERTEX_SHADER, RAINBOW_FRAG_SHADER) {

        private final long initTime = System.currentTimeMillis();

        @Override
        public void setupUniforms() {
            this.setupUniform("width");
            this.setupUniform("height");
            this.setupUniform("radius");
            this.setupUniform("u_time");
        }

        @Override
        public void updateUniforms() {
            glUniform1f(glGetUniformLocation(getProgram(), "u_time"), (System.currentTimeMillis() - initTime) / 1000.0f);
        }
    };
    public static float ticks, ticksSinceClickgui;

    private DrawUtil() {
    }

    public static void drawRoundedRect(final float x, final float y, final float width, final float height, float edgeRadius, int color, final float borderWidth, int borderColor) {
        if (color == 16777215) {
            color = -65794;
        }
        if (borderColor == 16777215) {
            borderColor = -65794;
        }
        if (edgeRadius < 0.0f) {
            edgeRadius = 0.0f;
        }
        if (edgeRadius > width / 2.0f) {
            edgeRadius = width / 2.0f;
        }
        if (edgeRadius > height / 2.0f) {
            edgeRadius = height / 2.0f;
        }
        drawRDRect(x + edgeRadius, y + edgeRadius, width - edgeRadius * 2.0f, height - edgeRadius * 2.0f, color);
        drawRDRect(x + edgeRadius, y, width - edgeRadius * 2.0f, edgeRadius, color);
        drawRDRect(x + edgeRadius, y + height - edgeRadius, width - edgeRadius * 2.0f, edgeRadius, color);
        drawRDRect(x, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        drawRDRect(x + width - edgeRadius, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        enableRender2D();
        color(color);
        GL11.glBegin(6);
        float centerX = x + edgeRadius;
        float centerY = y + edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        for (int vertices = (int) Math.min(Math.max(edgeRadius, 10.0f), 90.0f), i = 0; i < vertices + 1; ++i) {
            final double angleRadians = 6.283185307179586 * (i + 180) / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        for (int vertices = (int) Math.min(Math.max(edgeRadius, 10.0f), 90.0f), i = 0; i < vertices + 1; ++i) {
            final double angleRadians = 6.283185307179586 * (i + 90) / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        for (int vertices = (int) Math.min(Math.max(edgeRadius, 10.0f), 90.0f), i = 0; i < vertices + 1; ++i) {
            final double angleRadians = 6.283185307179586 * (i + 270) / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        for (int vertices = (int) Math.min(Math.max(edgeRadius, 10.0f), 90.0f), i = 0; i < vertices + 1; ++i) {
            final double angleRadians = 6.283185307179586 * i / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glEnd();
        color(borderColor);
        GL11.glLineWidth(borderWidth);
        GL11.glBegin(3);
        centerX = x + edgeRadius;
        centerY = y + edgeRadius;
        int vertices;
        int i;
        for (vertices = (i = (int) Math.min(Math.max(edgeRadius, 10.0f), 90.0f)); i >= 0; --i) {
            final double angleRadians = 6.283185307179586 * (i + 180) / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glVertex2d((x + edgeRadius), y);
        GL11.glVertex2d((x + width - edgeRadius), y);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        for (i = vertices; i >= 0; --i) {
            final double angleRadians = 6.283185307179586 * (i + 90) / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glVertex2d((x + width), (y + edgeRadius));
        GL11.glVertex2d((x + width), (y + height - edgeRadius));
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        for (i = vertices; i >= 0; --i) {
            final double angleRadians = 6.283185307179586 * i / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glVertex2d((x + width - edgeRadius), (y + height));
        GL11.glVertex2d((x + edgeRadius), (y + height));
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        for (i = vertices; i >= 0; --i) {
            final double angleRadians = 6.283185307179586 * (i + 270) / (vertices * 4);
            GL11.glVertex2d(centerX + Math.sin(angleRadians) * edgeRadius, centerY + Math.cos(angleRadians) * edgeRadius);
        }
        GL11.glVertex2d(x, (y + height - edgeRadius));
        GL11.glVertex2d(x, (y + edgeRadius));
        GL11.glEnd();
        disableRender2D();
    }

    public static void disableRender2D() {
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void color(final int color, final float alpha) {
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        GlStateManager.color(r, g, b, alpha);
    }

    public static void color(final int color) {
        color(color, (color >> 24 & 0xFF) / 255.0f);
    }

    public static void enableRender2D() {
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
    }

    public static void drawRDRect(final float left, final float top, final float width, final float height, final int color) {
        final float f3 = (color >> 24 & 0xFF) / 255.0f;
        final float f4 = (color >> 16 & 0xFF) / 255.0f;
        final float f5 = (color >> 8 & 0xFF) / 255.0f;
        final float f6 = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f4, f5, f6, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, (top + height), 0.0).endVertex();
        worldrenderer.pos((left + width), (top + height), 0.0).endVertex();
        worldrenderer.pos((left + width), top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void renderEnchantText(ItemStack stack, int x, float y) {
        RenderHelper.disableStandardItemLighting();
        float enchantmentY = y + 24f;
        if (stack.getItem() instanceof ItemArmor) {
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int thornLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
            if (protectionLevel > 0) {
                DrawUtil.drawEnchantTag("P" + ColorUtils.getColor(protectionLevel) + protectionLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                DrawUtil.drawEnchantTag("U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (thornLevel > 0) {
                DrawUtil.drawEnchantTag("T" + ColorUtils.getColor(thornLevel) + thornLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemBow) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (powerLevel > 0) {
                DrawUtil.drawEnchantTag("Pow" + ColorUtils.getColor(powerLevel) + powerLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (punchLevel > 0) {
                DrawUtil.drawEnchantTag("Pun" + ColorUtils.getColor(punchLevel) + punchLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (flameLevel > 0) {
                DrawUtil.drawEnchantTag("F" + ColorUtils.getColor(flameLevel) + flameLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                DrawUtil.drawEnchantTag("U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemSword) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (sharpnessLevel > 0) {
                DrawUtil.drawEnchantTag("S" + ColorUtils.getColor(sharpnessLevel) + sharpnessLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (knockbackLevel > 0) {
                DrawUtil.drawEnchantTag("K" + ColorUtils.getColor(knockbackLevel) + knockbackLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (fireAspectLevel > 0) {
                DrawUtil.drawEnchantTag("F" + ColorUtils.getColor(fireAspectLevel) + fireAspectLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                DrawUtil.drawEnchantTag("U" + ColorUtils.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getRarity() == EnumRarity.EPIC) {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            FontRenderer.drawOutlinedStringCock(Minecraft.getMinecraft().fontRendererObj, "God", x * 2, enchantmentY, new Color(255, 255, 0).getRGB(), new Color(100, 100, 0, 200).getRGB());
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    private static void drawEnchantTag(String text, int x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        FontRenderer.drawOutlinedStringCock(Minecraft.getMinecraft().fontRendererObj, text, x, y, -1, new Color(0, 0, 0, 220).darker().getRGB());
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawModel(final float yaw, final float pitch, final EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float renderYawOffset = entityLivingBase.renderYawOffset;
        final float rotationYaw = entityLivingBase.rotationYaw;
        final float rotationPitch = entityLivingBase.rotationPitch;
        final float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        final float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - 0.4f;
        entityLivingBase.rotationYaw = yaw - 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    public static void skeetRect(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x, y + -4.0, x1 + size, y1 + size, 0.5, new Color(60, 60, 60).getRGB(), new Color(10, 10, 10).getRGB());
        RenderUtil.rectangleBordered(x + 1.0, y + -3.0, x1 + size - 1.0, y1 + size - 1.0, 1.0, new Color(40, 40, 40).getRGB(), new Color(40, 40, 40).getRGB());
        RenderUtil.rectangleBordered(x + 2.5, y + -1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(40, 40, 40).getRGB(), new Color(60, 60, 60).getRGB());
        RenderUtil.rectangleBordered(x + 2.5, y + -1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(22, 22, 22).getRGB(), new Color(255, 255, 255, 0).getRGB());
    }

    public static void skeetRectSmall(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x + 4.35, y + 0.5, x1 + size - 84.5, y1 + size - 4.35, 0.5, new Color(48, 48, 48).getRGB(), new Color(10, 10, 10).getRGB());
        RenderUtil.rectangleBordered(x + 5.0, y + 1.0, x1 + size - 85.0, y1 + size - 5.0, 0.5, new Color(17, 17, 17).getRGB(), new Color(255, 255, 255, 0).getRGB());
    }

    public static void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();

        y = ScaledResolution.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void circle(final double x, final double y, final double radius, final Color color) {
        polygon(x, y, radius, 360, color);
    }

    public static void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null) color(color);
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public static void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public static void circle(final double x, final double y, final double radius) {
        polygon(x, y, radius, 360);
    }

    public static void roundedRect(final double x, final double y, double width, double height, final double edgeRadius, final Color color) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        float sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null) color(color);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 180; i <= 270; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + sideLength, y + sideLength);
        }

        end();
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null) color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 0; i <= 90; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + width, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null) color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 270; i <= 360; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + width, y + sideLength);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null) color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 90; i <= 180; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + sideLength, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        // Main block
        rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color);

        // Horizontal bars
        rect(x, y + halfRadius, edgeRadius / 2, height - halfRadius, color);
        rect(x + width, y + halfRadius, edgeRadius / 2, height - halfRadius, color);

        // Vertical bars
        rect(x + halfRadius, y, width - halfRadius, halfRadius, color);
        rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color);
    }

    public static void renderGradientRectLeftRight(final int left, final int top, final int right, final int bottom, final int startColor, final int endColor) {
        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, bottom, Gui.zLevel).func_181666_a(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, top, Gui.zLevel).func_181666_a(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(left, top, Gui.zLevel).func_181666_a(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, Gui.zLevel).func_181666_a(f1, f2, f3, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }

    public static void start() {
        enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        disable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_CULL_FACE);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        enable(GL11.GL_CULL_FACE);
        enable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_BLEND);
        color(Color.white);
    }

    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(Color color) {
        if (color == null) color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public static void rect(final double x, final double y, final double width, final double height, final boolean filled, final Color color) {
        start();
        if (color != null) color(color);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINES);

        {
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width, y + height);
            vertex(x, y + height);
            if (!filled) {
                vertex(x, y);
                vertex(x, y + height);
                vertex(x + width, y);
                vertex(x + width, y + height);
            }
        }
        end();
        stop();
    }

    public static void rect(final double x, final double y, final double width, final double height, final Color color) {
        rect(x, y, width, height, true, color);
    }

    public static void drawBorderedRect(float x, float y, float width, float height, float borderWidth, Color rectColor, Color borderColor) {
        drawBorderedRect(x, y, width, height, borderWidth, rectColor.getRGB(), borderColor.getRGB());
    }

    public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
        drawRect(x, y, x2, y2, col2);

        final float f = (col1 >> 24 & 0xFF) / 255.0F, // @off
                f1 = (col1 >> 16 & 0xFF) / 255.0F, f2 = (col1 >> 8 & 0xFF) / 255.0F, f3 = (col1 & 0xFF) / 255.0F; // @on

        glEnable(3042);
        glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);

        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();

        enableTexture2D();
        disableBlend();
        GL11.glColor4f(1, 1, 1, 255);
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
    }

    public static void drawBorderedRect(double x, double y, double x2, double y2, float l1, int col1, int col2) {
        drawRect((float) x, (float) y, (float) x2, (float) y2, col2);

        final float f = (col1 >> 24 & 0xFF) / 255.0F, // @off
                f1 = (col1 >> 16 & 0xFF) / 255.0F, f2 = (col1 >> 8 & 0xFF) / 255.0F, f3 = (col1 & 0xFF) / 255.0F; // @on

        glEnable(3042);
        glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);

        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        enableTexture2D();
        disableBlend();
        GL11.glPopMatrix();
        GL11.glColor4f(255, 1, 1, 255);
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
    }

    public static void drawRect(float left, float top, float right, float bottom, int col1) {
        final float f = (col1 >> 24 & 0xFF) / 255.0F, // @off
                f1 = (col1 >> 16 & 0xFF) / 255.0F, f2 = (col1 >> 8 & 0xFF) / 255.0F, f3 = (col1 & 0xFF) / 255.0F; // @on

        glEnable(3042);
        glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);

        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(7);
        GL11.glVertex2d(right, top);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glVertex2d(right, bottom);
        GL11.glEnd();
        GL11.glPopMatrix();

        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        enableTexture2D();
        disableBlend();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static double animateProgress(final double current, final double target, final double speed) {
        if (current < target) {
            final double inc = 1.0 / Minecraft.getDebugFPS() * speed;
            if (target - current < inc) {
                return target;
            } else {
                return current + inc;
            }
        } else if (current > target) {
            final double inc = 1.0 / Minecraft.getDebugFPS() * speed;
            if (current - target < inc) {
                return target;
            } else {
                return current - inc;
            }
        }

        return current;
    }

    public static double bezierBlendAnimation(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    public static void glDrawTriangle(final double x, final double y, final double x1, final double y1, final double x2, final double y2, final int colour) {
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Enable blending
        final boolean restore = glEnableBlend();
        // Enable anti-aliasing
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        glColour(colour);

        // Start drawing a triangle
        glBegin(GL_TRIANGLES);
        {
            glVertex2d(x, y);
            glVertex2d(x1, y1);
            glVertex2d(x2, y2);
        }
        glEnd();

        // Enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable blending
        glRestoreBlend(restore);
        // Disable anti-aliasing
        glDisable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
    }

    public static void glDrawFramebuffer(final int framebufferTexture, final int width, final int height) {
        // Bind the texture of our framebuffer
        glBindTexture(GL_TEXTURE_2D, framebufferTexture);
        // Disable alpha testing so fading out outline works
        glDisable(GL_ALPHA_TEST);
        // Make sure blend is enabled
        final boolean restore = DrawUtil.glEnableBlend();
        // Draw the frame buffer texture upside-down
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(0, 0);

            glTexCoord2f(0, 0);
            glVertex2f(0, height);

            glTexCoord2f(1, 0);
            glVertex2f(width, height);

            glTexCoord2f(1, 1);
            glVertex2f(width, 0);
        }
        glEnd();
        // Restore blend
        DrawUtil.glRestoreBlend(restore);
        // Restore alpha test
        glEnable(GL_ALPHA_TEST);
    }

    public static void glDrawPlusSign(final double x, final double y, final double size, final double rotation, final int colour) {
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Enable blending
        final boolean restore = glEnableBlend();
        // Enable anti-aliasing
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        // Set line width
        glLineWidth(1.f);
        // Push new matrix
        glPushMatrix();
        // Translate matrix
        glTranslated(x, y, 0);
        // Rotate matrix by rotation value (do after translation
        glRotated(rotation, 0, 1, 1);
        // Disable depth
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        glColour(colour);

        // Start drawing a triangle
        glBegin(GL_LINES);
        {
            // Horizontal stroke
            glVertex2d(-(size / 2.0), 0);
            glVertex2d(size / 2.0, 0);
            // Vertical stroke
            glVertex2d(0, -(size / 2.0));
            glVertex2d(0, size / 2.0);
        }
        glEnd();

        // Enable depth
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        // Pop off old matrix (restore)
        glPopMatrix();
        // Enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable blending
        glRestoreBlend(restore);
        // Disable anti-aliasing
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
    }

    public static void glDrawFilledEllipse(final double x, final double y, final double radius, final int startIndex, final int endIndex, final int polygons, final boolean smooth, final int colour) {
        // Enable blending
        final boolean restore = glEnableBlend();

        if (smooth) {
            // Enable anti-aliasing
            glEnable(GL_POLYGON_SMOOTH);
            glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        }
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        glColour(colour);
        // Required because of minecraft optimizations
        glDisable(GL_CULL_FACE);

        // Begin triangle fan
        glBegin(GL_POLYGON);
        {
            // Specify center vertex
            glVertex2d(x, y);

            for (double i = startIndex; i <= endIndex; i++) {
                final double theta = 2.0 * Math.PI * i / polygons;
                // Specify triangle fan vertices in a circle (size=radius) around x & y
                glVertex2d(x + radius * Math.cos(theta), y + radius * Math.sin(theta));
            }
        }
        // Draw the triangle fan
        glEnd();

        // Disable blending
        glRestoreBlend(restore);

        if (smooth) {
            // Disable anti-aliasing
            glDisable(GL_POLYGON_SMOOTH);
            glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
        }
        // See above
        glEnable(GL_CULL_FACE);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawFilledEllipse(final double x, final double y, final float radius, final int colour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Enable anti-aliasing
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        glColour(colour);
        // See the point size aka radius
        glPointSize(radius);

        glBegin(GL_POINTS);
        {
            glVertex2d(x, y);
        }
        glEnd();

        // Disable blending
        glRestoreBlend(restore);
        // Disable anti-aliasing
        glDisable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_DONT_CARE);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glScissorBox(final double x, final double y, final double width, final double height, final ScaledResolution scaledResolution) {
        if (!glIsEnabled(GL_SCISSOR_TEST)) glEnable(GL_SCISSOR_TEST);

        final int scaling = scaledResolution.getScaleFactor();

        glScissor((int) (x * scaling), (int) ((ScaledResolution.getScaledHeight() - (y + height)) * scaling), (int) (width * scaling), (int) (height * scaling));
    }

    public static void glRestoreScissor() {
        if (!glIsEnabled(GL_SCISSOR_TEST)) glEnable(GL_SCISSOR_TEST);

        // Restore the last saved scissor box
        glScissor(SCISSOR_BUFFER.get(0), SCISSOR_BUFFER.get(1), SCISSOR_BUFFER.get(2), SCISSOR_BUFFER.get(3));
    }

    public static void glEndScissor() {
        glDisable(GL_SCISSOR_TEST);
    }

    public static double[] worldToScreen(final double[] positionVector, final AxisAlignedBB boundingBox, final double[] projection, final double[] projectionBuffer) {
        final double[][] bounds = {{boundingBox.minX, boundingBox.minY, boundingBox.minZ}, {boundingBox.minX, boundingBox.maxY, boundingBox.minZ}, {boundingBox.minX, boundingBox.maxY, boundingBox.maxZ}, {boundingBox.minX, boundingBox.minY, boundingBox.maxZ}, {boundingBox.maxX, boundingBox.minY, boundingBox.minZ}, {boundingBox.maxX, boundingBox.maxY, boundingBox.minZ}, {boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ}, {boundingBox.maxX, boundingBox.minY, boundingBox.maxZ}};

        final double[] position;

        // null when chests (don't need pos vector proj. for chests)
        if (positionVector != null) {
            if (!worldToScreen(positionVector, projectionBuffer, projection[2])) return null;

            position = new double[]{projection[0], projection[1], // screen max width/height
                    -1.f, -1.f, // negative placeholder values for > comparison
                    projectionBuffer[0], projectionBuffer[1] // player position vector x/y
            };
        } else {
            position = new double[]{projection[0], projection[1], // screen max width/height
                    -1.f, -1.f, // negative placeholder values for > comparison
            };
        }

        for (final double[] vector : bounds) {
            if (worldToScreen(vector, projectionBuffer, projection[2])) {
                final double projected_x = projectionBuffer[0];
                final double projected_y = projectionBuffer[1];

                position[0] = Math.min(position[0], projected_x);
                position[1] = Math.min(position[1], projected_y);
                position[2] = Math.max(position[2], projected_x);
                position[3] = Math.max(position[3], projected_y);
            }
        }

        return position;
    }

    public static boolean worldToScreen(double[] in, double[] out, double scaling) {
        glGetFloat(GL_MODELVIEW_MATRIX, MODEL_MATRIX_BUFFER);
        glGetFloat(GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
        glGetInteger(GL_VIEWPORT, VIEWPORT_BUFFER);

        if (GLU.gluProject((float) in[0], (float) in[1], (float) in[2], MODEL_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, WND_POS_BUFFER)) {
            final float zCoordinate = WND_POS_BUFFER.get(2);
            // Check z coordinate is within bounds 0-<1.0
            if (zCoordinate < 0.0F || zCoordinate > 1.0F) return false;

            out[0] = WND_POS_BUFFER.get(0) / scaling; // window pos (x) / scaled resolution scale (normal = 2)
            // GL handles the 'y' window coordinate inverted to Minecraft
            // subtract window pos y from bottom of screen and divide by scaled res scale
            out[1] = (Display.getHeight() - WND_POS_BUFFER.get(1)) / scaling;
            return true;
        }

        return false;
    }

    public static void glColour(final int color) {
        glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }

    public static void glDrawGradientLine(final double x, final double y, final double x1, final double y1, final float lineWidth, final int colour) {
        // Enable blending (required for anti-aliasing)
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set line width
        glLineWidth(lineWidth);
        // Enable line anti-aliasing
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glShadeModel(GL_SMOOTH);

        final int noAlpha = ColourUtil.removeAlphaComponent(colour);

        glDisable(GL_ALPHA_TEST);

        // Begin line
        glBegin(GL_LINE_STRIP);
        {
            // Start
            glColour(noAlpha);
            glVertex2d(x, y);
            // Middle
            final double dif = x1 - x;

            glColour(colour);
            glVertex2d(x + dif * 0.4, y);

            glVertex2d(x + dif * 0.6, y);
            // End
            glColour(noAlpha);
            glVertex2d(x1, y1);
        }
        // Draw the line
        glEnd();

        glEnable(GL_ALPHA_TEST);

        glShadeModel(GL_FLAT);

        // Restore blend
        glRestoreBlend(restore);
        // Disable line anti-aliasing
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawLine(final double x, final double y, final double x1, final double y1, final float lineWidth, final boolean smoothed, final int colour) {
        // Enable blending (required for anti-aliasing)
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set line width
        glLineWidth(lineWidth);

        if (smoothed) {
            // Enable line anti-aliasing
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        }

        glColour(colour);

        // Begin line
        glBegin(GL_LINES);
        {
            // Start
            glVertex2d(x, y);
            // End
            glVertex2d(x1, y1);
        }
        // Draw the line
        glEnd();

        // Restore blend
        glRestoreBlend(restore);
        if (smoothed) {
            // Disable line anti-aliasing
            glDisable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        }
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawPlayerFace(final double x, final double y, final double width, final double height, final ResourceLocation skinLocation) {
        // Bind skin texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(skinLocation);
        // Colour solid
        glColor4f(1, 1, 1, 1);
        final float eightPixelOff = 1.0F / 8;

        glBegin(GL_QUADS);
        {
            glTexCoord2f(eightPixelOff, eightPixelOff);
            glVertex2d(x, y);

            glTexCoord2f(eightPixelOff, eightPixelOff * 2);
            glVertex2d(x, y + height);

            glTexCoord2f(eightPixelOff * 2, eightPixelOff * 2);
            glVertex2d(x + width, y + height);

            glTexCoord2f(eightPixelOff * 2, eightPixelOff);
            glVertex2d(x + width, y);
        }
        glEnd();
    }

    public static void glDrawSidewaysGradientRect(final double x, final double y, final double width, final double height, final int startColour, final int endColour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Enable vertex colour changing
        glShadeModel(GL_SMOOTH);

        // Begin rect
        glBegin(GL_QUADS);
        {
            // Start fade
            glColour(startColour);
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            // End fade
            glColour(endColour);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        // Draw the rect
        glEnd();

        // Restore shade model
        glShadeModel(GL_FLAT);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable blending
        glRestoreBlend(restore);
    }

    public static void glDrawFilledRect(final double x, final double y, final double x1, final double y1, final int startColour, final int endColour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Enable vertex colour changing
        glShadeModel(GL_SMOOTH);

        // Begin rect
        glBegin(GL_QUADS);
        {
            // Start fade
            glColour(startColour);
            glVertex2d(x, y);
            glColour(endColour);
            glVertex2d(x, y1);
            // End fade
            glVertex2d(x1, y1);
            glColour(startColour);
            glVertex2d(x1, y);
        }
        // Draw the rect
        glEnd();

        // Restore shade model
        glShadeModel(GL_FLAT);

        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable blending
        glRestoreBlend(restore);
    }

    public static void glDrawOutlinedQuad(final double x, final double y, final double width, final double height, final float thickness, final int colour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        glColour(colour);

        glLineWidth(thickness);

        // Begin rect
        glBegin(GL_LINE_LOOP);
        {
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        // Draw the rect
        glEnd();

        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable blend
        glRestoreBlend(restore);
    }

    public static void drawHollowRoundedRect(double x, double y, double width, double height, double cornerRadius, boolean smoothed, Color color) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255.0F, color.getAlpha() / 255F);
        glLineWidth(1.0f);
        glBegin(GL_LINE_LOOP);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        for (int i = 0; i <= 90; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        glEnd();
        cornerX = x + width - cornerRadius;
        cornerY = y + cornerRadius;
        glBegin(GL_LINE_LOOP);
        for (int i = 90; i <= 180; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        glEnd();
        cornerX = x + cornerRadius;
        cornerY = y + cornerRadius;
        glBegin(GL_LINE_LOOP);
        for (int i = 180; i <= 270; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        glEnd();
        cornerX = x + cornerRadius;
        cornerY = y + height - cornerRadius;
        glBegin(GL_LINE_LOOP);
        for (int i = 270; i <= 360; i += 30)
            glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
        glEnd();
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDrawLine(x + cornerRadius, y, x + width - cornerRadius, y, 1.0f, smoothed, color.getRGB());
        glDrawLine(x + cornerRadius, y + height, x + width - cornerRadius, y + height, 1.0f, smoothed, color.getRGB());
        glDrawLine(x, y + cornerRadius, x, y + height - cornerRadius, 1.0f, smoothed, color.getRGB());
        glDrawLine(x + width, y + cornerRadius, x + width, y + height - cornerRadius, 1.0f, smoothed, color.getRGB());
    }

    public static void glDrawOutlinedQuadGradient(final double x, final double y, final double width, final double height, final float thickness, final int colour, final int secondaryColour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);

        glLineWidth(thickness);

        // Begin rect
        glShadeModel(GL_SMOOTH);
        glBegin(GL_LINE_LOOP);
        {
            // Set color
            glColour(colour);
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            // Set second color
            glColour(secondaryColour);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        // Draw the rect
        glEnd();
        glShadeModel(GL_FLAT);

        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
        // Disable blend
        glRestoreBlend(restore);
    }

    public static void glDrawFilledQuad(final double x, final double y, final double width, final double height, final int colour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        glColour(colour);

        // Begin rect
        glBegin(GL_QUADS);
        {
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        // Draw the rect
        glEnd();

        // Disable blending
        glRestoreBlend(restore);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawFilledQuad(final double x, final double y, final double width, final double height, final int startColour, final int endColour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);

        glShadeModel(GL_SMOOTH);

        // Begin rect
        glBegin(GL_QUADS);
        {
            glColour(startColour);
            glVertex2d(x, y);

            glColour(endColour);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);

            glColour(startColour);
            glVertex2d(x + width, y);
        }
        // Draw the rect
        glEnd();

        glShadeModel(GL_FLAT);

        // Disable blending
        glRestoreBlend(restore);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawFilledRect(final double x, final double y, final double x1, final double y1, final int colour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        glColour(colour);

        // Begin rect
        glBegin(GL_QUADS);
        {
            glVertex2d(x, y);
            glVertex2d(x, y1);
            glVertex2d(x1, y1);
            glVertex2d(x1, y);
        }
        // Draw the rect
        glEnd();

        // Disable blending
        glRestoreBlend(restore);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawArcFilled(final double x, final double y, final float radius, final float angleStart, final float angleEnd, final int segments, final int colour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        glColour(colour);
        // Required because of minecraft optimizations
        glDisable(GL_CULL_FACE);
        // Translate to centre of arc
        glTranslated(x, y, 0);
        // Begin triangle fan
        glBegin(GL_POLYGON);
        {
            // Specify center vertex
            glVertex2f(0.f, 0.f);

            final float[][] vertices = MathUtils.getArcVertices(radius, angleStart, angleEnd, segments);

            for (float[] vertex : vertices) {
                // Specify triangle fan vertices in a circle (size=radius) around x & y
                glVertex2f(vertex[0], vertex[1]);
            }
        }
        // Draw the triangle fan
        glEnd();
        // Restore matrix
        glTranslated(-x, -y, 0);
        // Disable blending
        glRestoreBlend(restore);
        // See above
        glEnable(GL_CULL_FACE);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawArcOutline(final double x, final double y, final float radius, final float angleStart, final float angleEnd, final float lineWidth, final int colour) {
        // Derive segments from size
        final int segments = (int) (radius * 4);
        // Enable blending
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set the width of the line
        glLineWidth(lineWidth);
        // Set color
        glColour(colour);
        // Enable triangle smoothing
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        // Translate to centre of arc
        glTranslated(x, y, 0);
        // Begin triangle fan
        glBegin(GL_LINE_STRIP);
        {
            final float[][] vertices = MathUtils.getArcVertices(radius, angleStart, angleEnd, segments);

            for (float[] vertex : vertices) {
                // Specify triangle fan vertices in a circle (size=radius) around x & y
                glVertex2f(vertex[0], vertex[1]);
            }
        }
        // Draw the triangle fan
        glEnd();
        // Restore matrix
        glTranslated(-x, -y, 0);
        // Disable triangle smoothing
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        // Disable blending
        glRestoreBlend(restore);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawPoint(final double x, final double y, final float radius, final ScaledResolution scaledResolution, final int colour) {
        // Enable blending
        final boolean restore = glEnableBlend();
        // Enable anti-aliasing
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set color
        glColour(colour);
        // See the point size aka radius
        glPointSize(radius * glGetFloat(GL_MODELVIEW_MATRIX) * scaledResolution.getScaleFactor());

        glBegin(GL_POINTS);
        {
            glVertex2d(x, y);
        }
        glEnd();

        // Disable blending
        glRestoreBlend(restore);
        // Disable anti-aliasing
        glDisable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_DONT_CARE);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawRoundedOutline(final double x, final double y, final double width, final double height, final float lineWidth, final RoundingMode roundingMode, final float rounding, final int colour) {
        boolean bLeft = false;
        boolean tLeft = false;
        boolean bRight = false;
        boolean tRight = false;

        switch (roundingMode) {
            case TOP:
                tLeft = true;
                tRight = true;
                break;
            case BOTTOM:
                bLeft = true;
                bRight = true;
                break;
            case FULL:
                tLeft = true;
                tRight = true;
                bLeft = true;
                bRight = true;
                break;
            case LEFT:
                bLeft = true;
                tLeft = true;
                break;
            case RIGHT:
                bRight = true;
                tRight = true;
                break;
            case TOP_LEFT:
                tLeft = true;
                break;
            case TOP_RIGHT:
                tRight = true;
                break;
            case BOTTOM_LEFT:
                bLeft = true;
                break;
            case BOTTOM_RIGHT:
                bRight = true;
                break;
        }

        // Translate matrix to top-left of rect
        glTranslated(x, y, 0);
        // Enable blending
        final boolean restore = glEnableBlend();

        if (tLeft) {
            // Top left
            glDrawArcOutline(rounding, rounding, rounding, 270.f, 360.f, lineWidth, colour);
        }

        if (tRight) {
            // Top right
            glDrawArcOutline(width - rounding, rounding, rounding, 0.f, 90.f, lineWidth, colour);
        }

        if (bLeft) {
            // Bottom left
            glDrawArcOutline(rounding, height - rounding, rounding, 180, 270, lineWidth, colour);
        }

        if (bRight) {
            // Bottom right
            glDrawArcOutline(width - rounding, height - rounding, rounding, 90, 180, lineWidth, colour);
        }

        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set colour
        glColour(colour);

        // Begin polygon
        glBegin(GL_LINES);
        {
            if (tLeft) {
                glVertex2d(0.0, rounding);
            } else {
                glVertex2d(0.0, 0.0);
            }

            if (bLeft) {
                glVertex2d(0, height - rounding);
                glVertex2d(rounding, height);
            } else {
                glVertex2d(0.0, height);
                glVertex2d(0.0, height);
            }

            if (bRight) {
                glVertex2d(width - rounding, height);
                glVertex2d(width, height - rounding);
            } else {
                glVertex2d(width, height);
                glVertex2d(width, height);
            }

            if (tRight) {
                glVertex2d(width, rounding);
                glVertex2d(width - rounding, 0.0);
            } else {
                glVertex2d(width, 0.0);
                glVertex2d(width, 0.0);
            }

            if (tLeft) {
                glVertex2d(rounding, 0.0);
            } else {
                glVertex2d(0.0, 0.0);
            }
        }
        // Draw polygon
        glEnd();

        // Disable blending
        glRestoreBlend(restore);
        // Translate matrix back (instead of creating a new matrix with glPush/glPop)
        glTranslated(-x, -y, 0);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void glDrawSemiCircle(final double x, final double y, final double diameter, final float innerRadius, final double percentage, final int colour) {
        final boolean restore = glEnableBlend();

        final boolean alphaTest = glIsEnabled(GL_ALPHA_TEST);
        if (alphaTest) glDisable(GL_ALPHA_TEST);

        glUseProgram(CIRCLE_SHADER.getProgram());
        glUniform1f(CIRCLE_SHADER.getUniformLocation("innerRadius"), innerRadius);
        glUniform4f(CIRCLE_SHADER.getUniformLocation("colour"), (colour >> 16 & 0xFF) / 255.f, (colour >> 8 & 0xFF) / 255.f, (colour & 0xFF) / 255.f, (colour >> 24 & 0xFF) / 255.f);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.f, 0.f);
            glVertex2d(x, y);

            glTexCoord2f(0.f, 1.f);
            glVertex2d(x, y + diameter);

            glTexCoord2f(1.f, 1.f);
            glVertex2d(x + diameter, y + diameter);

            glTexCoord2f(1.f, 0.f);
            glVertex2d(x + diameter, y);
        }
        glEnd();

        glUseProgram(0);

        if (alphaTest) glEnable(GL_ALPHA_TEST);

        glRestoreBlend(restore);
    }

    public static void glDrawRoundedQuad(final double x, final double y, final float width, final float height, final float radius, final int colour) {
        final boolean restore = glEnableBlend();

        final boolean alphaTest = glIsEnabled(GL_ALPHA_TEST);
        if (alphaTest) glDisable(GL_ALPHA_TEST);

        glUseProgram(ROUNDED_QUAD_SHADER.getProgram());
        glUniform1f(ROUNDED_QUAD_SHADER.getUniformLocation("width"), width);
        glUniform1f(ROUNDED_QUAD_SHADER.getUniformLocation("height"), height);
        glUniform1f(ROUNDED_QUAD_SHADER.getUniformLocation("radius"), radius);
        glUniform4f(ROUNDED_QUAD_SHADER.getUniformLocation("colour"), (colour >> 16 & 0xFF) / 255.f, (colour >> 8 & 0xFF) / 255.f, (colour & 0xFF) / 255.f, (colour >> 24 & 0xFF) / 255.f);

        glDisable(GL_TEXTURE_2D);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.f, 0.f);
            glVertex2d(x, y);

            glTexCoord2f(0.f, 1.f);
            glVertex2d(x, y + height);

            glTexCoord2f(1.f, 1.f);
            glVertex2d(x + width, y + height);

            glTexCoord2f(1.f, 0.f);
            glVertex2d(x + width, y);
        }
        glEnd();

        glUseProgram(0);

        glEnable(GL_TEXTURE_2D);

        if (alphaTest) glEnable(GL_ALPHA_TEST);

        glRestoreBlend(restore);
    }

    public static void glDrawRoundedQuadRainbow(final double x, final double y, final float width, final float height, final float radius) {
        final boolean restore = glEnableBlend();

        final boolean alphaTest = glIsEnabled(GL_ALPHA_TEST);
        if (alphaTest) glDisable(GL_ALPHA_TEST);

        GL_COLOUR_SHADER.use();
        glUniform1f(GL_COLOUR_SHADER.getUniformLocation("width"), width);
        glUniform1f(GL_COLOUR_SHADER.getUniformLocation("height"), height);
        glUniform1f(GL_COLOUR_SHADER.getUniformLocation("radius"), radius);

        glDisable(GL_TEXTURE_2D);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.f, 0.f);
            glVertex2d(x, y);

            glTexCoord2f(0.f, 1.f);
            glVertex2d(x, y + height);

            glTexCoord2f(1.f, 1.f);
            glVertex2d(x + width, y + height);

            glTexCoord2f(1.f, 0.f);
            glVertex2d(x + width, y);
        }

        glEnd();

        glUseProgram(0);

        glEnable(GL_TEXTURE_2D);

        if (alphaTest) glEnable(GL_ALPHA_TEST);

        glRestoreBlend(restore);
    }

    public static void glDrawRoundedRect(final double x, final double y, final double width, final double height, final RoundingMode roundingMode, final float rounding, final float scaleFactor, final int colour) {
        boolean bLeft = false;
        boolean tLeft = false;
        boolean bRight = false;
        boolean tRight = false;

        switch (roundingMode) {
            case TOP:
                tLeft = true;
                tRight = true;
                break;
            case BOTTOM:
                bLeft = true;
                bRight = true;
                break;
            case FULL:
                tLeft = true;
                tRight = true;
                bLeft = true;
                bRight = true;
                break;
            case LEFT:
                bLeft = true;
                tLeft = true;
                break;
            case RIGHT:
                bRight = true;
                tRight = true;
                break;
            case TOP_LEFT:
                tLeft = true;
                break;
            case TOP_RIGHT:
                tRight = true;
                break;
            case BOTTOM_LEFT:
                bLeft = true;
                break;
            case BOTTOM_RIGHT:
                bRight = true;
                break;
        }

        final float alpha = (colour >> 24 & 0xFF) / 255.f;

        // Enable blending
        final boolean restore = glEnableBlend();

        // Set colour
        DrawUtil.glColour(colour);

        // Translate matrix to top-left of rect
        glTranslated(x, y, 0);
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);

        // Begin polygon
        glBegin(GL_POLYGON);
        {
            if (tLeft) {
                glVertex2d(rounding, rounding);
                glVertex2d(0, rounding);
            } else {
                glVertex2d(0, 0);
            }

            if (bLeft) {
                glVertex2d(0, height - rounding);
                glVertex2d(rounding, height - rounding);
                glVertex2d(rounding, height);
            } else {
                glVertex2d(0, height);
            }

            if (bRight) {
                glVertex2d(width - rounding, height);
                glVertex2d(width - rounding, height - rounding);
                glVertex2d(width, height - rounding);
            } else {
                glVertex2d(width, height);
            }

            if (tRight) {
                glVertex2d(width, rounding);
                glVertex2d(width - rounding, rounding);
                glVertex2d(width - rounding, 0);
            } else {
                glVertex2d(width, 0);
            }

            if (tLeft) {
                glVertex2d(rounding, 0);
            }
        }
        // Draw polygon
        glEnd();

        // Enable anti-aliasing
        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);

        // Set point size
        glPointSize(rounding * 2.f * glGetFloat(GL_MODELVIEW_MATRIX) * scaleFactor);

        glBegin(GL_POINTS);
        {
            if (tLeft) {
                // Top left
                glVertex2d(rounding, rounding);
            }

            if (tRight) {
                // Top right
                glVertex2d(width - rounding, rounding);
            }

            if (bLeft) {
                // Bottom left
                glVertex2d(rounding, height - rounding);
            }

            if (bRight) {
                // Bottom right
                glVertex2d(width - rounding, height - rounding);
            }
        }
        glEnd();

        // Disable anti-aliasing
        glDisable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_DONT_CARE);
        // Disable blending
        glRestoreBlend(restore);
        // Translate matrix back (instead of creating a new matrix with glPush/glPop)
        glTranslated(-x, -y, 0);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    // TODO :: Do this shader (its not hard)

    public static void glDrawRoundedRectEllipse(final double x, final double y, final double width, final double height, final RoundingMode roundingMode, final int roundingDef, final double roundingLevel, final int colour) {
        boolean bLeft = false;
        boolean tLeft = false;
        boolean bRight = false;
        boolean tRight = false;

        switch (roundingMode) {
            case TOP:
                tLeft = true;
                tRight = true;
                break;
            case BOTTOM:
                bLeft = true;
                bRight = true;
                break;
            case FULL:
                tLeft = true;
                tRight = true;
                bLeft = true;
                bRight = true;
                break;
            case LEFT:
                bLeft = true;
                tLeft = true;
                break;
            case RIGHT:
                bRight = true;
                tRight = true;
                break;
            case TOP_LEFT:
                tLeft = true;
                break;
            case TOP_RIGHT:
                tRight = true;
                break;
            case BOTTOM_LEFT:
                bLeft = true;
                break;
            case BOTTOM_RIGHT:
                bRight = true;
                break;
        }

        // Translate matrix to top-left of rect
        glTranslated(x, y, 0);
        // Enable triangle anti-aliasing
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        // Enable blending
        final boolean restore = glEnableBlend();

        if (tLeft) {
            // Top left
            glDrawFilledEllipse(roundingLevel, roundingLevel, roundingLevel, (int) (roundingDef * 0.5), (int) (roundingDef * 0.75), roundingDef, false, colour);
        }

        if (tRight) {
            // Top right
            glDrawFilledEllipse(width - roundingLevel, roundingLevel, roundingLevel, (int) (roundingDef * 0.75), roundingDef, roundingDef, false, colour);
        }

        if (bLeft) {
            // Bottom left
            glDrawFilledEllipse(roundingLevel, height - roundingLevel, roundingLevel, (int) (roundingDef * 0.25), (int) (roundingDef * 0.5), roundingDef, false, colour);
        }

        if (bRight) {
            // Bottom right
            glDrawFilledEllipse(width - roundingLevel, height - roundingLevel, roundingLevel, 0, (int) (roundingDef * 0.25), roundingDef, false, colour);
        }

        // Enable triangle anti-aliasing (to save performance on next poly draw)
        glDisable(GL_POLYGON_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);

        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set colour
        glColour(colour);

        // Begin polygon
        glBegin(GL_POLYGON);
        {
            if (tLeft) {
                glVertex2d(roundingLevel, roundingLevel);
                glVertex2d(0, roundingLevel);
            } else {
                glVertex2d(0, 0);
            }

            if (bLeft) {
                glVertex2d(0, height - roundingLevel);
                glVertex2d(roundingLevel, height - roundingLevel);
                glVertex2d(roundingLevel, height);
            } else {
                glVertex2d(0, height);
            }

            if (bRight) {
                glVertex2d(width - roundingLevel, height);
                glVertex2d(width - roundingLevel, height - roundingLevel);
                glVertex2d(width, height - roundingLevel);
            } else {
                glVertex2d(width, height);
            }

            if (tRight) {
                glVertex2d(width, roundingLevel);
                glVertex2d(width - roundingLevel, roundingLevel);
                glVertex2d(width - roundingLevel, 0);
            } else {
                glVertex2d(width, 0);
            }

            if (tLeft) {
                glVertex2d(roundingLevel, 0);
            }
        }
        // Draw polygon
        glEnd();

        // Disable blending
        glRestoreBlend(restore);
        // Translate matrix back (instead of creating a new matrix with glPush/glPop)
        glTranslated(-x, -y, 0);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static boolean glEnableBlend() {
        final boolean wasEnabled = glIsEnabled(GL_BLEND);

        if (!wasEnabled) {
            glEnable(GL_BLEND);
            glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        return wasEnabled;
    }

    public static void glRestoreBlend(final boolean wasEnabled) {
        if (!wasEnabled) {
            glDisable(GL_BLEND);
        }
    }

    public static float interpolate(final float old, final float now, final float progress) {
        return old + (now - old) * progress;
    }

    public static double interpolate(final double old, final double now, final double progress) {
        return old + (now - old) * progress;
    }

    public static Vec3 interpolate(final Vec3 old, final Vec3 now, final double progress) {
        final Vec3 difVec = now.subtract(old);
        return new Vec3(old.xCoord + difVec.xCoord * progress, old.yCoord + difVec.yCoord * progress, old.zCoord + difVec.zCoord * progress);
    }

    public static double[] interpolate(final Entity entity, final float partialTicks) {
        return new double[]{interpolate(entity.prevPosX, entity.posX, partialTicks), interpolate(entity.prevPosY, entity.posY, partialTicks), interpolate(entity.prevPosZ, entity.posZ, partialTicks),};
    }

    public static AxisAlignedBB interpolate(final Entity entity, final AxisAlignedBB boundingBox, final float partialTicks) {
        final float invertedPT = 1.0f - partialTicks;
        return boundingBox.offset((entity.posX - entity.prevPosX) * -invertedPT, (entity.posY - entity.prevPosY) * -invertedPT, (entity.posZ - entity.prevPosZ) * -invertedPT);
    }

    public static void glDrawBoundingBox(final AxisAlignedBB bb, final float lineWidth, final boolean filled) {
        if (filled) {
            // 4 sides
            glBegin(GL_QUAD_STRIP);
            {
                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
            }
            glEnd();

            // Bottom
            glBegin(GL_QUADS);
            {
                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.maxZ);
            }
            glEnd();

            glCullFace(GL_FRONT);

            // Top
            glBegin(GL_QUADS);
            {
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            }
            glEnd();

            glCullFace(GL_BACK);
        }


        if (lineWidth > 0) {
            glLineWidth(lineWidth);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            glBegin(GL_LINE_STRIP);
            {
                // Bottom
                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.minZ);

                // Top
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
            }
            glEnd();

            glBegin(GL_LINES);
            {
                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            }
            glEnd();

            glDisable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        }
    }

    public void circle(final double x, final double y, final double radius, final boolean filled, final Color color) {
        polygon(x, y, radius, 360, filled, color);
    }

    public void circle(final double x, final double y, final double radius, final boolean filled) {
        polygon(x, y, radius, 360, filled);
    }

    public void push() {
        GL11.glPushMatrix();
    }

    public void pop() {
        GL11.glPopMatrix();
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public void color(Color color, final int alpha) {
        if (color == null) color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public void lineWidth(final double width) {
        GL11.glLineWidth((float) width);
    }

    public void startSmooth() {
        enable(GL11.GL_POLYGON_SMOOTH);
        enable(GL11.GL_LINE_SMOOTH);
        enable(GL11.GL_POINT_SMOOTH);
    }

    public void endSmooth() {
        disable(GL11.GL_POINT_SMOOTH);
        disable(GL11.GL_LINE_SMOOTH);
        disable(GL11.GL_POLYGON_SMOOTH);
    }

    public void rect(final double x, final double y, final double width, final double height, final boolean filled) {
        rect(x, y, width, height, filled, null);
    }

    public void rect(final double x, final double y, final double width, final double height) {
        rect(x, y, width, height, true, null);
    }

    public enum RoundingMode {
        TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT,

        LEFT, RIGHT,

        TOP, BOTTOM,

        FULL
    }
}
