package com.darksoldier1404.dpsn.commands;

import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import com.darksoldier1404.dpsn.functions.DPSNFunction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.stream.Collectors;

import static com.darksoldier1404.dpsn.StreamNotify.plugin;


public class DPSNCommand {
    private final CommandBuilder builder = new CommandBuilder(plugin);

    public DPSNCommand() {
        // Example Sub-Commands
        builder.addSubCommand("add", "dpsn.admin", "/dpsn add <username> <방송이름> <방송URL>", false, (p, args) -> {
            if (args.length == 4) {
                String username = args[1];
                String streamName = args[2];
                String streamURL = args[3];
                DPSNFunction.addStream(p, username, streamName, streamURL);
                return true;
            }
            return false;
        });

        builder.addSubCommand("remove", "dpsn.admin", "/dpsn remove <username>", false, (p, args) -> {
            if (args.length == 2) {
                String username = args[1];
                DPSNFunction.removeStream(p, username);
                return true;
            }
            return false;
        });

        // cooldown
        builder.addSubCommand("cooldown", "dpsn.admin", "/dpsn cooldown <seconds>", false, (p, args) -> {
            if (args.length == 2) {
                try {
                    int seconds = Integer.parseInt(args[1]);
                    YamlConfiguration config = plugin.getConfig();
                    config.set("Settings.cooldown", seconds);
                    plugin.cooldown = seconds;
                    plugin.setConfig(config);
                    plugin.saveDataContainer();
                    p.sendMessage("Set cooldown to " + seconds + " seconds.");
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage("Invalid number format.");
                    return false;
                }
            }
            return false;
        });

        builder.addSubCommand("notify", "/dpsn notify", true, (p, args) -> {
            if (args.length == 1) {
                DPSNFunction.notifyNow(p);
                return true;
            }
            return false;
        });

        // Tab Completion
        for (String c : builder.getSubCommandNames()) {
            builder.addTabCompletion(c, (sender, args) -> {
                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("add")) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(player -> player.getName()).collect(Collectors.toList());
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        return plugin.data.keySet().stream()
                                .map(uuid -> {
                                    if (Bukkit.getPlayer(uuid) != null) {
                                        return Bukkit.getPlayer(uuid).getName();
                                    } else {
                                        return null;
                                    }
                                })
                                .filter(name -> name != null)
                                .collect(Collectors.toList());
                    }
                }
                return null;
            });
        }
    }

    public CommandBuilder getExecutor() {
        return builder;
    }
}
