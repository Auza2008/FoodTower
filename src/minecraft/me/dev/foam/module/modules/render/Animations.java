/*
Author:SuMuGod
Date:2022/7/10 4:48
Project:foam Reborn
*/
package me.dev.foam.module.modules.render;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPreUpdate;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.value.Mode;
import me.dev.foam.value.Option;

public class Animations extends Module {
    public static Mode<Enum> mode = new Mode("Mode", "mode", renderMode.values(), renderMode.Old);
    public static Option<Boolean> smooth = new Option("Smooth", "Smooth", false);

    public Animations() {
        super("Animations", "动作周还", new String[]{"Blockanimations"}, ModuleType.Render);
        this.setEnabled(true);
        this.setRemoved(true);
    }

    @NMSL
    public void OnUpdate(EventPreUpdate event) {
        this.setSuffix(mode.getValue());
    }

    public enum renderMode {
        Old, Vanilla, Exhibition, Flux, Jello
    }
}
