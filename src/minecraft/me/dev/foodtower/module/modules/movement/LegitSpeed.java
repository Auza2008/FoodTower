package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventJump;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.value.Numbers;
import net.minecraft.potion.Potion;

public class LegitSpeed extends Module {
    public final Numbers<Double> Boost1 = new Numbers<>("Level2 Boost", "level2 Boost", 0.03, 0.0, 0.1, 0.01);
    public final Numbers<Double> Boost2 = new Numbers<>("Level3 Boost", "level3 Boost", 0.07, 0.0, 0.2, 0.01);

    public LegitSpeed() {
        super("LegitSpeed", "合法加速", new String[]{"ls"}, ModuleType.Movement);
    }

    @NMSL
    public void onJump(EventJump event) {
        if (mc.thePlayer != null || MoveUtils.isMoving()) {
            double boost;
            if (mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() != 2) {
                boost = (Boost1.getValue());
                mc.thePlayer.motionX *= (1.0f + (float)BaseSpeed() * boost);
                mc.thePlayer.motionZ *= (1.0f + (float)BaseSpeed() * boost);
            }
            if (mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() != 3) {
                boost = (Boost2.getValue());
                mc.thePlayer.motionX *= (1.0f + (float)BaseSpeed() * boost);
                mc.thePlayer.motionZ *= (1.0f + (float)BaseSpeed() * boost);
            }
        }
    }

    @NMSL
    public void onUptade(EventPreUpdate e) {
        setSuffix(Boost1.getValue() + " | " + Boost2.getValue());
    }

    public int BaseSpeed() {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }
}
