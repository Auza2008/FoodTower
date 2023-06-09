package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.MovementUtils;
import net.minecraft.potion.Potion;

import java.awt.*;

public class Sprint extends Module {
    public final Option allDirectionsValue = new Option("AllDirections", true);
    public final Option blindnessValue = new Option("Blindness", true);
    public final Option foodValue = new Option("Food", true);
    public final Option checkServerSide = new Option("CheckServerSide", false);
    public final Option checkServerSideGround = new Option("CheckServerSideOnlyGround", false);

    public Sprint() {
        super("Sprint", new String[]{"run"}, ModuleType.Movement);
        this.setColor(new Color(158, 205, 125).getRGB());
        this.addValues(allDirectionsValue, blindnessValue, foodValue, checkServerSide, checkServerSideGround);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking() || (blindnessValue.get() && mc.thePlayer.isPotionActive(Potion.blindness)) || (foodValue.get() && !(mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying)) || (checkServerSide.get() && (mc.thePlayer.onGround || !checkServerSideGround.get()) && !allDirectionsValue.get())) {
            mc.thePlayer.setSprinting(false);
            return;
        }
        if (allDirectionsValue.get() || mc.thePlayer.movementInput.moveForward >= 0.8F)
            mc.thePlayer.setSprinting(true);
    }
}
