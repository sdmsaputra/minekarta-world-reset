package com.minekarta.worldreset.command;

import com.minekarta.worldreset.MinekartaWorldReset;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MWRCommand implements CommandExecutor, TabCompleter {

    private final MinekartaWorldReset plugin;

    public MWRCommand(MinekartaWorldReset plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /mwr <reload|nextreset|reset>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.loadConfig();
                sender.sendMessage("Configuration reloaded.");
                break;
            case "nextreset":
                Date nextReset = plugin.getSchedulerService().getNextResetTime();
                if (nextReset != null) {
                    sender.sendMessage("Next reset is scheduled for: " + nextReset);
                } else {
                    sender.sendMessage("No world reset is currently scheduled.");
                }
                break;
            case "reset":
                sender.sendMessage("World reset manually triggered.");
                plugin.triggerReset();
                break;
            default:
                sender.sendMessage("Unknown subcommand. Usage: /mwr <reload|nextreset|reset>");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "nextreset", "reset")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
