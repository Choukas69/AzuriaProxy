package fr.choukas.azuria.azuria_proxy.core.listeners.proxy;

import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class ProxyJoinListener implements Listener {

    private AzuriaProxy proxy;

    public ProxyJoinListener(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @EventHandler
    public void onProxyJoin(PostLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        proxy.getSchedulerManager().runAsync(() -> proxy.getPlayerManager().loadPlayer(uuid));
    }
}
