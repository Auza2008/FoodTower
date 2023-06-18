package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.combat.KeepSprint;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.util.entity.MovementUtils;
import net.minecraft.potion.Potion;

import java.awt.*;

public class Sprint extends Module {
    public final Option allDirectionsValue = new Option("AllDirections", true);
    public final Option blindnessValue = new Option("Blindness", true);
    public final Option foodValue = new Option("Food", true);

    public Sprint() {
        super("Sprint", new String[]{"run"}, ModuleType.Movement);
        this.setColor(new Color(158, 205, 125).getRGB());
        this.addValues(allDirectionsValue, blindnessValue, foodValue);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking() || (blindnessValue.get() && mc.thePlayer.isPotionActive(Potion.blindness)) || (foodValue.get() && !(mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying)) || ModuleManager.getModuleByClass(KeepSprint.class).isEnabled() && KillAura.curTarget != null) {
            mc.thePlayer.setSprinting(false);
            return;
        }
        if (allDirectionsValue.get() || mc.thePlayer.movementInput.moveForward >= 0.8F) mc.thePlayer.setSprinting(true);
    }
}
