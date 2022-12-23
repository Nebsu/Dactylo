package client.main;

public class NetPlayer {
    
    private int id;
    private String name;

    public int getId() {return this.id;}
    public String getName() {return this.name;}

    public NetPlayer(int id, String name) {
        this.id = id;
        this.name = name;
    }

}