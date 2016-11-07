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
import org.bukkit.entity.Entity;
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
		Player p = null;
		try {
		p = (Player) cs;
		}catch (Exception e) {
			 System.out.println("CommandSender is not a Player");
		}
		if (p.hasPermission("JumpAndRun.Admin")) {
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " help:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Gibt hilfe");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " syntax:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Zeigt die Syntax von den Befehlen an");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " list:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Zeigt eine Liste der Jumps");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " create:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Erstellt ein JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " remove:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Löscht ein JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setStart:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Start von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setEnde:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt das Ende von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setMoney:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Belonung von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setYaw:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Gradzahl für den Teleport zum Start");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " refreshStart:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Refresh den ArmorStand bei Start, für Config Änderungen.");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " refreshEnde:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Gradzahl für den Teleport zum Start");
		} else if (args[0].equalsIgnoreCase("syntax")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " list <Jump> (falls gewünscht");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " create <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " remove <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setStart <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setEnde <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setMoney <Name> <Geld>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/"+ label + " setYaw <Name> <Gradzahl>");
		} else if (args[0].equalsIgnoreCase("create")) {
			createJump(args[1], p);
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
				} else if (args[0].equalsIgnoreCase("refreshStart")) {
					ArmorStand Stand = getNearbyEntities(p.getLocation(), 2);
					Stand.remove();
					setStart(JumpName, p.getLocation());
				}else if (args[0].equalsIgnoreCase("refreshEnde")) {
					ArmorStand Stand = getNearbyEntities(p.getLocation(), 2);
					Stand.remove();
					setEnde(JumpName, p.getLocation());
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
		}
		pl.saveConfig();
		return true;
	}
		

	public void createJump(String Name, Player p) {
		config.set("Jumps." + Name, "");
		config.set("Jumps." + Name + ".Best.Player", "§r§6<Never done>");
		config.set("Jumps." + Name + ".Best.Time", null);
		config.set("Jumps." + Name + ".Author", p.getName());
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
		loc.setY((double) y + 1.75);
		loc.setZ((double) z + 0.5);
		
		ArmorStand Stand1 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand1.setCustomName("§m" + ChatColor.GRAY + "-------------------------------");
		Stand1.setCustomNameVisible(true);
		Stand1.setGravity(false);
		Stand1.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand2 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand2.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Jump And Run");
		Stand2.setCustomNameVisible(true);
		Stand2.setGravity(false);
		Stand2.setVisible(false);
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand3 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand3.setCustomName(ChatColor.AQUA + "Jump Name: " + ChatColor.GOLD + ChatColor.BOLD + Name);
		Stand3.setCustomNameVisible(true);
		Stand3.setGravity(false);
		Stand3.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand4 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand4.setCustomName(ChatColor.AQUA + "Jump Bester: " + ChatColor.GOLD + config.get("Jumps." + Name + ".Best.Player"));
		Stand4.setCustomNameVisible(true);
		Stand4.setGravity(false);
		Stand4.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand5 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand5.setCustomName(ChatColor.AQUA + "Mit einer Zeit von: " + ChatColor.GOLD + config.get("Jumps." + Name + ".Best.Time"));
		Stand5.setCustomNameVisible(true);
		Stand5.setGravity(false);
		Stand5.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand6 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand6.setCustomName("§m" + ChatColor.GRAY + "-------------------------------");
		Stand6.setCustomNameVisible(true);
		Stand6.setGravity(false);
		Stand6.setVisible(false);

	}

	public void setEnde(String Name, Location loc) {
		int x = Math.round(loc.getBlockX());
		int y = Math.round(loc.getBlockY());
		int z = Math.round(loc.getBlockZ());
		config.set("Jumps." + Name + ".Ende", loc.toVector());
		loc.setX((double) x + 0.5);
		loc.setY((double) y + 1.75);
		loc.setZ((double) z + 0.5);
		
		ArmorStand Stand1 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand1.setCustomName("§m" + ChatColor.GRAY + "-------------------------------");
		Stand1.setCustomNameVisible(true);
		Stand1.setGravity(false);
		Stand1.setVisible(false);

		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand3 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand3.setCustomName(ChatColor.AQUA + "Jump Name: " + ChatColor.GOLD + ChatColor.BOLD + Name);
		Stand3.setCustomNameVisible(true);
		Stand3.setGravity(false);
		Stand3.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand4 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand4.setCustomName(ChatColor.AQUA + "Jump Bester: " + ChatColor.GOLD+ config.get("Jumps." + Name + ".Best.Player"));
		Stand4.setCustomNameVisible(true);
		Stand4.setGravity(false);
		Stand4.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand5 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand5.setCustomName(ChatColor.AQUA + "Mit einer Zeit von: " + ChatColor.GOLD + config.get("Jumps." + Name + ".Best.Time"));
		Stand5.setCustomNameVisible(true);
		Stand5.setGravity(false);
		Stand5.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand7 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand7.setCustomName(ChatColor.AQUA + "Author: " + ChatColor.GOLD + config.get("Jumps." + Name + ".Author"));
		Stand7.setCustomNameVisible(true);
		Stand7.setGravity(false);
		Stand7.setVisible(false);
		
		loc.setY(loc.getY() - 0.25);
		ArmorStand Stand6 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand6.setCustomName("§m" + ChatColor.GRAY + "-------------------------------");
		Stand6.setCustomNameVisible(true);
		Stand6.setGravity(false);
		Stand6.setVisible(false);
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
	
	public static ArmorStand getNearbyEntities(Location where, int range) {
		where.setY(where.getY() - 2);
		for (Entity entity : where.getWorld().getEntities()) {
			if (Action.isInBorder(where, entity.getLocation(), range)) {
				if (entity.getType() == EntityType.ARMOR_STAND) {
							return (ArmorStand) entity;					
				}
			}
		}
		return null;
	}
}
