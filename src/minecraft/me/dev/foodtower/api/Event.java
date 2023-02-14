/*
Author:SuMuGod
Date:2022/7/10 3:11
Project:foodtower Reborn
*/
package me.dev.foodtower.api;

import net.minecraft.util.IChatComponent;

public abstract class Event {
    public String message;
    private IChatComponent ChatComponent;
    public byte type;
    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public byte getType() {
        return this.type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public IChatComponent getChatComponent() {
        return this.ChatComponent;
    }

    public String getMessage() {
        return message;
    }

    public void setChatComponent(IChatComponent ChatComponent) {
        this.ChatComponent = ChatComponent;
    }
}

