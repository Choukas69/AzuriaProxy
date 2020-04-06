package fr.choukas.azuria.azuria_proxy.core.commands;

import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.commands.BungeeCommandExecutor;
import net.md_5.bungee.api.CommandSender;

public class FriendCommand implements BungeeCommandExecutor {

    private AzuriaProxy proxy;

    public FriendCommand(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
