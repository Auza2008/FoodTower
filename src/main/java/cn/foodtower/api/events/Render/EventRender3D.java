/*
 * Decompiled with CFR 0_132.
 */
package cn.foodtower.api.events.Render;

import cn.foodtower.api.Event;
import net.optifine.shaders.Shaders;

public class EventRender3D extends Event {
    public static float ticks;
    private final boolean isUsingShaders;

    public EventRender3D() {
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public EventRender3D(float ticks) {
        EventRender3D.ticks = ticks;
        this.isUsingShaders = Shaders.getShaderPackName() != null;
    }

    public float getPartialTicks() {
        return ticks;
    }

    public void setPartialTicks(float ticks) {
        EventRender3D.ticks = ticks;
    }

    public boolean isUsingShaders() {
        return this.isUsingShaders;
    }
}
