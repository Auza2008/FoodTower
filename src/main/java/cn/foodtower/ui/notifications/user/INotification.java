package cn.foodtower.ui.notifications.user;

public interface INotification {
    String getHeader();

    String getSubtext();

    long getStart();

    long getDisplayTime();

    Notifications.Type getType();

    float getX();

    void setX(int var1);

    float getTarX();

    void setTarX(int var1);

    float getTarY();

    long checkTime();

    float getY();

    void setY(int var1);

    long getLast();

    void setLast(long var1);
}
