package client.main;

import client.packets.AddConnectionPacket;

public class Main {

	public static void main(String[] args) {
		
		Client client = new Client("localhost",5000);
		client.connect();
		
		AddConnectionPacket packet = new AddConnectionPacket();
		client.sendObject(packet);
		
	}

}
