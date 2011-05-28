package com.sethcran.cityscape.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.sethcran.cityscape.Cityscape;

public class Schemas {
	private Connection con = null;
	
	private final int NAME_MAX_CHARACTER_LENGTH = 32;
	
	public Schemas(Connection con) {
		this.con = con;
	}
	
	public void createCSDatabase() {
		createPlayersTable();
		createCitiesTable();
		createClaimsTable();
		createPlotsTable();
		createPlayerCityDataTable();
		createChestsTable();
	}
	
	public void createPlayersTable() {
		String sql = "CREATE TABLE IF NOT EXISTS CSPlayers(" +
		"name CHAR(" + NAME_MAX_CHARACTER_LENGTH + ") PRIMARY KEY," +
		"firstLogin DATETIME NOT NULL," +
		"lastLogin DATETIME NOT NULL) " +
		"ENGINE = InnoDB;";
		
		try {
			con.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			Cityscape.log.severe("There was an error creating a database table.");
			e.printStackTrace();
		}	
	}
	
	public void createCitiesTable() {
		String sql = "CREATE TABLE IF NOT EXISTS CSCities(" + 
		"name CHAR(" + NAME_MAX_CHARACTER_LENGTH + ") PRIMARY KEY," +
		"mayor CHAR(" + NAME_MAX_CHARACTER_LENGTH + ")," +
		"rank INT NOT NULL," +
		"founded DATETIME NOT NULL," +
		"baseClaims INT NOT NULL," +
		"bonusClaims INT NOT NULL," + 
		"spawn POINT," +
		"FOREIGN KEY(mayor) REFERENCES CSPlayers(name) ON DELETE CASCADE) " +
		"ENGINE = InnoDB;";
		
		try {
			con.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			Cityscape.log.severe("There was an error creating a database table.");
			e.printStackTrace();
		}
	}
	
	public void createClaimsTable() {
		String sql = "CREATE TABLE IF NOT EXISTS CSClaims(" +
		"loc POINT PRIMARY KEY," +
		"city CHAR(" + NAME_MAX_CHARACTER_LENGTH + ") NOT NULL) " + 
		"ENGINE = InnoDB;";
		
		try {
			con.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			Cityscape.log.severe("There was an error creating a database table.");
			e.printStackTrace();
		}
		
	}
	
	public void createPlotsTable() {
		String sql = "CREATE TABLE IF NOT EXISTS CSPlots(" +
		"loc1 POINT," +
		"loc2 POINT," + 
		"city CHAR(" + NAME_MAX_CHARACTER_LENGTH + ") NOT NULL," +
		"PRIMARY KEY(loc1, loc2)) " +
		"ENGINE = InnoDB;";
		
		try {
			con.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			Cityscape.log.severe("There was an error creating a database table.");
			e.printStackTrace();
		}
	}
	
	public void createPlayerCityDataTable() {
		String sql = "CREATE TABLE IF NOT EXISTS CSPlayerCityData(" +
		"player CHAR(" + NAME_MAX_CHARACTER_LENGTH + ")," +
		"city CHAR(" + NAME_MAX_CHARACTER_LENGTH + ")," +
		"startingfrom DATETIME NOT NULL," +
		"PRIMARY KEY(player, startingfrom)," +
		"FOREIGN KEY(player) REFERENCES CSPlayers(name) ON DELETE CASCADE," +
		"FOREIGN KEY(city) REFERENCES CSCities(name) ON DELETE NO ACTION) " +
		"ENGINE = InnoDB;";
		
		try {
			con.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			Cityscape.log.severe("There was an error creating a database table.");
			e.printStackTrace();
		}
	}
	
	public void createChestsTable() {
		String sql = "CREATE TABLE IF NOT EXISTS CSChests(" +
		"id INT AUTO_INCREMENT PRIMARY KEY," +
		"player CHAR(" + NAME_MAX_CHARACTER_LENGTH + ")," +
		"FOREIGN KEY(player) REFERENCES CSPlayers(name) ON DELETE SET NULL) " +
		"ENGINE = InnoDB;";
		
		try {
			con.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			Cityscape.log.severe("There was an error creating a database table.");
			e.printStackTrace();
		}
	}
}
