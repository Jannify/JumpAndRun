package de.Jannify.JandR;

import java.awt.List;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;

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
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Action implements Listener {
	public Action(Plugin plugin, Economy Econ) throws IOException {
		pl = plugin;
		config = pl.getConfig();
		econ = Econ;
	}
	
	List<ItemStack> Inv = new ArrayList<ItemStack>();
	HashMap<Player, String> PlayerList = new HashMap<Player, String>();
	HashMap<Player, Array[]> PlayerInv = new HashMap<Player, Array[]>();

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		Dye dye = new Dye();
		dye.setColor(DyeColor.RED);
		ItemStack Restart = dye.toItemStack();
		Restart.setAmount(1);
		ItemMeta Meta = Restart.getItemMeta();
		Meta.setDisplayName("Restart Jump and Run");
		Restart.setItemMeta(Meta);

		ItemStack Leave = new ItemStack(Material.GOLD_INGOT);
		ItemMeta Meta2 = Leave.getItemMeta();
		Meta2.setDisplayName("Leave Jump and Run");
		Leave.setItemMeta(Meta2);
		if (editBlock(loc, 0, 0).getType() == Material.IRON_PLATE || editBlock(loc, 0.5, 0).getType() == Material.IRON_PLATE || editBlock(loc, -0.5, 0).getType() == Material.IRON_PLATE || editBlock(loc, 0, 0.5).getType() == Material.IRON_PLATE
				|| editBlock(loc, 0, -0.5).getType() == Material.IRON_PLATE) {
			if (!PlayerList.containsKey(p)) {
				if (getNearbyEntities(p.getLocation(), 2) != null) {
					String JumpName = getNearbyEntities(p.getLocation(), 2);
					PlayerList.put(p, JumpName);
					for (ItemStack stack : p.getInventory()) {
						
					}
					p.getInventory().clear();

					p.getInventory().addItem(Restart);
					p.getInventory().addItem(Leave);
					p.sendMessage(ChatColor.GREEN + "Du joinst dem Jump: " + ChatColor.BOLD + ChatColor.GOLD + JumpName);
				}
			}
		} else if (editBlock(loc, 0, 0).getType() == Material.GOLD_PLATE || editBlock(loc, 0.5, 0).getType() == Material.GOLD_PLATE || editBlock(loc, -0.5, 0).getType() == Material.GOLD_PLATE || editBlock(loc, 0, 0.5).getType() == Material.GOLD_PLATE
				|| editBlock(loc, 0, -0.5).getType() == Material.GOLD_PLATE) {
			if (PlayerList.containsKey(p)) {
				String JumpName = PlayerList.get(p);
				EconomyResponse r = econ.depositPlayer(p, config.getDouble("Jumps." + JumpName + ".Money"));
				if (r.transactionSuccess()) {
					p.sendMessage(String.format(ChatColor.GREEN + "Für diesen Jump wurde dir " + ChatColor.BOLD + ChatColor.GOLD + "%s" + ChatColor.RESET + ChatColor.GREEN + " gegeben.", econ.format(r.amount)));
					p.sendMessage(String.format(ChatColor.GREEN + "Du hast jetzt " + ChatColor.BOLD + ChatColor.GOLD + "%s", econ.format(r.balance)));
				} else {
					p.sendMessage(String.format(ChatColor.DARK_RED + "Es ist ein Fehler aufgetreten: %s", r.errorMessage));
				}
				PlayerList.remove(p);
				p.getInventory().clear();
				p.sendMessage(ChatColor.GREEN + "Du hast den Jump: " + ChatColor.BOLD + ChatColor.GOLD + JumpName + ChatColor.RESET + ChatColor.GREEN + " geschaft");
			}
		}
	}

	@EventHandler
	public void onInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() != org.bukkit.event.block.Action.PHYSICAL) {
			if (!PlayerList.isEmpty() && PlayerList.containsKey(p)) {
				String JumpName = PlayerList.get(p);
				if (p.getItemInHand().getTypeId() == 351) {
					Location loc = config.getVector("Jumps." + JumpName + ".Start").toLocation(p.getWorld());
					if (config.contains("Jumps." + JumpName + ".StartYaw")) {
						loc.setYaw(config.getInt("Jumps." + JumpName + ".StartYaw"));
					} else {
						loc.setYaw(0);
					}
					p.teleport(loc);
				} else if (p.getItemInHand().getType() == Material.GOLD_INGOT) {
					PlayerList.remove(p);
					p.getInventory().clear();
					p.sendMessage(ChatColor.GREEN + "Du hast den Jump: " + ChatColor.BOLD + ChatColor.GOLD + JumpName + ChatColor.RESET + ChatColor.GREEN + " abgebrochen");

				}

			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (PlayerList.containsKey(p)) {
			e.setCancelled(true);
		}
	}

	public static String getNearbyEntities(Location where, int range) {
		where.setY(where.getY() - 2);
		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), range)) {
				if (entity.getType() == EntityType.ARMOR_STAND) {
					for (String jumps : config.getConfigurationSection("Jumps").getKeys(false)) {
						if (entity.getCustomName().contains(jumps)) {
							return jumps;
						}
					}
				}
			}
		}
		return null;
	}

	public static boolean isInBorder(Location center, Location notCenter, int range) {
		int x = center.getBlockX(), z = center.getBlockZ();
		int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();

		if (x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range)) {
			return false;
		}
		return true;
	}

	public Block editBlock(Location loc, double X, double Z) {
		double bX = loc.getX() + X;
		double bZ = loc.getZ() + Z;
		loc = new Location(loc.getWorld(), bX, loc.getY(), bZ);
		return loc.getBlock();
	}

	public Block editBlockY(Location loc, double Y) {
		double bY = loc.getY() + Y;
		loc = new Location(loc.getWorld(), loc.getX(), bY, loc.getZ());
		return loc.getBlock();
	}

	public Plugin pl;
	public static FileConfiguration config;
	public Economy econ;

}
