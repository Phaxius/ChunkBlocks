package com.bluecreeper111.chunkblocks.events;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import com.bluecreeper111.chunkblocks.Main;
import com.bluecreeper111.chunkblocks.commands.ChunkblocksCMD;

public class ChunkEvents implements Listener {
	
	private Main plugin;
	
	public ChunkEvents(Main pl) {
		plugin = pl;
	}
	
	public static HashMap<String, String> chunkblocks = new HashMap<>();
	
	@EventHandler(ignoreCancelled = true)
	public void place(BlockPlaceEvent e) {
		String prefix = "§a§lChunk§2§lBlocks §8§l>> §r";
		Player p = e.getPlayer();
		ItemStack item = e.getItemInHand();
		String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("displayname"));
		String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("placeMessage"));
		String type = plugin.getConfig().getString("material");
		if (item.getType() == Material.matchMaterial(type) && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(name)) {
			Block block = e.getBlock();
			if (chunkblocks.containsKey((block.getChunk().getX() + "," + block.getChunk().getZ() + "," + block.getChunk().getWorld().getName()))) {
				p.sendMessage(prefix + "§c[!] There is already a chunkloader in this chunk!");
				e.setCancelled(true);
				return;
			}
			if (ChatColor.stripColor(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)).equals("(Unlimited Use)")) {
				chunkblocks.put((block.getChunk().getX() + "," + block.getChunk().getZ() + "," + block.getChunk().getWorld().getName()), ("-1," + block.getX() + "," + block.getY() + "," + block.getZ()));
				p.sendMessage(prefix + message.replaceAll("%time%", "unlimited"));
				return;
				} else {
				double time = Double.parseDouble(ChatColor.stripColor(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)));
				chunkblocks.put((block.getChunk().getX() + "," + block.getChunk().getZ() + "," + block.getChunk().getWorld().getName()), ((time * 60) + "," + block.getX() + "," + block.getY() + "," + block.getZ()));
				p.sendMessage(prefix + message.replaceAll("%time%", Double.toString(time)));
				return;
			}
		}
	}
	@EventHandler(ignoreCancelled = true)
	public void breakb(BlockBreakEvent e) {
		String prefix = "§a§lChunk§2§lBlocks §8§l>> §r";
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		boolean needspermission = plugin.getConfig().getBoolean("permission-to-break");
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (needspermission && !p.hasPermission("chunkblocks.break")) {
			e.setCancelled(true);
			p.sendMessage(prefix + "§c[!] You cannot break that!");
			return;
		}
		if (chunkblocks.containsKey((b.getChunk().getX() + "," + b.getChunk().getZ() + "," + b.getChunk().getWorld().getName()))) {
			String key = b.getChunk().getX() + "," + b.getChunk().getZ() + "," + b.getChunk().getWorld().getName();
			String[] info = chunkblocks.get(key).split(",");
			if (!info[1].equals(Integer.toString(b.getX())) || !info[2].equals(Integer.toString(b.getY()))
					|| !info[3].equals(Integer.toString(b.getZ()))) { return; }
			String time = null;
			if (info[0] == "-1") {
				time = "unlimited";
			} else {
				time = df.format(Double.parseDouble(info[0]) / 60);
			}
			ItemStack give = ChunkblocksCMD.makeChunkloader(time);
			b.getWorld().dropItem(b.getLocation(), give);
			b.setType(Material.AIR);
			chunkblocks.remove((b.getChunk().getX() + "," + b.getChunk().getZ() + "," + b.getChunk().getWorld().getName()));
		}
	}
	@EventHandler(ignoreCancelled = true)
	public void explodeb(EntityExplodeEvent e) {
		boolean droptnt = plugin.getConfig().getBoolean("drop-on-tnt-blast");
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		if (!droptnt) { return; }
		for (Block b  :e.blockList()) {
			if (chunkblocks.containsKey((b.getChunk().getX() + "," + b.getChunk().getZ() + "," + b.getChunk().getWorld().getName()))) {
				String key = b.getChunk().getX() + "," + b.getChunk().getZ() + "," + b.getChunk().getWorld().getName();
				String[] info = chunkblocks.get(key).split(",");
				if (!info[1].equals(Integer.toString(b.getX())) || !info[2].equals(Integer.toString(b.getY()))
						|| !info[3].equals(Integer.toString(b.getZ()))) { return; }
				String time = null;
				if (info[0] == "-1") {
					time = "unlimited";
				} else {
					time = df.format(Double.parseDouble(info[0]) / 60);
				}
				ItemStack cl = ChunkblocksCMD.makeChunkloader(time);
				b.getWorld().dropItem(b.getLocation(), cl);
				b.setType(Material.AIR);
				chunkblocks.remove((b.getChunk().getX() + "," + b.getChunk().getZ() + "," + b.getChunk().getWorld().getName()));
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void unload(ChunkUnloadEvent e) {
		for (String c : chunkblocks.keySet()) {
			String[] get = c.split(",");
			int x = Integer.parseInt(get[0]);
			int z = Integer.parseInt(get[1]);
			int x1 = e.getChunk().getX();
			int z1 = e.getChunk().getZ();
			if (x == x1 && z == z1) {
				e.setCancelled(true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						e.getChunk().load();
					}
				}, 1L);
			}
		}
	}

}
