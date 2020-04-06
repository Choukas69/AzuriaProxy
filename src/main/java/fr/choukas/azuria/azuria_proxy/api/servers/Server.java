package fr.choukas.azuria.azuria_proxy.api.servers;

import fr.choukas.azuria.persistence.beans.GameBean;
import fr.choukas.azuria.persistence.beans.MapBean;
import fr.choukas.azuria.persistence.beans.ServerBean;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.players.DataPlayer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Server {

    private ServerBean bean;

    private ServerInfo info;
    private int port;

    private MapBean map;
    private ServerState state;
    private List<DataPlayer> players;

    public Server(ServerBean bean, ServerInfo info, int port, MapBean map) {
        this.bean = bean;

        this.info = info;
        this.port = port;

        this.map = map;
        this.state = ServerState.STARTING;
        this.players = new ArrayList<>();
    }

    public void start() {
        AzuriaProxy.log("Trying to start server " + this.getName() + " on port " + this.port);

        try {
            String command = "./start_themis_server.sh "
                    + this.getName() + " "
                    + this.port + " "
                    + this.bean.getMinRam() + " "
                    + this.bean.getMaxRam();

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            AzuriaProxy.log(Level.SEVERE, "Failed to start server " + this.getName() + " on port " + this.port);
            e.printStackTrace();

            return;
        }

        this.state = ServerState.STARTING;

        AzuriaProxy.log("Server " + this.getName() + " started successfully on port " + this.port);
    }

    public void kill() {
        AzuriaProxy.log("Trying to kill server " + this.getName() + " on port " + this.port);

        try {
            String command = "screen -S " + this.getName() + " -X quit";

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            AzuriaProxy.log(Level.SEVERE, "Failed to kill server " + this.getName());
            e.printStackTrace();

            return;
        }

        AzuriaProxy.log("Server " + this.getName() + " was successfully killed");
    }

    public String getName() {
        return this.info.getName();
    }

    public ServerInfo getInfo() {
        return this.info;
    }

    public GameBean getGame() { return this.bean.getGame(); }

    public int getPort() {
        return this.port;
    }

    public MapBean getMap() {
        return this.map;
    }

    public ServerState getState() {
        return this.state;
    }

    public List<DataPlayer> getPlayers() {
        return this.players;
    }
}
