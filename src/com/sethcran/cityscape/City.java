package com.sethcran.cityscape;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectProcedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

public class City {
	private String name = null;
	private String mayor = null;
	private String founded = null;
	private String welcome = null;
	
	private int rank = 0;
	private int usedClaims = 0;
	private int baseClaims = 0;
	private int bonusClaims = 0;
	
	private double spawnX = 0;
	private double spawnY = 0;
	private double spawnZ = 0;
	private float spawnPitch = 0;
	private float spawnYaw = 0;
	String world = null;
	private boolean residentBuild = false;
	
	private boolean residentDestroy = false;
	private boolean residentSwitch = false;
	private boolean outsiderBuild = false;
	private boolean outsiderDestroy = false;
	private boolean outsiderSwitch = false;
	private boolean snow = false;
	private RTree plotTree = null;
	
	private TIntObjectHashMap<Plot> plotMap = null;
	private HashMap<String, RankPermissions> rankMap = null;
	private HashMap<String, String> banList = null;
	public City() {
		rankMap = new HashMap<String, RankPermissions>();
		banList = new HashMap<String, String>();
		plotMap = new TIntObjectHashMap<Plot>();
		plotTree = new RTree();
		plotTree.init(null);
	}
	public void addPlot(Plot plot) {
		plotMap.put(plot.getId(), plot);
		plotTree.add(new Rectangle(plot.getXmin(), plot.getZmin(), 
				plot.getXmax(), plot.getZmax()), plot.getId());
	}
	public void addRank(RankPermissions rp) {
		rankMap.put(rp.getRankName(), rp);
	}
	public void ban(String player) {
		banList.put(player, player);
	}
	public void changeRank(RankPermissions rp) {
		rankMap.remove(rp.getRankName());
		rankMap.put(rp.getRankName(), rp);
	}
	public boolean doesRankExist(String rank) {
		RankPermissions rp = rankMap.get(rank);
		if(rp == null)
			return false;
		return true;
	}
	public Account getAccount() {
		return iConomy.getAccount(name + ":city");
	}
	public int getBaseClaims() {
		return baseClaims;
	}
	
	public int getBonusClaims() {
		return bonusClaims;
	}
	public String getFounded() {
		return founded;
	}
	
	public String getMayor() {
		return mayor;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumRanks() {
		return rankMap.size();
	}
	
	public Plot getPlotAt(int x, int z) {
		TreeProcedure tproc = new TreeProcedure();
		plotTree.intersects(new Rectangle(x, z, x, z), tproc);
		for(int i : tproc.getId()) {
			return plotMap.get(i);
		}
		return null;
	}
	
	public int getRank() {
		return rank;
	}
	
	public RankPermissions getRank(String rankName) {
		return rankMap.get(rankName);
	}
	
	public ArrayList<String> getRanks() {
		Set<String> set = rankMap.keySet();
		ArrayList<String> list = new ArrayList<String>();
		for(String s : set) {
			list.add(s);
		}
		return list;
	}
	
	public float getSpawnPitch() {
		return spawnPitch;
	}

	public double getSpawnX() {
		return spawnX;
	}
	
	public double getSpawnY() {
		return spawnY;
	}

	public float getSpawnYaw() {
		return spawnYaw;
	}

	public double getSpawnZ() {
		return spawnZ;
	}

	public int getUsedClaims() {
		return usedClaims;
	}

	public String getWelcome() {
		return welcome;
	}
	
	public String getWorld() {
		return world;
	}

	public boolean isBanned(String player) {
		String s = banList.get(player);
		if(s == null)
			return false;
		else
			return true;
	}
	
	public boolean isOutsiderBuild() {
		return outsiderBuild;
	}
	
	public boolean isOutsiderDestroy() {
		return outsiderDestroy;
	}

	public boolean isOutsiderSwitch() {
		return outsiderSwitch;
	}
	
	public Plot isPlotIntersect(int xmin, int zmin, int xmax, int zmax) {
		TreeProcedure tproc = new TreeProcedure();
		plotTree.intersects(new Rectangle(xmin, zmin, xmax, zmax), tproc);
		for(int i : tproc.getId()) {
			Plot plot = plotMap.get(i);
			return plot;
		}
		return null;
	}

	public boolean isResidentBuild() {
		return residentBuild;
	}

	public boolean isResidentDestroy() {
		return residentDestroy;
	}

	public boolean isResidentSwitch() {
		return residentSwitch;
	}
	public boolean isSnow() {
		return snow;
	}
	public void removePlot(Plot plot) {
		plotMap.remove(plot.getId());
		plotTree.delete(new Rectangle(plot.getXmin(), plot.getZmin(), 
				plot.getXmax(), plot.getZmax()), plot.getId());
	}
	
	public void removeRank(String rank) {
		rankMap.remove(rank);
	}
	
	public void rename(String newName) {
		name = newName;
		
		final String tempName = newName;
		plotMap.forEachEntry(new TIntObjectProcedure<Plot>() {
			
			public boolean execute(int i, Plot plot) {
				if(plot.getOwnerName().equals(plot.getCityName()))
					plot.setOwnerName(tempName);
				plot.setCityName(tempName);
				return true;
			}
		});
	}
	
	public void setBaseClaims(int baseClaims) {
		this.baseClaims = baseClaims;
	}
	
	public void setBonusClaims(int bonusClaims) {
		this.bonusClaims = bonusClaims;
	}
	
	public void setFounded(String founded) {
		this.founded = founded;
	}
	
	public void setMayor(String mayor) {
		this.mayor = mayor;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOutsiderBuild(boolean outsiderBuild) {
		this.outsiderBuild = outsiderBuild;
	}
	
	public void setOutsiderDestroy(boolean outsiderDestroy) {
		this.outsiderDestroy = outsiderDestroy;
	}
	
	public void setOutsiderSwitch(boolean outsiderSwitch) {
		this.outsiderSwitch = outsiderSwitch;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public void setResidentBuild(boolean residentBuild) {
		this.residentBuild = residentBuild;
	}
	
	public void setResidentDestroy(boolean residentDestroy) {
		this.residentDestroy = residentDestroy;
	}
	
	public void setResidentSwitch(boolean residentSwitch) {
		this.residentSwitch = residentSwitch;
	}
	
	public void setSnow(boolean snow) {
		this.snow = snow;
	}
	
	public void setSpawnPitch(float spawnPitch) {
		this.spawnPitch = spawnPitch;
	}
	
	public void setSpawnX(double spawnX) {
		this.spawnX = spawnX;
	}
	
	public void setSpawnY(double spawnY) {
		this.spawnY = spawnY;
	}
	
	public void setSpawnYaw(float spawnYaw) {
		this.spawnYaw = spawnYaw;
	}
	
	public void setSpawnZ(double spawnZ) {
		this.spawnZ = spawnZ;
	}
	
	public void setUsedClaims(int usedClaims) {
		this.usedClaims = usedClaims;
	}
	
	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}
	
	public void unban(String player) {
		banList.remove(player);
	}
}
