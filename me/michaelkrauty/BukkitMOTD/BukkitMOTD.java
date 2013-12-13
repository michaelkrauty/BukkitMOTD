package me.michaelkrauty.BukkitMOTD;
import java.io.IOException;

import me.michaelkrauty.BukkitMOTD.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
public class BukkitMOTD extends JavaPlugin implements Listener{

	Server server = Bukkit.getServer();
	ConsoleCommandSender console = server.getConsoleSender();
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		reloadConfig();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e){
		}
		console.sendMessage(ChatColor.GREEN + "[BukkitMOTD] BukkitMOTD v" + getDescription().getVersion() + " enabled!");
	}
	
	
	@EventHandler
	public void onPing(ServerListPingEvent event){
		if(getConfig().getString("enabled").equalsIgnoreCase("true")){
			if((getConfig().getString("players." + event.getAddress().toString().replace(".", "*")) == null)){
				event.setMotd(getConfig().getString("motd_never_joined").replace("(*)", "§"));
				return;
			}
			event.setMotd(getConfig().getString("motd").replace("<player>", getConfig().getString("players." + event.getAddress().toString().replace(".", "*"))).replace("(*)", "§"));
		}
	}
	
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		String playerAddress = event.getAddress().toString().replace(".", "*");
		String playerName = event.getPlayer().getName();
		getConfig().set("players." + playerAddress, playerName);
		saveConfig();
		reloadConfig();
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]){
		if(commandLabel.equalsIgnoreCase("motdreload")){
			if(sender instanceof Player){
				if(sender.hasPermission("bukkitmotd.motdreload")){
					reloadConfig();
					sender.sendMessage(ChatColor.GREEN + "BukkitMOTD config reloaded!");
					console.sendMessage(ChatColor.GREEN + sender.getName() + " reloaded the BukkitMOTD config.yml!");
					return true;
				}
			}else{
				reloadConfig();
				console.sendMessage(ChatColor.GREEN + "BukkitMOTD config reloaded!");
				return true;
			}
		}
		return true;
	}
	
}
