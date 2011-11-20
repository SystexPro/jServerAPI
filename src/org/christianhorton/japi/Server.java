package org.christianhorton.japi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.christianhorton.japi.user.Entity;

public class Server {

	private String ip, username, password, servername, serverdesc;
	private int port,serverid;
	private StringBuffer sendQ = new StringBuffer();
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out; 
	private boolean clientIntroduced = false;
	private String[] entitys = null;
	private HashMap<Entity, String> map = new HashMap<Entity, String>();
	public HashMap<String, String> channels = new HashMap<String, String>();

	
	/**
	 * Server Constructer
	 * @param ip
	 * @param port
	 * @param username
	 * @param password
	 * @param servername
	 * @param serverdesc
	 * @param serverid
	 */
	public Server(String ip, int port, String username, String password, String servername, String serverdesc, int serverid) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.servername = servername;
		this.serverdesc = serverdesc;
		this.port = port;
		this.serverid = serverid;
		socket = null;
	}
	
	public void connect() throws UnknownHostException, IOException {
		if(socket == null) {
			System.out.println("Connection to " + ip + ":" + port + " with the Username: " + username + " and Password: " + password);
			socket = new Socket(this.ip, this.port);
			if(socket.isConnected()) {
				//ok were connected
				System.out.println("Connected to " + ip + "!");
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				//lets send them a handshake
				handshake();
			} else {
				System.out.println("Failed to connect to " + ip);
			}
		} else {
			System.out.println("Why is socket not null?!?!?");
		}
	}
	
	/**
	 * Handle Incoming Data
	 * @param line
	 * @throws Exception
	 */
	private void handleData(String line) throws Exception {
		String[] tokens = line.split("\\s");
		if (tokens[0].equals("PING")) {
			send("PONG " + Utils.assembleSlice(tokens, 1));
		}
		else if (tokens[0].equals("NETINFO")) {
			sendSendQ();
			if (!this.clientIntroduced) {
				putEntitysIntoServer();
				this.clientIntroduced = true;
			}
//			send(":BNC-Admin JOIN #bnc");
//			send(":`status JOIN #services");
//			send(":`status PRIVMSG Christian :Welcome");
			putEntitysInChannels();
		} 
	}
	
	
	
	private void putEntitysIntoServer() {
		 Iterator it = map.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        send(pairs.getValue().toString());
		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
	}
	
	private void putEntitysInChannels() {
		 Iterator<?> it = channels.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = ((Map.Entry)it.next());
		        send(pairs.getValue().toString());
		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
	}

	private void readInput() throws Exception {
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println("<- " + line);
			handleData(line);
		}
	}
	
	
	/**
	 * Sends a Handshake to the server, attempting to accept the connection
	 */
	private void handshake() {
		send("PROTOCTL NICKv2");
		send("PASS " + this.password);
		send("SERVER " + this.servername + " " + serverid + " :" + this.serverdesc);
		sendSendQ();
		try {
			readInput();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Send data to the Server
	 * @param data
	 */
	public void send(String data) {
		if (socket.isConnected()) {
			this.out.println(data);
			String[] dataSplit = data.split("\n");
			for (String dataItem : dataSplit) {
				System.out.println("-> " + dataItem);
			}
		} else {
			if (this.sendQ.length() + data.length() >= 511) {
				this.sendSendQ();
			}
			this.sendQ.append(data + "\n");
		}
	}

	/**
	 * Send the sendQ to the Server
	 */
	private void sendSendQ() {
		this.out.println(this.sendQ.toString());
		String[] dataSplit = this.sendQ.toString().split("\n");
		for (String dataItem : dataSplit) {
			System.out.println("-> " + dataItem);
		}
		this.sendQ = new StringBuffer();
	}
	
	/**
	 * Add a Entity to the Server
	 * @param entity
	 */
	public void addEntityToServer(Entity entity) {
		System.out.println("Created Entity: " + entity.getNick());
		String layout = "NICK " + entity.getNick() + " 1 " + System.currentTimeMillis() / 1000 + " " + entity.getHostPrefix() + " " + entity.getHostSuffix()+ " " + this.servername + " 0" + " " + entity.getModes() + " * :" + entity.getNick()+"";
		map.put(entity, layout);
		//send(layout);
	}
	
	/**
	 * Returns the socket connected to the server
	 * @return
	 */
	public Socket getSocket() {
		return socket;
	}
}
