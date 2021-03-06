package com.sethcran.cityscape.commands.plotcommands;

import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sethcran.cityscape.City;
import com.sethcran.cityscape.Cityscape;
import com.sethcran.cityscape.Constants;
import com.sethcran.cityscape.Permissions;
import com.sethcran.cityscape.Plot;
import com.sethcran.cityscape.commands.CSCommand;
import com.sethcran.cityscape.error.ErrorManager;
import com.sethcran.cityscape.error.ErrorManager.CSError;

public class Destroy extends CSCommand {

	public Destroy(Cityscape plugin) {
		super(plugin);
		name = "destroy";
		description = "Displays the list of cities and players who can destroy here.";
		usage = "/plot destroy";
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
		
		Location location = player.getLocation();
		Chunk chunk = location.getBlock().getChunk();
		City city = plugin.getCityAt(chunk.getX(), chunk.getZ(),
				chunk.getWorld().getName());
		if(city == null) {
			ErrorManager.sendError(sender, CSError.MUST_BE_STANDING_IN_CITY, null);
			return;
		}
		
		Plot plot = city.getPlotAt(location.getBlockX(), location.getBlockZ());
		if(plot == null) {
			ErrorManager.sendError(sender, CSError.MUST_BE_STANDING_IN_PLOT, null);
			return;
		}
		
		player.sendMessage(Constants.CITYSCAPE + Constants.SUCCESS_COLOR + 
				"Plot Permissions:");
		
		if(plot.isOutsiderDestroy()) {
			player.sendMessage(Constants.SUCCESS_COLOR + "Everyone can destroy here.");
			return;
		}
		
		if(plot.isResidentDestroy()) {
			player.sendMessage(Constants.SUCCESS_COLOR + "Residents can destroy here.");
		}
		
		Set<Entry<String, Permissions>> citySet = plot.getCitiesWithPermissions();
		Set<Entry<String, Permissions>> playerSet = plot.getPlayersWithPermissions();
		
		player.sendMessage(Constants.CITYSCAPE + Constants.GROUP_COLOR + 
				"Cities with permissions to destroy here:");
		
		String message = "" + ChatColor.WHITE;
		for(Entry<String, Permissions> e : citySet) {
			if(e.getValue().isCanDestroy()) {
				if(message.length() + e.getKey().length() > Constants.CHAT_LINE_LENGTH) {
					sender.sendMessage(message);
					message = "" + ChatColor.WHITE;
				}
				else if(!message.equals("" + ChatColor.WHITE))
					message += ", ";
				message += e.getKey();
			}
		}
		
		if(!message.equals("" + ChatColor.WHITE))
			sender.sendMessage(message);
		
		player.sendMessage(Constants.CITYSCAPE + Constants.GROUP_COLOR + 
				"Players with permissions to destroy here:");
		
		message = "" + ChatColor.WHITE;
		for(Entry<String, Permissions> e : playerSet) {
			if(e.getValue().isCanDestroy()) {
				if(message.length() + e.getKey().length() > Constants.CHAT_LINE_LENGTH) {
					sender.sendMessage(message);
					message = "" + ChatColor.WHITE;
				}
				else if(!message.equals("" + ChatColor.WHITE))
					message += ", ";
				message += e.getKey();
			}
		}
		
		if(!message.equals("" + ChatColor.WHITE))
			sender.sendMessage(message);
	}

}
