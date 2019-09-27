package de.Jannify.JandR;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class JumpCompleter implements TabCompleter {
	public JumpCompleter(JumpSystem plugin, FileConfiguration jumpsConfig) {
		pl = plugin;
		config = plugin.getConfig();
		JumpConfig = jumpsConfig;
	}

	public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
		List<String> TabListe = new ArrayList<String>();
		if (args.length == 1) {
			for (String string : getCmd(args[0])) {
				TabListe.add(string);
			}
		} else if (args[0].equals("remove") || args[0].equals("setStart") || args[0].equals("setEnde") || args[0].equals("setLeave") || args[0].equals("setMoney") || args[0].equals("setYaw") || args[0].equals("setAuthor") || args[0].equals("setItem") || args[0].equals("setBestTime")
				|| args[0].equals("setBestPlayer")) {
			for (String string : getJumps(args[1])) {
				TabListe.add(string);
			}
		}
		return TabListe;
	}

	public List<String> getJumps(String key) {
		List<String> list = new ArrayList<String>();
		for (String jumps : JumpConfig.getConfigurationSection("Jumps").getKeys(false)) {
			if (jumps.substring(0, key.length()).equalsIgnoreCase(key)) {
				list.add(jumps);
			}
		}

		return list;
	}

	public List<String> getCmd(String key) {
		List<String> list = new ArrayList<String>();
		List<String> cmds = new ArrayList<String>();
		cmds.add("help");
		cmds.add("syntax");
		cmds.add("list");
		cmds.add("create");
		cmds.add("remove");
		cmds.add("setStart");
		cmds.add("setEnde");
		cmds.add("setLeave");
		cmds.add("setMoney");
		cmds.add("setYaw");
		cmds.add("setAuthor");
		cmds.add("setItem");
		cmds.add("setBestPlayer");
		cmds.add("setBestTime");
		cmds.add("refreshAll");
		cmds.add("spawnInv");
		cmds.add("killInv");
		if (key.length() == 0) {
			return cmds;
		}
		for (String cmd : cmds) {
			if (cmd.substring(0, key.length()).equalsIgnoreCase(key)) {
				list.add(cmd);
			}
		}

		return list;
	}

	public Plugin pl;
	public FileConfiguration config;
	public List<String> JumpList;
	public static FileConfiguration JumpConfig;

}