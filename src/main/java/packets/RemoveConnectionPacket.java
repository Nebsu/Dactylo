package packets;

import java.io.Serializable;

public class RemoveConnectionPacket implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;

	public int getId() {return this.id;} 

}