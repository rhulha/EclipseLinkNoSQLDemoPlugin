package net.raysforge.eclipselink.hashmap;

import javax.resource.cci.ConnectionSpec;

public class DemoJCAConnectionSpec implements ConnectionSpec {

	// unused but left in for demo purposes
	private String serverName;
	private String serverAddr;
	private String db;
	private String user;
	private String password;

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerAddress() {
		return serverAddr;
	}

	public void setServerAddress(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public void setDB(String db) {
		this.db = db;
	}
	
	public String getDB() {
		return db;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
