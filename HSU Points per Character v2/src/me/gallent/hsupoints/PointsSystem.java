package me.gallent.hsupoints;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class PointsSystem {

	public Commands cmds= null;
	public PointCounter plugin=null;
	private String activeChar = null;
	private Double oldVal = null;
	private Integer gPointsUnspent=null;
	private Integer mPointsUnspent=null;
	private Integer sPointsUnspent=null;
	private Double spendAmt=null;
	private Double oldSpendAmt=null;
	private Double parentPoints=null;
	private String pointPath=null;
	private String parentPath=null;
	private String unlockPath=null;
	private String mPPath=null;
	private String gPPath=null;
	private String sPPath=null;
	private Double oldPoints = null;
	private Player player=null;
	private Player target=null;

	public ItemStack mPoint=new ItemStack(Material.DIRT);;
	public ItemStack sPoint=new ItemStack(Material.DIRT);;
	public ItemStack gPoint=new ItemStack(Material.DIRT);;
	public ItemMeta mPMeta = mPoint.getItemMeta();
	public ItemMeta sPMeta = sPoint.getItemMeta();
	public ItemMeta gPMeta = gPoint.getItemMeta();

	public NamespacedKey pointCheck=null;

	public PointsSystem(Commands cmds, PointCounter plugin) {
		this.cmds = cmds;
		this.plugin= plugin;
	}

	public NBTTagCompound mpCompound=null;
	public net.minecraft.server.v1_15_R1.ItemStack nmsMPoint=null;
	public NBTTagCompound spCompound=null;
	public net.minecraft.server.v1_15_R1.ItemStack nmsSPoint =null;
	public NBTTagCompound gpCompound=null;
	public net.minecraft.server.v1_15_R1.ItemStack nmsGPoint =null;

	public String nbtTest=null;

	public void curStatSys(){
		ConfigurationSection section1 = PointCounter.getInstance().getConfig().getConfigurationSection("systemv2");
		section1.getKeys(true).forEach(key ->{
			if(cmds.isInt(section1.getString(key))) {
				int intValue = section1.getInt(key);
				cmds.getPData().set("characters."+cmds.charName+"."+key, intValue);
				return;
			}
			if(section1.isBoolean(key)) {
				cmds.getPData().set("characters."+cmds.charName+"."+key, section1.getBoolean(key));
				return;
			}

			String stringValue = section1.getString(key);

			cmds.getPData().set("characters."+cmds.charName+"."+key, stringValue);

		});
		cmds.getPData().set("choosing", true);
		cmds.createAversions();
		cmds.player.openInventory(cmds.aversions);







		/*cmds.getPData().set("characters."+cmds.charName+".points.magic.elemental.base", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.elemental.fire", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.elemental.water", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.elemental.wind", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.elemental.earth", 0);

		cmds.getPData().set("characters."+cmds.charName+".points.magic.cosmic.base", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.cosmic.astral", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.cosmic.shadow", 0);

		cmds.getPData().set("characters."+cmds.charName+".points.magic.nature.base", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.nature.ecomancy", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.nature.life", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.magic.nature.death", 0);

		cmds.getPData().set("characters."+cmds.charName+".points.physical.health", 100);
		cmds.getPData().set("characters."+cmds.charName+".points.physical.strength", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.physical.agility", 0);
		cmds.getPData().set("characters."+cmds.charName+".points.physical.craftsmanship", 0);

		cmds.getPData().set("characters."+cmds.charName+".points.unspent.physical", 40);
		cmds.getPData().set("characters."+cmds.charName+".points.unspent.magic", 20);
		cmds.getPData().set("characters."+cmds.charName+".points.unspent.general", 0);*/
	}

	//banking
	public void bank() {
		this.player=cmds.player;
		if(!(cmds.isInt(cmds.varA))) {
			player.sendMessage(ChatColor.RED+cmds.varA+" is not a number.");
			return;
		}
		Integer bankAmt = Integer.valueOf(cmds.varA);
		this.activeChar = cmds.getPData().getString("ActiveCharacter");

		pointItems();
		mPoint.setAmount(bankAmt);
		sPoint.setAmount(bankAmt);
		gPoint.setAmount(bankAmt);



		//bank magic
		if(player.getInventory().getItemInMainHand().isSimilar(mPoint)&&player.getInventory().getItemInMainHand().getAmount()>=bankAmt) {
			if(!checkM()) {
				player.sendMessage("Check failed. Not a point.");
				return;
			}

			player.getInventory().removeItem(mPoint);
			this.oldVal=cmds.getPData().getDouble("characters."+activeChar+".points.unspent.magic");
			Double newVal = oldVal + bankAmt;
			cmds.getPData().set("characters."+activeChar+".points.unspent.magic", newVal);
			if(bankAmt==1)player.sendMessage(ChatColor.GOLD+"You have banked "+bankAmt+" magic point.");
			player.sendMessage(ChatColor.GOLD+"You have banked "+bankAmt+" magic points.");
			cmds.savePData();
			return;
		}

		if(player.getInventory().getItemInMainHand().isSimilar(sPoint)&&player.getInventory().getItemInMainHand().getAmount()>=bankAmt) {
			player.getInventory().removeItem(sPoint);
			this.oldVal=cmds.getPData().getDouble("characters."+activeChar+".points.unspent.physical");
			Double newVal = oldVal + bankAmt;
			cmds.getPData().set("characters."+activeChar+".points.unspent.physical", newVal);
			if(bankAmt==1) player.sendMessage(ChatColor.GOLD+"You have banked "+bankAmt+" stat point.");
			player.sendMessage(ChatColor.GOLD+"You have banked "+bankAmt+" stat points.");
			cmds.savePData();
			return;
		}

		if(player.getInventory().getItemInMainHand().isSimilar(gPoint)) {
			if(player.getInventory().getItemInMainHand().getAmount()<bankAmt) {
				player.sendMessage(ChatColor.RED+"Not enough points.");
				return;
			}
			Integer handitem=player.getInventory().getItemInMainHand().getAmount();
			player.getInventory().getItemInMainHand().setAmount(handitem-bankAmt);
			this.oldVal=cmds.getPData().getDouble("characters."+activeChar+".points.unspent.general");
			Double newVal = oldVal + bankAmt;
			cmds.getPData().set("characters."+activeChar+".points.unspent.general", newVal);
			if(bankAmt==1) player.sendMessage(ChatColor.GOLD+"You have banked "+bankAmt+" general point.");
			player.sendMessage(ChatColor.GOLD+"You have banked "+bankAmt+" general points.");
			cmds.savePData();
			return;
		}

		player.sendMessage(ChatColor.RED+"You are not holding enough valid points in your hand.");

		return;
	}

	//point items
	public void pointItems() {		

		PointCounter.instance.reloadConfig();
		if(!(PointCounter.instance.getConfig().contains("pointItem.magic"))) {
			cmds.player.sendMessage(ChatColor.RED+"No magic point found in Config");
			return;
		}
		String mPItem = PointCounter.instance.getConfig().getString("pointItem.magic.type");


		try {
			this.mPoint.setType(Material.matchMaterial(mPItem));
		} catch (Exception e) {
			this.mPoint.setType(Material.QUARTZ);
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+mPItem+" not found.");
		}
		this.mPMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', PointCounter.instance.getConfig().getString("pointItem.magic.name")));
		List<String> mPLore = new ArrayList<String>();
		for(String msg : PointCounter.instance.getConfig().getStringList("pointItem.magic.lore")) {
			mPLore.add(ChatColor.translateAlternateColorCodes('&', msg));
		}

		this.mPMeta.setLore(mPLore);



		//physical
		if(!(PointCounter.instance.getConfig().contains("pointItem.physical"))) {
			cmds.player.sendMessage(ChatColor.RED+"No physical point found in Config");
			return;
		}

		String sPItem = PointCounter.instance.getConfig().getString("pointItem.physical.type");

		try {
			this.sPoint.setType(Material.matchMaterial(sPItem));
		} catch (Exception e) {
			this.sPoint.setType(Material.QUARTZ);
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+sPItem+" not found.");
		}
		this.sPMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', PointCounter.instance.getConfig().getString("pointItem.physical.name")));
		List<String> sPLore = new ArrayList<String>();
		for(String msg : PointCounter.instance.getConfig().getStringList("pointItem.physical.lore")) {
			sPLore.add(ChatColor.translateAlternateColorCodes('&', msg));
		}
		this.sPMeta.setLore(sPLore);



		//general

		if(!(PointCounter.instance.getConfig().contains("pointItem.general"))) {
			cmds.player.sendMessage(ChatColor.RED+"No general point found in Config");
			return;
		}
		String gPItem = PointCounter.instance.getConfig().getString("pointItem.general.type");

		try {
			this.gPoint.setType(Material.matchMaterial(gPItem));
		} catch (Exception e) {
			this.gPoint.setType(Material.ENDER_PEARL);
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+gPItem+" not found.");
		}
		this.gPMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', PointCounter.instance.getConfig().getString("pointItem.general.name")));
		List<String> gPLore = new ArrayList<String>();
		for(String msg : PointCounter.instance.getConfig().getStringList("pointItem.general.lore")) {
			gPLore.add(ChatColor.translateAlternateColorCodes('&', msg));
		}

		this.gPMeta.setLore(gPLore);


		this.mPoint.setItemMeta(this.mPMeta);
		this.sPoint.setItemMeta(this.sPMeta);
		this.gPoint.setItemMeta(this.gPMeta);

		this.nmsMPoint = CraftItemStack.asNMSCopy(mPoint);
		this.nmsSPoint = CraftItemStack.asNMSCopy(sPoint);
		this.nmsGPoint = CraftItemStack.asNMSCopy(gPoint);

		this.mpCompound = (nmsMPoint.hasTag()) ? nmsMPoint.getTag() : new NBTTagCompound();
		this.spCompound = (nmsSPoint.hasTag()) ? nmsSPoint.getTag() : new NBTTagCompound();
		this.gpCompound = (nmsGPoint.hasTag()) ? nmsGPoint.getTag() : new NBTTagCompound();
		this.mpCompound.setString("isPoint", "Yes");
		this.spCompound.setString("isPoint", "Yes");
		this.gpCompound.setString("isPoint", "Yes");
		nmsMPoint.setTag(mpCompound);
		nmsSPoint.setTag(spCompound);
		nmsGPoint.setTag(gpCompound);
		mPoint = CraftItemStack.asBukkitCopy(nmsMPoint);
		sPoint = CraftItemStack.asBukkitCopy(nmsSPoint);
		gPoint = CraftItemStack.asBukkitCopy(nmsGPoint);

		

		return;
	}


	public boolean checkM() {
		this.nbtTest=mpCompound.getString("isPoint");
		this.nmsMPoint= CraftItemStack.asNMSCopy(mPoint);
		if(this.nmsMPoint.hasTag()) {
			Bukkit.getConsoleSender().sendMessage("Tag Detected");
		}
		if(this.nbtTest=="Yes") {
			return true;
		}
		return false;
	}

	public boolean checkS() {
		this.nbtTest=spCompound.getString("isPoint");
		this.nmsSPoint= CraftItemStack.asNMSCopy(sPoint);
		if(this.nmsSPoint.hasTag()) {
			Bukkit.getConsoleSender().sendMessage("Tag Detected");
		}
		if(this.nbtTest=="Yes") {
			return true;
		}
		return false;
	}

	public boolean checkG() {
		this.nbtTest=gpCompound.getString("isPoint");
		this.nmsGPoint= CraftItemStack.asNMSCopy(mPoint);
		if(this.nmsGPoint.hasTag()) {
			Bukkit.getConsoleSender().sendMessage("Tag Detected");
		}
		if(this.nbtTest=="Yes") {
			return true;
		}
		return false;
	}


	//spending points
	public void spend() {
		this.player =cmds.player;
		cmds.reloadPData();
		this.activeChar = cmds.getPData().getString("ActiveCharacter");
		if(!(cmds.isInt(cmds.varA))) {
			player.sendMessage(ChatColor.RED+cmds.varA+" is not a number.");
			return;
		}
		this.spendAmt = Double.valueOf(cmds.varA);
		this.mPPath="characters."+activeChar+".points.unspent.magic";
		this.gPPath="characters."+activeChar+".points.unspent.general";
		this.sPPath="characters."+activeChar+".points.unspent.physical";
		this.gPointsUnspent=cmds.getPData().getInt(gPPath);
		this.mPointsUnspent=cmds.getPData().getInt(mPPath);
		this.sPointsUnspent=cmds.getPData().getInt(sPPath);

		//MAGIC
		//Tier 1
		//elemental
		if(cmds.varB.equalsIgnoreCase("Elemental")) {
			this.pointPath= "characters."+activeChar+".points.magic.elemental.base";
			this.oldPoints = cmds.getPData().getDouble(pointPath);
			if(oldPoints>=50.0) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have reached tier 2");
				return;
			}
			if(Double.sum(spendAmt,oldPoints)>50.0) {
				this.oldSpendAmt=Double.sum(spendAmt, oldPoints)-50.0;
				this.spendAmt=50.0;
				player.sendMessage("Could not spend "+oldSpendAmt+" points: cannot have more than 50 in a tier 1 magic.");
			}
			magicSpend(1);

		}

		//cosmic
		if(cmds.varB.equalsIgnoreCase("Cosmic")) {
			this.pointPath= "characters."+activeChar+".points.magic.cosmic.base";
			this.oldPoints = cmds.getPData().getDouble(pointPath);

			Bukkit.getConsoleSender().sendMessage(pointPath);
			if(oldPoints>=50.0) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have reached tier 2");
				return;
			}
			if(Double.sum(spendAmt,oldPoints)>50.0) {
				this.oldSpendAmt=Double.sum(spendAmt, oldPoints)-50.0;
				this.spendAmt=50.0;
				player.sendMessage("Could not spend "+oldSpendAmt+" points: cannot have more than 50 in a tier 1 magic.");
			}
			magicSpend(1);
		}

		//nature
		if(cmds.varB.equalsIgnoreCase("Nature")) {
			this.pointPath= "characters."+activeChar+".points.magic.nature.base";
			this.oldPoints = cmds.getPData().getDouble(pointPath);

			Bukkit.getConsoleSender().sendMessage(pointPath);
			if(oldPoints>=50.0) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have reached tier 2");
				return;
			}
			if(Double.sum(spendAmt,oldPoints)>50.0) {
				this.oldSpendAmt=Double.sum(spendAmt, oldPoints)-50.0;
				this.spendAmt=50.0;
				player.sendMessage("Could not spend "+oldSpendAmt+" points: cannot have more than 50 in a tier 1 magic.");
			}
			magicSpend(1);
		}

		//tier 2
		//Elemental
		//water
		if(cmds.varB.equalsIgnoreCase("water")||cmds.varB.equalsIgnoreCase("hydromancy")) {
			this.parentPath="characters."+activeChar+".points.magic.elemental.base";

			this.pointPath= "characters."+activeChar+".points.magic.elemental.water";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);
		}
		//earth
		if(cmds.varB.equalsIgnoreCase("earth")||cmds.varB.equalsIgnoreCase("geomancy")) {
			this.parentPath="characters."+activeChar+".points.magic.elemental.base";

			this.pointPath= "characters."+activeChar+".points.magic.elemental.earth";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}

			magicSpend(2);
		}
		//fire
		if(cmds.varB.equalsIgnoreCase("Fire")||cmds.varB.equalsIgnoreCase("pyromancy")) {
			this.parentPath="characters."+activeChar+".points.magic.elemental.base";

			this.pointPath= "characters."+activeChar+".points.magic.elemental.fire";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);

		}
		//air
		if(cmds.varB.equalsIgnoreCase("air")||cmds.varB.equalsIgnoreCase("aeromancy")||cmds.varB.equalsIgnoreCase("wind")) {
			this.parentPath="characters."+activeChar+".points.magic.elemental.base";

			this.pointPath= "characters."+activeChar+".points.magic.elemental.wind";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);
		}




		//cosmic
		if(cmds.varB.equalsIgnoreCase("Astral")||cmds.varB.equalsIgnoreCase("lunimancy")||cmds.varB.equalsIgnoreCase("celestial")) {

			this.pointPath= "characters."+activeChar+".points.magic.cosmic.astral";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);

		}
		if(cmds.varB.equalsIgnoreCase("shadow")||cmds.varB.equalsIgnoreCase("umbramancy")||cmds.varB.equalsIgnoreCase("void")) {

			this.pointPath= "characters."+activeChar+".points.magic.cosmic.shadow";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);

		}

		//nature
		if(cmds.varB.equalsIgnoreCase("Life")||cmds.varB.equalsIgnoreCase("vivimancy")||cmds.varB.equalsIgnoreCase("healing")) {
			this.parentPath="characters."+activeChar+".points.magic.nature.base";

			this.pointPath= "characters."+activeChar+".points.magic.nature.life";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);

		}
		if(cmds.varB.equalsIgnoreCase("ecomancy")||cmds.varB.equalsIgnoreCase("plants")||cmds.varB.equalsIgnoreCase("animals")) {
			this.parentPath="characters."+activeChar+".points.magic.nature.base";

			this.pointPath= "characters."+activeChar+".points.magic.nature.ecomancy";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);

		}
		if(cmds.varB.equalsIgnoreCase("death")||cmds.varB.equalsIgnoreCase("necromancy")) {
			this.parentPath="characters."+activeChar+".points.magic.nature.base";

			this.pointPath= "characters."+activeChar+".points.magic.nature.death";
			this.oldPoints = cmds.getPData().getDouble(pointPath+".points");
			this.parentPoints=cmds.getPData().getDouble(parentPath);
			if(parentPoints<50) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not reached tier 2");
				return;
			}
			if(!cmds.getPData().getBoolean(pointPath+".unlocked")) {
				player.sendMessage(ChatColor.RED+"You cannot spend in this magic: You have not unlocked this magic.");
				return;
			}
			magicSpend(2);

		}

		//PHYSICAL
		if(cmds.varB.equalsIgnoreCase("health")) {
			this.pointPath= "characters."+activeChar+".points.physical.health";
			this.oldPoints = cmds.getPData().getDouble(pointPath);
			statSpend();
		}
		if(cmds.varB.equalsIgnoreCase("strength")) {
			this.pointPath= "characters."+activeChar+".points.physical.strength";
			this.oldPoints = cmds.getPData().getDouble(pointPath);
			statSpend();
		}
		if(cmds.varB.equalsIgnoreCase("agility")) {
			this.pointPath= "characters."+activeChar+".points.physical.agility";
			this.oldPoints = cmds.getPData().getDouble(pointPath);
			statSpend();
		}
		if(cmds.varB.equalsIgnoreCase("craftsmanship")||cmds.varB.equalsIgnoreCase("craft")) {
			this.pointPath= "characters."+activeChar+".points.physical.craftsmanship";
			this.oldPoints = cmds.getPData().getDouble(pointPath);
			statSpend();
		}


	}

	public void magicSpend(int tier) {

		if(spendAmt<=Integer.sum(mPointsUnspent, gPointsUnspent)){
			double spendTrue=spendAmt;
			if(cmds.getPData().getBoolean(pointPath+".aversion")) {
				spendTrue= (Double) spendAmt/2;
				player.sendMessage(ChatColor.RED+"WARNING: You have an aversion to this magic. You will only recieve "+spendTrue+" points in the magic.");
			}
			if(tier==2) {
				if(spendAmt>mPointsUnspent) {

					cmds.getPData().set(pointPath+".points", oldPoints+spendTrue);
					spendAmt=spendAmt-mPointsUnspent;
					gPointsUnspent=(int) (gPointsUnspent-spendAmt);
					cmds.player.sendMessage(ChatColor.GOLD+"You spend "+mPointsUnspent.toString()+" magic points and "+spendAmt+" general points.");
					cmds.getPData().set(mPPath, 0);
					cmds.getPData().set(gPPath, gPointsUnspent);
					cmds.savePData();
					return;
				}

				cmds.getPData().set(pointPath+".points", oldPoints+spendTrue);
				mPointsUnspent=(int) (mPointsUnspent-spendAmt);
				cmds.player.sendMessage(ChatColor.GOLD+"You spend "+spendAmt+" magic points.");
				cmds.getPData().set(mPPath, mPointsUnspent);
				cmds.savePData();
				return;
			}
			if(spendAmt>mPointsUnspent) {

				cmds.getPData().set(pointPath, oldPoints+spendTrue);
				spendAmt=spendAmt-mPointsUnspent;
				gPointsUnspent=(int) (gPointsUnspent-spendAmt);
				cmds.player.sendMessage(ChatColor.GOLD+"You spend "+mPointsUnspent.toString()+" magic points and "+spendAmt+" general points.");
				cmds.getPData().set(mPPath, 0);
				cmds.getPData().set(gPPath, gPointsUnspent);
				cmds.savePData();
				return;
			}

			cmds.getPData().set(pointPath, oldPoints+spendTrue);
			mPointsUnspent=(int) (mPointsUnspent-spendAmt);
			cmds.player.sendMessage(ChatColor.GOLD+"You spend "+spendAmt+" magic points.");
			cmds.getPData().set(mPPath, mPointsUnspent);
			cmds.savePData();
			return;
		}
		cmds.player.sendMessage(ChatColor.RED+"You do not have enough points.");
		return;
	}
	public void statSpend() {
		if(spendAmt<=Integer.sum(sPointsUnspent, gPointsUnspent)){
			if(spendAmt>sPointsUnspent) {
				cmds.getPData().set(pointPath, oldPoints+spendAmt);
				spendAmt=spendAmt-sPointsUnspent;
				gPointsUnspent=(int) (gPointsUnspent-spendAmt);
				cmds.player.sendMessage(ChatColor.GOLD+"You spend "+sPointsUnspent.toString()+" physical stat points and "+spendAmt+" general points.");
				cmds.getPData().set(sPPath, 0);
				cmds.getPData().set(gPPath, gPointsUnspent);
				cmds.savePData();
				return;
			}

			cmds.getPData().set(pointPath, oldPoints+spendAmt);
			sPointsUnspent=(int) (sPointsUnspent-spendAmt);
			cmds.player.sendMessage(ChatColor.GOLD+"You spend "+spendAmt+" physical stat points.");
			cmds.getPData().set(sPPath, sPointsUnspent);
			cmds.savePData();
			return;
		}
		cmds.player.sendMessage(ChatColor.RED+"You do not have enough points.");
		return;
	}


	public void view() {
		this.player =cmds.player;
		cmds.reloadPData();
		this.activeChar = cmds.getPData().getString("ActiveCharacter");
		this.mPPath="characters."+activeChar+".points.unspent.magic";
		this.gPPath="characters."+activeChar+".points.unspent.general";
		this.sPPath="characters."+activeChar+".points.unspent.physical";
		this.gPointsUnspent=cmds.getPData().getInt(gPPath);
		this.mPointsUnspent=cmds.getPData().getInt(mPPath);
		this.sPointsUnspent=cmds.getPData().getInt(sPPath);

		//MAGIC
		//Tier 1
		//elemental
		if(cmds.varB.equalsIgnoreCase("Elemental")) {
			int pointAmt = cmds.getPData().getInt("characters."+activeChar+".points.magic.elemental.base");
			player.sendMessage(ChatColor.GOLD+"You have "+pointAmt+" points in Tier 1 elemental magic.");
			return;
		}

		//cosmic
		if(cmds.varB.equalsIgnoreCase("Cosmic")) {
			int pointAmt = cmds.getPData().getInt("characters."+activeChar+".points.magic.cosmic.base");
			player.sendMessage(ChatColor.BLUE+"You have "+pointAmt+" points in Tier 1 cosmic magic.");
			return;
		}

		//nature
		if(cmds.varB.equalsIgnoreCase("Nature")) {
			int pointAmt = cmds.getPData().getInt("characters."+activeChar+".points.magic.nature.base");
			player.sendMessage(ChatColor.GREEN+"You have "+pointAmt+" points in Tier 1 nature magic.");
			return;
		}

		//tier 2
		//Elemental
		//water
		if(cmds.varB.equalsIgnoreCase("water") || cmds.varB.equalsIgnoreCase("hydromancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.water";
			this.unlockPath = pointPath+".unlocked";
			viewG("Elemental", "Hydromancy");
			return;
		}
		//earth
		if(cmds.varB.equalsIgnoreCase("earth")||cmds.varB.equalsIgnoreCase("geomancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.earth";
			this.unlockPath = pointPath+".unlocked";
			viewG("Elemental", "Geomancy");
			return;

		}
		//fire
		if(cmds.varB.equalsIgnoreCase("Fire")||cmds.varB.equalsIgnoreCase("pyromancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.fire";
			this.unlockPath = pointPath+".unlocked";
			viewG("Elemental", "Pyromancy");
			return;
		}
		//air
		if(cmds.varB.equalsIgnoreCase("air")||cmds.varB.equalsIgnoreCase("aeromancy")||cmds.varB.equalsIgnoreCase("wind")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.wind";
			this.unlockPath = pointPath+".unlocked";
			viewG("Elemental", "Aeromancy");
			return;
		}




		//cosmic
		if(cmds.varB.equalsIgnoreCase("Astral")||cmds.varB.equalsIgnoreCase("lunimancy")||cmds.varB.equalsIgnoreCase("celestial")) {
			this.parentPath = "characters."+activeChar+".points.magic.cosmic.base";
			this.pointPath = "characters."+activeChar+".points.magic.cosmic.astral";
			this.unlockPath = pointPath+".unlocked";
			viewG("Cosmic", "Lunimancy");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("shadow")||cmds.varB.equalsIgnoreCase("umbramancy")||cmds.varB.equalsIgnoreCase("void")) {
			this.parentPath = "characters."+activeChar+".points.magic.cosmic.base";
			this.pointPath = "characters."+activeChar+".points.magic.cosmic.shadow";
			this.unlockPath = pointPath+".unlocked";
			viewG("Cosmic", "Umbramancy");
			return;
		}

		//nature
		if(cmds.varB.equalsIgnoreCase("Life")||cmds.varB.equalsIgnoreCase("vivimancy")||cmds.varB.equalsIgnoreCase("healing")) {
			this.parentPath = "characters."+activeChar+".points.magic.nature.base";
			this.pointPath = "characters."+activeChar+".points.magic.nature.life";
			this.unlockPath = pointPath+".unlocked";
			viewG("Nature", "Vivimancy");
			return;
		}

		if(cmds.varB.equalsIgnoreCase("ecomancy")||cmds.varB.equalsIgnoreCase("plants")||cmds.varB.equalsIgnoreCase("animals")) {
			this.parentPath = "characters."+activeChar+".points.magic.nature.base";
			this.pointPath = "characters."+activeChar+".points.magic.nature.life";
			this.unlockPath = pointPath+".unlocked";
			viewG("Nature", "Ecomancy");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("death")||cmds.varB.equalsIgnoreCase("necromancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.nature.base";
			this.pointPath = "characters."+activeChar+".points.magic.nature.life";
			this.unlockPath = pointPath+".unlocked";
			viewG("Nature", "Necromancy");
			return;
		}

		//PHYSICAL
		if(cmds.varB.equalsIgnoreCase("health")) {
			this.pointPath= "characters."+activeChar+".points.physical.health";
			int pointAmt = cmds.getPData().getInt(pointPath);
			player.sendMessage(ChatColor.GOLD+"You have "+pointAmt+" health points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("strength")) {
			this.pointPath= "characters."+activeChar+".points.physical.strength";
			int pointAmt = cmds.getPData().getInt(pointPath);
			player.sendMessage(ChatColor.GOLD+"You have "+pointAmt+" strength points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("agility")) {
			this.pointPath= "characters."+activeChar+".points.physical.agility";
			int pointAmt = cmds.getPData().getInt(pointPath);
			player.sendMessage(ChatColor.GOLD+"You have "+pointAmt+" agility points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("craftsmanship")||cmds.varB.equalsIgnoreCase("craft")) {
			this.pointPath= "characters."+activeChar+".points.physical.craftsmanship";
			int pointAmt = cmds.getPData().getInt(pointPath);
			player.sendMessage(ChatColor.GOLD+"You have "+pointAmt+" craftsmanship points");
			return;
		}

		if(cmds.varB.equalsIgnoreCase("Unspent")) {
			player.sendMessage(ChatColor.GOLD+"You have "+mPointsUnspent+" unspent magic points");
			player.sendMessage(ChatColor.GOLD+"You have "+sPointsUnspent+" unspent stat points");
			player.sendMessage(ChatColor.GOLD+"You have "+gPointsUnspent+" unspent general points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("magic")) {
			player.sendMessage(ChatColor.GOLD+"You have "+mPointsUnspent+" unspent magic points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("stat")||cmds.varB.equalsIgnoreCase("physical")) {
			player.sendMessage(ChatColor.GOLD+"You have "+sPointsUnspent+" unspent stat points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("general")) {
			player.sendMessage(ChatColor.GOLD+"You have "+gPointsUnspent+" unspent general points");
			return;
		}
	}

	public void viewG(String parentSchool, String magicName) {
		if(cmds.getPData().getInt(parentPath)<50) {
			player.sendMessage(ChatColor.RED+"You have not unlocked this magic. Must have 50 points in "+parentSchool+" magic.");
			return;
		}
		if(!cmds.getPData().getBoolean(unlockPath)) {
			player.sendMessage(ChatColor.RED+"You have not unlocked this magic.");
			return;
		}

		Double schoolPoints = cmds.getPData().getDouble(pointPath+".points");
		Double parentPoints = cmds.getPData().getDouble(parentPath);
		Double pointAmt=Double.sum(parentPoints, schoolPoints);
		player.sendMessage(ChatColor.GOLD+"You have "+pointAmt.intValue()+" ("+pointAmt+") points in "+magicName+".");
		if(cmds.getPData().getBoolean(pointPath+".aversion")) {
			player.sendMessage(ChatColor.RED+"You have an aversion in this magic.");
		}
		return;
	}





	public void view2() {
		this.target = cmds.target;
		cmds.reloadPData();
		cmds.reloadTarData();
		this.activeChar = cmds.getTarData().getString("ActiveCharacter");
		this.mPPath="characters."+activeChar+".points.unspent.magic";
		this.gPPath="characters."+activeChar+".points.unspent.general";
		this.sPPath="characters."+activeChar+".points.unspent.physical";
		this.gPointsUnspent=cmds.getTarData().getInt(gPPath);
		this.mPointsUnspent=cmds.getTarData().getInt(mPPath);
		this.sPointsUnspent=cmds.getTarData().getInt(sPPath);

		//MAGIC
		//Tier 1
		//elemental
		if(cmds.varB.equalsIgnoreCase("Elemental")) {
			int pointAmt = cmds.getTarData().getInt("characters."+activeChar+".points.magic.elemental.base");
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+pointAmt+" points in Tier 1 elemental magic.");
			return;
		}

		//cosmic
		if(cmds.varB.equalsIgnoreCase("Cosmic")) {
			int pointAmt = cmds.getTarData().getInt("characters."+activeChar+".points.magic.cosmic.base");
			cmds.sendB.sendMessage(ChatColor.BLUE+""+target.getName()+" has "+pointAmt+" points in Tier 1 cosmic magic.");
			return;
		}

		//nature
		if(cmds.varB.equalsIgnoreCase("Nature")) {
			int pointAmt = cmds.getTarData().getInt("characters."+activeChar+".points.magic.nature.base");
			cmds.sendB.sendMessage(ChatColor.GREEN+""+target.getName()+" has "+pointAmt+" points in Tier 1 nature magic.");
			return;
		}

		//tier 2
		//Elemental
		//water
		if(cmds.varB.equalsIgnoreCase("water")||cmds.varB.equalsIgnoreCase("hydromancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.water";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Hydromancy");
			return;
		}
		//earth
		if(cmds.varB.equalsIgnoreCase("earth")||cmds.varB.equalsIgnoreCase("geomancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.earth";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Geomancy");
			return;
		}
		//fire
		if(cmds.varB.equalsIgnoreCase("Fire")||cmds.varB.equalsIgnoreCase("pyromancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.fire";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Pyromancy");
			return;
		}
		//air
		if(cmds.varB.equalsIgnoreCase("air")||cmds.varB.equalsIgnoreCase("aeromancy")||cmds.varB.equalsIgnoreCase("wind")) {
			this.parentPath = "characters."+activeChar+".points.magic.elemental.base";
			this.pointPath = "characters."+activeChar+".points.magic.elemental.earth";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Geomancy");
			return;
		}




		//cosmic
		if(cmds.varB.equalsIgnoreCase("Astral")||cmds.varB.equalsIgnoreCase("lunimancy")||cmds.varB.equalsIgnoreCase("celestial")) {
			this.parentPath = "characters."+activeChar+".points.magic.cosmic.base";
			this.pointPath = "characters."+activeChar+".points.magic.cosmic.astral";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Lunimancy");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("shadow")||cmds.varB.equalsIgnoreCase("umbramancy")||cmds.varB.equalsIgnoreCase("void")) {
			this.parentPath = "characters."+activeChar+".points.magic.cosmic.base";
			this.pointPath = "characters."+activeChar+".points.magic.cosmic.shadow";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Umbramancy");
			return;
		}

		//nature
		if(cmds.varB.equalsIgnoreCase("Life")||cmds.varB.equalsIgnoreCase("vivimancy")||cmds.varB.equalsIgnoreCase("healing")) {
			this.parentPath = "characters."+activeChar+".points.magic.nature.base";
			this.pointPath = "characters."+activeChar+".points.magic.nature.life";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Vivimancy");
			return;
		}

		if(cmds.varB.equalsIgnoreCase("ecomancy")||cmds.varB.equalsIgnoreCase("plants")||cmds.varB.equalsIgnoreCase("animals")) {
			this.parentPath = "characters."+activeChar+".points.magic.nature.base";
			this.pointPath = "characters."+activeChar+".points.magic.nature.ecomancy";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Ecomancy");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("death")||cmds.varB.equalsIgnoreCase("necromancy")) {
			this.parentPath = "characters."+activeChar+".points.magic.nature.base";
			this.pointPath = "characters."+activeChar+".points.magic.nature.death";
			this.unlockPath = pointPath+".unlocked";
			viewG2("Necromancy");
			return;
		}

		//PHYSICAL
		if(cmds.varB.equalsIgnoreCase("health")) {
			this.pointPath= "characters."+activeChar+".points.physical.health";
			int pointAmt = cmds.getTarData().getInt(pointPath);
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+pointAmt+" health points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("strength")) {
			this.pointPath= "characters."+activeChar+".points.physical.strength";
			int pointAmt = cmds.getTarData().getInt(pointPath);
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+pointAmt+" strength points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("agility")) {
			this.pointPath= "characters."+activeChar+".points.physical.agility";
			int pointAmt = cmds.getTarData().getInt(pointPath);
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+pointAmt+" agility points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("craftsmanship")||cmds.varB.equalsIgnoreCase("craft")) {
			this.pointPath= "characters."+activeChar+".points.physical.craftsmanship";
			int pointAmt = cmds.getTarData().getInt(pointPath);
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+pointAmt+" craftsmanship points");
			return;
		}

		if(cmds.varB.equalsIgnoreCase("Unspent")) {
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+mPointsUnspent+" unspent magic points");
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+sPointsUnspent+" unspent stat points");
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+gPointsUnspent+" unspent general points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("magic")) {
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+mPointsUnspent+" unspent magic points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("stat")||cmds.varB.equalsIgnoreCase("physical")) {
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+sPointsUnspent+" unspent stat points");
			return;
		}
		if(cmds.varB.equalsIgnoreCase("general")) {
			cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+gPointsUnspent+" unspent general points");
			return;
		}
		cmds.sendB.sendMessage(ChatColor.RED+"Unknown Point Type");
	}
	public void viewG2(String magicName) {
		if(!cmds.getTarData().getBoolean(unlockPath)) {
			cmds.sendB.sendMessage(ChatColor.RED+""+target.getName()+" has not unlocked this magic.");
			return;
		}

		int pointAmt = cmds.getTarData().getInt(pointPath+".points");
		cmds.sendB.sendMessage(ChatColor.GOLD+""+target.getName()+" has "+pointAmt+" points in "+magicName+".");
		return;
	}


	public void unlock() {
		this.player =cmds.player;
		cmds.reloadPData();
		this.activeChar = cmds.getPData().getString("ActiveCharacter");
		Integer unlockedMagic = cmds.getPData().getInt("characters."+activeChar+".unlockedMagic");
		Integer maxUnlock = cmds.getPData().getInt("characters."+activeChar+".maxUnlock");
		if(unlockedMagic>=maxUnlock) {
			Bukkit.getConsoleSender().sendMessage(unlockedMagic.toString());
			Bukkit.getConsoleSender().sendMessage(maxUnlock.toString());
			player.sendMessage(ChatColor.RED+"You cannot unlock any more magic.");
			return;
		}
		//water
		if(cmds.varB.equalsIgnoreCase("water")||cmds.varB.equalsIgnoreCase("hydromancy")) {
			this.unlockPath = "characters."+activeChar+".points.magic.elemental.water.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.elemental.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Elemental magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}

		//earth
		if(cmds.varB.equalsIgnoreCase("earth")||cmds.varB.equalsIgnoreCase("geomancy")) {
			this.unlockPath = "characters."+activeChar+".points.magic.elemental.earth.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.elemental.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Elemental magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}

		//fire
		if(cmds.varB.equalsIgnoreCase("Fire")||cmds.varB.equalsIgnoreCase("pyromancy")) {
			this.unlockPath = "characters."+activeChar+".points.magic.elemental.fire.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.elemental.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Elemental magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}
		//air
		if(cmds.varB.equalsIgnoreCase("air")||cmds.varB.equalsIgnoreCase("aeromancy")||cmds.varB.equalsIgnoreCase("wind")) {
			this.unlockPath = "characters."+activeChar+".points.magic.elemental.wind.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.elemental.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Elemental magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}




		//cosmic
		if(cmds.varB.equalsIgnoreCase("Astral")||cmds.varB.equalsIgnoreCase("lunimancy")||cmds.varB.equalsIgnoreCase("celestial")) {
			this.unlockPath = "characters."+activeChar+".points.magic.cosmic.astral.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.cosmic.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Cosmic magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}
		if(cmds.varB.equalsIgnoreCase("shadow")||cmds.varB.equalsIgnoreCase("umbramancy")||cmds.varB.equalsIgnoreCase("void")) {
			this.unlockPath = "characters."+activeChar+".points.magic.cosmic.shadow.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.cosmic.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Cosmic magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}

		//nature
		if(cmds.varB.equalsIgnoreCase("Life")||cmds.varB.equalsIgnoreCase("vivimancy")||cmds.varB.equalsIgnoreCase("healing")) {
			this.unlockPath = "characters."+activeChar+".points.magic.nature.life.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.nature.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Nature magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}

		if(cmds.varB.equalsIgnoreCase("ecomancy")||cmds.varB.equalsIgnoreCase("plants")||cmds.varB.equalsIgnoreCase("animals")) {
			this.unlockPath = "characters."+activeChar+".points.magic.nature.ecomancy.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.nature.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Nature magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}
		if(cmds.varB.equalsIgnoreCase("death")||cmds.varB.equalsIgnoreCase("necromancy")) {
			this.unlockPath = "characters."+activeChar+".points.magic.nature.death.unlocked";
			if(cmds.getPData().getDouble("characters."+activeChar+".points.magic.nature.base")<50.0) {
				player.sendMessage(ChatColor.RED+"You have not reached tier II Nature magic yet.");
				return;
			}
			if(cmds.getPData().getBoolean(unlockPath)) {
				player.sendMessage(ChatColor.RED+"You have already unlocked this magic.");
				return;
			}
			cmds.getPData().set(unlockPath, true);
			cmds.getPData().set("characters."+activeChar+".unlockedMagic", unlockedMagic+1);
			player.sendMessage(ChatColor.GREEN+"Magic unlocked.");
			cmds.savePData();
			return;
		}
	}
}
