package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.events.World.EventPostUpdate;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.misc.Helper;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class PacketMotior extends Module {
    private int packetcount;
    private final TimerUtil time = new TimerUtil();

    public PacketMotior() {
        super("PacketMotior", new String[]{"rotate"}, ModuleType.Render);
        this.setColor((new Color(17, 250, 154)).getRGB());
    }

    @EventHandler
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            if (packetcount > 22) {
                e.setCancelled(true);
            }
            ++this.packetcount;
        }
    }

    @EventHandler
    public void OnUpdate(EventPostUpdate event) {
        if (this.time.hasReached(1000.0D)) {
            super.setSuffix("PPS:" + this.packetcount);
            if (this.packetcount > 22) {
                Helper.sendMessage("C03PacketPlayer发送数量过多！ (" + this.packetcount + "/22)");
            }
            this.packetcount = 0;
            this.time.reset();
        }
    }
}
