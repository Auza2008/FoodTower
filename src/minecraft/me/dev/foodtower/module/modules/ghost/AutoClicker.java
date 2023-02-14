/*
Author:SuMuGod
Date:2022/7/10 4:22
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.ghost;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.Killaura;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.utils.normal.PlayerUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Random;

public class AutoClicker extends Module {
    public static Numbers<Double> cpsmax = new Numbers<>("CPSMax", "CPSMax", 8.0, 2.0, 20.0, 1.0);
    public static Numbers<Double> cpsmin = new Numbers<>("CPSMin", "CPSMin", 8.0, 2.0, 20.0, 1.0);
    public static boolean Clicked;
    private final TimeHelper time2 = new TimeHelper();
    public TimeHelper time = new TimeHelper();
    public Option<Boolean> ab = new Option<>("BlockHit", "BlockHit", false);
    public Option<Boolean> BreakBlock = new Option<>("BreakBlock", "BreakBlock", true);
    protected Random r = new Random();
    private double delay = 0.0;

    public AutoClicker() {
        super("AutoClicker", "自击器点击", new String[]{"ac"}, ModuleType.Ghost);
        this.setColor(new Color(208, 30, 142).getRGB());
    }

    private void delay() {
        float minCps = cpsmin.getValue().floatValue();
        float maxCps = cpsmax.getValue().floatValue();
        float minDelay = 1000.0f / minCps;
        float maxDelay = 1000.0f / maxCps;
        this.delay = (double) maxDelay + this.r.nextDouble() * (double) (minDelay - maxDelay);
    }


    @NMSL
    private void onUpdate(EventPreUpdate event) {
        boolean isblock;
        BlockPos bp = mc.thePlayer.rayTrace(6.0, 0.0f).getBlockPos();
        boolean bl = isblock = mc.theWorld.getBlockState(bp).getBlock() != Blocks.air && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY;
        if (!BreakBlock.getValue()) isblock = false;
        if (this.time2.delay((float) this.delay) && !this.time2.delay((float) (this.delay + this.delay / 2)))
            Clicked = true;
        if (this.time2.delay((float) (this.delay + this.delay - 1.0))) {
            Clicked = false;
            time2.reset();
        }

        if (!Client.instance.getModuleManager().getModuleByClass(Killaura.class).isEnabled() && Mouse.isButtonDown(0) && this.time.delay((float) this.delay) && Minecraft.currentScreen == null && !isblock) {
            PlayerUtil.blockHit(mc.objectMouseOver.entityHit, this.ab.getValue());
            mc.leftClickCounter = 0;
            mc.clickMouse();
            this.delay();
            this.time.reset();
        }
    }
}
