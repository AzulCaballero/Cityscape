package com.sethcran.cityscape.commands.citycommands;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sethcran.cityscape.City;
import com.sethcran.cityscape.Cityscape;
import com.sethcran.cityscape.Constants;
import com.sethcran.cityscape.RankPermissions;
import com.sethcran.cityscape.commands.CSCommand;
import com.sethcran.cityscape.error.ErrorManager;
import com.sethcran.cityscape.error.ErrorManager.CSError;

public class Unclaim extends CSCommand {

	public Unclaim(Cityscape plugin) {
		super(plugin);
		name = "unclaim";
		description = "Used to unclaim the land the user is standing on.";
		usage = "/c unclaim";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = null;
		if(sender instanceof Player)
			player = (Player)sender;
		else {
			ErrorManager.sendError(sender, CSError.IN_GAME_ONLY, null);
			return;
		}
		
		String playerCity = plugin.getCache(player.getName()).getCity();
		if(playerCity == null) {
			ErrorManager.sendError(sender, CSError.NOT_IN_CITY, null);
			return;
		}
		
		RankPermissions rp = plugin.getPermissions(player.getName());
		if(rp == null) {
			ErrorManager.sendError(sender, CSError.NO_RANK_PERMISSION, null);
			return;
		}
		
		if(!rp.isUnclaim()) {
			ErrorManager.sendError(sender, CSError.NO_RANK_PERMISSION, null);
			return;
		}
		
		Chunk chunk = player.getLocation().getBlock().getChunk();
		int x = chunk.getX();
		int z = chunk.getZ();
		String world = chunk.getWorld().getName();
		City city = plugin.getCityAt(x, z, world);
		
		if(city == null) {
			ErrorManager.sendError(sender, CSError.WILDERNESS, null);
			return;
		}
		
		if(!city.getName().equals(playerCity)) {
			player.sendMessage(Constants.CITYSCAPE + Constants.ERROR_COLOR + 
					"Your city does not own this claim.");
			return;
		}
		
		if(city.getUsedClaims() == 1) {
			player.sendMessage(Constants.CITYSCAPE + Constants.ERROR_COLOR +
					"You can't unclaim your last claim!");
			return;
		}
		
		com.sethcran.cityscape.Claim claim = plugin.getClaimAt(x, z, world);
		
		if(args != null && args.length == 1 && args[0].equalsIgnoreCase("all")) {
			plugin.unclaimAll(city.getName(), claim);
			plugin.getDB().unclaimAll(city.getName(), claim);
			
			player.sendMessage(Constants.CITYSCAPE + Constants.SUCCESS_COLOR +
					"You have unclaimed all chunks except the on you are standing on.");
			
			return;
		}
		
		
		com.sethcran.cityscape.Claim north = plugin.getClaimAt(x, z + 1, world);
		com.sethcran.cityscape.Claim east = plugin.getClaimAt(x + 1, z, world);
		com.sethcran.cityscape.Claim south = plugin.getClaimAt(x, z - 1, world);
		com.sethcran.cityscape.Claim west = plugin.getClaimAt(x - 1, z, world);
		
		if(north != null) {
			if(!north.getCityName().equals(claim.getCityName()))
				north = null;
		}
		if(east != null) {
			if(!east.getCityName().equals(claim.getCityName()))
				east = null;
		}
		if(south != null) {
			if(!south.getCityName().equals(claim.getCityName()))
				south = null;
		}
		if(west != null) {
			if(!west.getCityName().equals(claim.getCityName()))
				west = null;
		}
		
		if(!city.canUnclaim(claim, north, east, south, west)) {
			player.sendMessage(Constants.CITYSCAPE + Constants.ERROR_COLOR +
					"That would create seperate areas in your claims.");
			return;
		}
		
		plugin.removeClaim(claim);
		plugin.getDB().unclaimChunk(claim);
		
		player.sendMessage(Constants.CITYSCAPE + Constants.SUCCESS_COLOR +
				"Your town has unclaimed the area at " + x + ", " +
				z + ".");
		plugin.addLogEntry("CITY", player.getName() + " unclaimed the area at " +
				x + ", " + z);
	}

}
