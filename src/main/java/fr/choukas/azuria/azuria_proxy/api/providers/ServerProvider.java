package fr.choukas.azuria.azuria_proxy.api.providers;

import fr.choukas.azuria.azuria_proxy.api.servers.Server;
import fr.choukas.azuria.azuria_proxy.api.servers.ServerState;
import fr.choukas.azuria.persistence.beans.GameBean;
import fr.choukas.azuria.persistence.beans.MapBean;
import fr.choukas.azuria.persistence.utils.AbstractProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerProvider extends AbstractProvider<String, Server> {

    public ServerProvider(List<Server> servers) {
        super(servers.stream().map(Server::getName).collect(Collectors.toList()), servers);
    }

    public ServerProvider(Map<String, Server> servers) { super(servers); }

    public ServerProvider withName(String name) {
        this.filter(server -> server.getName().equals(name));

        return this;
    }

    public ServerProvider withPort(int id) {
        this.filter(server -> server.getPort() == id);

        return this;
    }

    public ServerProvider withGame(GameBean game) {
        this.filter(server -> server.getGame().equals(game));

        return this;
    }

    public ServerProvider withMap(MapBean map) {
        this.filter(server -> server.getMap().equals(map));

        return this;
    }

    public ServerProvider withState(ServerState state) {
        this.filter(server -> server.getState().equals(state));

        return this;
    }

    public ServerProvider withPlayersAmountLesserThan(int amount) {
        this.filter(server -> server.getPlayers().size() < amount);

        return this;
    }
}
