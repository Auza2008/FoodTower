/*
 * Decompiled with CFR 0_132.
 */
package cn.foodtower.ui.login;

import cn.foodtower.Client;
import cn.foodtower.manager.FileManager;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.net.Proxy;

public class AltLoginThread extends Thread {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final String password;
    private final String username;
    private final Alt alt;
    private String status;

    public AltLoginThread(Alt alt) {
        super("Alt Login Thread");
        this.username = alt.getUsername();
        this.password = alt.getPassword();
        this.status = "\u00a7e闲置中...";
        this.alt = alt;
    }

    private Session createSession(Alt alt) {
        if (alt.isMircosoft()) {
            me.liuli.elixir.compat.Session session = alt.getMinecraftAccount().getSession();
            return new Session(session.getUsername(), session.getUuid(), session.getToken(), session.getType());
        } else {
            YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service
                    .createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(username);
            auth.setPassword(password);
            try {
                auth.logIn();
                return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(),
                        auth.getAuthenticatedToken(), "mojang");
            } catch (AuthenticationException authenticationException) {
                return null;
            }
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            this.mc.session = new Session(this.username, "", "", "mojang");
            this.status = "\u00a7a登录成功. (" + this.username + " - 离线模式)";
            return;
        }
        this.status = "\u00a7e尝试登录...";
        Session auth = this.createSession(alt);
        if (auth == null) {
            this.status = "\u00a7c登录失败!";
        } else {
            Client.instance.getAltManager().setLastAlt(alt);
            FileManager.saveLastAlt();
            this.status = "\u00a7a登录成功. (" + auth.getUsername() + ")";
            this.mc.session = auth;
        }
    }
}
