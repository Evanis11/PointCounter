package me.gallent.hsupoints;

import java.io.File;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Commands implements CommandExecutor, Listener{


	public FileConfiguration pdataConfig=null;
	public FileConfiguration tardataConfig=null;
	public File playerFile = null;
	public File targetFile = null;
	public PointCounter plugin = null;
	public String playerDataFolder = null;
	public String targetDataFolder = null;
	public Player player=null;
	public Player target=null;
	public String charName=null;
	public String charSel=null;
	public String newChar=null;
	public Inventory delConfirm;
	public Inventory aversions;
	public PointsSystem pointSys = new PointsSystem(this, plugin);
	public String varA = null;
	public String varB = null;
	public CommandSender sendB=null;
	public String stringChar=null;
	public Boolean success=false;
	public ArrayList<String> characters =new ArrayList<String>();


	public Commands(PointCounter plugin) {
		this.plugin = plugin;
		this.playerDataFolder = plugin.getDataFolder()+File.separator+"playerdata";
	}




	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("PointCounter") || label.equalsIgnoreCase("PC")) {
			if(args.length==0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lHSU Point Counter"));
				sender.sendMessage(ChatColor.GREEN+"Version 1.1");
				sender.sendMessage(ChatColor.GREEN+"By Gallent");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&oCommands:"));
				sender.sendMessage(ChatColor.GOLD+"PointCounter Character (char)");
				sender.sendMessage(ChatColor.GOLD+"PointCounter Points (p)");
				return true;
			}

			//character commands
			if(args[0].equalsIgnoreCase("character")||args[0].equalsIgnoreCase("char")) {
				//permission check
				if(!(sender.hasPermission("pointCounter.basic"))) {
					sender.sendMessage("You cannot use this");
					return true;
				}
				
				//character create
				if(args[1].equalsIgnoreCase("create")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"Only players can create characters");	
						return true;
					}
					if(args.length==2) {
						sender.sendMessage(ChatColor.RED+"Use: /PointCounter Character Create <name>");
						return true;
					}
					this.player=(Player) sender;
					//this.charName =args[2].toString();
					this.charName="";
					for(int i = 2; i<args.length;i++) {
						if(!charName.equalsIgnoreCase("")) {
							charName=charName+" ";
						}
						String namePiece = args[i];
						this.charName = charName+namePiece;
					}
					
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					reloadPData();
					
					if(!charCheck(charName)) {
						getPData().set("characters."+charName, " ");
						getPData().set("ActiveCharacter",charName);

						pointSys.curStatSys();

						savePData();

						player.sendMessage(ChatColor.GREEN+charName+" created successfully.");
						return true;
					}
					player.sendMessage(ChatColor.RED+"This character already exists.");
					return true;

				}

				//character switch
				if(args[1].equalsIgnoreCase("Switch")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"The Console has no characters.");
						return true;
					}
					if(args.length==2) {
						sender.sendMessage(ChatColor.RED+"Use: /PointCounter Character Switch <Name>");
						return true;
					}
					this.player = (Player) sender;
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					reloadPData();
					this.charSel ="";
					for(int i = 2; i<args.length;i++) {
						if(!charSel.equalsIgnoreCase("")) {
							charSel=charSel+" ";
						}
						String namePiece = args[i];
						this.charSel = charSel+namePiece;
					}
					
					if(charCheck(charSel)) {
						if(getPData().getString("ActiveCharacter").equalsIgnoreCase(stringChar)) {
							player.sendMessage(ChatColor.RED+"You are already this character.");
							return true;
						}
						String oldChar=getPData().getString("ActiveCharacter");
						getPData().set("ActiveCharacter", stringChar);
						player.sendMessage(ChatColor.GREEN+"You switched from "+oldChar+" to "+stringChar);
						savePData();
						return true;
					}
					player.sendMessage(ChatColor.RED+"You do not have a character named "+charSel);
					return true;
				}

				//character delete
				if(args[1].equalsIgnoreCase("delete")||args[1].equalsIgnoreCase("Del")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"The Console has no characters to delete.");
						sender.sendMessage(ChatColor.RED+"To delete someone else's characters, edit their config file for now.");
						return true;
					}
					this.player = (Player) sender;
					if(args.length==2) {
						sender.sendMessage(ChatColor.RED+"Use: /PointCounter Character Delete <Name>");
						return true;
					}
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					reloadPData();
					this.charSel ="";
					for(int i = 2; i<args.length;i++) {
						if(!charSel.equalsIgnoreCase("")) {
							charSel=charSel+" ";
						}
						String namePiece = args[i];
						this.charSel = charSel+namePiece;
					}
					if(charCheck(charSel)) {
						if(getPData().getString("ActiveCharacter").equalsIgnoreCase(this.stringChar)) {
							player.sendMessage(ChatColor.RED+"You cannot delete your current character");
							return true;
						}
						createDelConfirm();
						getPData().set("DeleteCharacter", stringChar);
						savePData();
						player.openInventory(delConfirm);
						return true;
					}
					player.sendMessage(ChatColor.RED+"You do not have a character named "+charSel);
					return true;

				}
				
				//character rename
				if(args[1].equalsIgnoreCase("Rename")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"The Console has no characters to rename.");
						sender.sendMessage(ChatColor.RED+"To rename someone else's characters, edit their config file for now.");
						return true;
					}
					this.player = (Player) sender;
					if(args.length==2||args.length==3) {
						sender.sendMessage(ChatColor.RED+"Use: /PointCounter Character rename <Old Name> <New Name>");
						return true;
					}
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					reloadPData();
					int i = 0;
					Boolean charOne=false;
					this.charSel = "";
					for(i = 2; i<args.length;i++) {
						if(!charSel.equalsIgnoreCase("")) {
							charSel=charSel+" ";
						}
						String namePiece = args[i];
						this.charSel = charSel+namePiece;
						if(charCheck(charSel)) {
							charOne=true;
							break;
						}
					}
					this.newChar = "";
					if(!charOne==true) {
						player.sendMessage(ChatColor.RED+"You do not have a character of that name");
					}
					for(i++; i<args.length; i++) {
						if(!newChar.equalsIgnoreCase("")) {
							newChar=newChar+" ";
						}
						String namePiece = args[i];
						newChar = newChar+namePiece;
					}
					
					
					if(charCheck(newChar)) {
						player.sendMessage(ChatColor.RED+"You already have a character named "+newChar);
						return true;
					}
					
					if(charCheck(this.charSel)) {
						if(getPData().getString("ActiveCharacter").equalsIgnoreCase(this.stringChar)) {
							ConfigurationSection section = getPData().getConfigurationSection("characters."+stringChar);
							section.getKeys(true).forEach(key ->{
								if(isInt(section.getString(key))) {
									int intValue = section.getInt(key);
									getPData().set("characters."+newChar+"."+key, intValue);
									return;
								}
								if(section.isBoolean(key)) {
									getPData().set("characters."+newChar+"."+key, section.getBoolean(key));
									return;
								}

								String stringValue = section.getString(key);
								getPData().set("characters."+newChar+"."+key, stringValue);
								Bukkit.getConsoleSender().sendMessage("PingTest");
							});
							getPData().set("characters."+stringChar, null);
							getPData().set("ActiveCharacter", newChar);
							savePData();
							player.sendMessage(ChatColor.GREEN+"Renamed "+stringChar+" To "+newChar);
							return true;
						}
						ConfigurationSection section = getPData().getConfigurationSection("characters."+stringChar);
						section.getKeys(true).forEach(key ->{
							int test = section.getInt(key);
							getPData().set("characters."+newChar+"."+key, test);
						});
						getPData().set("characters."+stringChar, null);
						savePData();
						return true;
					}
					return true;
				}
				
				sender.sendMessage(ChatColor.RED+"Use: /PointCounter Character <Create|Switch|Delete|Rename>");
				return true;
			}

			//points commands
			if(args[0].equalsIgnoreCase("points")||args[0].equalsIgnoreCase("p")) {
				//permissions check
				if(!(sender.hasPermission("pointCounter.basic"))) {
					sender.sendMessage("You cannot use this");
					return true;
				}
				
				if(args.length==1) {
					sender.sendMessage(ChatColor.RED+"Use: /PointCounter give <type> <amount> <player (optional)>");
					return true;
				}
				//banking
				if(args[1].equalsIgnoreCase("bank")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"The console does not have any points to bank.");
						return true;
					}
					if(args.length==2) {
						sender.sendMessage(ChatColor.RED+"Use: /Pointcounter Points Bank <Amount>");
						return true;
					}
					this.varA= args[2].toString();
					this.player = (Player) sender;
					if(isInt(varA)&&Integer.valueOf(varA)<=0) {
						player.sendMessage(ChatColor.RED+"Must be greater than zero");
						return true;
					}
					
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					if(!(getPData().contains("ActiveCharacter"))) {
						player.sendMessage(ChatColor.RED+"You need a character before you can use this.");
						return true;
					}
					pointSys.bank();
					return true;
				}
				//spending
				if(args[1].equalsIgnoreCase("spend")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"The console does not have any points to spend.");
						return true;
					}
					if(args.length==2||args.length==3) {
						sender.sendMessage(ChatColor.RED+"Use: /Pointcounter Points spend <Amount> <Category>");
						return true;
					}
					this.varA= args[2].toString();
					this.varB= args[3].toString();
					this.player = (Player) sender;
					if(isInt(varA)&&Integer.valueOf(varA)<=0) {
						player.sendMessage(ChatColor.RED+"Must be greater than zero");
						return true;
					}
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					if(!(getPData().contains("ActiveCharacter"))) {
						player.sendMessage(ChatColor.RED+"You need a character before you can use this.");
						return true;
					}
					pointSys.spend();
					return true;
				}
				
				if(args[1].equalsIgnoreCase("view")) {
					//view other
					if(sender.hasPermission("pointCounter.viewOther")&&args.length==4) {
						this.varB= args[2].toString();
						this.target = Bukkit.getPlayer(args[3]);
						this.player = this.target;
						this.sendB = sender;
						reloadTarData();
						reloadPData();
						this.playerFile=new File(playerDataFolder, target.getUniqueId().toString()+".yml");
						Bukkit.getConsoleSender().sendMessage(this.playerFile.toString());
						if(this.target==null) {
							sender.sendMessage(ChatColor.RED+"Can't find player by the name of "+args[3]);
						}
						Bukkit.getConsoleSender().sendMessage(getPData().getString("ActiveCharacter"));
						if(!(getTarData().contains("ActiveCharacter"))) {
							sender.sendMessage(ChatColor.RED+"Target has no active character.");
							return true;
						}

						pointSys.view2();
						return true;
						
					}
						
					
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"The console does not have any points to view.");
						return true;
					}
					if(args.length==2) {
						sender.sendMessage(ChatColor.RED+"Use: /Pointcounter Points View <Category>");
						return true;
					}
					this.varB= args[2].toString();
					this.player = (Player) sender;
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					if(!(getPData().contains("ActiveCharacter"))) {
						player.sendMessage(ChatColor.RED+"You need a character before you can use this.");
						return true;
					}
					pointSys.view();
					return true;
				}
				
				if(args[1].equalsIgnoreCase("unlock")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"The console does not have any schools to unlock.");
						return true;
					}
					if(args.length==2) {
						sender.sendMessage(ChatColor.RED+"Use: /Pointcounter Points Unlock <Category>");
						return true;
					}
					this.varB= args[2].toString();
					this.player = (Player) sender;
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					if(!(getPData().contains("ActiveCharacter"))) {
						player.sendMessage(ChatColor.RED+"You need a character before you can use this.");
						return true;
					}
					pointSys.unlock();
					return true;
				}
				
				/*if(args[1].equalsIgnoreCase("withdraw"))
				{
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"You cannot withdraw points from the console.");
						return true;
					}
					this.player = (Player) sender;
					this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
					if(!(getPData().contains("ActiveCharacter"))) {
						player.sendMessage(ChatColor.RED+"You need a character before you can use this.");
						return true;
					}
					String activeChar=getPData().getString("ActiveCharacter");
					
					
					
					if(args.length==4) {
						if(!isInt(args[2])) {
							player.sendMessage(ChatColor.RED+args[2]+" is not a number.");
							return true;
						}
						Integer withdrawAmt=Integer.valueOf(args[2]);
						if(args[3].equalsIgnoreCase("magic")){
							if(getPData().getInt("characters."+activeChar+".")) {
								
							}
						}
						
					}
					player.sendMessage(ChatColor.RED+"Use: /Pointcounter Withdraw");
					return true;
				}*/
				
				
				sender.sendMessage(ChatColor.RED+"Use: /Pointcounter Points <Bank|Spend|View|Unlock>");
				return true;
			}
			
			
			if(args[0].equalsIgnoreCase("Give")) {
				
				if(!(sender.hasPermission("pointCounter.give"))) {
					sender.sendMessage("You cannot use this");
					return true;
				}
				
				
				if(args.length==3&&isInt(args[2])) {
					
					if(!(sender instanceof Player)) {
						sender.sendMessage("you cannot give the console points.");
						return true;
					}
					this.player=(Player) sender;
					
					if(player.getInventory().firstEmpty()==-1) {
						player.sendMessage("Inventory full");
						return true;
					}
					
					pointSys.pointItems();
					
					if(!(isInt(args[2]))) {
						sender.sendMessage(ChatColor.RED+"That is not an number value.");
						return true;
						
					}
					
					Integer giveAmt=Integer.valueOf(args[2]);
					if(args[1].equalsIgnoreCase("Magic")) {
						pointSys.mPoint.setAmount(giveAmt);
						player.getInventory().addItem(pointSys.mPoint);
						if(giveAmt==1) {
							sender.sendMessage(ChatColor.GOLD+"Giving one magic point.");
							return true;
						}
						player.sendMessage(ChatColor.GOLD+"Giving "+giveAmt+" magic points.");
						return true;
					}
					if (args[1].equalsIgnoreCase("physical")||args[1].equalsIgnoreCase("stat")) {
						pointSys.sPoint.setAmount(giveAmt);
						player.getInventory().addItem(pointSys.sPoint);
						if(giveAmt==1) {
							sender.sendMessage(ChatColor.GOLD+"Giving one physical point.");
							return true;
						}
						player.sendMessage(ChatColor.GOLD+"Giving "+giveAmt+" physical points.");
						return true;
					}
					if(args[1].equalsIgnoreCase("general")) {
						pointSys.gPoint.setAmount(giveAmt);
						player.getInventory().addItem(pointSys.sPoint);
						if(giveAmt==1) {
							sender.sendMessage(ChatColor.GOLD+"Giving one general point.");
							return true;
						}
						player.sendMessage(ChatColor.GOLD+"Giving "+giveAmt+" general points.");
						return true;
					}
					 
					player.sendMessage(ChatColor.RED+"No points specified.");
					return true;
				}
				
				if(args.length==4&&isInt(args[2])) {
					pointSys.pointItems();
					if(!(isInt(args[2]))) {
						sender.sendMessage(ChatColor.RED+"That is not an number value.");
						return true;
						
					}
					
					Integer giveAmt=Integer.valueOf(args[2]);
					Player target=Bukkit.getPlayer(args[3]);
					if(target==null) {
						sender.sendMessage(ChatColor.RED+"No player selected");
						return true;
					}
					
					if(target.getInventory().firstEmpty()==-1) {
						sender.sendMessage("Inventory full");
						return true;
					}
					
					if(args[1].equalsIgnoreCase("Magic")) {
						pointSys.mPoint.setAmount(giveAmt);
						target.getInventory().addItem(pointSys.mPoint);
						if(giveAmt==1) {
							sender.sendMessage(ChatColor.GOLD+"Giving one general point to "+target.getName());
							return true;
						}
						sender.sendMessage(ChatColor.GOLD+"Giving "+giveAmt+" magic points to "+target.getName());
						return true;
					}
					if (args[1].equalsIgnoreCase("physical")||args[1].equalsIgnoreCase("stat")) {
						pointSys.sPoint.setAmount(giveAmt);
						target.getInventory().addItem(pointSys.sPoint);
						if(giveAmt==1) {
							sender.sendMessage(ChatColor.GOLD+"Giving one general point to "+target.getName());
							return true;
						}
						sender.sendMessage(ChatColor.GOLD+"Giving "+giveAmt+" physical points to "+target.getName());
						return true;
					}
					if(args[1].equalsIgnoreCase("general")) {
						pointSys.gPoint.setAmount(giveAmt);
						target.getInventory().addItem(pointSys.gPoint);
						if(giveAmt==1) {
							sender.sendMessage(ChatColor.GOLD+"Giving one general point to "+target.getName());
							return true;
						}
						sender.sendMessage(ChatColor.GOLD+"Giving "+giveAmt+" general points to "+target.getName());
						return true;
					}
					 
					sender.sendMessage(ChatColor.RED+"No points specified.");
					return true;
				}

				sender.sendMessage(ChatColor.RED+"Use: /PointCounter give <type> <amount> <player (optional)>");
				
			}
			
			if(args[0].equalsIgnoreCase("reload")) {
				if(sender instanceof Player) {
					this.player=(Player) sender;
					reloadPData();
				}
				plugin.reloadConfig();
				sender.sendMessage(ChatColor.GREEN+"PointCounter configs reloaded.");
				return true;
			}
			
			//tutorial
			if(args[0].equalsIgnoreCase("tutorial")||args[0].equalsIgnoreCase("tut")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED+"You cannot create or modify a character from the console.");
				}
				this.player=(Player) sender;
				if(args.length==2&&args[1].equalsIgnoreCase("2")) {
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&nPoint Counter Tutorial"));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Page 2: Character commands"));
					this.player.sendMessage("");
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTo switch between multiple characters, use \"/Pointcounter Character Switch\" followed by the name of the character you would like to switch to."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTo rename a character, use \"/Pointcounter Character Rename <Old Character Name> <New Character Name>\""));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTo delete a character, use \"/Pointcounter Character Delete <Character Name>\". You cannot delete your active character."));
					player.sendMessage("");
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Page 2 of 4."));
					return true;
				}
				if(args.length==2&&args[1].equalsIgnoreCase("3")) {
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&nPoint Counter Tutorial"));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Page 3: Points"));
					this.player.sendMessage("");
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aBy voting or participating in events, you can earn point items."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThese can be banked to a specific character by holding them in your hand and typing \"/Pointcounter Points Bank <amount>\""));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThe point types are as follows:"));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dMagic Points &aare used to increase your magic schools. You must advance a tier 1 school (Elemental, Cosmic, Nature) to 50 before you can specialize."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bStat Points &aare used to increase your physical stats such as Strength, Agility, Health, and Craftsmanship. These have no prerequisits."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6General Points &aare able to be used as either of the above points."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTo use the points, use \"/PointCounter points spend <amount> <category>\"."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTier II magic has to be unlocked first. Once a tier 1 school is at 50, you can type \"/PointCounter Points Unlock <Magic Type>\". You can only unlock 4 magics."));
					player.sendMessage("");
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Page 3 of 4."));
					return true;
				}
				if(args.length==2&&args[1].equalsIgnoreCase("4")) {
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&nPoint Counter Tutorial"));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Page 4: Tips"));
					this.player.sendMessage("");
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a1. If your character was used on TDC or has an unusual amount of points, an Admin can edit your file to change the values."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a2. Many of the commands can be shortened. For example, /PointCounter can be shortened to /pc, /PointCounter Character can be shortened to /pc char, and /PointCounter Points can be shortened to /pc p."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a3. Spending points and deleting characters are permanent."));
					this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a4. Just as on the character application, you start with 40 physical points and 20 magic points already banked. Please allocate them the same as you have on your sheet."));
					player.sendMessage("");
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Page 4 of 4."));
					return true;
				}
				this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&nPoint Counter Tutorial"));
				this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Page 1: Character creation"));
				this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Page 1: Please read all pages before starting"));
				this.player.sendMessage("");
				this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTo use the plugin, you must first have a character."));
				this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aStart by using \"/PointCounter Character Create\" followed by the name of your character."));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWhen prompted, click the item that matches your race (or racial aversion for custom races.)"));
				player.sendMessage("");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Page 1 of 4"));
				return true;
			}
			sender.sendMessage(ChatColor.RED+"Unknown PointCounter Command.");
		}
		return false;
	}


	//Config info
	public void reloadPData() {
		if(this.playerFile==null) {
			try {
				this.playerFile=new File(playerDataFolder, this.player.getUniqueId().toString()+".yml");
			} catch (NullPointerException npe) {
				Bukkit.getConsoleSender().sendMessage("Reloaded pdata without player");
			}
		}
		this.pdataConfig=YamlConfiguration.loadConfiguration(this.playerFile);
		InputStream defaultStream = this.plugin.getResource("playerdata."+playerFile);
		if(defaultStream!=null) {
			YamlConfiguration testConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.pdataConfig.setDefaults(testConfig);
		}
	}
	public FileConfiguration getPData() {
		if (this.pdataConfig == null)
			reloadPData();
		return this.pdataConfig;
	}
	
	public void savePData() {
		if(this.pdataConfig==null||this.playerFile==null) {
			return;
		}
		try {
			this.getPData().save(this.playerFile);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Saving Failed");
		}
	}
	
	public void reloadTarData() {
		if(this.playerFile==null) {
			try {
				this.playerFile=new File(playerDataFolder, this.target.getUniqueId().toString()+".yml");
			} catch (NullPointerException npe) {
				Bukkit.getConsoleSender().sendMessage("Reloaded pdata without target");
			}
		}
		this.tardataConfig=YamlConfiguration.loadConfiguration(this.playerFile);
		InputStream defaultStream = this.plugin.getResource("playerdata."+playerFile);
		if(defaultStream!=null) {
			YamlConfiguration testConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.tardataConfig.setDefaults(testConfig);
		}
	}
	public FileConfiguration getTarData() {
		if (this.tardataConfig == null)
			reloadTarData();;
		return this.tardataConfig;
	}
	
	public void saveTarData() {
		if(this.tardataConfig==null||this.playerFile==null) {
			return;
		}
		try {
			this.getTarData().save(this.playerFile);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Saving Failed");
		}
	}



	//check for number
	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public boolean charCheck(String checkName) {
		this.stringChar=null;
		this.characters.clear();
		this.success=false;
		if(!(getPData().contains("characters"))) {
			return false;
		}
		getPData().getConfigurationSection("characters").getKeys(false).forEach(key -> {
			if(key.equalsIgnoreCase(checkName)) {
				Bukkit.getConsoleSender().sendMessage("ping");
				this.stringChar=key;
				this.success=true;
			}
		});
		
		if(this.success==true) {
			return true;
		}
		
		return false;
	}


	//login
	@EventHandler
	public void login(PlayerJoinEvent event2) {
		this.player= event2.getPlayer();
		reloadPData();
		getPData().set("DisplayName", player.getName().toString());
		if(!getPData().contains("characters")&&player.hasPermission("pointCounter.basic")) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&nPointCounter"));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou do not currently have any characters."));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aUse \"/PointCounter Tutorial\" to get started"));
		}
		if(getPData().getDouble("Point System Version")==1.0&&getPData().contains("characters")) {
			
			//Note to Self: Figure out conversion code
			ConfigurationSection section = getPData().getConfigurationSection("characters");
			section.getKeys(true).forEach(key ->{
				String test = section.getString(key);
				getPData().set("oldCharacters."+key, test);
			});
			getPData().set("characters", null);
			getPData().set("ActiveCharacter", null);
			savePData();
			player.sendMessage(ChatColor.RED+"ALERT: The point counter plugin has updated to version 1.1 from version 1.0");
			player.sendMessage(ChatColor.RED+"Your characters (if you made any with this plugin) are no longer compatible");
			player.sendMessage(ChatColor.RED+"Please let Gallent_Bristle know if you need your characters updated.");
		}
		getPData().set("Point System Version", 1.1);
		if(getPData().contains("DeleteCharacter")) {
			getPData().set("DeleteCharacter", null);
		}
		savePData();
	}

	//Delete Confirmation
	public void createDelConfirm() {
		delConfirm= Bukkit.createInventory(null, 9,ChatColor.BOLD+""+ChatColor.GOLD+"Are you sure?");
		ItemStack choiceItem = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta meta = choiceItem.getItemMeta();

		//Confirm
		meta.setDisplayName(ChatColor.RED+""+ChatColor.BOLD+"Confirm Delete");
		List<String> delLore = new ArrayList<String>();
		delLore.add(ChatColor.GRAY+"You cannot undo this.");
		meta.setLore(delLore);
		choiceItem.setItemMeta(meta);
		delConfirm.setItem(0, choiceItem);

		//Exit
		choiceItem.setType(Material.EMERALD_BLOCK);
		meta.setDisplayName(ChatColor.GREEN + ""+ ChatColor.BOLD+"EXIT");
		delLore.clear();
		delLore.add(ChatColor.GRAY+"Return to safety.");
		meta.setLore(delLore);
		choiceItem.setItemMeta(meta);
		delConfirm.setItem(8, choiceItem);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getView().getTitle().contains("Are you sure?")) {
			if(event.getCurrentItem()==null) return;
			if(event.getCurrentItem().getItemMeta()==null) return;
			if(event.getCurrentItem().getItemMeta().getDisplayName()==null) return;
			event.setCancelled(true);
			//if(!event.getRawSlot()<event.getView().getTopInventory().getSize()) return;

			this.player = (Player) event.getWhoClicked();
			this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
			reloadPData();
			if(event.getRawSlot()==0) {
				Bukkit.getConsoleSender().sendMessage(player.getName());
				if(getPData().contains("DeleteCharacter")) {
					String delChar= getPData().getString("DeleteCharacter");
					getPData().set("characters."+delChar, null);
					player.sendMessage(ChatColor.DARK_RED+delChar+" has been deleted.");
					getPData().set("DeleteCharacter", null);
					player.closeInventory();
					savePData();
					return;
				}
				player.sendMessage(ChatColor.RED+"Error: No character selected for deletion.");
				player.closeInventory();
				return;
			}
			if(event.getRawSlot()==8) {
				player.closeInventory();
				getPData().set("DeleteCharacter", null);
				return;
			}
		}
		if(event.getView().getTitle().contains(ChatColor.BOLD+""+ChatColor.DARK_GREEN+"Choose your Race/Aversion.")) {
			if(event.getCurrentItem()==null) return;
			if(event.getCurrentItem().getItemMeta()==null) return;
			if(event.getCurrentItem().getItemMeta().getDisplayName()==null) return;
			event.setCancelled(true);
			this.player=(Player) event.getWhoClicked();
			this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
			reloadPData();
			String activeChar= getPData().getString("ActiveCharacter");
			if(!(charName==null)) {
				activeChar = charName;
			}
			reloadPData();
			if(event.getRawSlot()==0) {
				getPData().set("characters."+activeChar+".points.magic.nature.life.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.GOLD+"Human (Vivimancy Aversion)");
				return;
			}
			
			if(event.getRawSlot()==1) {
				getPData().set("characters."+activeChar+".points.magic.elemental.fire.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.GREEN+"Kami (Pyromancy Aversion)");
				return;
			}
			
			if(event.getRawSlot()==2) {
				getPData().set("characters."+activeChar+".points.magic.elemental.wind.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.AQUA+"Merfolk (Aeromancy Aversion)");
				return;
			}

			if(event.getRawSlot()==3) {
				getPData().set("characters."+activeChar+".points.magic.nature.ecomancy.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.DARK_RED+"Demon (Ecomancy Aversion)");
				return;
			}
			
			if(event.getRawSlot()==4) {
				getPData().set("characters."+activeChar+".points.magic.nature.death.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.DARK_PURPLE+"Moon Elf (Necromancy Aversion)");
				return;
			}
			if(event.getRawSlot()==5) {
				getPData().set("characters."+activeChar+".points.magic.elemental.earth.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.LIGHT_PURPLE+"Avian (Geomancy Aversion)");
				return;
			}
			if(event.getRawSlot()==6) {
				getPData().set("characters."+activeChar+".points.magic.elemental.water.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.DARK_GRAY+"Icaar (Hydromancy Aversion)");
				return;
			}
			if(event.getRawSlot()==7) {
				getPData().set("characters."+activeChar+".points.magic.elemental.water.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.RED+"Specter (Lunimancy Aversion)");
				return;
			}
			if(event.getRawSlot()==8) {
				getPData().set("characters."+activeChar+".points.magic.elemental.water.aversion", true);
				getPData().set("choosing", null);
				savePData();
				player.closeInventory();
				player.sendMessage(ChatColor.GREEN+"You have chosen: "+ChatColor.YELLOW+"Ethereal (Umbramancy Aversion)");
				return;
			}
		}
		
		return;

	}
	
	
	//Aversion List
	public void createAversions() {
		aversions= Bukkit.createInventory(null, 9,ChatColor.BOLD+""+ChatColor.DARK_GREEN+"Choose your Race/Aversion.");
		ItemStack choiceItem = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta meta = choiceItem.getItemMeta();

		//Human
		choiceItem.setType(Material.PLAYER_HEAD);
		meta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Human");
		List<String> raceLore = new ArrayList<String>();
		raceLore.add(ChatColor.GRAY+"Aversion: Vivimancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(0, choiceItem);

		//Kami
		choiceItem.setType(Material.BOW);
		meta.setDisplayName(ChatColor.GREEN + ""+ ChatColor.BOLD+"Kami");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Pyromancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(1, choiceItem);
		
		choiceItem.setType(Material.PRISMARINE_SHARD);
		meta.setDisplayName(ChatColor.AQUA + ""+ ChatColor.BOLD+"Merfolk");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Aeromancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(2, choiceItem);
		
		choiceItem.setType(Material.FIRE_CHARGE);
		meta.setDisplayName(ChatColor.DARK_RED	 + ""+ ChatColor.BOLD+"Demon");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Ecomancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(3, choiceItem);
		
		choiceItem.setType(Material.LAPIS_LAZULI);
		meta.setDisplayName(ChatColor.DARK_PURPLE + ""+ ChatColor.BOLD+"Moon Elf");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Necromancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(4, choiceItem);

		choiceItem.setType(Material.ELYTRA);
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + ""+ ChatColor.BOLD+"Avian");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Geomancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(5, choiceItem);
		
		choiceItem.setType(Material.CHARCOAL);
		meta.setDisplayName(ChatColor.DARK_GRAY + ""+ ChatColor.BOLD+"Icaar");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Hydromancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(6, choiceItem);
		
		choiceItem.setType(Material.BLAZE_POWDER);
		meta.setDisplayName(ChatColor.RED + ""+ ChatColor.BOLD+"Specter");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Lunimancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(7, choiceItem);
		
		choiceItem.setType(Material.END_CRYSTAL);
		meta.setDisplayName(ChatColor.YELLOW + ""+ ChatColor.BOLD+"Ethereal");
		raceLore.clear();
		raceLore.add(ChatColor.GRAY+"Aversion: Umbramancy");
		meta.setLore(raceLore);
		choiceItem.setItemMeta(meta);
		aversions.setItem(8, choiceItem);
	}
	
	@EventHandler
	public void raceClose(InventoryCloseEvent event) {
		if(event.getPlayer()==null) {
			Bukkit.getConsoleSender().sendMessage("Error with RaceClose");
			return;
		}
		this.player=(Player) event.getPlayer();
		this.playerFile=new File(playerDataFolder, player.getUniqueId().toString()+".yml");
		if(getPData().getBoolean("choosing")==true) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					createAversions();
					player.openInventory(aversions);
					return;
				}
			}, 10l);
			
		}
	}
}
