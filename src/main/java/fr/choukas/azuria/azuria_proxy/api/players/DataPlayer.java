package fr.choukas.azuria.azuria_proxy.api.players;

import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import fr.choukas.azuria.persistence.beans.PlayerBean;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;

public class DataPlayer extends AbstractPlayer {

    public DataPlayer(PlayerBean bean) {
        super(bean);
    }

    @Override
    public void sendMessage(BaseComponent component) {
        ProxyServer.getInstance().getPlayer(this.getUniqueId()).sendMessage(component);
    }

    @Override
    public String getName() {
        return ProxyServer.getInstance().getPlayer(this.getUniqueId()).getName();
    }
}
