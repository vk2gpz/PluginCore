//
// This file is a component of PluginCore for Bukkit, and was written by Acru Jovian.
// Distributed under the The Non-Profit Open Software License version 3.0 (NPOSL-3.0)
// http://www.opensource.org/licenses/NOSL3.0
//


package org.yi.acru.bukkit;


// Imports.
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.Clan;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yi.acru.bukkit.PluginCoreLink.LinkType;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.nijikokun.register.payment.Method.MethodAccount;
import com.nijikokun.register.payment.Methods;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;



public abstract class PluginCore extends JavaPlugin{
	private static final String				coreVersion = "1.3.5";
	public static final Logger				log = Logger.getLogger("Minecraft");
	
	private static boolean					registered = false;
	private final PluginCoreServerListener	serverListener = new PluginCoreServerListener(this);
	
	private static int						useExternalGroups = 0;
	private static int						useExternalPermissions = 0;
	private static int						useExternalZones = 0;
	private static int						useExternalEconomy = 0;
	
	//private static String					lastGroupFound = "lockette.unknown";
	//private static String					lastPermissionAllow = "lockette.unknown";
	private static String					lastZoneDeny = "lockette.unknown";
	//private static String					lastEconomyAccount = "lockette.unknown";
	
	private static List<PluginCoreLink>		linkList = null;
	//private static PluginCoreLink			linkSuperPerms = null;
	private static PluginCoreLink			linkVaultPermissions = null;
	private static PluginCoreLink			linkTowny = null;
	private static PluginCoreLink			linkSimpleClans = null;
	private static PluginCoreLink			linkMcmmo = null;
	private static PluginCoreLink			linkFactions = null;
	private static PluginCoreLink			linkLWC = null;
	//private static PluginCoreLink			linkIConomy = null;
	private static PluginCoreLink			linkRegister = null;
	
	//********************************************************************************
	// Constructors and methods.
	
	
	public PluginCore(){}
	
	/* BukkitMulti
	public PluginCore(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader){
		super(pluginLoader, noNag(instance, true), desc, folder, plugin, cLoader);
		noNag(instance, false);
	}
	
	
	private static Server noNag(Server server, boolean enable){
		try{
			Method		getlogger = Server.class.getMethod("getLogger", (Class[]) null);
			Logger		thelogger = (Logger) getlogger.invoke(server, (Object[]) null);
			
			if(enable) thelogger.setLevel(Level.SEVERE);
			else thelogger.setLevel(null);
		}
		catch(Throwable ex){}
		return(server);
	}
	*/
	
	public void onEnable(){
		//Server server = getServer();
		//if(server == null) return;
		
		if(!registered){
			serverListener.registerEvents();
			registered = true;
		}
		
		if(linkList != null){
			linkList.clear();
			linkList = null;
			
			useExternalGroups = 0;
			useExternalPermissions = 0;
			useExternalZones = 0;
			useExternalEconomy = 0;
		}
		
		linkList = new ArrayList<PluginCoreLink>(10);
		
		// Link to various plugins, if available.
		linkTowny = linkExternalPlugin("Towny", LinkType.GROUPS_ZONES);
		linkSimpleClans = linkExternalPlugin("SimpleClans", LinkType.GROUPS);
		linkMcmmo = linkExternalPlugin("mcMMO", LinkType.GROUPS);
		linkFactions = linkExternalPlugin("Factions", LinkType.GROUPS);
		linkLWC = linkExternalPlugin("LWC", LinkType.ZONES);
		//linkIConomy = linkExternalPlugin("iConomy", LinkType.ECONOMY);
		linkRegister = linkExternalPlugin("Register", LinkType.ECONOMY);
		// Permissions Vault last.
		linkVaultPermissions = linkExternalPlugin("Vault", LinkType.Permissions);
		
		if(usingExternalPermissions()) log.info("[" + getDescription().getName() + "] Using linked plugin for admin permissions.");
		else log.info("[" + getDescription().getName() + "] Using ops file for admin permissions.");
	}
	
	
	public void onDisable(){
		if(linkList != null){
			linkList.clear();
			linkList = null;
			
			useExternalGroups = 0;
			useExternalPermissions = 0;
			useExternalZones = 0;
			useExternalEconomy = 0;
		}
	}
	
	
	//********************************************************************************
	// Accessors.
	

	public static String getCoreVersion(){return(coreVersion);}
	public static String lastZoneDeny(){return(lastZoneDeny);}
	
	
	public void dumpCoreInfo(){
		log.info("[" + getDescription().getName() + "] Dumping core information:");
		log.info("Number of linked group plugins: " + useExternalGroups);
		log.info("Number of linked permission plugins: " + useExternalPermissions);
		log.info("Number of linked zone plugins: " + useExternalZones);
		log.info("Number of linked economy plugins: " + useExternalEconomy);
		
		//Player[]	players = getServer().getOnlinePlayers();
		Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

		log.info("Using Bukkit permissions, " + players.length + " players online.");
			
		for(Player player: players){
			log.info("Player: " + player.getName() + " has the following permissions:");
				
			Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
				
			for(PermissionAttachmentInfo perm: perms){
				log.info("    " + perm.getPermission() + " = " + perm.getValue());
			}
		}
	}
	
	//********************************************************************************
	// Start of external plugin linking section.
	
	// Returns a special link holder class.
	private PluginCoreLink linkExternalPlugin(String pluginName, LinkType handler){
		Plugin			plugin = getServer().getPluginManager().getPlugin(pluginName);
		PluginCoreLink	link = new PluginCoreLink(this, plugin, handler);
		
		// Return if not available.
		if(plugin == null) return(link);
		if(linkList == null) return(link);
		
		// Set up the link.
		link.setLinked(true);
		linkList.add(0, link);
		if(getServer().getPluginManager().isPluginEnabled(plugin)){
			// If the plugin is already enabled, enable the link too.
			setLink(link.getPluginName(), true, false);
		}
		
		return(link);
	}
	
	
	public void setLink(String pluginName, boolean enable, boolean listener){
		if(linkList == null) return;

		boolean		external = usingExternalPermissions();
		int			groups = useExternalGroups;
		int			perms = useExternalPermissions;
		int			zones = useExternalZones;
		int			economy = useExternalEconomy;
		
		
		boolean		result = true;
		int			difference;
		
		if(enable) difference = 1;
		else difference = -1;
		
		// Search the link list for the plugin that is being enabled/disabled.
		for(PluginCoreLink link: linkList) if(pluginName.equals(link.getPluginName())){
			// Ignore unlinked plugins.
			if(!link.isLinked()) return;
			
			
			// Handle enables for the generic types.
			switch(link.getType()){
			case GROUPS:
			case GROUPS_PERMISSIONS:
			case GROUPS_ZONES:
			case GROUPS_PERMISSIONS_ZONES:
				useExternalGroups += difference;
				break;
			}
			
			switch(link.getType()){
			case PERMISSIONS:
			case GROUPS_PERMISSIONS:
			case PERMISSIONS_ZONES:
			case GROUPS_PERMISSIONS_ZONES:
				useExternalPermissions += difference;
				break;
			}
			
			switch(link.getType()){
			case ZONES:
			case GROUPS_ZONES:
			case PERMISSIONS_ZONES:
			case GROUPS_PERMISSIONS_ZONES:
				useExternalZones += difference;
				break;
			}
			
			switch(link.getType()){
			case ECONOMY:
				useExternalEconomy += difference;
				break;
			}
			
			
			// Handle specific plugin enables.
			switch(link.getType()){
			case Permissions:
				result = enableLinkPermissions(link, enable, difference);
				break;
				
			default:
				link.setEnabled(enable);
			}
			
			
			// Report the result.
			
			if(enable){
				if(result){
					String		changed = "";
					
					if(groups != useExternalGroups) changed += "/Groups";
					if(usingExternalPermissions()) if(perms != useExternalPermissions) changed += "/Permissions";
					if(zones != useExternalZones) changed += "/Zones";
					if(economy != useExternalEconomy) changed += "/Economy";
					
					if(changed.startsWith("/")) changed = changed.substring(1);
					else if(!usingExternalPermissions()) changed = "NO REASON (permissions disabled)";
					else changed = "NO REASON";
					
					log.info("[" + getDescription().getName() + "] Enabled link to plugin " + link.getPluginName() + " for " + changed + ", version " + link.getPluginVersion());
					if(listener) if(!external) if(usingExternalPermissions()) log.info("[" + getDescription().getName() + "] Ops file overridden, using linked plugin for admin permissions.");
				}
				else if(!link.isLinked()) log.info("[" + getDescription().getName() + "] Ignoring fake Permissions plugin version " + link.getPluginVersion());
				else log.warning("[" + getDescription().getName() + "] Failed to enable link to plugin" + link.getPluginName() + ", version " + link.getPluginVersion() + "!");
			}
			else if(result) log.info("[" + getDescription().getName() + "] Disabled link to plugin " + link.getPluginName() + ", version " + link.getPluginVersion());
			else log.warning("[" + getDescription().getName() + "] Failed to disable link to plugin " + link.getPluginName() + ", version " + link.getPluginVersion() + "!");
			
			return;
		}
		
		// Handle an unmatched plugin.
		//if(enable) log.warning("[" + getDescription().getName() + "] Unknown error with enabling " + pluginName);
		//else log.warning("[" + getDescription().getName() + "] Unknown error with disabling " + pluginName);
	}
	
	
	// Enable handler for a specialized external plugin, returns true is successful.
	protected boolean enableLinkPermissions(PluginCoreLink link, boolean enable, int difference){
		boolean		usePerms = true;
		
		if(enable){
			
			// Detect the vault permission system
			if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
				return(false);
			}
			
			RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	        if(rsp.getProvider() == null) {
	        	return(false);
	        }
		}
		

		// Loaded okay, set the appropriate flags for this plugin.
		useExternalGroups += difference;
		if(usePerms) useExternalPermissions += difference;
		link.setEnabled(enable);
		return(true);
	}
	
	
	//********************************************************************************************************************
	// Start of external permissions section
	
	protected boolean pluginEnableOverride(String pluginName){return(false);}
	protected boolean usingExternalGroups(){return(useExternalGroups > 0);}
	protected boolean usingExternalPermissions(){return(useExternalPermissions > 0);}
	protected boolean usingExternalZones(){return(useExternalZones > 0);}
	protected boolean usingExternalEconomy(){return(useExternalEconomy > 0);}
	protected String getLocalizedEveryone(){return(null);}
	protected String getLocalizedOperators(){return(null);}
	
	
	// True if in the group.
	public boolean inGroup(World world, Player player, String groupName){
		return(inGroup(world, player, player.getName(), groupName));
	}
	public boolean inGroup(World world, String playerName, String groupName){
		return(inGroup(world, getServer().getPlayer(playerName), playerName, groupName));
	}

	private boolean inGroup(World world, Player player, String playerName, String groupName){
		// Built in groups.
		String		local;
		
		
		if(groupName.equalsIgnoreCase("[Everyone]")) return(true);
		local = getLocalizedEveryone();
		if(local != null) if(groupName.equalsIgnoreCase(local)) return(true);
		//if(groupName.equalsIgnoreCase(Lockette.altEveryone)) return(true);
		
		if(player != null) if(player.isOp()){
			if(groupName.equalsIgnoreCase("[Operators]")) return(true);
			local = getLocalizedOperators();
			if(local != null) if(groupName.equalsIgnoreCase(local)) return(true);
			//if(groupName.equalsIgnoreCase(Lockette.altOperators)) return(true);
		}
		
		if(!usingExternalGroups()) return(false);
		
		
		// Use external group plugins here.
		
		boolean		result = false;
		int			end = groupName.length() - 1;
		
		if(end >= 2) if((groupName.charAt(0) == '[') && (groupName.charAt(end) == ']')){
			
			if(linkTowny.isEnabled()){
				try{
					Resident	resident = TownyUniverse.getDataSource().getResident(playerName);
					
					try{
						Town		town = resident.getTown();
						if(town.getName().equalsIgnoreCase(groupName.substring(1, end))) return(true);
						
						try{
							Nation		nation = town.getNation();
							if(nation.getName().equalsIgnoreCase(groupName.substring(1, end))) return(true);
						}
						catch(Throwable ex){}
					}
					catch(Throwable ex){}
				}
				catch(Throwable ex){}
			}
			
			if(linkSimpleClans.isEnabled()){
				Clan		clan = linkSimpleClans.getSimpleClans().getClanManager().getClanByPlayerName(playerName);
				
				if(clan != null){
					if(clan.getName().equalsIgnoreCase(groupName.substring(1, end))) return(true);
					if(clan.getTag().equalsIgnoreCase(groupName.substring(1, end))) return(true);
				}
			}
			
			if(linkMcmmo.isEnabled()) if(player != null){
				PlayerProfile	pProfile = linkMcmmo.getMcmmo().getPlayerProfile(player.getName());
				
				if(pProfile != null) if(pProfile.inParty()){
					if(pProfile.getParty().getName().equalsIgnoreCase(groupName.substring(1, end))) return(true);
				}
			}
			
			if(linkFactions.isEnabled()) if(player != null){
				String		tag = linkFactions.getFactions().getPlayerFactionTag(player);
				
				if(tag != null){
					if(tag.equalsIgnoreCase(groupName.substring(1, end))) return(true);
				}
			}
			
			// Permissions classic last.
			if(linkVaultPermissions.isEnabled()){
				result = linkVaultPermissions.getVaultPermissions().playerInGroup(world.getName(), player, groupName.substring(1, end));
				return(true);
			}
		}
		
		return(false);
	}
	
	
	// True if has permission.
	public boolean hasPermission(World world, String playerName, String permissionNode){
		return(hasPermission(world, getServer().getPlayer(playerName), permissionNode));
	}
	public boolean hasPermission(World world, Player player, String permissionNode){
		if(player == null) return(false);
		
		return player.hasPermission(permissionNode);
	}
	
	
	// True if can build.
	public boolean canBuild(String playerName, Block block){
		return(canBuild(getServer().getPlayer(playerName), block));
	}
	public boolean canBuild(Player player, Block block){
		lastZoneDeny = "noone";
		if(!usingExternalZones()) return(true);
		
		if(linkTowny.isEnabled()){
			try {
				if (TownyUniverse.getDataSource().getWorld(block.getWorld().getName()).isUsingTowny())
					if(TownyUniverse.isWilderness(block)){
						if(usingExternalPermissions()){
							if(!hasPermission(block.getWorld(), player, "lockette.towny.wilds")){
								lastZoneDeny = "towny.wilds";
								return(false);
							}
						}
						else{
							// Anything needed here?
						}
					}
			} catch (Exception e) {
				// Failed to fetch world from Towny, so ignore
			}
		}
		
		if(linkLWC.isEnabled()){
			LWC			lwc = linkLWC.getLWCPlugin().getLWC();
			Protection	protection = lwc.findProtection(block);
			
			if(protection != null){
				if(!lwc.canAdminProtection(player, protection)){
					lastZoneDeny = "lwc.protection";
					return(false);
				}
			}
		}
		
		return(true);
	}
	
	
	
	//********************************************************************************
	// Start of economy section
	
	
	// Formats a currency value into a string.
	public String economyFormat(double amount){
		if(!usingExternalEconomy()) return(Double.toString(amount));

		if(linkRegister.isEnabled()){
			if(Methods.hasMethod()){
				return(Methods.getMethod().format(amount));
			}
		}
		
		return(Double.toString(amount));
	}
	
	
	// Returns true if the transaction is made.
	public boolean economyTransfer(String source, String destination, double amount){
		if(!usingExternalEconomy()) return(false);
		
		//iConomy.getAccount(player.getName()).getHoldings();
		
		if(linkRegister.isEnabled()){
			if(Methods.hasMethod()){
				if(Methods.getMethod().hasAccount(source)){
					MethodAccount sourceAccount = Methods.getMethod().getAccount(source);
					
					if(sourceAccount.hasEnough(amount)){
						if(!Methods.getMethod().hasAccount(destination)) Methods.getMethod().createAccount(destination);
						
						if(Methods.getMethod().hasAccount(destination)){
							MethodAccount destinationAccount = Methods.getMethod().getAccount(destination);
							
							if(sourceAccount.subtract(amount)){
								if(destinationAccount.add(amount)){
									return(true);
								}
								else sourceAccount.add(amount);
							}
						}
					}
				}
			}
		}
		
		return(false);
	}
	
	
	
	//********************************************************************************
	// Start of utility section
	
	
	protected void selectiveBroadcast(String target, String message){
		if(target == null) return;
		if(target.isEmpty()) return;
		if(message == null) return;
		if(message.isEmpty()) return;
		
		//Player[]	players = getServer().getOnlinePlayers();
		Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

		if(target.charAt(0) == '['){
			// For groups.
			for(int x = 0; x < players.length; ++x){
				if(inGroup(players[x].getWorld(), players[x], target)){
					// Send the message.
					players[x].sendMessage(message);
				}
			}
		}
		else{
			// For player names.
			for(int x = 0; x < players.length; ++x){
				if(target.equalsIgnoreCase(players[x].getName())){
					// Send the message.
					players[x].sendMessage(message);
				}
			}
		}
	}
	
	
	public boolean playerOnline(String truncName){
		String		text = truncName.replaceAll("(?i)\u00A7[0-F]", "");
		//Player[]	players = getServer().getOnlinePlayers();
                Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);

		int			length;
		
		for(int x = 0; x < players.length; ++x){
			length = players[x].getName().length();
			if(length > 15) length = 15;
			
			if(text.equals(players[x].getName().substring(0, length))) return(true);
		}
		
		return(false);
	}
	
	
	protected static float getBuildVersion(){
		//String		version = CraftServer.class.getPackage().getImplementationVersion() + ' ';
		String		version = Server.class.getPackage().getImplementationVersion() + ' ';
		//String		version = server.getVersion();
		int			index = version.lastIndexOf("-b");
		int			build;
		
		//-b231 to
		//-b326
		//hash codes...
		//unknowns...
		//-b{35}jnks to
		//-b{42}jnks
		//-b43jnks to
		//-b54jnks
		//-b400jnks and up
		//With Jenkins, /45/ TSLPC check.
		
		// Check if -b tag not found.
		if(index == -1) return(0);
		
		index += 2;
		
		// Check if remains is too short.
		if(version.length() < index + 3) return(0);
		
		if(version.charAt(index) == '{') ++index;
		
		
		// Get the build number.
		int			x, endIndex = index;
		
		for(x = index; x < version.length(); ++x){
			if(Character.isDigit(version.charAt(x))) ++endIndex;
			else break;
		}
		
		try{
			build = Integer.parseInt(version.substring(index, endIndex));
		}
		catch(NumberFormatException ex){
			return(0);
		}

		// Get the build source.
		boolean		jenkins = false, bamboo = false;
		
		if(version.length() >= endIndex + 3){
			if(version.charAt(endIndex) == '}') ++endIndex;
			if(version.charAt(endIndex) == ' '){}
			//if(version.charAt(endIndex) == 'j')
			else if(version.substring(endIndex).startsWith("jnks ")) jenkins = true;
			//else if(version.charAt(endIndex) == 'b') bamboo = true;
			else bamboo = true;
		}
		
		
		// Check if recognized.
		if((build >= 231) && (build <= 326) && (!jenkins) && (!bamboo)) return(build);
		
		if((build >= 35) && (build <= 54) && jenkins) return(399 + (build/100.0f));
		
		if((build >= 400) && jenkins) return(build);
		
		return(0);
	}
	
	
	public static Block getSignAttachedBlock(Block block){
		if(block.getTypeId() != Material.WALL_SIGN.getId()) return(null);
		
		
		int			face = block.getData() & 0x7;
		
		if(face == BlockUtil.faceList[0]) return(block.getRelative(BlockFace.NORTH));
		if(face == BlockUtil.faceList[1]) return(block.getRelative(BlockFace.EAST));
		if(face == BlockUtil.faceList[2]) return(block.getRelative(BlockFace.SOUTH));
		if(face == BlockUtil.faceList[3]) return(block.getRelative(BlockFace.WEST));
		
		return(null);
	}
	
	
	public static Block getTrapDoorAttachedBlock(Block block){
		int			type = block.getTypeId();

		if(type != Material.TRAP_DOOR.getId() && type != Material.IRON_TRAPDOOR.getId()) return(null);
		
		
		int			face = block.getData() & 0x3;
		
		if(face == BlockUtil.attachList[0]) return(block.getRelative(BlockFace.NORTH));
		if(face == BlockUtil.attachList[1]) return(block.getRelative(BlockFace.EAST));
		if(face == BlockUtil.attachList[2]) return(block.getRelative(BlockFace.SOUTH));
		if(face == BlockUtil.attachList[3]) return(block.getRelative(BlockFace.WEST));
		
		return(null);
	}
	
	
	public static BlockFace getPistonFacing(Block block){
		int			type = block.getTypeId();
		
		if((type != Material.PISTON_BASE.getId()) && (type != Material.PISTON_STICKY_BASE.getId()) && (type != Material.PISTON_EXTENSION.getId())){
			return(BlockFace.SELF);
		}
		
		
		int			face = block.getData() & 0x7;
		
		switch(face){
			case 0: return(BlockFace.DOWN);
			case 1: return(BlockFace.UP);
			case 2: return(BlockFace.NORTH);
			case 3: return(BlockFace.SOUTH);
			case 4: return(BlockFace.WEST);
			case 5: return(BlockFace.EAST);
		}
		
		return(BlockFace.SELF);
	}
}

