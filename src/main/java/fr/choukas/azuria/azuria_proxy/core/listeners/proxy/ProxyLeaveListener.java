package fr.choukas.azuria.azuria_proxy.core.listeners.proxy;

import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class ProxyLeaveListener implements Listener {

    private AzuriaProxy proxy;

    public ProxyLeaveListener(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @EventHandler
    public void onProxyLeave(PlayerDisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        proxy.getSchedulerManager().runAsync(() -> proxy.getPlayerManager().unloadPlayer(uuid));
    }
}
