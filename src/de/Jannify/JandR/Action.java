package de.Jannify.JandR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.plugin.Plugin;

public class Action implements Listener {
	public Action(Plugin plugin) throws IOException {
		pl = plugin;
		config = pl.getConfig();
	}

	HashMap<Player, String> PlayerList;

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Dye dye = new Dye();
		dye.setColor(DyeColor.RED);
		ItemStack Item = dye.toItemStack();
		ItemMeta Meta = Item.getItemMeta();
		Meta.setDisplayName("Restart Jump and Run");
		Item.setItemMeta(Meta);

		ItemStack Item2 = new ItemStack(Material.GOLD_INGOT);
		ItemMeta Meta2 = Item2.getItemMeta();
		Meta2.setDisplayName("Leave Jump and Run");
		Item2.setItemMeta(Meta2);

		Player p = e.getPlayer();
		try {
			if (p.getLocation().getBlock().getType() == Material.IRON_PLATE) {
				if (!PlayerList.containsKey(p)) {
					if (!getNearbyEntities(p.getLocation(), 2).isEmpty()) {
						String JumpName = getNearbyEntities(p.getLocation(), 2).get(0).getCustomName().toString();
						PlayerList.put(p, JumpName);
						p.getInventory().clear();

						p.getInventory().addItem(Item);
						p.getInventory().addItem(Item2);
						p.sendMessage("Du joinst Jump " + JumpName);
					}
				}
			} else if (p.getLocation().getBlock().getType() == Material.GOLD_PLATE) {
				p.sendMessage("Jumps." + PlayerList.get(p));
				PlayerList.remove(p);
				p.getInventory().clear();
				p.sendMessage("Du hast es geschaft");
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public static List<Entity> getNearbyEntities(Location where, int range) {
		List<Entity> found = new ArrayList<Entity>();

		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), range)) {
				if (entity.getType() == EntityType.ARMOR_STAND) {
					found.add(entity);
				}
			}
		}
		return found;
	}

	public static boolean isInBorder(Location center, Location notCenter, int range) {
		int x = center.getBlockX(), z = center.getBlockZ();
		int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();

		if (x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range)) {
			return false;
		}
		return true;
	}
	
	public Plugin pl;
	public FileConfiguration config;


}
