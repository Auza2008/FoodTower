package net.minecraft.client;

import cn.foodtower.util.ClientSetting;

public class ClientBrandRetriever
{
    public static String getClientModName()
    {
        return (ClientSetting.fakeForge.get() ? "fml,forge" : "vanilla");
    }
}
