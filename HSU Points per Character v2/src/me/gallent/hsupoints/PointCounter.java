package me.gallent.hsupoints;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PointCounter extends JavaPlugin implements Listener{

	public File playerDataFolder = null;
	public FileConfiguration dataConfig=null;
	public File playerFile = null;
	public Commands cmds=new Commands(this);
	public PointTab pTab=new PointTab(cmds);
	public PointsSystem pointSys=new PointsSystem(cmds, this);;
	public String playerName=null;
	public Plugin mainPlugin=null;
	public static PointCounter instance;
	public static PointCounter getInstance() {
		return instance;
	}
	public NamespacedKey pointCheck=new NamespacedKey(this, "IsPoint");

	
	
	//private FileConfiguration dataConfig = null;

	@Override
	public void onEnable() {
		this.getCommand("pointcounter").setExecutor(new Commands(this));
		this.getCommand("pointcounter").setTabCompleter(pTab);
		saveDefaultConfig();
		playerDataFolder = new File(getDataFolder() + File.separator+"playerdata");
		if(!playerDataFolder.exists()) {
			playerDataFolder.mkdirs();
		}
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(cmds, this);
		getServer().getPluginManager().registerEvents(pTab, this);
		instance = this;
		mainPlugin=this;
		pointSys.pointItems();
	}
	

	@Override
	public void onDisable() {

	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(player!=null) {
			File playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
			if(!playerFile.exists()) {
				try {
					playerFile.createNewFile();} catch (Exception e) {
						Bukkit.getConsoleSender().sendMessage("Player File Failed");
					}
			}
		}
	}


}
