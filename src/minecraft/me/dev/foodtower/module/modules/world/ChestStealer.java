/*
Author:SuMuGod
Date:2022/7/10 5:11
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.AutoPot;
import me.dev.foodtower.utils.math.MathUtils;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChestStealer
        extends Module {
    private Numbers<Double> firstItemDelay = new Numbers<Double>("First Item Delay", "First Item Delay", 50.0, 0.0, 1000.0, 10.0);
    private Numbers<Double> delay = new Numbers<Double>("Delay", "delay", 50.0, 0.0, 1000.0, 10.0);
    private Option<Boolean> blink = new Option<>("Blink", "Blink", false);
    private Option<Boolean> onlychest = new Option<>("Only Chest", "Only Chest", false);
    private Option<Boolean> trash = new Option<>("Trash", "Trash", false);
    private Option<Boolean> tools = new Option<>("Tools", "Tools", true);
    private Option<Boolean> bow = new Option<>("Bow", "Bow", true);

    public static boolean isChest;
    public static TimeHelper time = new TimeHelper();
    public static TimeHelper openGuiHelper = new TimeHelper();
    private final int[] itemHelmet;
    private final int[] itemChestplate;
    private final int[] itemLeggings;
    private final int[] itemBoots;
    int nextDelay = 0;

    public ChestStealer() {
        super("ChestStealer", "自动搜箱", new String[]{"cheststeal", "chests", "stealer"}, ModuleType.World);
        this.setColor(new Color(218, 97, 127).getRGB());
        this.itemHelmet = new int[]{298, 302, 306, 310, 314};
        this.itemChestplate = new int[]{299, 303, 307, 311, 315};
        this.itemLeggings = new int[]{300, 304, 308, 312, 316};
        this.itemBoots = new int[]{301, 305, 309, 313, 317};
        setKey(Keyboard.KEY_C);
    }

    public static boolean isStealing() {
        return !time.isDelayComplete(200);
    }

    CopyOnWriteArrayList<Packet<?>> packets = new CopyOnWriteArrayList<>();
    boolean isReleasing = false;

    @NMSL
    public void onPacketSend(EventPacketSend e) {
        if (blink.getValue() && e.getPacket() instanceof C03PacketPlayer) {
            if (mc.thePlayer.openContainer instanceof ContainerChest && isChest) {
                packets.add(e.getPacket());
                e.setCancelled(true);
                AutoPot.timer.reset();
            } else if (!packets.isEmpty() && !isReleasing) {
                isReleasing = true;
                for (Packet<?> packet : packets) {
                    mc.getNetHandler().getNetworkManager().sendPacket(packet);
                }
                packets.clear();
                isReleasing = false;
            }
        }
    }

    @NMSL
    public void onUpdate(EventPostUpdate event) {
        if (!GuiChest.firstItem.isDelayComplete(firstItemDelay.getValue()))
            return;

        if (!isChest && onlychest.getValue())
            return;

        if (mc.thePlayer.openContainer != null) {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest c = (ContainerChest) mc.thePlayer.openContainer;

                if (isChestEmpty(c) && openGuiHelper.isDelayComplete(800) && time.isDelayComplete(400)) {
                    mc.thePlayer.closeScreen();
                }

                for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                        if (time.isDelayComplete(nextDelay) && (itemIsUseful(c, i) || trash.getValue())) {
                            nextDelay = (int) (delay.getValue() * MathUtils.getRandomInRange(0.75, 1.25));
                            if (new Random().nextInt(100) > 80) continue; // Random
                            Minecraft.playerController.windowClick(c.windowId, i, 0, 1, mc.thePlayer);
                            time.reset();
                        }
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                if (itemIsUseful(c, i) || trash.getValue()) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isPotionNegative(ItemStack itemStack) {
        ItemPotion potion = (ItemPotion) itemStack.getItem();

        List<PotionEffect> potionEffectList = potion.getEffects(itemStack);

        return potionEffectList.stream().map(potionEffect -> Potion.potionTypes[potionEffect.getPotionID()])
                .anyMatch(Potion::isBadEffect);
    }


    private boolean itemIsUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();

        if ((item instanceof ItemAxe || item instanceof ItemPickaxe) && tools.getValue()) {
            return true;
        }

        if (item instanceof ItemFood)
            return true;
        if ((item instanceof ItemBow || item == Items.arrow) && bow.getValue())
            return true;
        if (item instanceof ItemPotion && !isPotionNegative(itemStack))
            return true;
        if (item instanceof ItemSword && isBestSword(c, itemStack))
            return true;
        if (item instanceof ItemArmor && isBestArmor(c, itemStack))
            return true;
        if (item instanceof ItemBlock)
            return true;

        return item instanceof ItemEnderPearl;
    }


    private float getSwordDamage(ItemStack itemStack) {
        float damage = 0f;
        Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = (float) ((AttributeModifier) attributeModifier.get()).getAmount();
        }
        return damage + EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
    }


    private boolean isBestSword(ContainerChest c, ItemStack item) {
        float itemdamage1 = getSwordDamage(item);
        float itemdamage2 = 0f;
        for (int i = 0; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                float tempdamage = getSwordDamage(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
                if (tempdamage >= itemdamage2)
                    itemdamage2 = tempdamage;
            }
        }
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                float tempdamage = getSwordDamage(c.getLowerChestInventory().getStackInSlot(i));
                if (tempdamage >= itemdamage2)
                    itemdamage2 = tempdamage;
            }
        }
        return itemdamage1 == itemdamage2;
    }


    private boolean isBestArmor(ContainerChest c, ItemStack item) {
        float itempro1 = ((ItemArmor) item.getItem()).damageReduceAmount;
        float itempro2 = 0f;
        if (isContain(itemHelmet, Item.getIdFromItem(item.getItem()))) { // ͷ��
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemHelmet,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemHelmet,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemChestplate, Item.getIdFromItem(item.getItem()))) { // �ؼ�
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemChestplate,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemChestplate,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemLeggings, Item.getIdFromItem(item.getItem()))) { // ����
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemLeggings,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemLeggings,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemBoots, Item.getIdFromItem(item.getItem()))) { // Ь��
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemBoots,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemBoots,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        return itempro1 == itempro2;
    }

    public static boolean isContain(int[] arr, int targetValue) {
        return ArrayUtils.contains(arr, targetValue);
    }
}
