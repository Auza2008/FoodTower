package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.ui.font.FontLoaders;
import cn.foodtower.util.render.DrawUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class TargetDebug extends Module {
    private final Numbers<Double> x = new Numbers<>("X", 5d, 0d, 300d, 1d);
    private final Numbers<Double> y = new Numbers<>("Y", 20d, 0d, 300d, 1d);

    public TargetDebug() {
        super("TargetDebug", null, ModuleType.Render);
        addValues(x, y);
    }

    private double getSpeed(Entity e) {
        final double motionX = e.motionX;
        final double n = motionX * e.motionX;
        final double motionZ = e.motionZ;
        return Math.sqrt(n + motionZ * e.motionZ);
    }

    @EventHandler
    private void on2D(EventRender2D e) {
        DrawUtil.roundedRect(x.get(), y.get(), 165, 160, 8, new Color(0, 0, 0, 120));
        float x = 5;
        float y = 25;
        if (ModuleManager.getModuleByClass(KillAura.class).isEnabled() && KillAura.curTarget != null) {
            EntityLivingBase target = KillAura.curTarget;
            FontLoaders.GoogleSans16.drawStringWithShadow("ID: " + target.getEntityId(), x + 4, y + 2, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("LivingTime: " + target.ticksExisted, x + 4, y + 10, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("PosY: " + String.format("%.1f", target.posY), x + 4, y + 18, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("PrevPosY: " + String.format("%.1f", target.prevPosY), x + 4, y + 26, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("PosY=PrevPosY: " + (target.posY == target.prevPosY ? "true" : "false"), x + 4, y + 34, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("Health: " + String.format("%.1f", target.getHealth()), x + 4, y + 42, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("MaxHealth: " + String.format("%.1f", target.getMaxHealth()), x + 4, y + 50, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("IsOnGround: " + target.onGround, x + 4, y + 58, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("Speed: " + String.format("%.1f", getSpeed(target)), x + 4, y + 66, -1);
        }
    }
}
