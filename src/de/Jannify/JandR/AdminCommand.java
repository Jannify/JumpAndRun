package de.Jannify.JandR;

import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class AdminCommand implements Listener, CommandExecutor {
	public AdminCommand(Plugin plugin) throws IOException {
		pl = plugin;
		config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender cs, Command cmd, String label, String args[]) {
		Player p = (Player) cs;
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump help:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Gibt hilfe");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump syntax:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Zeigt die Syntax von den Befehlen an");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump list:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Zeigt eine Liste der Jumps");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump create:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Erstellt ein JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump remove:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Löscht ein JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setStart:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Start von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setEnde:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt das Ende von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setMoney:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Belonung von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setYaw:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Gradzahl für den Teleport zum Start");
		} else if (args[0].equalsIgnoreCase("syntax")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump list <Jump> (falls gewünscht");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump create <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump remove <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setStart <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setEnde <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setMoney <Name> <Geld>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setYaw <Name> <Gradzahl>");
		} else if (args[0] == null) {
			p.sendMessage("Kein Command angegeben.");
		} else if (args[0].equalsIgnoreCase("create")) {
			createJump(args[1]);
			p.sendMessage("Jump: " + args[1] + " wurde erstellt");
		} else if (args[0].equalsIgnoreCase("remove")) {
			removeJump(args[1]);
			p.sendMessage("Jump: " + args[1] + " wurde gelöscht");
		} else if (args[0].equalsIgnoreCase("list")) {
			if (args.length == 1) {
				for (String jumps : config.getConfigurationSection("Jumps").getKeys(false)) {
					p.sendMessage(ChatColor.GREEN + jumps);
				}
			} else {
				p.sendMessage("=======" + config.getConfigurationSection("Jumps").getKeys(false) + "=======");
				for (String jumps : config.getConfigurationSection("Jumps." + args[1]).getKeys(false)) {
					if (jumps.equals("Start") || jumps.equals("Ende")) {
						Location loc = config.getVector("Jumps." + args[1] + "." + jumps).toLocation(p.getWorld());
						p.sendMessage(ChatColor.GREEN + jumps + ": " + ChatColor.RESET + ChatColor.BOLD + ChatColor.GOLD + "  |  " + ChatColor.BLUE + loc.getBlockX() + ChatColor.GOLD + "  |  " + ChatColor.BLUE + loc.getBlockY() + ChatColor.GOLD + "  |  " + ChatColor.BLUE + loc.getBlockZ()
								+ ChatColor.GOLD + "  |  ");
					} else {
						p.sendMessage(ChatColor.GREEN + jumps + ": " + ChatColor.BOLD + ChatColor.BLUE + config.get("Jumps." + args[1] + "." + jumps));
					}
				}
			}
		} else {
			if (config.isSet("Jumps." + args[1])) {
				String JumpName = args[1];
				if (args[0].equalsIgnoreCase("setStart")) {
					setStart(JumpName, p.getLocation());
					p.sendMessage("Start XYZ: " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + " " + "wurde gesetzt");
				} else if (args[0].equalsIgnoreCase("setEnde")) {
					setEnde(JumpName, p.getLocation());
					p.sendMessage("Ende XYZ: " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + " " + "wurde gesetzt");
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
		pl.saveConfig();
		return true;
	}

	public void createJump(String Name) {
		config.set("Jumps." + Name, "");
	}

	public void removeJump(String Name) {
		config.set("Jumps." + Name, null);
	}

	public void setStart(String Name, Location loc) {
		int x = Math.round(loc.getBlockX());
		int y = Math.round(loc.getBlockY());
		int z = Math.round(loc.getBlockZ());
		config.set("Jumps." + Name + ".Start", loc.toVector());
		loc.setX((double) x + 0.5);
		loc.setY((double) y - 2);
		loc.setZ((double) z + 0.5);
		ArmorStand Stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand.setCustomName(Name);
		Stand.setCustomNameVisible(false);
		Stand.setGravity(false);
		Stand.setVisible(true);
	}

	public void setEnde(String Name, Location loc) {
		config.set("Jumps." + Name + ".Ende", loc.toVector());
	}

	public void setMoney(String Name, double Money) {
		config.set("Jumps." + Name + ".Money", Money);
	}

	public void setYaw(String Name, int yaw) {
		config.set("Jumps." + Name + ".StartYaw", yaw);
	}

	public Plugin pl;
	public FileConfiguration config;
	public List<String> tmp;
}
