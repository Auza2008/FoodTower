package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.time.TimerUtil;

public class AirLadder extends Module {
    public final TimerUtil timer = new TimerUtil();
    public Option hytBypass = new Option("HYTAntiFlag", false);
    boolean enable = true;

    public AirLadder() {
        super("AirLadder", new String[]{"airladder"}, ModuleType.Movement);
        addValues(hytBypass);
    }

    @EventHandler
    public void onUpdate(EventMotionUpdate e) {
        if (hytBypass.get()) {
            setSuffix("HuaYuTing" + " " + (enable ? "On" : "Off"));
            if (timer.hasReached(2000)) {
                enable = !enable;
                timer.reset();
            }
        } else {
            enable = true;
            setSuffix("");
        }
        if (enable && (mc.thePlayer.isOnLadder() && mc.gameSettings.keyBindJump.pressed)) mc.thePlayer.motionY = 0.11;
    }
}
