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
        super("DuelInfo", null, ModuleType.Render);
        addValues(x, y);
    }

    @EventHandler
    private void on2D(EventRender2D e) {
        DrawUtil.roundedRect(x.get().floatValue(), y.get().floatValue(), 165, 160, 8, new Color(0, 0, 0, 160));
        RenderUtil.drawLine(x.get().floatValue() + 5, y.get().floatValue() + 20, x.get().floatValue() + 160, y.get().floatValue() + 20, 4);
        FontLoaders.SF20.drawStringWithShadow("DuelInfo", x.get().floatValue() + 61.5, y.get().floatValue() + 6, -1);
        FontLoaders.SF18.drawStringWithShadow("Yourself", x.get().floatValue() + 25.5, y.get().floatValue() + 145, -1);
        FontLoaders.SF18.drawStringWithShadow("Target", x.get().floatValue() + 103, y.get().floatValue() + 145, -1);
        double x = this.x.get();
        double y = this.y.get();
        if (mc.thePlayer != null) {
            if (mc.thePlayer.getHealth() > 0)
                DrawUtil.rect(x + 40, y + 140, 5d, -5 * mc.thePlayer.getHealth(), new Color(-1));
        }
        if (KillAura.curTarget != null) {
            if (KillAura.curTarget.getHealth() > 0)
                DrawUtil.rect(x + 115, y + 140, 5d, -5 * KillAura.curTarget.getHealth(), new Color(-1));
        }
    }
}
