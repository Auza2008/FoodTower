package cn.foodtower.module.modules.render;


import cn.foodtower.api.EventHandler;
import cn.foodtower.api.Priority;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.anim.AnimationUtils;
import cn.foodtower.util.render.Colors2;
import cn.foodtower.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Crosshair extends Module {
    public static Numbers<Double> GAP;
    public static Numbers<Double> SIZE;
    public static Numbers<Double> r = new Numbers<>("Red", "Red", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> g = new Numbers<>("Green", "Green", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> b = new Numbers<>("Blue", "Blue", 255.0, 0.0, 255.0, 1.0);
    private static double gaps = 0;

    static {
        Crosshair.GAP = new Numbers<>("Gap", 0.5, 0.25, 15.0, 0.25);
        Crosshair.SIZE = new Numbers<>("Size", 7.0, 0.25, 15.0, 0.25);
    }

    float hue;
    private boolean dragging;
    private final Option DYNAMIC;
    private final Numbers<Double> WIDTH;

    public Crosshair() {
        super("Crosshair", new String[]{"Crosshair"}, ModuleType.Render);
        this.DYNAMIC = new Option("Dynamic", true);
        this.WIDTH = new Numbers<>("Width", 0.25, 0.25, 10.0, 0.25);
        this.addValues(r, g, b, this.DYNAMIC, Crosshair.GAP, this.WIDTH, Crosshair.SIZE);
    }

    @EventHandler(priority = Priority.LOW)
    public void onGui(final EventRender2D e) {
        gaps = AnimationUtils.animate(isMoving() ? 4 : 0, gaps, 20f / Minecraft.getDebugFPS());
        final int red = r.getValue().intValue();
        final int green = g.getValue().intValue();
        final int blue = b.getValue().intValue();
        final int alph = 255;
        final double gap = Crosshair.GAP.getValue();
        final double width = this.WIDTH.getValue();
        final double size = Crosshair.SIZE.getValue();
        final ScaledResolution scaledRes = new ScaledResolution(Crosshair.mc);
        RenderUtil.rectangleBordered(ScaledResolution.getScaledWidth() / 2f - width, ScaledResolution.getScaledHeight() / 2f - gap - size - (gaps), ScaledResolution.getScaledWidth() / 2f + 1.0f + width, ScaledResolution.getScaledHeight() / 2f - gap - (gaps), 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
        RenderUtil.rectangleBordered(ScaledResolution.getScaledWidth() / 2f - width, ScaledResolution.getScaledHeight() / 2f + gap + 1.0 + (gaps) - 0.15, ScaledResolution.getScaledWidth() / 2f + 1.0f + width, ScaledResolution.getScaledHeight() / 2f + 1 + gap + size + (gaps) - 0.15, 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
        RenderUtil.rectangleBordered(ScaledResolution.getScaledWidth() / 2f - gap - size - (gaps) + 0.15, ScaledResolution.getScaledHeight() / 2f - width, ScaledResolution.getScaledWidth() / 2f - gap - (gaps) + 0.15, ScaledResolution.getScaledHeight() / 2f + 1.0f + width, 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
        RenderUtil.rectangleBordered(ScaledResolution.getScaledWidth() / 2f + 1 + gap + (gaps), ScaledResolution.getScaledHeight() / 2f - width, ScaledResolution.getScaledWidth() / 2f + size + gap + 1.0 + (gaps), ScaledResolution.getScaledHeight() / 2f + 1.0f + width, 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
    }

    public boolean isMoving() {
        if (this.DYNAMIC.getValue()) {
            final Minecraft mc = Crosshair.mc;
            if (!mc.thePlayer.isCollidedHorizontally) {
                final Minecraft mc2 = Crosshair.mc;
                if (!mc.thePlayer.isSneaking()) {
                    if (mc.thePlayer.movementInput.moveForward == 0.0f) {
                        return mc.thePlayer.movementInput.moveStrafe != 0.0f;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
