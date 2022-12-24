package server;

import packets.AddConnectionPacket;
import packets.RemoveConnectionPacket;

public class EventListener {
	
	public void received(Object p,Connection connection) {
		if (p instanceof AddConnectionPacket) {
			AddConnectionPacket packet = (AddConnectionPacket) p;
			packet.setId(connection.getId());
			for (int i=0; i<Server.connections.size(); i++) {
				Connection c = Server.connections.get(i);
				if (c != connection)
					c.sendObject(packet);
			}
			
		} else if (p instanceof RemoveConnectionPacket) {
			RemoveConnectionPacket packet = (RemoveConnectionPacket) p;
			System.out.println("Connection: " + packet.getId() + " has disconnected");
			Server.connections.get(packet.getId()).close();
			Server.connections.remove(packet.getId());
		}
	}

}