package client;

import packets.AddConnectionPacket;
import packets.RemoveConnectionPacket;

import java.util.HashMap;

public class EventListener {

	private static final HashMap<Integer,Connection> packets = new HashMap<Integer,Connection>();
	
	public void received(Object p) {
		if (p instanceof AddConnectionPacket) {
			AddConnectionPacket packet = (AddConnectionPacket) p;
			packets.put(packet.getId(), new Connection(packet.getId()));
			System.out.println(packet.getId() + " has connected");
		} else if (p instanceof RemoveConnectionPacket) {
			RemoveConnectionPacket packet = (RemoveConnectionPacket) p;
			System.out.println("Connection: " + packet.getId() + " has disconnected");
			packets.remove(packet.getId());
		}
	}

}