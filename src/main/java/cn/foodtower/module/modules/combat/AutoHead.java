package cn.foodtower.module.modules.combat;


import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.misc.Helper;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoHead extends Module {
    private final TimerUtil timer = new TimerUtil();
    private final Option eatHeads = new Option("Eatheads", true);
    private final Option eatApples = new Option("Eatapples", true);
    private final Numbers<Double> health = new Numbers("Health", 10.0, 1.0, 20.0, 1.0);
    private final Numbers<Double> delay = new Numbers("Delay", 750.0, 10.0, 2000.0, 25.0);
    private final Option NoAbsorption = new Option("NoAbsorption", true);

    public AutoHead() {
        super("AutoHead", new String[]{"AutoHead", "EH", "eathead"}, ModuleType.Combat);
        addValues(health, delay, eatApples, eatHeads, NoAbsorption);
    }

    public void onEnable() {
        timer.reset();
    }

    public void onDisable() {
    }


    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (!timer.hasReached(delay.get())) return;
        if (mc.thePlayer.getHealth() <= health.get()) {
            doEat(false);
            timer.reset();
        }

//                if (doEatHeads) {
//                    slot = this.getItemFromHotbar(397);
//                } else {
//                    slot = this.getItemFromHotbar(322);
//                }


    }

    private void doEat(Boolean warn) {
        if (NoAbsorption.get() && !warn) {
            float abAmount = mc.thePlayer.getAbsorptionAmount();
            if (abAmount > 0) {
                return;
            }
        }
        if (eatApples.get()) {
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
        if (eatHeads.get()) {
            int headInHotbar = findItem(36, 45, Item.getItemById(397));
            if (headInHotbar != -1) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(headInHotbar - 36));
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                for (int i = 0; i < 35; i++) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                }
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            } else if (warn) {
                Helper.sendMessage("No Head were found in hotbar!");
            }
        }
    }

    public static int findItem(final int startSlot, final int endSlot, final Item item) {
        for (int i = startSlot; i < endSlot; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null && stack.getItem() == item) return i;
        }
        return -1;
    }
}
