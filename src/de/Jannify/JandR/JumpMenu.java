package de.Jannify.JandR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class JumpMenu implements Listener, CommandExecutor {
	public JumpMenu(Plugin plugin, FileConfiguration JumpCon) throws IOException {
		pl = plugin;
		config = plugin.getConfig();
		JumpConfig = JumpCon;
	}

	public boolean onCommand(CommandSender cs, Command cmd, String label, String args[]) {
		Player p = null;
		try {
			p = (Player) cs;
		} catch (Exception e) {
			System.out.println(" ");
			System.out.println("============[JumpAndRun]============");
			System.out.println("CommandSender is not a Player");
			System.out.println("============[/JumpAndRun]============");
			System.out.println(" ");
			p.toString();
			return false;
		}
		if (args[0].equalsIgnoreCase("help")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " help:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Gibt hilfe");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + ":" + ChatColor.RESET + "" + ChatColor.WHITE + "Teleportiert dich zum Jump Villager zurück");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " open:" + ChatColor.RESET + "" + ChatColor.WHITE + "Öffnet das Jump Inventar");
		} else if (args.length == 0) {
			try {
				Location loc = config.getVector("Inv.Loc").toLocation(p.getWorld());
				p.teleport(loc);
			} catch (Exception e) {
				p.sendMessage("Villager wurde noch nicht gespawnt. Melde dich bei dem Server Owner.");
			}
		} else if (args[0].equalsIgnoreCase("open")) {
			openInv(p);
		}
		return true;
	}

	public static void openInv(Player p) {
		int index = 0;

		ItemStack item1 = new ItemStack(Material.BARRIER);
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName("" + ChatColor.DARK_GREEN + "Letzte Seite");
		item1.setItemMeta(meta1);

		ItemStack item2 = new ItemStack(Material.WATER_LILY);
		ItemMeta meta2 = item2.getItemMeta();
		meta2.setDisplayName("" + ChatColor.DARK_GREEN + "Nächste Seite");
		item2.setItemMeta(meta2);

		for (String jump : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
			List<String> tmp = new ArrayList<String>();
			double a = (double) JumpConfig.getInt("Jumps." + jump + ".Difficult.Ranking");
			double b = (double) JumpConfig.getInt("Jumps." + jump + ".Difficult.Voter");
			double Ranking = a / b;
			Ranking = Math.round(Ranking * 100) / 100.0;
			if (a == 0 && b == 0) {
				Ranking = 0.0;
			}
			String Time = config.getString("Start.Linie5");
			tmp.add(config.getString("Start.Linie4") + JumpConfig.getString("Jumps." + jump + ".Best.Player"));
			tmp.add(Time.replaceAll("<Zeit>", String.valueOf(JumpConfig.getInt("Jumps." + jump + ".Best.Time"))));
			tmp.add(config.getString("Start.Linie6") + Ranking + "/10");
			tmp.add(config.getString("Ende.Linie5") + JumpConfig.getString("Jumps." + jump + ".Author"));

			ItemStack item;
			if (JumpConfig.getItemStack("Jumps." + jump + ".Item") != null) {
				item = JumpConfig.getItemStack("Jumps." + jump + ".Item");
			} else {
				item = new ItemStack(Material.PAPER);
			}
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("" + ChatColor.GOLD + jump);
			meta.setLore(tmp);
			item.setItemMeta(meta);
			if (index < 45) {
				inv1.setItem(index, item);
			} else if (index < 99 && index > 53) {
				inv2.setItem(index - 54, item);
			} else if (index < 153 && index > 107) {
				inv3.setItem(index - 108, item);
			} else if (index < 207 && index > 161) {
				inv4.setItem(index - 162, item);
			} else if (index < 261 && index > 215) {
				inv5.setItem(index - 216, item);
			}
			index++;
		}
		inv1.setItem(52, item1);
		inv1.setItem(53, item2);
		item1.setType(Material.WATER_LILY);
		inv2.setItem(52, item1);
		inv2.setItem(53, item2);
		inv3.setItem(52, item1);
		inv3.setItem(53, item2);
		inv4.setItem(52, item1);
		inv4.setItem(53, item2);
		item2.setType(Material.BARRIER);
		inv5.setItem(52, item1);
		inv5.setItem(53, item2);

		p.openInventory(inv1);
	}

	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getInventory().getTitle().contains("Seite ")) {
			String Title = e.getInventory().getTitle();
			int SeitenNummer = Integer.valueOf(Title.split(" ")[1]);
			e.setCancelled(true);
			String ItemName = "";

			if (e.getCurrentItem() == null) {
				return;
			}
			if (e.getCurrentItem().getType() == Material.AIR) {
				return;
			} else {
				ItemName = e.getCurrentItem().getItemMeta().getDisplayName();
			}
			if (e.getCurrentItem().getType() == Material.WATER_LILY) {
				if (ItemName.contains("Nächste Seite")) {
					if (SeitenNummer == 1) {
						p.openInventory(inv2);
					} else if (SeitenNummer == 2) {
						p.openInventory(inv3);
					} else if (SeitenNummer == 3) {
						p.openInventory(inv4);
					} else if (SeitenNummer == 4) {
						p.openInventory(inv5);
					}
				} else if (ItemName.contains("Letzte Seite")) {
					if (SeitenNummer == 2) {
						p.openInventory(inv1);
					} else if (SeitenNummer == 3) {
						p.openInventory(inv2);
					} else if (SeitenNummer == 4) {
						p.openInventory(inv3);
					} else if (SeitenNummer == 5) {
						p.openInventory(inv4);
					}
				}
			} else {
				String jump = ItemName.substring(2);
				if (JumpConfig.get("Jumps." + jump + ".Respawn") != null) {
					p.teleport(JumpConfig.getVector("Jumps." + jump + ".Respawn").toLocation(p.getWorld()));
				}
			}

		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		if (e.getRightClicked().getType() == EntityType.VILLAGER) {
			if (e.getRightClicked().getCustomName() != null && e.getRightClicked().getCustomName().contains(e.getRightClicked().getCustomName())) {
				e.setCancelled(true);
				openInv(p);

			}
		}
	}

	@EventHandler
	public void onIteractItem(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() != org.bukkit.event.block.Action.PHYSICAL) {
			if (p.getItemInHand() != null) {
				if (p.getItemInHand().getItemMeta() != null && p.getItemInHand().getItemMeta().getDisplayName() != null && p.getItemInHand().getItemMeta().getDisplayName().equals("§6Jump-List")) {
					openInv(p);
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity().getType() == EntityType.VILLAGER) {
			if (e.getDamager().getType() == EntityType.PLAYER) {
				if (e.getEntity().getCustomName() != null && e.getEntity().getCustomName().contains(e.getEntity().getCustomName())) {
					e.setCancelled(true);
				}
			}
		}
	}

	public Plugin pl;
	public static FileConfiguration config;
	public List<String> tmp;
	public static FileConfiguration JumpConfig;
	public static Inventory inv1 = Bukkit.createInventory(null, 54, "Seite 1");
	public static Inventory inv2 = Bukkit.createInventory(null, 54, "Seite 2");
	public static Inventory inv3 = Bukkit.createInventory(null, 54, "Seite 3");
	public static Inventory inv4 = Bukkit.createInventory(null, 54, "Seite 4");
	public static Inventory inv5 = Bukkit.createInventory(null, 54, "Seite 5");
	public static List<String> lore;

	public static String getNearbyEntities(Location where, int range) {
		for (Entity entity : where.getWorld().getEntities()) {
			if (Action.isInBorder(where, entity.getLocation(), range)) {
				if (entity.getType() == EntityType.ARMOR_STAND) {
					return entity.getCustomName();
				}
			}
		}
		return null;
	}

	public static ItemStack getBackItem() {
		ItemStack bitem = new ItemStack(Material.CLAY_BRICK);
		ItemMeta bmeta = bitem.getItemMeta();
		bmeta.setDisplayName("§6Jump-List");
		lore = new ArrayList<>();
		lore.add("Öffnet das Jump Menu wo du");
		lore.add("dich zu einem Jump adn Run");
		lore.add("teleportieren kannst");
		bmeta.setLore(lore);
		bitem.setItemMeta(bmeta);
		return bitem;
	}
}
