package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventAttack;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleManager;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

public class ComboOneHit extends Module {
    private final Numbers<Double> packets = new Numbers<>("Packets", "packets", 200.0, 0.0, 500.0, 10.0);
    private final Option<Boolean> swing = new Option<>("Swing", "swing", true);

    public ComboOneHit() {
        super("ComboOneHit", "自连击", new String[]{"ComboOneHit"}, ModuleType.Combat);
    }

    @NMSL
    public void onAttack(EventAttack e) {
        if (Client.instance.getModuleManager().getModuleByClass(Killaura.class).isEnabled() && Killaura.target != null) {
            for (int i = 0; i <= packets.getValue(); i++) {
                if (swing.getValue()) {
                    mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                }
                mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(Killaura.target, C02PacketUseEntity.Action.ATTACK));
            }
        }
    }
}
