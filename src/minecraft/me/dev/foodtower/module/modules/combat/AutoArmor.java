/*
Author:SuMuGod
Date:2022/7/10 4:04
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.player.InvCleaner;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

import java.awt.*;

public class AutoArmor extends Module {

    public static Mode<Enum<EMode>> MODE = new Mode<>("MODE", "MODE", EMode.values(), EMode.Blink);
    public static Numbers<Double> DELAY = new Numbers<>("DELAY", "DELAY", 1.0D, 0.0D, 10.0D, Double.valueOf(1.0D));

    private final TimerUtil timer = new TimerUtil();

    public AutoArmor() {
        super("AutoArmor", "介胄自擐甲", new String[]{"AutoArmor"}, ModuleType.Combat);
        setColor(new Color(0xFFFFFF).getRGB());
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        for (int i = 5; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                    return false;
            }
        }
        return true;
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor)) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100d;
        }
        return prot;
    }

    @NMSL
    public void onEvent(EventTick event) {
        this.setSuffix(MODE.getValue());
        if (Client.instance.getModuleManager().getModuleByClass(InvCleaner.class).isEnabled())
            return;
        long delay = DELAY.getValue().longValue() * 50;
        if (MODE.getValue() == EMode.Normal && !(Minecraft.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (Minecraft.currentScreen == null || Minecraft.currentScreen instanceof GuiInventory || Minecraft.currentScreen instanceof GuiChat) {
            if (timer.hasReached(delay)) {
                getBestArmor();
            }
        }
    }

    public void getBestArmor() {
        for (int type = 1; type < 5; type++) {
            if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                } else {
                    if (MODE.getValue() == EMode.Blink) {
                        C16PacketClientStatus p = new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT);
                        mc.thePlayer.sendQueue.addToSendQueue(p);
                    }
                    drop(4 + type);
                }
            }
            for (int i = 9; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is, type) && getProtection(is) > 0) {
                        shiftClick(i);
                        timer.reset();
                        if (DELAY.getValue().longValue() > 0)
                            return;
                    }
                }
            }
        }
    }

    public void shiftClick(int slot) {
        Minecraft.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public void drop(int slot) {
        Minecraft.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }

    public enum EMode {
        Normal, Blink
    }
}
