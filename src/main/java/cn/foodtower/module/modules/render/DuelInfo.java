package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.ui.font.FontLoaders;
import cn.foodtower.util.render.DrawUtil;
import cn.foodtower.util.render.RenderUtil;

import java.awt.*;

public class DuelInfo extends Module {
    private final Numbers<Double> x = new Numbers<>("X", 5d, 0d, 300d, 1d);
    private final Numbers<Double> y = new Numbers<>("Y", 20d, 0d, 300d, 1d);

    public DuelInfo() {
        super("DuelInfo", new String[]{""}, ModuleType.Render);
        addValues(x, y);
    }

    @EventHandler
    private void on2D(EventRender2D e) {
        DrawUtil.roundedRect(x.get().floatValue(), y.get().floatValue(), 120, 120, 8, new Color(0, 0, 0, 160));
        RenderUtil.drawLine(x.get().floatValue() + 5, y.get().floatValue() + 20, x.get().floatValue() + 115, y.get().floatValue() + 20, 4);
        FontLoaders.SF20.drawStringWithShadow("DuelInfo", x.get().floatValue() + 42.5, y.get().floatValue() + 6.5, -1);
        FontLoaders.SF18.drawStringWithShadow("Yourself", x.get().floatValue() + 9.5, y.get().floatValue() + 100.5, -1);
        FontLoaders.SF18.drawStringWithShadow("Target", x.get().floatValue() + 80.5, y.get().floatValue() + 100.5, -1);
        double x = this.x.get();
        double y = this.y.get();
        if (mc.thePlayer != null) {
            if (mc.thePlayer.getHealth() > 0)
                DrawUtil.rect(x + 24.5, y + 95, 5d, -5 * mc.thePlayer.getHealth() / 1.5, new Color(-1));
        }
        if (KillAura.curTarget != null) {
            if (KillAura.curTarget.getHealth() > 0)
                DrawUtil.rect(x + 92.5, y + 95, 5d, -5 * KillAura.curTarget.getHealth() / 1.5, new Color(-1));
        }
    }
}
