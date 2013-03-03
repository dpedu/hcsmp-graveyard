package com.dpedu.graveyard;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class Graveyard extends JavaPlugin implements Listener {
	private GraveyardDAO _dao;
	
	public Graveyard()
	{
		this._dao = new GraveyardDAO();
	}
	
	
	/**
	 * Plugin enabled
	 */
	public void onEnable()
	{
		// Check the configuration
		FileConfiguration config = this.getConfig();
		if ( !config.contains( "hostname" ) ) config.set( "hostname", "localhost" );
		if ( !config.contains( "database" ) ) config.set( "database", "hardcore" );
		if ( !config.contains( "username" ) ) config.set( "username", "root" );
		if ( !config.contains( "password" ) ) config.set( "password", "" );
		this.saveConfig();
		
		// Connect to the database
		this._dao.connect( config.getString( "hostname", "localhost" ), 
				config.getString( "database", "hardcore" ), 
				config.getString( "username", "root" ), 
				config.getString( "password", "" ),
				this.getDataFolder() );
		
		// Register events
		this.getServer().getPluginManager().registerEvents( this, this );
		
	}
	
	/**
	 * Plugin disabled
	 */
	public void onDisable()
	{
		// Disconnect from the database and kill the protection check thread
		this._dao.disconnect( this.getDataFolder() );
	}
	
	/**
	 * Kick live players
	 * @param e
	 */
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerLoginDeathCheck( PlayerLoginEvent e )
	{
		Player player = e.getPlayer();
		String playerName = player.getName();
		
		if(player.isOp()) {
			return;
		}
		
		// Kick out dead players
		if ( !this._dao.playerDead( playerName ) )
		{
			e.setKickMessage( "You are not dead! Please connect to http://hardcore.hcsmp.com" );
			e.setResult( Result.KICK_OTHER );
			return;
		}
	}
	
}
