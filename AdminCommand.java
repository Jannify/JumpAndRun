package de.Jannify.JandR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
		config = pl.getConfig();
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender cs, Command cmd, String label, String args[]) {
		Player p = (Player) cs;
		pl.saveConfig();
		pl.reloadConfig();
		
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump help:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Gibt hilfe");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump syntax:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Zeigt die Syntax von den Befehlen an");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump create:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Erstellt ein JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump remove:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Löscht ein JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setStart:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Start von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setEnde:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt das Ende von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setMoney:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt die Belonung von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump addMaterial:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Setzt den Rück TP Block von einem JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump removeMaterial:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Löscht den Rück TP Block von einem JandR");
		} else if (args[0].equalsIgnoreCase("syntax")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump create <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump remove <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setStart <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setEnde <Name>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump setMoney <Name> <Geld>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump addMaterial <Name> <Block-ID>");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/jump removeMaterial <Name> <Block-ID>");
		} else if (args[0] == null) {
			p.sendMessage("Kein Command angegeben.");
		} else if (args[0].equalsIgnoreCase("create")) {
			createJump(args[1]);
			p.sendMessage("Jump: " + args[1] + " wurde erstellt");
		} else if (args[0].equalsIgnoreCase("remove")) {
			removeJump(args[1]);
			p.sendMessage("Jump: " + args[1] + " wurde gelöscht");
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
						p.sendMessage("setMoney, setMaterial und removeMaterial benutzten Zahle als Parameter. " + param + " ist keine Zahl.");
					}
					if (param != -1) {
						if (args[0].equalsIgnoreCase("setMoney")) {
							setMoney(JumpName, param);
							p.sendMessage(param + " Money wurde gesetzt");
						} else {
							Material mat;
							try {
								mat = Material.getMaterial(param);
							} catch (Exception e) {
								p.sendMessage("Der angegebene Parameter(" + param + " passt zu keine Block/Item ID");
								mat = Material.AIR;
							}
							if (mat != Material.AIR) {
								if (args[0].equalsIgnoreCase("addMaterial")) {
									addMaterial(JumpName, param);
									p.sendMessage("Material: " + mat + " wurde hinzugefügt");
								} else if (args[0].equalsIgnoreCase("removeMaterial")) {
									removeMaterial(JumpName, param, p);
									p.sendMessage("Material: " + mat + " wurde entfernt");
								} else {
									p.sendMessage(args[0] + " ist kein bekannter Command");
								}
							}
						}
					}
				} else {
					p.sendMessage("Syntax: /jump <setStart/setEnd/setMoney/...> <JumpName> <Parameter>");
					p.sendMessage("Falls die Syntax richtig ist, wurde kein Parameter angegeben.");
				}
			} else {
				p.sendMessage("Syntax: /jump <setStart/setEnd/setMoney/...> <JumpName> [falls benözigt Paramenter]");
				p.sendMessage("Falls die Syntax richtig ist, existiert der Jump nicht, erstelle ihn mit /jump create <Name>");
			}
		}
		pl.saveConfig();
		pl.reloadConfig();
		return true;
	}

	public void createJump(String Name) {
		config.set("Jumps." + Name, "");
		pl.saveConfig();
		pl.reloadConfig();
		config.set("Jumps." + Name + ".Material", "");
		pl.saveConfig();
		pl.reloadConfig();
		config.set("Jumps." + Name + ".Money", 0);
		pl.saveConfig();
		pl.reloadConfig();
	}

	public void removeJump(String Name) {
		config.set("Jumps." + Name, null);
	}

	public void setStart(String Name, Location loc) {
		config.set("Jumps." + Name + ".Start", loc.toVector());
		ArmorStand Stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		Stand.setCustomName(Name);
		Stand.setCustomNameVisible(true);
		Stand.setGravity(false);
		Stand.setVisible(false);
	}

	public void setEnde(String Name, Location loc) {
		config.set("Jumps." + Name + ".Ende", loc.toVector());
	}

	public void setMoney(String Name, int Money) {
		config.set("Jumps." + Name + ".Money", Money);
	}

	public void addMaterial(String Name, int mat) {
		Materials = config.getIntegerList("Jumps." + Name + ".Material");
		Materials.add(mat);
		config.set("Jumps." + Name + ".Material", Materials);
	}

	public void removeMaterial(String Name, int mat, Player p) {
		Materials = (ArrayList<Integer>) config.getIntegerList("Jumps." + Name + ".Material");
		Materials.remove((Object) mat);
		config.set("Jumps." + Name + ".Material", Materials);
	}

	public List<Integer> Materials;
	public Plugin pl;
	public FileConfiguration config;
	public List<String> tmp;

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
