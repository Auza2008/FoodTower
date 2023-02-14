/*
Author:SuMuGod
Date:2022/7/10 4:47
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.awt.*;

public class Zoot
        extends Module {
    public Zoot() {
        super("Zoot", "则不善矣", new String[]{"Firion", "antipotion", "antifire"}, ModuleType.Player);
        this.setColor(new Color(208, 203, 229).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        Potion[] arrpotion = Potion.potionTypes;
        int n = arrpotion.length;
        int n2 = 0;
        while (n2 < n) {
            PotionEffect effect;
            Potion potion = arrpotion[n2];
            if (e.getType() == 0 && potion != null && ((effect = this.mc.thePlayer.getActivePotionEffect(potion)) != null && potion.isBadEffect() || this.mc.thePlayer.isBurning() && !this.mc.thePlayer.isInWater() && this.mc.thePlayer.onGround)) {
                int i = 0;
                while (!(this.mc.thePlayer.isBurning() ? i >= 20 : i >= effect.getDuration() / 20)) {
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    ++i;
                }
            }
            ++n2;
        }
    }
}

