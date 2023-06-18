package cn.foodtower.module.modules.player;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.math.RandomUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;

//By TIQS
public class ChatManager extends Module {
    private static final Option tail = new Option("Tails", true);
    private static final Option tailweb = new Option("TailWeb", false);
    private static final Option prefix = new Option("Prefix", false);
    private static final Option randomstring = new Option("RandomString", false);
    private static final Numbers<Double> randomlength = new Numbers<>("RandomStringlength", 5d, 1d, 20d, 1d);

    public ChatManager() {
        super("ChatManager", new String[]{"ChatMan"}, ModuleType.Player);
        addValues(tail, tailweb, prefix, randomstring, randomlength);
    }

    @EventHandler
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) e.getPacket();
            String msg = packet.getMessage();
            if (msg.startsWith("/")) return;
            if (msg.startsWith(".")) return;
            e.setCancelled(true);
            if (tail.get() || tailweb.get()) {
                msg = msg + " | ";
            }
            if (tail.get()) {
                msg = msg + "\u1d05\u026a\u0073\u1d1b\u1d00\u0274\u1d04\u1d07 ";
            }
            if (tailweb.get()) {
                msg = msg + "\u029c\u1d1b\u1d1b\u1d18\u0073\u003a\u002f\u002f\u1d21\u1d21\u1d21\u002e\u1d04\u1d00\u0073\u1d0f\u1d05\u1d0f\u002e\u0078\u028f\u1d22\u002f\u1d05\u026a\u0073\u1d1b\u1d00\u0274\u1d04\u1d07 ";
            }
            if (prefix.get()) {
                msg = "[FoodTower] " + msg;
            }
            if (randomstring.get()) {
                msg = msg + RandomUtil.randomString(randomlength.get().intValue());
            }
            mc.getNetHandler().addToSendQueueSilent(new C01PacketChatMessage(msg));
        }
    }
}
