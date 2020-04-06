package fr.choukas.azuria.azuria_proxy.core.listeners;

import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.servers.Server;
import fr.choukas.azuria.azuria_proxy.api.servers.ServerJoinRequest;
import fr.choukas.azuria.messenger.bukkit.MessengerRequest;
import fr.choukas.azuria.messenger.bungee.MessageListener;
import fr.choukas.azuria.messenger.bungee.MessengerResponse;
import fr.choukas.azuria.persistence.beans.GameBean;

import java.util.List;
import java.util.UUID;

public class ChannelListener implements MessageListener {

    private AzuriaProxy proxy;

    public ChannelListener(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public MessengerResponse onReceive(String subChannel, MessengerRequest request) {
        if (subChannel.equalsIgnoreCase("ConnectPlayer")) {
            if (request.getData("uuid") != null) {
                AbstractPlayer sender = proxy.getPlayerManager().getPlayers().withUniqueId(UUID.fromString(request.getData("uuid"))).get();
                GameBean game = proxy.getProvider().getGames().withName(request.getData("game")).get();

                ServerJoinRequest joinRequest = new ServerJoinRequest(sender, game, request.getData("gameMode"));
                proxy.getSchedulerManager().runAsync(() -> this.proxy.getServerManager().requestJoin(joinRequest));
            }
        } else if (subChannel.equalsIgnoreCase("GetPlayers")) {
            if (request.getData("game") != null) {
                GameBean game = proxy.getProvider().getGames().withName(request.getData("game")).get();
                List<Server> servers = proxy.getServerManager().getServers().withGame(game).getList();

                int sum = !servers.isEmpty() ? servers.stream().mapToInt(server -> server.getPlayers().size()).sum(): -1;

                MessengerResponse response = new MessengerResponse("GetPlayers");
                response.setData("players", String.valueOf(sum));

                return response;
            }
        }

        return null;
    }
}
