package com.sethcran.cityscape.commands.playercommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sethcran.cityscape.Cityscape;
import com.sethcran.cityscape.Constants;
import com.sethcran.cityscape.commands.CSCommand;
import com.sethcran.cityscape.commands.Player;

public class Help extends CSCommand {
	
	private Player playerCommands = null;
	
	private String[] commands = { "default", "history" };

	public Help(Cityscape plugin, Player playerCommands) {
		super(plugin);
		this.playerCommands = playerCommands;
		name = "help";
		description = "Displays help on the specified player command";
		usage = "/p help";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args == null) {
			sender.sendMessage(Constants.CITYSCAPE + Constants.SUCCESS_COLOR +
					"Player Commands List:");
			String message = "" + ChatColor.WHITE;
			for(String c : commands) {
				if(message.length() + c.length() > Constants.CHAT_LINE_LENGTH) {
					sender.sendMessage(message);
					message = "" + ChatColor.WHITE;
				}
				else if(!message.equals("" + ChatColor.WHITE))
					message += ", ";
				message += c;
			}
			
			if(!message.equals("" + ChatColor.WHITE))
				sender.sendMessage(message);
			return;
		}
		
		if(args.length != 1) {
			sender.sendMessage(Constants.CITYSCAPE + Constants.ERROR_COLOR +
					"You must type a specific command that you want help on.");
			return;
		}
		
		CSCommand cmd = playerCommands.getCommand(args[0]);
		
		if(cmd == null) {
			sender.sendMessage(Constants.CITYSCAPE + Constants.ERROR_COLOR +
					"That command does not exist.");
			return;
		}
		
		sender.sendMessage(Constants.CITYSCAPE + Constants.SUCCESS_COLOR +
				"Player Command: " + args[0]);
		sender.sendMessage(Constants.GROUP_COLOR + "Description: " + ChatColor.WHITE + 
				cmd.getDescription());
		sender.sendMessage(Constants.GROUP_COLOR + "Usage: " + ChatColor.WHITE +
				cmd.getUsage());
	}
}
