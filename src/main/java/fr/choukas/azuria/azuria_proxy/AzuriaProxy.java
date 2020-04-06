package fr.choukas.azuria.azuria_proxy;

import fr.choukas.azuria.azuria_common.redis.managers.RedisManager;
import fr.choukas.azuria.azuria_proxy.api.commands.CommandManager;
import fr.choukas.azuria.azuria_proxy.core.commands.FriendCommand;
import fr.choukas.azuria.azuria_proxy.core.commands.PartyCommand;
import fr.choukas.azuria.azuria_proxy.core.listeners.PartyListener;
import fr.choukas.azuria.azuria_proxy.core.listeners.proxy.ProxyJoinListener;
import fr.choukas.azuria.azuria_proxy.core.listeners.proxy.ProxyLeaveListener;
import fr.choukas.azuria.messenger.bungee.Messenger;
import fr.choukas.azuria.persistence.DataService;
import fr.choukas.azuria.azuria_proxy.api.parties.PartyManager;
import fr.choukas.azuria.azuria_proxy.api.players.PlayerManager;
import fr.choukas.azuria.azuria_proxy.api.servers.ServerManager;
import fr.choukas.azuria.azuria_proxy.api.utils.SchedulerManager;
import fr.choukas.azuria.azuria_proxy.core.listeners.ChannelListener;
import fr.choukas.azuria.persistence.providers.Provider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.logging.Level;

public class AzuriaProxy extends Plugin {

    private DataService dataService;
    private RedisManager redisManager;

    private PlayerManager playerManager;
    private PartyManager partyManager;
    private ServerManager serverManager;

    private CommandManager commandManager;
    private SchedulerManager schedulerManager;

    @Override
    public void onEnable() {
        log("========== [AzuriaProxy] ==========");
        log("= AzuriaProxy is loading...       =");
        log("===================================");

        this.dataService = new DataService();
        this.dataService.init();

        this.redisManager = new RedisManager(this.dataService.getRedisAccess());

        Messenger messenger = new Messenger(this);
        messenger.registerListener("GetPlayers", new ChannelListener(this));

        this.playerManager = new PlayerManager(this);
        this.partyManager = new PartyManager(this);
        this.serverManager = new ServerManager(this);

        this.commandManager = new CommandManager(this);
        this.schedulerManager = new SchedulerManager(this);

        this.registerListeners();
        this.registerCommands();
    }

    private void registerListeners() {
        PluginManager pm = this.getProxy().getPluginManager();

        pm.registerListener(this, new ProxyJoinListener(this));
        pm.registerListener(this, new ProxyLeaveListener(this));

        pm.registerListener(this, new PartyListener(this));
    }

    private void registerCommands() {
        CommandManager cm = this.commandManager;

        cm.registerCommand("party", new PartyCommand(this));
        cm.registerCommand("friend", new FriendCommand(this));
    }

    @Override
    public void onDisable() {
        log("========== [AzuriaProxy] ==========");
        log("= AzuriaProxy is disabling...     =");
        log("===================================");

        this.dataService.shutdown();

        this.serverManager.shutdown();
    }

    public static void log(Level level, String message) {
        ProxyServer.getInstance().getLogger().log(level, message);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public DataService getDataService() {
        return this.dataService;
    }

    public Provider getProvider() {
        return this.dataService.getProvider();
    }

    public RedisManager getRedisManager() {
        return this.redisManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public PartyManager getPartyManager() {
        return this.partyManager;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public SchedulerManager getSchedulerManager() {
        return this.schedulerManager;
    }
}
