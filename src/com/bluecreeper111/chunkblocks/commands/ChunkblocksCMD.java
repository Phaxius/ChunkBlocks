package com.bluecreeper111.chunkblocks.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.bluecreeper111.chunkblocks.Main;

public class ChunkblocksCMD implements CommandExecutor {
	
	private static Main plugin;
	
	public ChunkblocksCMD(Main pl) {
		plugin = pl;
	}

	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String prefix = "§a§lChunk§2§lBlocks §8§l>> §r";
		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("chunkblocks.reload")) {
				sender.sendMessage(prefix + "§c[!] You do not have permission to do that!");
				return true;
			}
			plugin.reloadConfig();
			sender.sendMessage(prefix + "§aConfiguration file reloaded.");
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
			if (!sender.hasPermission("chunkblocks.give")) {
				sender.sendMessage(prefix + "§c[!] You do not have permission to do that!");
				return true;
			}
			Player tar = Bukkit.getPlayerExact(args[1]);
			if (tar == null) {
				sender.sendMessage(prefix + "§c[!] Player §4" + args[1] + "§c was not found!");
				return true;
			}
			boolean isunlimited = args[2].equalsIgnoreCase("unlimited");
			try {
				Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				if (!isunlimited) {
				sender.sendMessage(prefix + "§c[!] Argument §4" + args[2] + "§c must either be a number or \"unlimited\"");
				return true;
				}
			}
			ItemStack give = makeChunkloader(args[2]);
			if (tar.getInventory().firstEmpty() == -1) {
				tar.getWorld().dropItem(tar.getLocation(), give);
			} else {
				tar.getInventory().addItem(give);
			}
			sender.sendMessage(prefix + "§aGiven a ChunkBlock to player §2" + tar.getName());
			return true;
		} else {
			sender.sendMessage(prefix + "§c[!] Invalid command syntax! Try /chunkblocks [give | reload] <player> <time>");
		}
		return true;
	}
	
	public static ItemStack makeChunkloader(String time) {
		boolean isunlimited = false;
		try {
			Double.parseDouble(time);
		} catch (NumberFormatException e) {
			if (time.equalsIgnoreCase("unlimited")) {
				isunlimited = true;
			} else {
				return null;
			}
		}
		String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("displayname"));
		String lore = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("lore"));
		String material = plugin.getConfig().getString("material");
		ItemStack chunkblock = new ItemStack(Material.matchMaterial(material), 1);
		ItemMeta meta = chunkblock.getItemMeta();
		meta.setDisplayName(name);
		List<String> loreadd = new ArrayList<>();
		loreadd.add(lore);
		if (isunlimited) {
			loreadd.add("§7(Unlimited Use)");
		} else {
			loreadd.add("§c§lTime Left: §f(In Minutes):");
			loreadd.add("§7" + time);
		}
		meta.setLore(loreadd);
		chunkblock.setItemMeta(meta);
		return chunkblock;
	}

}
