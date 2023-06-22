package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventJump;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.MoveUtils;
import net.minecraft.potion.Potion;

public class LegitSpeed extends Module {
    public LegitSpeed() {
        super("LegitSpeed", new String[]{""}, ModuleType.Movement);
    }

    @EventHandler
    private void onJump(EventJump jump) {
        if (mc.thePlayer != null || MoveUtils.isMoving()) {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() != 2) {
                mc.thePlayer.motionX *= (0.7 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
                mc.thePlayer.motionZ *= (0.7 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
            }
        }
    }
}
