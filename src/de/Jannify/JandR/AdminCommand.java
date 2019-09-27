package de.Jannify.JandR;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AdminCommand implements Listener, CommandExecutor {
	public AdminCommand(Plugin plugin, FileConfiguration JumpCon) throws IOException {
		pl = plugin;
		config = plugin.getConfig();
		JumpConfig = JumpCon;
	}

	public boolean onCommand(CommandSender cs, Command cmd, String label, String args[]) {
		Player p = null;
		try {
			p = (Player) cs;
		} catch (Exception e) {
			System.out.println("============[JumpAndRun]============");
			System.out.println("CommandSender is not a Player");
			System.out.println("============[/JumpAndRun]============");
		}
		if (p.hasPermission("JumpAndRun.Admin")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
				p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " help:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Gibt hilfe");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " syntax:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Zeigt die Syntax von den Befehlen an");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " list:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Zeigt eine Liste der Jumps");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " create:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Erstellt ein JandR");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " remove:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Löscht ein JandR");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setStart:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Start von einem JandR");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setEnde:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt das Ende von einem JandR");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setLeave:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Leave Point von einem JandR");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setMoney:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Belonung von einem JandR");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setYaw:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Gradzahl für den Bsck-Teleport zum Start");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setAuthor:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Author eines JandR");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setItem:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt das Item für die Jump Inventar Liste");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setBestTime:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Beste Zeit vom Jump");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setBestPlayer:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Beste Player vom Jump");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " refreshAll:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt alle Titel der JandR neu");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " spawnInv:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Spawn ein Villager mit Jump Inventar");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " killInv:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Kill den Villager mit demJump Inventar");
			} else if (args[0].equalsIgnoreCase("syntax")) {
				p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " list <Jump> [falls gewünscht Jump Name]");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " create <Jump>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " remove <Jump>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setStart <Jump>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setEnde <Jump>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setLeave <Jump>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setMoney <Jump> <Geld>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setYaw <Jump> <Gradzahl>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setAuthor <Jump> <Author>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setItem <Jump>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setBestTime <Jump> <Time>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " setBestPlayer <Jump> <Player Name>");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " refreshAll");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " spawnInv");
				p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " killInv");
			} else if (args[0].equalsIgnoreCase("create")) {
				createJump(args[1], p);
			} else if (args[0].equalsIgnoreCase("remove")) {
				removeJump(args[1], p);
			} else if (args[0].equalsIgnoreCase("spawnInv")) {
				spawnInv(p);
			}else if (args[0].equalsIgnoreCase("killInv")) {
				killNearbyArmorStands(p.getLocation(), 2);
			} else if (args[0].equalsIgnoreCase("refreshAll")) {
				if (JumpConfig.getConfigurationSection("Jumps") != null) {
					for (String jump : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
						if (JumpConfig.getVector("Jumps." + jump + ".Start") != null && JumpConfig.getVector("Jumps." + jump + ".Ende") != null) {
							refreshOne(jump, p);
						} else {
							p.sendMessage("Es gibt keinen Start oder kein Ende für den Jump §6" + jump + "§r. Erstelle welche.");
						}
					}
				} else {
					p.sendMessage("Es gibt keine Jumps. Erstelle welche.");
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (args.length == 1) {
					for (String jumps : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
						p.sendMessage(ChatColor.GREEN + jumps);
					}
				} else {
					p.sendMessage("=======" + JumpConfig.getConfigurationSection("Jumps").getKeys(false) + "=======");
					for (String jumps : JumpConfig.getConfigurationSection("Jumps." + args[1]).getKeys(false)) {
						if (jumps.equals("Start") || jumps.equals("Ende")) {
							Location loc = JumpConfig.getVector("Jumps." + args[1] + "." + jumps).toLocation(p.getWorld());
							p.sendMessage(ChatColor.GREEN + jumps + ": " + ChatColor.RESET + ChatColor.BOLD + ChatColor.GOLD + "  |  " + ChatColor.BLUE + loc.getBlockX() + ChatColor.GOLD + "  |  " + ChatColor.BLUE + loc.getBlockY() + ChatColor.GOLD + "  |  " + ChatColor.BLUE + loc.getBlockZ()
									+ ChatColor.GOLD + "  |  ");
						} else {
							p.sendMessage(ChatColor.GREEN + jumps + ": " + ChatColor.BOLD + ChatColor.BLUE + JumpConfig.get("Jumps." + args[1] + "." + jumps));
						}
					}
				}
			} else {
				if (args[1] != null && JumpConfig.isSet("Jumps." + args[1])) {
					String JumpName = args[1];
					if (args[0].equalsIgnoreCase("setStart")) {
						setStart(JumpName, p.getLocation(), p, true);
						p.sendMessage("Start XYZ: " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + " " + "wurde gesetzt");
					} else if (args[0].equalsIgnoreCase("setEnde")) {
						setEnde(JumpName, p.getLocation(), p, true);
						p.sendMessage("Ende XYZ: " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + " " + "wurde gesetzt");
					} else if (args[0].equalsIgnoreCase("setLeave")) {
						setLeave(JumpName, p.getLocation(), p);
						p.sendMessage("Leave XYZ: " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + " " + "wurde gesetzt");					
					}else if (args[0].equalsIgnoreCase("setItem")) {
						JumpConfig.set("Jumps." + JumpName + ".Item", p.getInventory().getItemInHand());
						p.sendMessage("Item: " + p.getInventory().getItemInHand().getType() + " wurde gesetzt");					
					} else if (args[0].equalsIgnoreCase("setAuthor")) {
						JumpConfig.set("Jumps." + JumpName + ".Author", args[2]);
						p.sendMessage(args[2] + " wurde als Author gesetzt");
					}else if (args[0].equalsIgnoreCase("setBestPlayer")) {
						JumpConfig.set("Jumps." + JumpName + ".Best.Player", args[2]);
						refreshOne(JumpName, p);
						p.sendMessage(args[2] + " wurde als Beste Player gesetzt gesetzt");

					} else if (args[2] != null) {
					
						int param = -1;
						try {
							param = Integer.valueOf(args[2]);
						} catch (Exception e) {
								p.sendMessage("setMoney, setMaterial und removeMaterial benutzten Zahle(setMoney benutzt Kommazahlen) als Parameter. " + param + " ist keine Zahl.");
						}
						if (param != -1) {
							if (args[0].equalsIgnoreCase("setMoney")) {
								setMoney(JumpName, Double.valueOf(args[2]));
								p.sendMessage(param + " Money wurde gesetzt");
							} else if (args[0].equalsIgnoreCase("setYaw")) {
								setYaw(JumpName, param);
								p.sendMessage(param + "° wurde gesetzt");
							} else if (args[0].equalsIgnoreCase("setBestTime")) {
								JumpConfig.set("Jumps." + JumpName + ".Best.Time", param);
								refreshOne(JumpName, p);
								p.sendMessage(param + " wurde als Beste Zeit gesetzt gesetzt");
							}
						}
					} else {
						p.sendMessage("Syntax: /jump <setStart/setEnd/setMoney/...> <JumpName> <Parameter>");
						p.sendMessage("Falls die Syntax richtig ist, wurde kein Parameter angegeben.");
					}
				} else {
					p.sendMessage("Syntax: /jump <setStart/setEnd/setMoney/...> <JumpName> [fals benötigt Paramenter]");
					p.sendMessage("Falls die Syntax richtig ist, existiert der Jump nicht, erstelle ihn mit /jump create <Name>");
				}
			}
		}
		JumpSystem.saveJumpsConfig();
		pl.saveConfig();
		return true;
	}

	public void createJump(String Name, Player p) {
		Boolean IsNotThere = true;
		if (JumpConfig.getConfigurationSection("Jumps") != null) {
			for (String jump : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
				if (Name.equals(jump)) {
					IsNotThere = false;
				}
			}
		}
		if (IsNotThere) {
			JumpConfig.set("Jumps." + Name, "");
			JumpConfig.set("Jumps." + Name + ".Start", "");
			JumpConfig.set("Jumps." + Name + ".Ende", "");
			JumpConfig.set("Jumps." + Name + ".Respawn", "");
			JumpConfig.set("Jumps." + Name + ".StartYaw", 0);
			JumpConfig.set("Jumps." + Name + ".Money", 0);
			JumpConfig.set("Jumps." + Name + ".Author", p.getName());
			JumpConfig.set("Jumps." + Name + ".Item", "Paper");
			JumpConfig.set("Jumps." + Name + ".Best.Player", "<Never done>");
			JumpConfig.set("Jumps." + Name + ".Best.Time", 0);
			JumpConfig.set("Jumps." + Name + ".Difficult.Voter", 0);
			JumpConfig.set("Jumps." + Name + ".Difficult.Ranking", 0);
			JumpConfig.set("Jumps." + Name + ".Difficult.PlayerList", "");
			p.sendMessage("Jump: " + Name + " wurde erstellt");
		} else {
			p.sendMessage("Diesen Jump gibt es schon. Nimm einen anderen Namen oder löschen ihn zuerst");
		}
	}

	public void removeJump(String Name, Player p) {
		if (JumpConfig.getConfigurationSection("Jumps") != null) {
			for (String jump : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
				if (Name.equals(jump)) {
					JumpConfig.set("Jumps." + Name, null);
					p.sendMessage("Jump: " + Name + " wurde gelöscht");
					return;
				}
			}
		}
		p.sendMessage("Diesen Jump gibt es nicht. Nimm einen anderen/den richtigen Namen");
	}

	public static void setStart(String Name, Location loc, Player p, Boolean Config) {
		int x = Math.round(loc.getBlockX());
		int y = Math.round(loc.getBlockY());
		int z = Math.round(loc.getBlockZ());
		loc.setX((double) x + 0.5);
		loc.setZ((double) z + 0.5);
		if (Config) {
			loc.setY((double) y + 1.75 + config.getDouble("Start.Hoehe"));
			JumpConfig.set("Jumps." + Name + ".Start", loc.toVector());
		} else {
			loc = JumpConfig.getVector("Jumps." + Name + ".Start").toLocation(p.getWorld());
			loc.setY((double) loc.getY() + config.getDouble("Start.Hoehe"));
		}
		double a = (double) JumpConfig.getInt("Jumps." + Name + ".Difficult.Ranking");
		double b = (double) JumpConfig.getInt("Jumps." + Name + ".Difficult.Voter");
		double Ranking = a / b;
		Ranking = Math.round(Ranking * 100) / 100.0;
		if (a == 0 && b == 0) {
			Ranking = 0.0;
		}

		ArmorStand Stand1 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand1.setCustomName(config.getString("Start.Linie1"));
		Stand1.setCustomNameVisible(true);
		Stand1.setGravity(false);
		Stand1.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand2 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand2.setCustomName(config.getString("Start.Linie2"));
		Stand2.setCustomNameVisible(true);
		Stand2.setGravity(false);
		Stand2.setVisible(false);
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand3 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand3.setCustomName(config.getString("Start.Linie3") + Name);
		Stand3.setCustomNameVisible(true);
		Stand3.setGravity(false);
		Stand3.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand4 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand4.setCustomName(config.getString("Start.Linie4") + JumpConfig.getString("Jumps." + Name + ".Best.Player"));
		Stand4.setCustomNameVisible(true);
		Stand4.setGravity(false);
		Stand4.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand5 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		String Time = config.getString("Start.Linie5");
		Stand5.setCustomName(Time.replaceAll("<Zeit>", String.valueOf(JumpConfig.getInt("Jumps." + Name + ".Best.Time"))));
		Stand5.setCustomNameVisible(true);
		Stand5.setGravity(false);
		Stand5.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand6 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand6.setCustomName(config.getString("Start.Linie6") + Ranking + "/10");
		Stand6.setCustomNameVisible(true);
		Stand6.setGravity(false);
		Stand6.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand7 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand7.setCustomName(config.getString("Start.Linie7"));
		Stand7.setCustomNameVisible(true);
		Stand7.setGravity(false);
		Stand7.setVisible(false);
	}

	public static void setEnde(String Name, Location loc, Player p, Boolean Config) {
		int x = Math.round(loc.getBlockX());
		int y = Math.round(loc.getBlockY());
		int z = Math.round(loc.getBlockZ());
		loc.setX((double) x + 0.5);
		loc.setZ((double) z + 0.5);
		if (Config) {
			loc.setY((double) y + 1.75 + config.getDouble("Ende.Hoehe"));
			JumpConfig.set("Jumps." + Name + ".Ende", loc.toVector());
		} else {
			loc = JumpConfig.getVector("Jumps." + Name + ".Ende").toLocation(p.getWorld());
			loc.setY((double) loc.getY() + config.getDouble("Ende.Hoehe"));
		}

		ArmorStand Stand1 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand1.setCustomName(config.getString("Ende.Linie1"));
		Stand1.setCustomNameVisible(true);
		Stand1.setGravity(false);
		Stand1.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand3 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand3.setCustomName(config.getString("Ende.Linie2") + Name);
		Stand3.setCustomNameVisible(true);
		Stand3.setGravity(false);
		Stand3.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand4 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand4.setCustomName(config.getString("Ende.Linie3") + JumpConfig.getString("Jumps." + Name + ".Best.Player"));
		Stand4.setCustomNameVisible(true);
		Stand4.setGravity(false);
		Stand4.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand5 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		String Time = config.getString("Ende.Linie4");
		Stand5.setCustomName(Time.replaceAll("<Zeit>", String.valueOf(JumpConfig.getInt("Jumps." + Name + ".Best.Time"))));		
		Stand5.setCustomNameVisible(true);
		Stand5.setGravity(false);
		Stand5.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand7 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand7.setCustomName(config.getString("Ende.Linie5") + JumpConfig.getString("Jumps." + Name + ".Author"));
		Stand7.setCustomNameVisible(true);
		Stand7.setGravity(false);
		Stand7.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand6 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand6.setCustomName(config.getString("Ende.Linie6"));
		Stand6.setCustomNameVisible(true);
		Stand6.setGravity(false);
		Stand6.setVisible(false);
	}

	public void setMoney(String Name, double Money) {
		JumpConfig.set("Jumps." + Name + ".Money", Money);
	}

	public void setYaw(String Name, int yaw) {
		JumpConfig.set("Jumps." + Name + ".StartYaw", yaw);
	}

	public static void refreshOne(String jump, Player p) {
		Location StartLoc = JumpConfig.getVector("Jumps." + jump + ".Start").toLocation(p.getWorld());
		Location EndeLoc = JumpConfig.getVector("Jumps." + jump + ".Ende").toLocation(p.getWorld());
		killNearbyArmorStands(StartLoc, 3);
		killNearbyArmorStands(EndeLoc, 3);
		setStart(jump, StartLoc, p, false);
		setEnde(jump, EndeLoc, p, false);
	}

	public void setLeave(String Name, Location loc, Player p) {
		int x = Math.round(loc.getBlockX());
		int y = Math.round(loc.getBlockY());
		int z = Math.round(loc.getBlockZ());
		loc.setX((double) x + 0.5);
		loc.setY((double) y);
		loc.setZ((double) z + 0.5);
		JumpConfig.set("Jumps." + Name + ".Respawn", loc.toVector());
	}
	public void spawnInv(Player p) {
		Location loc = p.getLocation();
		
		Villager Villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		Villager.setCustomName(config.getString("Inv.Name"));
		Villager.setCustomNameVisible(false);
		Villager.setCanPickupItems(false);
		Villager.setRemoveWhenFarAway(false);
		PotionEffect addSlow = new PotionEffect(PotionEffectType.SLOW, 9999999, 99999999, false, false);
		Villager.addPotionEffect(addSlow);
		setSilentTag(Villager, true, true);
		
		ArmorStand StandV = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		StandV.setCustomName(config.getString("Inv.Name"));
		StandV.setCustomNameVisible(true);
		StandV.setGravity(false);
		StandV.setVisible(false);
		
		config.set("Inv.Loc", p.getLocation().toVector());
	}

	public Plugin pl;
	public static FileConfiguration config;
	public List<String> tmp;
	public static FileConfiguration JumpConfig;

	public static void killNearbyArmorStands(Location where, int range) {
		where.setY(where.getY() - 2);
		for (Entity entity : where.getWorld().getEntities()) {
			if (Action.isInBorder(where, entity.getLocation(), range)) {
				if (entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.VILLAGER) {
					entity.remove();
				}
			}
		}
	}
	
	public static void setSilentTag(Entity ent, boolean silent, boolean noAi) {
		try {
			String pack = Bukkit.getServer().getClass().getPackage().getName();
			String version = pack.substring(pack.lastIndexOf(".") + 1);

			Method k = Class.forName("net.minecraft.server." + version + ".EntityInsentient").getMethod("k", boolean.class);
			Method b = Class.forName("net.minecraft.server." + version + ".EntityInsentient").getMethod("b", boolean.class);
			Object entity = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftEntity").getMethod("getHandle").invoke(ent);
			k.invoke(entity, silent);
			b.invoke(entity, noAi);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
