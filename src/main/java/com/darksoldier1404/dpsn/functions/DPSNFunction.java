package com.darksoldier1404.dpsn.functions;


import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dpsn.obj.StreamInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

import static com.darksoldier1404.dpsn.StreamNotify.plugin;

public class DPSNFunction {
    public static boolean isExistsStream(UUID uuid) {
        return plugin.data.containsKey(uuid);
    }

    public static void addStream(CommandSender sender, String username, String streamName, String streamURL) {
        Player p = Bukkit.getPlayer(username);
        if (p == null) {
            sender.sendMessage("Player not found: " + username);
            return;
        }
        UUID uuid = p.getUniqueId();
        if (isExistsStream(uuid)) {
            StreamInfo si = plugin.data.get(uuid);
            si.setStreamName(streamName);
            si.setStreamUrl(streamURL);
            plugin.data.put(uuid, si);
            plugin.saveDataContainer();
            sender.sendMessage("Updated stream notification for: " + username + " - " + streamName + " (" + streamURL + ")");
            return;
        }
        StreamInfo si = new StreamInfo(streamName, streamURL, 0);
        plugin.data.put(uuid, si);
        plugin.saveDataContainer();
        sender.sendMessage("Added stream notification for: " + username + " - " + streamName + " (" + streamURL + ")");
    }

    public static void removeStream(CommandSender sender, String username) {
        Player p = Bukkit.getPlayer(username);
        if (p == null) {
            sender.sendMessage("Player not found: " + username);
            return;
        }
        UUID uuid = p.getUniqueId();
        if (!isExistsStream(uuid)) {
            sender.sendMessage("No stream notification found for: " + username);
            return;
        }
        new File(plugin.getDataFolder(), "data/" + uuid + ".yml").delete();
        plugin.data.remove(uuid);
        sender.sendMessage("Removed stream notification for: " + username);
    }

    public static void notifyNow(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return;
        }
        Player p = (Player) sender;
        StreamInfo si = plugin.data.get(p.getUniqueId());
        if (si == null) {
            p.sendMessage("No stream information found for you.");
            return;
        }
        if (si.getCooldown() > 0) {
            p.sendMessage("You are on cooldown for " + si.getCooldown() + " seconds.");
            return;
        }
        plugin.getServer().broadcastMessage("§6==============================");
        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage("§e" + si.getStreamName() + " §a님이 방송을 시작하셨습니다!");
        TextComponent tc = new TextComponent(ColorUtils.applyColor("&f[ &6&l방송보러가기 &7- &a&l링크클릭 &f]"));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, si.getStreamUrl()));
        plugin.getServer().spigot().broadcast(tc);
        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage("§6==============================");
        Bukkit.getOnlinePlayers().forEach(op -> {
            op.sendTitle("§6방송 알림", "§e" + si.getStreamName() + " §a님이 방송을 시작하셨습니다!", 10, 70, 10);
            op.playSound(op.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
        });
        si.setCooldown(plugin.cooldown);
        si.startTask();
        plugin.data.put(p.getUniqueId(), si);
        plugin.saveDataContainer();
    }
}
