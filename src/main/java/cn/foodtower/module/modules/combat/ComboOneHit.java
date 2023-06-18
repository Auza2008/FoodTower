package cn.foodtower.module.modules.combat;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

public class ComboOneHit extends Module {
    private final Numbers<Double> packets = new Numbers<>("Packets", 100d, 1d, 500d, 1d);
    private final Mode swing = new Mode("Swing", SwingE.values(), SwingE.Client);

    public ComboOneHit() {
        super("ComboOneHit", null, ModuleType.Combat);
        addValues(packets, swing);
    }

    @EventHandler
    private void onAttack(EventAttack e) {
        if (e.getEntity() == null) return;
        for (int i = 0; i < packets.get(); ++i) {
            sendPacket(new C02PacketUseEntity(e.getEntity(), C02PacketUseEntity.Action.ATTACK));
            switch ((SwingE) swing.get()) {
                case Client:
                    mc.thePlayer.swingItem();
                    break;
                case Packet:
                    sendPacket(new C0APacketAnimation());
                    break;
            }
        }
    }

    private enum SwingE {
        Client, Packet, None
    }
}
