package cn.foodtower.module.modules.player;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class AutoEat extends Module {
    private final Numbers<Double> FoodLevel = new Numbers<Double>("FoodLevel", 10.0, 1.0, 20.0, 1.0);
    private final Option Gapple = new Option("Gapple", false);
    private final Option RottenFlesh = new Option("RottenFlesh", false);
    private int oldSlot;
    private int bestSlot;

    public AutoEat() {
        super("AutoEat", new String[]{"AutoEat"}, ModuleType.Player);
        this.addValues(this.FoodLevel, this.Gapple, this.RottenFlesh);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        block14:
        {
            block16:
            {
                block15:
                {
                    block11:
                    {
                        block13:
                        {
                            block12:
                            {
                                if (this.oldSlot == -1) {
                                    return;
                                }
                                if (this.oldSlot != -1) break block11;
                                if (mc.thePlayer.capabilities.isCreativeMode) break block12;
                                if (!((double) mc.thePlayer.getFoodStats().getFoodLevel() >= this.FoodLevel.get()))
                                    break block13;
                            }
                            return;
                        }
                        float item = 0.0f;
                        this.bestSlot = -1;
                        for (int i = 0; i < 9; ++i) {
                            ItemStack item1 = mc.thePlayer.inventory.getStackInSlot(i);
                            if (item1 == null) continue;
                            float saturation = 0.0f;
                            if (item1.getItem() instanceof ItemFood && (this.Gapple.get().booleanValue() || item1.getItem() != Items.golden_apple) && (this.RottenFlesh.get().booleanValue() || item1.getItem() != Items.rotten_flesh)) {
                                saturation = ((ItemFood) item1.getItem()).getSaturationModifier(item1);
                            }
                            if (!(saturation > item)) continue;
                            item = saturation;
                            this.bestSlot = i;
                        }
                        if (this.bestSlot == -1) {
                            return;
                        }
                        this.oldSlot = mc.thePlayer.inventory.currentItem;
                        break block14;
                    }
                    if (mc.thePlayer.capabilities.isCreativeMode) break block15;
                    if (mc.thePlayer.getFoodStats().getFoodLevel() < 20) break block16;
                }
                this.stop();
                return;
            }
            ItemStack var6 = mc.thePlayer.inventory.getStackInSlot(this.bestSlot);
            if (var6 == null || !(var6.getItem() instanceof ItemFood)) {
                this.stop();
                return;
            }
            mc.thePlayer.inventory.currentItem = this.bestSlot;
            AutoEat.mc.gameSettings.keyBindUseItem.pressed = true;
        }
    }

    private void stop() {
        AutoEat.mc.gameSettings.keyBindUseItem.pressed = false;
        mc.thePlayer.inventory.currentItem = this.oldSlot;
        this.oldSlot = -1;
    }
}
