package cn.foodtower.module.modules.world;


import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.events.World.EventTick;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.world.BlockUtils;
import net.minecraft.util.BlockPos;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", new String[]{"AutoTool"}, ModuleType.Player);
    }

    public Class type() {
        return EventPacketSend.class;
    }

    @EventHandler
    public void onEvent(EventTick event) {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        if (mc.objectMouseOver == null) {
            return;
        }
        BlockPos pos = mc.objectMouseOver.getBlockPos();
        if (pos == null) {
            return;
        }
        BlockUtils.updateTool(pos);
    }
}
