/*
 * Decompiled with CFR 0_132.
 */
package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;

public class EventPostUpdate extends Event {
	private float yaw;
	private float pitch;

	public EventPostUpdate(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
