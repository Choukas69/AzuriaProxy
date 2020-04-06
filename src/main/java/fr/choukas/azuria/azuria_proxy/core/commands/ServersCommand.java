package fr.choukas.azuria.azuria_proxy.core.commands;

import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.commands.BungeeCommandExecutor;
import fr.choukas.azuria.azuria_proxy.api.servers.Server;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.IOException;

public class ServersCommand implements BungeeCommandExecutor {

    private AzuriaProxy proxy;

    public ServersCommand(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0]) {
            case "start":
                if (proxy.getProvider().getGames().withName(args[1]).findAny()) {
                    Server server = proxy.getServerManager().addServer(args[1]);
                    server.start();
                } else {
                    sender.sendMessage(new TextComponent("Ce nom de jeu n'existe pas"));
                }

                break;

            case "kill":
                if (proxy.getServerManager().getServers().withName(args[1]).findAny()) {
                    try {
                        proxy.getServerManager().removeServer(args[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(new TextComponent("Ce serveur n'existe pas"));
                }

                break;
        }
    }
}
