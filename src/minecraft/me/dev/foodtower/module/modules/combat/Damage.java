/*
Author:SuMuGod
Date:2022/7/10 4:09
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.ColorUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

public class Damage extends Module {
    public Damage() {
        super("SelfAura", "看淡了", new String[]{"dmg"}, ModuleType.Combat);
        this.setColor(ColorUtils.randomColor());
    }

    @NMSL
    private void onTick(EventMove e) {
        damagePlayer(1);
        mc.thePlayer.swingItem();
    }

    public void damagePlayer(int damage) {
        if (damage < 1)
            damage = 1;
        if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
            damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

        double offset = 0.0625;
        if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
            for (int i = 0; i <= ((3 + damage) / offset); ++i) { // TODO: teach rederpz (and myself) how math works
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
            }
        }
    }
}

