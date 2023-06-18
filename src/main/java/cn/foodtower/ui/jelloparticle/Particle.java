package cn.foodtower.ui.jelloparticle;

import net.minecraft.client.gui.ScaledResolution;

import java.util.Random;

public class Particle {

    public float x, y, radius, speed, ticks, opacity;

    public Particle(ScaledResolution sr, float r, float s) {
        x = new Random().nextFloat() * ScaledResolution.getScaledWidth();
        y = new Random().nextFloat() * ScaledResolution.getScaledHeight();
        ticks = new Random().nextFloat() * ScaledResolution.getScaledHeight() / 2;
        radius = r;
        speed = s;
    }
}
