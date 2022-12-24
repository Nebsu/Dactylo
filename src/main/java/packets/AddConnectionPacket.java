package packets;

import java.io.Serializable;

public class AddConnectionPacket implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;

	public int getId() {return this.id;}
	public void setId(int id) {this.id = id;}

}