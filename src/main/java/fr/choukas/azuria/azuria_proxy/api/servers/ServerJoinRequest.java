package fr.choukas.azuria.azuria_proxy.api.servers;

import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import fr.choukas.azuria.persistence.beans.GameBean;

public class ServerJoinRequest {

    private AbstractPlayer sender;
    private GameBean game;
    private String gameMode;

    public ServerJoinRequest(AbstractPlayer sender, GameBean game) {
        this(sender, game, null);
    }

    public ServerJoinRequest(AbstractPlayer sender, GameBean game, String gameMode) {
        this.sender = sender;
        this.game = game;
        this.gameMode = gameMode;
    }

    public AbstractPlayer getSender() {
        return this.sender;
    }

    public GameBean getGame() {
        return this.game;
    }

    public String getGameMode() {
        return this.gameMode;
    }
}
