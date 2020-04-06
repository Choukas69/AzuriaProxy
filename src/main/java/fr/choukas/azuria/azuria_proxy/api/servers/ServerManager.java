package fr.choukas.azuria.azuria_proxy.api.servers;

import com.google.gson.Gson;
import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.providers.ServerProvider;
import fr.choukas.azuria.azuria_proxy.api.utils.BungeeRunnable;
import fr.choukas.azuria.persistence.beans.GameBean;
import fr.choukas.azuria.persistence.beans.MapBean;
import fr.choukas.azuria.persistence.beans.ServerBean;
import fr.choukas.azuria.persistence.providers.MapProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerManager {

    private AzuriaProxy proxy;

    private Map<String, Server> servers;

    public ServerManager(AzuriaProxy proxy) {
        this.proxy = proxy;
        this.servers = new ConcurrentHashMap<>();
    }

    public Server addServer(MapBean map) {
        ServerBean bean = proxy.getDataService().getServersManager().getServerConfiguration(map.getGame().getId());

        return this.addServer(bean, map);
    }

    public Server addServer(String gameName) {
        GameBean game = proxy.getProvider().getGames()
                .withName(gameName)
                .get();

        return this.addServer(game);
    }

    public Server addServer(GameBean game) {
        MapBean map = proxy.getProvider().getMaps()
                .withGame(game)
                .get();

        return this.addServer(map);
    }

    private boolean isPortUsed(int port) {
        return this.getServers().withPort(port).findAny();
    }

    public Server addServer(ServerBean bean, MapBean map) {
        String id = UUID.randomUUID().toString().substring(0, 8);

        String name = bean.getModelFolder() + "-" + id;

        int port;
        do {
            port = new Random().nextInt(1000) + 5000; // 5000 - 6000
        } while (this.isPortUsed(port));

        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(name, InetSocketAddress.createUnresolved("localhost", port), "", false);
        ProxyServer.getInstance().getServers().put(name, serverInfo); // Synchronise bungee

        Server server = new Server(bean, serverInfo, port, map);

        new BungeeRunnable() {
            @Override
            public void run() {
                try {
                    File source = new File("/users/dev/desktop/azuria/models/" + bean.getModelFolder());
                    File destination = new File("/users/dev/desktop/azuria/servers/" + name);

                    FileUtils.copyDirectory(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    File map = new File("/users/dev/desktop/azuria/maps/" + bean.getModelFolder() + "/" + server.getMap().getName(), "map.json");
                    Gson gson = new Gson();

                    gson.toJson(server.getMap(), new FileWriter(map));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    // Copy the map
                    File source = new File("/users/dev/desktop/azuria/maps/" + bean.getModelFolder() + "/" + server.getMap().getName());
                    File destination = new File("/users/dev/desktop/azuria/servers/world/" + name);

                    FileUtils.copyDirectory(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                server.start();

                super.interrupt();
            }
        }.start();

        this.servers.put(name, server);

        return server;
    }

    public void removeServer(String name) throws IOException {
        Server server = this.getServers().withName(name).get();

        server.kill();

        FileUtils.deleteDirectory(new File("/users/dev/desktop/azuria/servers/" + server.getName()));

        ProxyServer.getInstance().getServers().remove(name); // Synchronise bungee
        this.servers.remove(name);
    }

    public void shutdown() {
        for (Server server : this.servers.values()) {
            try {
                this.removeServer(server.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestJoin(ServerJoinRequest request) {
        AbstractPlayer sender = request.getSender();
        GameBean game = request.getGame();
        String gameMode = request.getGameMode();

        if (sender.hasParty() && !sender.getParty().getOwner().equals(sender)) {
            // Not party owner
            sender.sendMessage(new TextComponent("Vous ne pouvez pas lancer de partie car vous n'êtes pas le propriétaire de votre partie"));
        } else {
            MapProvider mapProvider = proxy.getProvider().getMaps().withGame(game);

            if (gameMode != null) {
                // Add game mode if it is present
                mapProvider.withGameMode(gameMode);
            }

            int playerAmount = sender.hasParty() ? sender.getParty().getSize() : 1;

            ServerProvider serverProvider = this.getServers()
                    .withState(ServerState.OPEN)
                    .withPlayersAmountLesserThan(playerAmount); // Get open servers

            List<Server> servers = new ArrayList<>();
            for (MapBean map : mapProvider.getList()) {
                // Add all servers which satisfy map conditions
                servers.addAll(serverProvider.withMap(map).getList());
            }

            if (servers.size() == 0) {
                // No servers
                MapBean map = mapProvider.get();
                Server server = this.addServer(map);

                proxy.getSchedulerManager().runRepeatingTask(new BungeeRunnable() {
                    boolean checked = false;

                    @Override
                    public void run() {
                        server.getInfo().ping(((result, error) -> {
                            if (!checked && result != null) {
                                checked = true;
                                connect(sender, server);
                                super.interrupt();
                            }
                        }));
                    }
                }, 1, 1, TimeUnit.SECONDS);
            } else {
                this.connect(sender, new ServerProvider(servers).get());
            }
        }
    }

    private void connect(AbstractPlayer sender, Server server) {
        if (sender.hasParty()) {
            for (AbstractPlayer player : sender.getParty().getPlayers().getList()) {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUniqueId());

                proxiedPlayer.connect(server.getInfo());
            }
        } else {
            ProxyServer.getInstance().getPlayer(sender.getUniqueId()).connect(server.getInfo());
        }
    }

    public ServerProvider getServers() {
        return new ServerProvider(this.servers);
    }
}
