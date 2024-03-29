package cn.foodtower.module.modules.combat;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.time.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

/**
 * @author cool1
 */
public class AutoSoup extends Module {

    public static Numbers<Double> DELAY = new Numbers<>("Delay", 350d, 100d, 1000d, 50d);
    public static Numbers<Double> HEALTH = new Numbers<>("Health", 3d, 20d, 1d, 1d);
    public static Option DROP = new Option("Drop", true);
    Timer timer = new Timer();

    public AutoSoup() {
        super("AutoSoup", new String[]{"autosoup"}, ModuleType.Combat);
        addValues(DELAY, HEALTH, DROP);
    }

    public static int getSoupFromInventory() {
        Minecraft mc = Minecraft.getMinecraft();
        int soup = -1;
        for (int i = 1; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (Item.getIdFromItem(item) == 282) {
                    soup = i;
                }
            }
        }
        return soup;
    }

    @EventHandler
    public void onEvent(EventPreUpdate event) {
        int soupSlot = getSoupFromInventory();
        if (soupSlot != -1 && mc.thePlayer.getHealth() < (HEALTH.get().floatValue())
                && timer.delay(DELAY.get().floatValue())) {
            swap(getSoupFromInventory(), 6);
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(6));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    protected void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }
}
