package com.bluecreeper111.chunkblocks;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bluecreeper111.chunkblocks.commands.ChunkblocksCMD;
import com.bluecreeper111.chunkblocks.events.ChunkEvents;
import com.bluecreeper111.chunkblocks.events.ChunkManager;

public class Main extends JavaPlugin {
	
	public void onEnable() {
		Metrics metrics = new Metrics(this);
		PluginManager pm = Bukkit.getPluginManager();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveResource("config.yml", false);
		}
		if (!metrics.isEnabled()) {
			getLogger().warning("- WARNING - Plugin metrics could not be loaded.");
		}
		getCommand("chunkblocks").setExecutor(new ChunkblocksCMD(this));
		pm.addPermission(new Permission("chunkblocks.reload"));
		pm.addPermission(new Permission("chunkblocks.give"));
		new ChunkManager().runTaskTimer(this, 20, 20);
		getLogger().info("------------------------------------------------------------");
		getLogger().info("Successfully enabled ChunkBlocks developed by bluecreeper111");
		getLogger().info("------------------------------------------------------------");
		pm.registerEvents(new ChunkEvents(this), this);
		loadHashset();
	}
	
	public void onDisable() {
		saveHashset();
	}
	
	private void saveHashset() {
		int number = 0;
		for (String loc : ChunkEvents.chunkblocks.keySet()) {
			getConfig().set("chunkblocks." + Integer.toString(number) + ".chunk", loc);
			getConfig().set("chunkblocks." + Integer.toString(number) + ".info", ChunkEvents.chunkblocks.get(loc));
			number++;
		}
		saveConfig();
	}
	private void loadHashset() {
		if (getConfig().getConfigurationSection("chunkblocks") == null) { return; }
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
            	for (String n : getConfig().getConfigurationSection("chunkblocks").getKeys(false)) {
            		String chunk = getConfig().getString("chunkblocks." + n + ".chunk");
            		String info = getConfig().getString("chunkblocks." + n + ".info");
            		ChunkEvents.chunkblocks.put(chunk, info);
            	}
            	getConfig().set("chunkblocks", null);
            	saveConfig();
			}
		}, 10L);
	}

}
