package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

import java.awt.*;

public class EnchantEffect extends Module {
    public static float hue = 0.0F;
    public static Numbers<Double> r = new Numbers<>("Red", 255d, 0d, 255d, 1d);
    public static Numbers<Double> g = new Numbers<>("Green", 0d, 0d, 255d, 1d);
    public static Numbers<Double> b = new Numbers<>("Blue", 0d, 0d, 255d, 1d);
    public static Option Rainbow = new Option("Rainbow", false);

    public EnchantEffect() {
        super("EnchantEffect", new String[]{"EnchantColor"}, ModuleType.Render);
        addValues(r, g, b, Rainbow);
    }

    public static Color getEnchantColor() {
        if (Rainbow.get()) {
            return Color.getHSBColor(hue / 255f, 0.75f, 0.9f);
        } else {
            return new Color(r.get().intValue(), g.get().intValue(), b.get().intValue());
        }
    }

    @EventHandler
    public void Render2d(EventRender2D e) {
        hue += HUD.RainbowSpeed.get().floatValue() / 5.0F;
        if (hue > 255.0F) {
            hue = 0.0F;
        }
    }
}
