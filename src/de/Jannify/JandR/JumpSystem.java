package de.Jannify.JandR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

//import com.sun.istack.internal.logging.Logger;

import net.milkbowl.vault.economy.Economy;

public class JumpSystem extends JavaPlugin {
	String prefix = "[JumpAndRun]  ";

	@Override
	public void onDisable() {
		System.out.println(prefix + "Bye Bye");

	}

	@Override
	public void onEnable() {
		System.out.println(prefix + "Starte  Jump and Run. . .");

		System.out.println(prefix + "Pruefe Vault. . .");
		if (!setupEconomy()) {
			System.out.println("Vault nicht daaaaaa, oder put put. . .");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// ############################################
		// Config Laden
		// ############################################
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			System.out.println(prefix + "Config.yml not found! Creating . . .");
			getConfig().options().header("#############################################\n - JumpAndRun Updated By Jannify - \n \\xa7<FarbCode-Wert> \n#############################################");

			getConfig().addDefault("Start.Hoehe", 0.0);
			getConfig().addDefault("Start.Linie1", "§7§m-------------------------------");
			getConfig().addDefault("Start.Linie2", "§a§l Jump And Run");
			getConfig().addDefault("Start.Linie3", "§bJump Name: §6§l");
			getConfig().addDefault("Start.Linie4", "§bJump Bester: §6");
			getConfig().addDefault("Start.Linie5", "§bMit einer Zeit von §6<Zeit>§b sek");
			getConfig().addDefault("Start.Linie6", "§bSchwierigkeit: §6");
			getConfig().addDefault("Start.Linie7", "§7§m-------------------------------");

			getConfig().addDefault("Ende.Hoehe", 0.0);
			getConfig().addDefault("Ende.Linie1", "§7§m-------------------------------");
			getConfig().addDefault("Ende.Linie2", "§bJump Name: §6§l");
			getConfig().addDefault("Ende.Linie3", "§bJump Bester: §r§6");
			getConfig().addDefault("Ende.Linie4", "§bMit einer Zeit von §6<Zeit>§b sek");
			getConfig().addDefault("Ende.Linie5", "§bAuthor: §6");
			getConfig().addDefault("Ende.Linie6", "§7§m-------------------------------");
			getConfig().addDefault("Inv.Name", "§l§6Jump-List");
			getConfig().addDefault("Inv.Loc", "");
			getConfig().options().copyHeader(true);
			getConfig().options().copyDefaults(true);
			saveConfig();
			reloadConfig();
		} else {
			System.out.println(prefix + "Config.yml found! Loading . . .");
		}
		File fileJumps = new File(getDataFolder(), "Jumps.yml");
		if (!fileJumps.exists()) {
			System.out.println(prefix + "Jumps.yml not found! Creating . . .");
			getJumpsConfig();
			saveJumpsConfig();
		} else {
			System.out.println(prefix + "Jumps.yml found! Loading . . .");
			getJumpsConfig();
			saveJumpsConfig();
		}

		System.out.println(prefix + "Lade Events...");
		try {
			this.getServer().getPluginManager().registerEvents(new AdminCommand(this, JumpsConfig), this);
			this.getServer().getPluginManager().registerEvents(new Action(this, econ, JumpsConfig), this);
			this.getServer().getPluginManager().registerEvents(new JumpMenu(this, JumpsConfig), this);
		} catch (IOException e) {
			System.out.println(prefix + "Fehler beim Events laden");
			System.out.println(e);
		}
		System.out.println(prefix + "Lade Commands . . .");
		try {
			getCommand("jump").setExecutor(new AdminCommand(this, JumpsConfig));
			getCommand("jvote").setExecutor(new PlayerCommand(this, JumpsConfig));
			getCommand("jback").setExecutor(new JumpMenu(this, JumpsConfig));
			getCommand("jump").setTabCompleter(new JumpCompleter(this, JumpsConfig));
		} catch (IOException e) {
			System.out.println(prefix + "Fehler beim Commands laden");
			System.out.println(e);
		}

		System.out.println(prefix + "Lade Scheduler . . .");
		startLevelScheduler();

		System.out.println(prefix + "Starte  Jump and Run [READY]");

	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public void reloadJumpsConfig() {
		if (JumpsFile == null) {
			JumpsFile = new File(getDataFolder(), "Jumps.yml");
		}
		JumpsConfig = YamlConfiguration.loadConfiguration(JumpsFile);

		// Schaut nach den Standardwerten in der jar
		InputStream defConfigStream = getResource("Jumps.yml");
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			JumpsConfig.setDefaults(defConfig);
		}

		if (JumpsConfig.getString("Jumps") == null) {
			JumpsConfig.options().header("#############################################\n - JumpAndRun Updated By Jannify - \n#############################################");
			JumpsConfig.addDefault("Jumps", "");
			JumpsConfig.options().copyHeader(true);
			JumpsConfig.options().copyDefaults(true);
		}
	}

	public FileConfiguration getJumpsConfig() {
		if (JumpsConfig == null) {
			reloadJumpsConfig();
		}
		return JumpsConfig;
	}

	public static void saveJumpsConfig() {
		if (JumpsConfig == null || JumpsFile == null) {
			return;
		}
		try {
			JumpsConfig.save(JumpsFile);
		} catch (IOException ex) {
			//Logger.getLogger(JavaPlugin.class.getName(), null).log(Level.SEVERE, "Konfiguration konnte nicht nach " + JumpsFile + " geschrieben werden.", ex);
		}
	}

	public Economy econ;
	public static FileConfiguration JumpsConfig = null;
	public static File JumpsFile = null;

	public void startLevelScheduler() {
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (Action.PlayerList.containsKey(p)) {
						p.setLevel(p.getLevel() + 1);
						Location loc = p.getLocation();
						loc.setY(loc.getY() - 7);
						if (p.getLevel() % 5 == 0) {
							p.playSound(loc, Sound.LEVEL_UP, (float) 1, (float) 2);
						}
					}
				}

			}

		}, 0, 20);
	}
	
	
}
