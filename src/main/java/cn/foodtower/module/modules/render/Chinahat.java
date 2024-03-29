package cn.foodtower.module.modules.render;


import cn.foodtower.api.EventHandler;
import cn.foodtower.api.Priority;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.render.gl.GLUtils;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;

public class Chinahat extends Module {

    public static Option renderInFirstPerson = new Option("ChinaHat", "ShowInFirstPerson", false);
    public final Option colorRainbow = new Option("Rainbow", false);

    public final Numbers<Double> side = new Numbers<>("ChinaHat", "Side", 45.0d, 30.0d, 50.0d, 1.0d);
    public final Numbers<Double> stack = new Numbers<>("ChinaHat", "Stacks", 50.0d, 45.0d, 200.0d, 5.0d);
    public final Numbers<Double> colorRedValue = new Numbers<>("R", 255d, 0d, 255d, 1d);
    public final Numbers<Double> colorGreenValue = new Numbers<>("G", 179d, 0d, 255d, 1d);
    public final Numbers<Double> colorBlueValue = new Numbers<>("B", 72d, 0d, 255d, 1d);

    public Chinahat() {
        super("ChinaHat", new String[]{"Chinahat"}, ModuleType.Render);

        this.addValues(colorRedValue, colorBlueValue, colorGreenValue, colorRainbow, renderInFirstPerson, side, stack);
    }

    @EventHandler
    public void onRender3D(EventRender3D evt) {
        if (mc.gameSettings.thirdPersonView == 0 && !renderInFirstPerson.get()) {
            return;
        }

        this.drawChinaHat(mc.thePlayer, evt);
    }


    @EventHandler(priority = Priority.HIGH)
    private void drawChinaHat(EntityLivingBase entity, EventRender3D evt) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) evt.getPartialTicks() - RenderManager.renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) evt.getPartialTicks() - RenderManager.renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) evt.getPartialTicks() - RenderManager.renderPosZ;
        final Color color = colorRainbow.get() ? HUD.RainbowColor : new Color(colorRedValue.get().intValue(), colorGreenValue.get().intValue(), colorBlueValue.get().intValue());
        int side = this.side.get().intValue();
        int stack = this.stack.get().intValue();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + (mc.thePlayer.isSneaking() ? 2.0 : 2.2), z);

        GL11.glRotatef(-entity.width, 0.0f, 1.0f, 0.0f);

        GLUtils.glColor(color.getRGB());


        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(1.0f);

        Cylinder c = new Cylinder();
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.0f, 0.8f, 0.4f, side, stack);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glPopMatrix();
    }

}
