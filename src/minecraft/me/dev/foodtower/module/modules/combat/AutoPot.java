/*
Author:SuMuGod
Date:2022/7/10 4:05
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.movement.Scaffold;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.utils.normal.InvUtils;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

public class AutoPot extends Module {
    public AutoPot() {
        super("AutoPot", "自出药水", new String[]{"Autopot"}, ModuleType.Combat);
    }
    private Numbers<Double> health = new Numbers<Double>("Health", "health", 10.0, 1.0, 20.0, 1.0);
    private Numbers<Double> delay = new Numbers<Double>("Delay", "delay", 500.0, 100.0, 1500.0, 50.0);
    private Option<Boolean> jump = new Option<>("Jump","Jump",false);
    private Option<Boolean> heal = new Option<>("Heal","Heal",false);
    private Option<Boolean> regen = new Option<>("Regen","Regen",false);
    private Option<Boolean> speed = new Option<>("Speed","Speed",false);
    private Option<Boolean> nofrog = new Option<>("Nofrog","Nofrog",false);

    private boolean jumping;
    private boolean rotated;
    public static TimeHelper timer = new TimeHelper();
    private TimeHelper cooldown = new TimeHelper();

    private int lastPottedSlot;

    @NMSL
    private void onMove(final EventMove event) {
        if (this.jumping) {
            this.mc.thePlayer.motionX = 0;
            this.mc.thePlayer.motionZ = 0;
            event.x = 0;
            event.z = 0;

            if (cooldown.hasReached(100) && this.mc.thePlayer.onGround) {
                this.jumping = false;
            }
        }
    }

    @NMSL
    private void onPreUpdate(final EventPreUpdate event) {
        if (MoveUtils.getBlockUnderPlayer(mc.thePlayer, 0.01) instanceof BlockGlass || !MoveUtils.isOnGround(0.01))  {
            timer.reset();
            return;
        }

        if (mc.thePlayer.openContainer != null) {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                timer.reset();
                return;
            }
        }

        if (Client.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled())
            return;

        if (Killaura.target != null) {
            rotated = false;
            timer.reset();
            return;
        }

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasReached(delay.getValue())) {
            if (jump.getValue() && !mc.thePlayer.isInLiquid()) {
                event.setPitch(-89.5f);

                this.jumping = true;
                if (this.mc.thePlayer.onGround) {
                    this.mc.thePlayer.jump();
                    cooldown.reset();
                }
            } else {
                event.setPitch(89.5f);
            }

            rotated = true;
        }
    }

    @NMSL
    private void onPostUpdate(final EventPostUpdate event) {
        if (!rotated)
            return;

        rotated = false;

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasReached(delay.getValue()) && mc.thePlayer.isCollidedVertically) {
            final int prevSlot = mc.thePlayer.inventory.currentItem;
            if (potSlot < 9) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(potSlot));
                mc.thePlayer.sendQueue.addToSendQueue(
                        new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(prevSlot));
                mc.thePlayer.inventory.currentItem = prevSlot;
                timer.reset();

                this.lastPottedSlot = potSlot;
            }
        }
    }

    @NMSL
    public void onTick(EventTick event) {
        if (this.mc.currentScreen != null)
            return;

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && potSlot > 8 && this.mc.thePlayer.ticksExisted % 4 == 0) {
            this.swap(potSlot, InvUtils.findEmptySlot(this.lastPottedSlot));
        }
    }

    private void swap(final int slot, final int hotbarNum) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2,
                this.mc.thePlayer);
    }

    private int getPotFromInventory() {
        // heals
        for (int i = 0; i < 36; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                ItemPotion pot = (ItemPotion) item;

                if (!ItemPotion.isSplash(is.getMetadata())) {
                    continue;
                }

                List<PotionEffect> effects = pot.getEffects(is);

                for (PotionEffect effect : effects) {
                    if (mc.thePlayer.getHealth() < health.getValue() && ((heal.getValue() && effect.getPotionID() == Potion.heal.id) || (regen.getValue() && effect.getPotionID() == Potion.regeneration.id && !hasEffect(Potion.regeneration.id))))
                        return i;
                }
            }
        }

        // others
        for (int i = 0; i < 36; ++i) {
            if (this.mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = this.mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                List<PotionEffect> effects = ((ItemPotion) item).getEffects(is);

                for (PotionEffect effect : effects) {
                    if (effect.getPotionID() == Potion.moveSpeed.id && speed.getValue()
                            && !hasEffect(Potion.moveSpeed.id))
                        if (!is.getDisplayName().contains("\247a") || !nofrog.getValue())
                            return i;
                }
            }
        }

        return -1;
    }

    private boolean hasEffect(int potionId) {
        for (PotionEffect item : mc.thePlayer.getActivePotionEffects()) {
            if (item.getPotionID() == potionId)
                return true;
        }
        return false;
    }
}

