package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.misc.liquidbounce.LiquidRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.tileentity.*;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ChestESP extends Module {

    public final Mode modeValue = new Mode("Mode", Modes.values(), Modes.Outline);
    private final Numbers<Double> r = new Numbers<>("Red", 0d, 0d, 255d, 1d), g = new Numbers<>("Green", 0d, 0d, 255d, 1d), b = new Numbers<>("Blue", 0d, 0d, 255d, 1d);
    private final Option chestValue = new Option("Chest", true);
    private final Option enderChestValue = new Option("EnderChest", true);
    private final Option furnaceValue = new Option("Furnace", true);
    private final Option dispenserValue = new Option("Dispenser", true);
    private final Option hopperValue = new Option("Hopper", true);

    public ChestESP() {
        super("ChestESP", new String[]{"StorageESP"}, ModuleType.Render);
        addValues(modeValue, r, g, b, chestValue, enderChestValue, furnaceValue, dispenserValue, hopperValue);
        setValueDisplayable(new Value[]{chestValue, enderChestValue, furnaceValue, dispenserValue, hopperValue}, modeValue, new Enum[]{Modes.Box, Modes.TowD, Modes.OtherBox, Modes.WireFrame});
        setValueDisplayable(new Value[]{r, g, b}, chestValue, chestValue.get());
        setValueDisplayable(new Value[]{r, g, b}, modeValue, new Enum[]{Modes.Box, Modes.OtherBox, Modes.TowD, Modes.WireFrame});
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        try {
            float gamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 100000.0F;

            for (final TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
                Color color = null;

                if (chestValue.get() && tileEntity instanceof TileEntityChest)
                    color = new Color(r.get().intValue(), g.get().intValue(), b.get().intValue());

                if (enderChestValue.get() && tileEntity instanceof TileEntityEnderChest) color = Color.MAGENTA;

                if (furnaceValue.get() && tileEntity instanceof TileEntityFurnace) color = Color.BLACK;

                if (dispenserValue.get() && tileEntity instanceof TileEntityDispenser) color = Color.BLACK;

                if (hopperValue.get() && tileEntity instanceof TileEntityHopper) color = Color.GRAY;

                if (color == null) continue;

                if (!(tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest)) {
                    LiquidRender.drawBlockBox(tileEntity.getPos(), color, !modeValue.get().equals(Modes.OtherBox));
                    continue;
                }

                switch ((Modes) modeValue.get()) {
                    case OtherBox:
                    case Box:
                        LiquidRender.drawBlockBox(tileEntity.getPos(), color, !modeValue.get().equals(Modes.OtherBox));
                        break;
                    case TowD:
                        LiquidRender.draw2D(tileEntity.getPos(), color.getRGB(), Color.BLACK.getRGB());
                        break;
                    case WireFrame:
                        glPushMatrix();
                        glPushAttrib(GL_ALL_ATTRIB_BITS);
                        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                        glDisable(GL_TEXTURE_2D);
                        glDisable(GL_LIGHTING);
                        glDisable(GL_DEPTH_TEST);
                        glEnable(GL_LINE_SMOOTH);
                        glEnable(GL_BLEND);
                        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        LiquidRender.glColor(color);
                        glLineWidth(1.5F);
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
                        glPopAttrib();
                        glPopMatrix();
                        break;
                }
            }

            for (final Entity entity : mc.theWorld.loadedEntityList)
                if (entity instanceof EntityMinecartChest) {
                    switch ((Modes) modeValue.get()) {
                        case OtherBox:
                        case Box:
                            LiquidRender.drawEntityBox(entity, new Color(0, 66, 255, 80), !modeValue.get().equals(Modes.OtherBox));
                            break;
                        case TowD:
                            LiquidRender.draw2D(entity.getPosition(), new Color(0, 66, 255).getRGB(), Color.BLACK.getRGB());
                            break;
                        case WireFrame: {
                            final boolean entityShadow = mc.gameSettings.entityShadows;
                            mc.gameSettings.entityShadows = false;

                            glPushMatrix();
                            glPushAttrib(GL_ALL_ATTRIB_BITS);
                            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                            glDisable(GL_TEXTURE_2D);
                            glDisable(GL_LIGHTING);
                            glDisable(GL_DEPTH_TEST);
                            glEnable(GL_LINE_SMOOTH);
                            glEnable(GL_BLEND);
                            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                            LiquidRender.glColor(new Color(0, 66, 255));
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            LiquidRender.glColor(new Color(0, 66, 255));
                            glLineWidth(1.5F);
                            mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                            glPopAttrib();
                            glPopMatrix();

                            mc.gameSettings.entityShadows = entityShadow;
                            break;
                        }
                    }
                }

            LiquidRender.glColor(new Color(255, 255, 255, 255));
            mc.gameSettings.gammaSetting = gamma;
        } catch (Exception ignored) {
        }
    }

    private AxisAlignedBB renderOutlineSecond(double posX, double posY, double posZ) {
        RenderManager renderManager = mc.getRenderManager();
        return new AxisAlignedBB(posX + (double) 0.05f - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + (double) 0.05f - RenderManager.renderPosZ, posX + (double) 0.95f - RenderManager.renderPosX, posY + (double) 0.9f - RenderManager.renderPosY, posZ + (double) 0.95f - RenderManager.renderPosZ);
    }

    private AxisAlignedBB renderOutlineFirst(double posX, double posY, double posZ) {
        RenderManager renderManager = mc.getRenderManager();
        return new AxisAlignedBB(posX + (double) 0.05f - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + (double) 0.05f - RenderManager.renderPosZ - 1.0, posX + (double) 0.95f - RenderManager.renderPosX, posY + (double) 0.9f - RenderManager.renderPosY, posZ + (double) 0.95f - RenderManager.renderPosZ);
    }

    private AxisAlignedBB renderOutlineZero(double posX, double posY, double posZ) {
        RenderManager renderManager = mc.getRenderManager();
        return new AxisAlignedBB(posX + (double) 0.05f - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ + (double) 0.05f - RenderManager.renderPosZ, posX + (double) 1.95f - RenderManager.renderPosX, posY + (double) 0.9f - RenderManager.renderPosY, posZ + (double) 0.95f - RenderManager.renderPosZ);
    }

    public float[] getColorForTileEntity() {
        Color color = new Color(this.r.get().intValue(), this.g.get().intValue(), this.b.get().intValue());
        return new float[]{color.getRed(), color.getGreen(), color.getBlue(), 200.0f};
    }

    public int toRGBAHex(float r, float g, float b, float a) {
        return ((int) (a * 255.0f) & 0xFF) << 24 | ((int) (r * 255.0f) & 0xFF) << 16 | ((int) (g * 255.0f) & 0xFF) << 8 | (int) (b * 255.0f) & 0xFF;
    }

    public void renderOutline(int color) {
        this.setColor(color);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0f, -2000000.0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
    }

    public void setupStencilFirst() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    public void setupStencilSecond() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    public void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencilDepthBufferId = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferId);
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferId);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferId);
    }

    public void checkSetupFBO() {
        Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
        if (framebuffer != null && framebuffer.depthBuffer > -1) {
            this.setupFBO(framebuffer);
            framebuffer.depthBuffer = -1;
        }
    }

    public void pre3D() {
        this.checkSetupFBO();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(3.0f);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    public void post3D() {
        GL11.glPolygonOffset(1.0f, 2000000.0f);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

    public enum Modes {
        Outline, Box, OtherBox, TowD, WireFrame
    }
}
