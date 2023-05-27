package cn.foodtower.api.events.Misc;

import cn.foodtower.api.Event;

public class EventClickWindow extends Event {
    public int windowId;
    public int slotId;
    public int mouseButtonClicked;
    public int mode;

    public EventClickWindow(int windowId, int slotId, int mouseButtonClicked, int mode) {
        this.windowId = windowId;
        this.slotId = slotId;
        this.mouseButtonClicked = mouseButtonClicked;
        this.mode = mode;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMouseButtonClicked() {
        return mouseButtonClicked;
    }

    public void setMouseButtonClicked(int mouseButtonClicked) {
        this.mouseButtonClicked = mouseButtonClicked;
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }
}
