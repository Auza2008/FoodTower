package cn.foodtower.module.modules.player;

import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("NojumpDelay", new String[]{"nojumodelay"}, ModuleType.Player);
    }
}
