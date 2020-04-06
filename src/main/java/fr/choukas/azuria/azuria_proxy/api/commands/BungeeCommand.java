package fr.choukas.azuria.azuria_proxy.api.commands;

import fr.choukas.azuria.azuria_common.commands.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BungeeCommand extends net.md_5.bungee.api.plugin.Command implements TabExecutor {

    private Command command;

    public BungeeCommand(Command command) {
        super(command.getName(), command.getPermission(), command.getAliases());

        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.command.getUsages().size() == 0) {
            ((BungeeCommandExecutor) this.command.getExecutor()).execute(sender, args);
        } else {
            if (args.length > 0 && (this.command.getUsages().containsKey(args[0]))) {
                // Usage exists
                Command.CommandUsage usage = this.command.getUsages().get(args[0]);

                String[] usageArgs = usage.getUsage().split(" ");

                long requiredArgs = Arrays.stream(usageArgs).filter((s) -> s.startsWith("<")).count();
                long optionalArgs = usageArgs.length - requiredArgs - 1; // Optional args = args_length - required_args - 1 (sub command name)

                if (args.length >= requiredArgs + 1 && args.length <= requiredArgs + optionalArgs + 1) {
                    // Everything is ok -> run command
                    ((BungeeCommandExecutor) this.command.getExecutor()).execute(sender, args);
                } else {
                    // Advanced Help
                    String help = this.command.getHelpFormat(usage);
                    sender.sendMessage(new TextComponent(help));
                }
            } else {
                // Full help
                sender.sendMessage(new TextComponent(this.command.getName() + " : " + this.command.getDescription()));

                for (Command.CommandUsage usage : this.command.getUsages().values()) {
                    String help = this.command.getHelpFormat(usage);
                    sender.sendMessage(new TextComponent(help));
                }
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return this.command.getUsages().keySet().stream().filter(usage -> usage.startsWith(args[0])).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
