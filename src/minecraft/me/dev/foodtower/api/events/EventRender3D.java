/*
Author:SuMuGod
Date:2022/7/10 3:25
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;
import net.optifine.shaders.Shaders;

public class EventRender3D
        extends Event {
    private float ticks;
    private boolean isUsingShaders;

    public EventRender3D() {
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public EventRender3D(float ticks) {
        this.ticks = ticks;
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public float getPartialTicks() {
        return this.ticks;
    }

    public boolean isUsingShaders() {
        return this.isUsingShaders;
    }
}
