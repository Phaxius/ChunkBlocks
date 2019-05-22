package com.bluecreeper111.chunkblocks.events;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkManager extends BukkitRunnable {

	@Override
	public void run() {
		for (Iterator<String> iterator = ChunkEvents.chunkblocks.keySet().iterator(); iterator.hasNext();) {
			String loc = iterator.next();
			String[] info = ChunkEvents.chunkblocks.get(loc).split(",");
			if (info[0] == "-1") {
				return;
			} else {
				double time = Double.parseDouble(info[0]);
				if (time >= 0 && time <= 1) {
					String[] chunk = loc.split(",");
					int x = Integer.parseInt(info[1]);
					int y = Integer.parseInt(info[2]);
					int z = Integer.parseInt(info[3]);
					Bukkit.getWorld(chunk[2]).getBlockAt(x, y, z).setType(Material.AIR);
					iterator.remove();
					return;
				}
				ChunkEvents.chunkblocks.put(loc, (Double.toString(time - 1) + "," + info[1] + "," + info[2] + "," + info[3]));
			}
		}
	}

}
