package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.utils.normal.Helper;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoGapple extends Module {
    private final Numbers<Double> health = new Numbers<>("Health", "health", 10.0, 1.0, 20.0, 0.5);
    private final Numbers<Double> delay = new Numbers<>("Delay", "delay", 0.0, 100.0, 1000.0, 100.0);
    private final Option<Boolean> NoAbsorption = new Option<>("NoAbsorption", "noabsorption", true);
    private final TimerUtil timer = new TimerUtil();

    public AutoGapple() {
        super("AutoGapple", "食金丹", new String[]{"ag"}, ModuleType.Combat);
    }

    public static int findItem(final int startSlot, final int endSlot, final Item item) {
        for (int i = startSlot; i < endSlot; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null && stack.getItem() == item) return i;
        }
        return -1;
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        if (!timer.hasReached(delay.getValue())) return;
        if (mc.thePlayer.getHealth() <= health.getValue()) {
            doEat(false);
            timer.reset();
        }
    }

    private void doEat(Boolean warn) {
        if (NoAbsorption.getValue() && !warn) {
            float abAmount = mc.thePlayer.getAbsorptionAmount();
            if (abAmount > 0) {
                return;
            }
        }
        int gappleInHotbar = findItem(36, 45, Items.golden_apple);
        if (gappleInHotbar != -1) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(gappleInHotbar - 36));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            for (int i = 0; i < 35; i++) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
            }
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        } else if (warn) {
            Helper.sendMessage("No Gapple were found in hotbar!");
        }
    }
}
