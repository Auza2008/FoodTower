package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventTick;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

import java.awt.Color;

public class FastPlace extends Module {
	public FastPlace() {
		super("FastPlace", new String[] { "fplace", "fc" }, ModuleType.World);
		this.setColor(new Color(226, 197, 78).getRGB());
	}

	@EventHandler
	private void onTick( EventTick e) {
		this.mc.rightClickDelayTimer = 0;
	}
}
