package network;

import misc.Global;

public class Player {

    private final String name;
    private int id;
    private boolean isReady;
    private int score;
    private int health;
    private boolean isDeath;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.isReady = false;
        this.score = Global.DEFAULT_WORD_COUNT;
        this.health = Global.DEFAULT_HEALTH;
        this.isDeath = false;
    }
    public Player(String name) {
        this(name, -1);
    }

    public String getName() {return this.name;}
    public int getScore() {return this.score;}
    public int getId() {return this.id;}
    public boolean isReady() {return this.isReady;}
    public int getHealth() {return this.health;}
    public boolean isDeath() {return this.isDeath;}

    public void setId(int id) {this.id = id;}
    public void setReady(boolean ready) {this.isReady = ready;}
    public void setScore(int score) {this.score = score;}
    public void setHealth(int health) {this.health = health;}
    public void kill() {this.isDeath = true;}

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