package de.Jannify.JandR;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Action implements Listener {
	public Action(Plugin plugin, Economy Econ, FileConfiguration Jump) throws IOException {
		pl = plugin;
		config = pl.getConfig();
		econ = Econ;
		JumpConfig = Jump;
	}

	public static HashMap<Player, String> PlayerList = new HashMap<Player, String>();

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
				String getTest = getNearbyEntities(p.getLocation(), 4);
				if (getTest != null) {
					String JumpName = getNearbyEntities(p.getLocation(), 4);
					PlayerList.put(p, JumpName);
					p.getInventory().setItem(0, Restart);
					p.getInventory().setItem(1, Leave);
					p.setLevel(0);
					p.sendMessage(ChatColor.GREEN + "Du joinst dem Jump: " + ChatColor.BOLD + ChatColor.GOLD + JumpName);
					p.playSound(p.getLocation(), Sound.LEVEL_UP, (float) 50, (float) 3);
				}
			}
		} else if (editBlock(loc, 0, 0).getType() == Material.GOLD_PLATE || editBlock(loc, 0.5, 0).getType() == Material.GOLD_PLATE || editBlock(loc, -0.5, 0).getType() == Material.GOLD_PLATE || editBlock(loc, 0, 0.5).getType() == Material.GOLD_PLATE
				|| editBlock(loc, 0, -0.5).getType() == Material.GOLD_PLATE) {
			if (PlayerList.containsKey(p)) {
				String JumpName = PlayerList.get(p);
				if (getNearbyEntities(p.getLocation(), 2).contains(JumpName)) {
					p.sendMessage(ChatColor.GREEN + "Du hast den Jump: " + ChatColor.BOLD + ChatColor.GOLD + JumpName + ChatColor.RESET + ChatColor.GREEN + " geschaft");
					EconomyResponse r = econ.depositPlayer(p, JumpConfig.getDouble("Jumps." + JumpName + ".Money"));
					if (r.transactionSuccess()) {
						p.sendMessage(String.format(ChatColor.GREEN + "Für diesen Jump wurde dir " + ChatColor.BOLD + ChatColor.GOLD + "%s" + ChatColor.RESET + ChatColor.GREEN + " gegeben.", econ.format(r.amount)));
						p.sendMessage(String.format(ChatColor.GREEN + "Du hast jetzt " + ChatColor.BOLD + ChatColor.GOLD + "%s", econ.format(r.balance)));
					} else {
						p.sendMessage(String.format(ChatColor.DARK_RED + "Es ist ein Fehler aufgetreten: %s", r.errorMessage));
					}

					p.getInventory().setItem(0, getCompass());
					p.getInventory().setItem(1, JumpMenu.getBackItem());
					PlayerList.remove(p);

					p.sendMessage(ChatColor.GREEN + "Gebe " + ChatColor.BOLD + ChatColor.GOLD + "/jvote <Zahl von 1-10>" + ChatColor.RESET + ChatColor.GREEN + " um das Jump and Run zur Schwierigkeit zu Bewerten, 10 ist das höchste");
					Location tploc = JumpConfig.getVector("Jumps." + JumpName + ".Respawn").toLocation(p.getWorld());
					if (JumpConfig.contains("Jumps." + JumpName + ".StartYaw")) {
						tploc.setYaw(JumpConfig.getInt("Jumps." + JumpName + ".StartYaw"));
					} else {
						tploc.setYaw(0);
					}
					p.teleport(tploc);
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, (float) 50, (float) 1);
					p.playSound(p.getLocation(), Sound.FIREWORK_BLAST, (float) 50, (float) 1);
					p.playSound(p.getLocation(), Sound.FIREWORK_BLAST2, (float) 50, (float) 1);
					p.playSound(p.getLocation(), Sound.FIREWORK_LARGE_BLAST, (float) 50, (float) 1);
					p.playSound(p.getLocation(), Sound.FIREWORK_LARGE_BLAST2, (float) 50, (float) 1);

					if (p.getLevel() < JumpConfig.getInt("Jumps." + JumpName + ".Best.Time") || JumpConfig.getInt("Jumps." + JumpName + ".Best.Time") == 0) {
						if (JumpConfig.getInt("Jumps." + JumpName + ".Best.Time") != 0) {
							p.sendMessage("§l§bDu hast eine neue Bestzeit aufgestellt!!! Mit einer Zeit von §e" + p.getLevel() + "§b Sekuden warst du §e" + (JumpConfig.getInt("Jumps." + JumpName + ".Best.Time") - p.getLevel()) + "§b sekunde schneller als §9"
									+ JumpConfig.getString("Jumps." + JumpName + ".Best.Player"));
						} else {
							p.sendMessage("§l§bDu hast eine neue Bestzeit aufgestellt!!! Du warst mit einer Zeit von §e" + p.getLevel() + "§b der erste Spieler der dieses Jump and Run geschafft hat");

						}
						JumpConfig.set("Jumps." + JumpName + ".Best.Time", p.getLevel());
						JumpConfig.set("Jumps." + JumpName + ".Best.Player", p.getName());
						JumpSystem.saveJumpsConfig();
						AdminCommand.refreshOne(JumpName, p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
						spawnFirework(p);
					}
					p.setLevel(0);
				} else {
					p.sendMessage("§cDas ist nicht das richtigt Ende des Jump and Run's.");
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() != org.bukkit.event.block.Action.PHYSICAL) {
			if (!PlayerList.isEmpty() && PlayerList.containsKey(p)) {
				String JumpName = PlayerList.get(p);
				e.setCancelled(true);
				if (p.getItemInHand().getTypeId() == 351) {
					Location loc = JumpConfig.getVector("Jumps." + JumpName + ".Start").toLocation(p.getWorld());
					loc.setY(loc.getY() -1);
					if (JumpConfig.get("Jumps." + JumpName + ".StartYaw") != null) {
						loc.setYaw(JumpConfig.getInt("Jumps." + JumpName + ".StartYaw"));
					} else {
						loc.setYaw(0);
					}
					p.teleport(loc);
					p.setLevel(0);
				} else if (p.getItemInHand().getType() == Material.GOLD_INGOT) {
					p.getInventory().setItem(0, getCompass());
					p.getInventory().setItem(1, JumpMenu.getBackItem());
					PlayerList.remove(p);
					p.sendMessage(ChatColor.GREEN + "Du hast den Jump: " + ChatColor.BOLD + ChatColor.GOLD + JumpName + ChatColor.RESET + ChatColor.GREEN + " abgebrochen");
					if (JumpConfig.get("Jumps." + JumpName + ".Respawn") != null) {
						Location tploc = JumpConfig.getVector("Jumps." + JumpName + ".Respawn").toLocation(p.getWorld());
						if (JumpConfig.contains("Jumps." + JumpName + ".StartYaw")) {
							tploc.setYaw(JumpConfig.getInt("Jumps." + JumpName + ".StartYaw"));
						} else {
							tploc.setYaw(0);
						}
						p.teleport(tploc);
					}
					p.setLevel(0);
				}

			}
		}
	}

	@EventHandler
	public void onDeath(EntityDamageByBlockEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			if (PlayerList.containsKey(p)) {
				String JumpName = PlayerList.get(p);
				Location loc = JumpConfig.getVector("Jumps." + JumpName + ".Start").toLocation(p.getWorld());
				if (JumpConfig.get("Jumps." + JumpName + ".StartYaw") != null) {
					loc.setYaw(JumpConfig.getInt("Jumps." + JumpName + ".StartYaw"));
				} else {
					loc.setYaw(0);
				}
				p.teleport(loc);
				e.setCancelled(true);
				p.setLevel(0);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (PlayerList.containsKey(p)) {
				if (e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK) {
					e.setCancelled(true);
					p.setFireTicks(0);
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

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = (Player) e.getPlayer();
		if (PlayerList.containsKey(p)) {
			// Inv = PlayerInv.get(p).clone();
			// p.getInventory().setItem(0, (ItemStack) Inv[0]);
			// p.getInventory().setItem(1, (ItemStack) Inv[1]);
			PlayerList.remove(p);
			// PlayerInv.remove(p);
		}
	}

	public static String getNearbyEntities(Location where, int range) {
		where.setY(where.getY() - 2);
		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), range)) {
				if (entity.getType() == EntityType.ARMOR_STAND) {
					for (String jumps : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
						if (entity.getCustomName().contains(jumps)) {
							String[] Name = entity.getCustomName().split("§");
							return Name[Name.length - 1].substring(1);
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

	public void spawnFirework(Player p) {
		Random r = new Random();
		Location loc = p.getLocation();
		loc.setX(loc.getX() + r.nextInt(20));
		loc.setZ(loc.getZ() + r.nextInt(20));
		loc.setX(loc.getX() - r.nextInt(20));
		loc.setZ(loc.getZ() - r.nextInt(20));

		Firework fw = (Firework) p.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		int rt = r.nextInt(4) + 1;
		Type type = Type.BALL;
		if (rt == 1)
			type = Type.BALL;
		if (rt == 2)
			type = Type.BALL_LARGE;
		if (rt == 3)
			type = Type.BURST;
		if (rt == 4)
			type = Type.CREEPER;
		if (rt == 5)
			type = Type.STAR;
		int r1i = r.nextInt(17) + 1;
		int r2i = r.nextInt(17) + 1;
		Color c1 = getColor(r1i);
		Color c2 = getColor(r2i);
		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
		fwm.addEffect(effect);
		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);
		fw.setFireworkMeta(fwm);
		p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, (float) 50, (float) 1);
	}

	private Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}

	public Plugin pl;
	public static FileConfiguration config;
	public static FileConfiguration JumpConfig;
	public Economy econ;

	public static ItemStack getCompass() {
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§aNavigator");
		item.setAmount(1);
		item.setItemMeta(meta);
		return item;
	}
}
