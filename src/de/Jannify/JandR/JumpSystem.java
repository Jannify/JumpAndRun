package de.Jannify.JandR;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

	
public class JumpSystem extends JavaPlugin {
	String prefix = "[JumpAndRun]  ";
    public JumpSystem()
    {
    }
	
	@Override
	public void onDisable() {
		System.out.println(prefix+"Bye Bye");
	}

	@Override
	public void onEnable() {
		System.out.println(prefix+"Starte  Jump and Run. . .");
		
//############################################
//		Config Laden 
//############################################
		if(!getDataFolder().exists())
            getDataFolder().mkdirs();
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists())
        {
            System.out.println(prefix+"Config not found! Creating..");
            getConfig().options().header("#############################################\n# - JumpAndRun Updated By Jannify - #\n#############################################");
            getConfig().addDefault("Test", "Test");
            getConfig().addDefault("Jumps", "");
            getConfig().options().copyHeader(true);
            getConfig().options().copyDefaults(true);
            saveConfig();
            reloadConfig();
        } else
        {
            System.out.println(prefix+"Config.yml found! Loading..");
        }
        
        
        System.out.println(prefix+"Lade Events...");
        try {
			this.getServer().getPluginManager().registerEvents(new AdminCommand(this), this);
			this.getServer().getPluginManager().registerEvents(new Action(this), this);
		} catch (IOException e) {
			System.out.println(prefix+"Fehler beim Events laden");
			System.out.println(e);
		}
        System.out.println(prefix+"Lade Commands...");
        try {
        	getCommand("jump").setExecutor(new AdminCommand(this));
		} catch (IOException e) {
			System.out.println(prefix+"Fehler beim Commands laden");
			System.out.println(e);
		}
		System.out.println(prefix+"Starte  Jump and Run [READY]");
		

	}
}
