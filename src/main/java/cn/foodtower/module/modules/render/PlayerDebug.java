package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.World.EventTick;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.font.FontLoaders;
import cn.foodtower.util.render.DrawUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class PlayerDebug extends Module {
    private final Numbers<Double> x = new Numbers<>("X", 5d, 0d, 300d, 1d);
    private final Numbers<Double> y = new Numbers<>("Y", 20d, 0d, 300d, 1d);
    int i = 0;

    public PlayerDebug() {
        super("PlayerDebug", null, ModuleType.Render);
        addValues(x, y);
    }

    private double getSpeed(Entity e) {
        final double motionX = e.motionX;
        final double n = motionX * e.motionX;
        final double motionZ = e.motionZ;
        return Math.sqrt(n + motionZ * e.motionZ);
    }

    private double getFallTicks() {
        if (mc.thePlayer == null) return 0;
        if (mc.thePlayer.onGround) return 0;
        if (mc.thePlayer.fallDistance != 0) {
            return 1;
        }
        return 0;
    }

    @EventHandler
    private void getFallTicks(EventTick e) {
        if (getFallTicks() == 0) {
            i = 0;
        } else {
            ++i;
        }
    }

    @EventHandler
    private void on2D(EventRender2D e) {
        DrawUtil.roundedRect(x.get(), y.get(), 165, 160, 8, new Color(0, 0, 0, 120));
        float x = 5;
        float y = 25;
        if (mc.thePlayer != null) {
            EntityLivingBase player = mc.thePlayer;
            FontLoaders.GoogleSans16.drawStringWithShadow("ID: " + player.getEntityId(), x + 4, y + 2, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("LivingTime: " + player.ticksExisted, x + 4, y + 10, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("PosY: " + String.format("%.1f", player.posY), x + 4, y + 18, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("PrevPosY: " + String.format("%.1f", player.prevPosY), x + 4, y + 26, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("PosY=PrevPosY: " + (player.posY == player.prevPosY ? "true" : "false"), x + 4, y + 34, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("Health: " + String.format("%.1f", player.getHealth()), x + 4, y + 42, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("MaxHealth: " + String.format("%.1f", player.getMaxHealth()), x + 4, y + 50, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("IsOnGround: " + player.onGround, x + 4, y + 58, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("Speed: " + String.format("%.1f", getSpeed(player)), x + 4, y + 66, -1);
            FontLoaders.GoogleSans16.drawStringWithShadow("FallTicks: " + i, x + 4, y + 74, -1);
        }
    }
}
