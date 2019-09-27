package de.Jannify.JandR;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerCommand implements CommandExecutor {
	public PlayerCommand(Plugin plugin, FileConfiguration JumpCon) throws IOException {
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
		}
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			p.sendMessage(ChatColor.GOLD + "===========[ " + ChatColor.RED + "JumpAndRun+" + ChatColor.GOLD + " ]===========");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " help:  " + ChatColor.RESET + "" + ChatColor.WHITE + "Gibt hilfe");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + ":" + ChatColor.RESET + "" + ChatColor.WHITE + "Gibt eine Bewertung für das am nächsten liegende JandR");
			p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "/" + label + " <Zahl von 1 -10>");
		} else {
			int param = -1;
			try {
				param = Integer.valueOf(args[0]);
			} catch (Exception e) {
				p.sendMessage(args[0] + " ist keine Zahl");
			}
			if (param >= 0 && param <= 10) {
				String Name = getNearbyEntities(p.getLocation(), 10);
				if (!JumpConfig.getStringList("Jumps." + Name + ".Difficult.PlayerList").contains(p.getName())) {
					if (JumpConfig.getVector("Jumps." + Name + ".Start") != null && JumpConfig.getVector("Jumps." + Name + ".Ende") != null) {
						List<String> PList = JumpConfig.getStringList("Jumps." + Name + ".Difficult.PlayerList");
						PList.add(p.getName());
						JumpConfig.set("Jumps." + Name + ".Difficult.Voter", JumpConfig.getInt("Jumps." + Name + ".Difficult.Voter") + 1);
						JumpConfig.set("Jumps." + Name + ".Difficult.Ranking", JumpConfig.getInt("Jumps." + Name + ".Difficult.Ranking") + param);
						JumpConfig.set("Jumps." + Name + ".Difficult.PlayerList", PList);
						AdminCommand.refreshOne(Name, p);
					} else {
						p.sendMessage("Für den Jump §6" + Name + "§r gibt es keinen Anfang oder kein Ende.");
					}
					JumpSystem.saveJumpsConfig();
				} else {
					p.sendMessage("Du hast für §6" + Name + "§r schon gevotet.");
				}
			} else {
				p.sendMessage(args[0] + " ist keine Zahl von 0-10.");
			}
		}

		JumpSystem.saveJumpsConfig();
		return true;
	}

	public Plugin pl;
	public FileConfiguration config;
	public List<String> tmp;
	public static FileConfiguration JumpConfig;

	public static String getNearbyEntities(Location where, int range) {
		HashMap<Integer, String> JumpList = new HashMap<Integer, String>();
		where.setY(where.getY() - 2);
		for (Entity entity : where.getWorld().getEntities()) {
			for (int i = 1; i <= range; i++) {
				if (Action.isInBorder(where, entity.getLocation(), i)) {
					if (entity.getType() == EntityType.ARMOR_STAND) {
						for (String jumps : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
							if (entity.getCustomName().contains(jumps)) {
								JumpList.put(i, jumps);
							}
						}
					}
				}
			}
		}
		for (int i = 1; i <= range; i++) {
			if (JumpList.containsKey(i)) {
				return JumpList.get(i);
			}
		}
		return null;
	}
}
