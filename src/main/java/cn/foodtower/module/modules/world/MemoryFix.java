package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventTick;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.time.TimerUtil;

public class MemoryFix extends Module {
    private final TimerUtil mftimer = new TimerUtil();

    public MemoryFix() {
        super("MemoryFix", new String[]{"memoryfix"}, ModuleType.World);
    }

    @Override
    public void onEnable() {
        Runtime.getRuntime().gc();
        mftimer.reset();
    }

    @EventHandler
    public void onTick(EventTick e) {
        double mflimit = 10.0;
        if (mftimer.hasReached(120000) && mflimit <= ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) * 100f / Runtime.getRuntime().maxMemory())) {
            Runtime.getRuntime().gc();
            mftimer.reset();
        }
    }
}
