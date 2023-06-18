package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPacketReceive;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.notifications.user.Notifications;
import cn.foodtower.util.misc.Helper;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;

public class LightningCheck extends Module {
    public LightningCheck() {
        super("LightningCheck", new String[]{"LightningCheck"}, ModuleType.World);
    }

    @EventHandler
    public void onPacketReceive(EventPacketReceive packetEvent) {
        if (packetEvent.getPacket() instanceof S2CPacketSpawnGlobalEntity) {
            S2CPacketSpawnGlobalEntity packetIn = (S2CPacketSpawnGlobalEntity) packetEvent.getPacket();
            if (packetIn.func_149053_g() == 1) {
                int x = packetIn.func_149051_d() / 32;
                int y = packetIn.func_149050_e() / 32;
                int z = packetIn.func_149049_f() / 32;
                Helper.sendMessage("[LightningCheck] X:" + x + " Y:" + y + " Z:" + z);
                Notifications.getManager().post("LightningCheck", "位于坐标X:" + x + " Y:" + y + " Z:" + z + "发现了闪电实体", 2500L);
            }
        }
    }
}
