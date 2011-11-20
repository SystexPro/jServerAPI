package org.christianhorton.japi.user;

public abstract class Entity {

	public Entity() {}
	
	public abstract String getNick();//Get Nick
	public abstract String getHostPrefix();//Get Prefix
	public abstract String getHostSuffix();//Get Suffix
	public abstract String getRealName();
	public abstract String getModes();//Get Modes
	public abstract void joinChannel(String channel);
	
}
