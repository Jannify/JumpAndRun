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
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.plugin.Plugin;

public class Action implements Listener {
	Plugin pl;
	FileConfiguration config;
	String test;

	public Action(Plugin plugin) throws IOException {
		pl = plugin;
		config = pl.getConfig();
	}

	HashMap<Player, String> PlayerList = new HashMap<Player, String>();

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Dye dye = new Dye();
		dye.setColor(DyeColor.RED);
		ItemStack Item = dye.toItemStack();
		ItemMeta Meta = Item.getItemMeta();
		Item.setAmount(1);
		Meta.setDisplayName("Restart Jump and Run");
		Item.setItemMeta(Meta);

		ItemStack Item2 = new ItemStack(Material.GOLD_INGOT);
		ItemMeta Meta2 = Item2.getItemMeta();
		Meta2.setDisplayName("Leave Jump and Run");
		Item2.setItemMeta(Meta2);

		Player p = e.getPlayer();
		if (p.getLocation().getBlock().getType() == Material.IRON_PLATE) {
			if (!PlayerList.containsKey(p) || PlayerList.isEmpty()) {
				String JumpName = getNearbyEntities(p.getLocation(), 2).get(0).getCustomName();
				PlayerList.put(p, JumpName);
				p.getInventory().clear();

				p.getInventory().addItem(Item);
				p.getInventory().addItem(Item2);
				p.sendMessage(ChatColor.GREEN + "Du joinst Jump " + ChatColor.BOLD + "" + ChatColor.GOLD + JumpName);
			}
		} else if (p.getLocation().getBlock().getType() == Material.GOLD_PLATE) {
			if (PlayerList.containsKey(p)) {
				String JumpName = PlayerList.get(p);
				PlayerList.remove(p);
				p.getInventory().clear();
				p.sendMessage(ChatColor.GREEN + "Du hast es den Jump: " + ChatColor.BOLD + "" + ChatColor.GOLD
						+ JumpName + ChatColor.RESET + "" + ChatColor.GREEN + " geschaft");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void onFall(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (PlayerList.containsKey(p)) {
			p.sendMessage("1");
			String JumpName = PlayerList.get(p);
			Location loc = p.getLocation();
			loc.setY(loc.getY() - 1);
			for (Integer mat : config.getIntegerList("Jumps." + JumpName + ".Material")) {
				p.sendMessage("2");
				if (loc.getBlock().getTypeId() == mat) {
					p.sendMessage("3");
					p.teleport((Entity) config.getVector("Jumps." + JumpName + ".Start"));
				}
			}

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

}
