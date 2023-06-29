package cn.foodtower.module.modules.combat;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.value.Mode;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.InventoryUtils;
import cn.foodtower.util.time.TimeHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class AutoSword extends Module {
    private final Mode mode = new Mode("Mpde", ModeE.values(), ModeE.New);
    public TimeHelper timer = new TimeHelper();

    public AutoSword() {
        super("AutoSword", new String[]{"AutoSword"}, ModuleType.Combat);
        addValues(mode);
    }

    @EventHandler
    public void onAttack(EventAttack e) {
        if (mode.get().equals(ModeE.Old)) {
            int best = getBestSword();
            if (best != 0) {
                mc.playerController.windowClick(0, best, 0, 2, mc.thePlayer);
            }
        } else {
            float damage = 1.0f;
            int bestSwordSlot = -1;
            for (int i = 0; i < 9; ++i) {
                float damageLevel;
                ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                if (itemStack == null || !(itemStack.getItem() instanceof ItemSword) || !((damageLevel = (float) this.getSwordDamage(itemStack)) > damage))
                    continue;
                damage = damageLevel;
                bestSwordSlot = i;
            }
            if (bestSwordSlot != -1) {
                mc.thePlayer.inventory.currentItem = bestSwordSlot;
            }
        }
    }

    int getBestSword() {
        ItemStack bestSword = this.getBestItem(ItemSword.class, Comparator.comparingDouble(this::getSwordDamage));
        if (bestSword == null) {
            return 0;
        }
        int slotHB = InventoryUtils.getBestSwordSlotID(bestSword, this.getSwordDamage(bestSword));
        switch (slotHB) {
            case 0:
                slotHB = 36;
                break;
            case 1:
                slotHB = 37;
                break;
            case 2:
                slotHB = 38;
                break;
            case 3:
                slotHB = 39;
                break;
            case 4:
                slotHB = 40;
                break;
            case 5:
                slotHB = 41;
                break;
            case 6:
                slotHB = 42;
                break;
            case 7:
                slotHB = 43;
                break;
            case 8:
                slotHB = 44;
        }
        this.timer.reset();
        return slotHB;
    }

    private ItemStack getBestItem(Class itemType, Comparator comparator) {

        Optional bestItem = mc.thePlayer.inventoryContainer.inventorySlots.stream().map(Slot::getStack).filter(Objects::nonNull).filter((itemStack) -> {
            return itemStack.getItem().getClass().equals(itemType);
        }).max(comparator);
        return (ItemStack) bestItem.orElse(null);
    }

    private double getSwordDamage(ItemStack itemStack) {
        double damage = 0.0D;
        Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = ((AttributeModifier) attributeModifier.get()).getAmount();
        }

        return damage + (double) EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED);
    }

    enum ModeE {
        New, Old
    }
}
