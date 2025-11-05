package com.darksoldier1404.dpsn;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dpsn.commands.DPSNCommand;
import com.darksoldier1404.dpsn.obj.StreamInfo;

import java.util.UUID;

@DPPCoreVersion(since = "5.3.0")
public class StreamNotify extends DPlugin {
    public static StreamNotify plugin;
    public static DataContainer<UUID, StreamInfo> data;
    public static int cooldown;

    public StreamNotify() {
        super(false);
        plugin = this;
        init();
        data = loadDataContainer(new DataContainer<>(plugin, DataType.CUSTOM, "data"), StreamInfo.class);
        cooldown = config.getInt("Settings.cooldown", 3600);
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(plugin, 27577);
    }

    @Override
    public void onEnable() {
        getCommand("dpsn").setExecutor(new DPSNCommand().getExecutor());
        for (StreamInfo streamInfo : data.values()) {
            streamInfo.startTask();
        }
    }

    @Override
    public void onDisable() {
        saveAllData();
    }
}
