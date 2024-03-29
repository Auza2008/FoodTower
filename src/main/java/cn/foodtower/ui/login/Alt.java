package cn.foodtower.ui.login;

import me.liuli.elixir.account.MicrosoftAccount;
import me.liuli.elixir.account.MinecraftAccount;

public class Alt {
    private final String username;
    private String mask = "";
    private String password;
    private boolean mircosoft = false;
    private MinecraftAccount minecraftAccount;

    public Alt(String username, String password) {
        this(username, password, "");
    }

    public Alt(String username, String password, String mask) {
        this.username = username;
        this.password = password;
        this.mask = mask;
    }

    public Alt(MicrosoftAccount account) {
        this.username = account.getName();
        this.password = "MIRCOSOFT";
        minecraftAccount = account;
        this.mircosoft = true;
    }

    public MinecraftAccount getMinecraftAccount() {
        return minecraftAccount;
    }

    public boolean isMircosoft() {
        return mircosoft;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }
}
