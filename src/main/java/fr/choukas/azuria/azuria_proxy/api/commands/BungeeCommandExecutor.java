package fr.choukas.azuria.azuria_proxy.api.commands;

import fr.choukas.azuria.azuria_common.commands.CommandExecutor;
import net.md_5.bungee.api.CommandSender;

public interface BungeeCommandExecutor extends CommandExecutor {

    /**
     * Call when all check of the command have been made.
     * @param sender Command's sender
     * @param args Command's arguments
     */
    void execute(CommandSender sender, String[] args);
}
