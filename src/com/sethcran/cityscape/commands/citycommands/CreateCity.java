package com.sethcran.cityscape.commands.citycommands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;
import com.sethcran.cityscape.City;
import com.sethcran.cityscape.Cityscape;
import com.sethcran.cityscape.Constants;
import com.sethcran.cityscape.PlayerCache;
import com.sethcran.cityscape.RankPermissions;
import com.sethcran.cityscape.commands.CSCommand;
import com.sethcran.cityscape.database.Database;

public class CreateCity extends CSCommand {
	
	public CreateCity(Cityscape plugin) {
		super(plugin);
		name = "create";
		description = "Creates a new city at the chunk you are standing on " +
				"if you have enough money.";
		usage = "/city create [cityname]";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = null;
		
		// Check if that sender is a player
		if(sender instanceof Player)
			player = (Player)sender;
		else {
			sender.sendMessage(Constants.CITYSCAPE + ChatColor.RED + 
					"Only a player in game can do that!");
			return;
		}
		
		// Check that arguments were provided correctly
		if(args == null) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED +
					"You must provide a city name.");
			return;
		}
		if(args.length > 1) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED + 
					"Spaces are not allowed in city names.");
			return;
		}
		
		// Check that town name is of appropriate length
		if(args[0].length() > Constants.TOWN_MAX_NAME_LENGTH) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED + 
					"The city name must be under " + Constants.TOWN_MAX_NAME_LENGTH + 
					" characters.");
			return;
		}
		
		// Check that town name is alphabetic
		if(!args[0].matches("[a-zA-Z]+")) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED +
					"The city name must consist only of alphabetic characters.");
			return;
		}
		
		// Check that user has permissions to create a city.
		if(!plugin.permissionHandler.has(player, "cityscape.createcity")) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED + 
					"You do not have permission to create a city.");
			return;
		}
		
		// Check that user has enough money.
		Holdings balance = iConomy.getAccount(player.getName()).getHoldings();
		if(balance == null) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED +
					"There was an error executing that command.");
			return;
		}	
		if(!balance.hasEnough(plugin.getSettings().cityCost)) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED + 
			"You do not have enough money for that.");
			return;
		}
		
		// Get needed tables;
		Database db = plugin.getDB();
		
		// Check that player is not in a city
		String currentCity = db.getCurrentCity(player.getName());
		if(currentCity != null) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED + 
				"You must first leave your current city.");
			return;
		}
		
		// Check that selected cityname does not exist
		if(db.doesCityExist(args[0])) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED +
					"That city already exists!");
			return;
		}
		
		// Get the coordinates and world where player is standing
		Chunk chunk = player.getLocation().getBlock().getChunk();
		int x = chunk.getX();
		int z = chunk.getZ();
		String worldName = chunk.getWorld().getName();
		
		// Check that the current chunk is unclaimed
		if(plugin.isChunkClaimed(worldName, x, z)) {
			player.sendMessage(Constants.CITYSCAPE + ChatColor.RED +
					"A city already owns that spot!");
			return;
		}
		
		db.createCity(player.getName(), args[0], worldName, x, z);
		
		plugin.getServer().broadcastMessage(Constants.CITYSCAPE + 
				ChatColor.GREEN + "The city of " + args[0] + " was founded!");
		
		City city = plugin.getDB().getCity(args[0]);
		plugin.insertIntoCityCache(args[0], city);
		com.sethcran.cityscape.Claim claim = new com.sethcran.cityscape.Claim(
				args[0], worldName, x, z, db.getLastClaimID());
		plugin.addClaim(claim);
		
		PlayerCache cache = plugin.getCache(player.getName());
		cache.setCity(args[0]);
		cache.setRank("Mayor");
		RankPermissions rp = new RankPermissions(true);
		rp.setRankName("Mayor");
		city.addRank(rp);
		
		city.getAccount();
		
		plugin.getDB().removeAllInvites(player.getName());
		
		balance.subtract(plugin.getSettings().cityCost);
	}
}
