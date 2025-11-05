package com.darksoldier1404.dpsn.obj;

import com.darksoldier1404.dppc.data.DataCargo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import static com.darksoldier1404.dpsn.StreamNotify.plugin;

public class StreamInfo implements DataCargo {
    private String streamName;
    private String streamUrl;
    private BukkitTask task;
    private int cooldown;

    public StreamInfo() {
    }

    public StreamInfo(String streamName, String streamUrl, int cooldown) {
        this.streamName = streamName;
        this.streamUrl = streamUrl;
        this.cooldown = cooldown;
    }

    public void clearTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void startTask() {
        clearTask();
        if (cooldown <= 0) return;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            cooldown--;
            if (cooldown <= 0) {
                clearTask();
                cooldown = 0;
            }
        }, 0L, 20L);
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("streamName", streamName);
        data.set("streamUrl", streamUrl);
        data.set("cooldown", cooldown);
        return data;
    }

    @Override
    public StreamInfo deserialize(YamlConfiguration data) {
        if (data != null) {
            String name = data.getString("streamName");
            String url = data.getString("streamUrl");
            int cooldown = data.getInt("cooldown", 0);
            return new StreamInfo(name, url, cooldown);
        }
        return null;
    }
}
