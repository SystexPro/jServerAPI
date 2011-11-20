package org.christianhorton.japi.user;

import java.util.HashMap;

import org.christianhorton.japi.Server;

public class User extends Entity {

	private String username;
	private String realname;
	private String modes;
	private String suffix;
	private String prefix;
	private Server server;
	private HashMap<String, String> channels = new HashMap<String, String>();


	public User(String username, String realname, String prefix, String suffix, String modes, Server server) {
		this.username = username;
		this.realname = realname;
		this.modes = modes;
		this.suffix = suffix;
		this.prefix = prefix;
		this.server = server;
	}

	@Override
	public String getNick() {
		return username;
	}
	
	public String getRealName() {
		return realname;
	}

	@Override
	public String getHostPrefix() {
		return prefix;
	}

	@Override
	public String getHostSuffix() {
		return suffix;
	}

	@Override
	public String getModes() {
		return this.modes;
	}

	@Override
	public void joinChannel(String channel) {
		if(!server.channels.containsKey(channel)) {
			String layout = ":" + getNick() + " JOIN " + channel;
			server.channels.put(getNick(), layout);
		} else {
			System.out.println(getNick() + " in already in " + channel);
		}
	}


}
