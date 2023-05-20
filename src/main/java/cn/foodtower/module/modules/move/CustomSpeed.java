package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.MovementUtils;

public class CustomSpeed extends Module {
    public final Numbers<Double> customSpeedValue = new Numbers<>("CustomSpeed", 1.6d, 0.2d, 2d, 0.01d);
    public final Numbers<Double> customYValue = new Numbers<>("CustomY", 0d, 0d, 4d, 0.01d);
    public final Numbers<Double> customTimerValue = new Numbers<>("CustomTimer", 1d, 0.1d, 2d, 0.01d);
    public final Option customSprint = new Option("CustomSprint", true);
    public final Option customStrafeValue = new Option("CustomStrafe", true);
    public final Option resetXZValue = new Option("CustomResetXZ", false);
    public final Option resetYValue = new Option("CustomResetY", false);

    public CustomSpeed() {
        super("CustomSpeed", new String[]{"Cspeed"}, ModuleType.Movement);
        addValues(customSpeedValue, customYValue, customTimerValue, customStrafeValue, resetXZValue, resetYValue);
    }

    @EventHandler
    public void onMotion(EventMotionUpdate e) {
        if (mc.thePlayer.isSneaking() || !e.isPre()) return;
        if (MovementUtils.isMoving()) {
            mc.timer.timerSpeed = customTimerValue.getValue().floatValue();

            if (mc.thePlayer.onGround) {
                MovementUtils.strafe(customSpeedValue.getValue().floatValue());
                mc.thePlayer.motionY = customYValue.getValue();
            } else if (customStrafeValue.getValue()) {
                MovementUtils.strafe(customSpeedValue.getValue().floatValue());
            } else {
                MovementUtils.strafe();
            }
        } else
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
    }

    @EventHandler
    public void onRender2d(EventRender2D e) {
        setSuffix(customTimerValue.getValue() + "|" + customSpeedValue.getValue() + "|" + customYValue.getValue());
    }

    @Override
    public void onEnable() {
        if (resetXZValue.getValue()) mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
        if (resetYValue.getValue()) mc.thePlayer.motionY = 0D;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        super.onDisable();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer.isSneaking())
            return;

        if (MovementUtils.isMoving() && customSprint.getValue())
            mc.thePlayer.setSprinting(true);
    }

    @EventHandler
    public void onMove(EventMove event) {
    }
}
