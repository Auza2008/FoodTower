package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;

public class NoClickDelay extends Module {

    public NoClickDelay() {
        super("NoClickDelay", "无击键之缓", new String[]{"noclickdelay"}, ModuleType.Combat);
    }

    @NMSL
    public void onPre(EventPreUpdate e) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.leftClickCounter = 0;
        }
    }
}
