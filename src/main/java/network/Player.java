package network;

public class Player {

    private final String name;
    private int id;
    private int score;
    private boolean isReady;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.score = 0;
        this.isReady = false;
    }
    public Player(String name) {
        this(name, -1);
    }

    public String getName() {return this.name;}
    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}
    public boolean isReady() {return this.isReady;}
    public void setReady(boolean ready) {this.isReady = ready;}

    @Override
    public String toString() {
        return name + " " + String.valueOf(id);
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Player)) return false;
        Player p = (Player) arg0;
        return this.name.equals(p.getName()) && this.id == p.getId();
    }

}