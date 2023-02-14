/*
Author:SuMuGod
Date:2022/7/10 4:06
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.utils.normal.InventoryUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.awt.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class AutoSword
        extends Module {
    public TimerUtil timer = new TimerUtil();
    private ItemStack bestSword;
    private ItemStack prevBestSword;
    private boolean shouldSwitch = false;

    public AutoSword() {
        super("AutoSword", "剑自剑归", new String[]{"autosword"}, ModuleType.Combat);
        this.setColor(new Color(255, 255, 255).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate event) {
        if (this.mc.thePlayer.ticksExisted % 7 == 0) {
            if (this.mc.thePlayer.capabilities.isCreativeMode || this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer.windowId != 0) {
                return;
            }
            this.bestSword = this.getBestItem(ItemSword.class, Comparator.comparingDouble(this::getSwordDamage));
            if (this.bestSword == null) {
                return;
            }
            boolean isInHBSlot = InventoryUtils.hotbarHas(this.bestSword.getItem(), 0);
            if (isInHBSlot) {
                if (InventoryUtils.getItemBySlotID(0) != null) {
                    if (InventoryUtils.getItemBySlotID(0).getItem() instanceof ItemSword) {
                        isInHBSlot = this.getSwordDamage(InventoryUtils.getItemBySlotID(0)) >= this.getSwordDamage(this.bestSword);
                    }
                } else {
                    isInHBSlot = false;
                }
            }
            if (this.prevBestSword == null || !this.prevBestSword.equals(this.bestSword) || !isInHBSlot) {
                this.shouldSwitch = true;
                this.prevBestSword = this.bestSword;
            } else {
                this.shouldSwitch = false;
            }
            if (this.shouldSwitch && this.timer.hasReached(1.0)) {
                int slotHB = InventoryUtils.getBestSwordSlotID(this.bestSword, this.getSwordDamage(this.bestSword));
                switch (slotHB) {
                    case 0: {
                        slotHB = 36;
                        break;
                    }
                    case 1: {
                        slotHB = 37;
                        break;
                    }
                    case 2: {
                        slotHB = 38;
                        break;
                    }
                    case 3: {
                        slotHB = 39;
                        break;
                    }
                    case 4: {
                        slotHB = 40;
                        break;
                    }
                    case 5: {
                        slotHB = 41;
                        break;
                    }
                    case 6: {
                        slotHB = 42;
                        break;
                    }
                    case 7: {
                        slotHB = 43;
                        break;
                    }
                    case 8: {
                        slotHB = 44;
                        break;
                    }
                }
                this.mc.playerController.windowClick(0, slotHB, 0, 2, this.mc.thePlayer);
                this.timer.reset();
            }
        }
    }

    private ItemStack getBestItem(Class<? extends Item> itemType, Comparator comparator) {
        Optional<ItemStack> bestItem = this.mc.thePlayer.inventoryContainer.inventorySlots.stream().map(Slot::getStack).filter(Objects::nonNull).filter(itemStack -> itemStack.getItem().getClass().equals((Object) itemType)).max(comparator);
        return bestItem.orElse(null);
    }

    private double getSwordDamage(ItemStack itemStack) {
        double damage = 0.0;
        Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = ((AttributeModifier) attributeModifier.get()).getAmount();
        }
        return damage += (double) EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
    }
}


