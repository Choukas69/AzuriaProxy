package fr.choukas.azuria.azuria_proxy.api.commands;

import fr.choukas.azuria.azuria_common.commands.AbstractCommandManager;
import fr.choukas.azuria.azuria_common.commands.Command;
import fr.choukas.azuria.azuria_common.commands.CommandExecutor;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;

import java.io.File;

public class CommandManager extends AbstractCommandManager {

    private AzuriaProxy proxy;

    public CommandManager(AzuriaProxy proxy) {
        super();
        this.proxy = proxy;

        File config = new File(proxy.getDataFolder(), "commands.json");
        super.loadCommands(config);
    }

    @Override
    public void registerCommand(String name, CommandExecutor executor) {
        Command command = this.commands.get(name);
        command.setExecutor(executor);

        this.proxy.getProxy().getPluginManager().registerCommand(this.proxy, new BungeeCommand(command));
    }
}
