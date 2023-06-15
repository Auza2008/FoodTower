package cn.foodtower.module.modules.combat;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventWorldChanged;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.notifications.user.Notifications;

public class SmartDCJ extends Module {
    boolean once = false;

    public SmartDCJ() {
        super("SmartDCJ", null, ModuleType.Combat);
    }

    @Override
    public void onEnable() {
        once = false;
    }

    @EventHandler
    private void nmsl(EventRender3D e) {
        if (ModuleManager.getModuleByClass(KillAura.class).isEnabled()) {
            if (KillAura.curTarget != null) {
                if ((KillAura.curTarget.getHealth() < mc.thePlayer.getHealth())) {
                    if (!once) {
                        once = true;
                        Notifications.getManager().post("SmartDCJ", "恭喜您打出先手伤害!");
                    }
                }
            }
        }
    }

    @EventHandler
    private void L(EventWorldChanged e) {
        once = false;
    }
}
