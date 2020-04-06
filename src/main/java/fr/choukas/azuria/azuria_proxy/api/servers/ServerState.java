package fr.choukas.azuria.azuria_proxy.api.servers;

public enum ServerState {

    STARTING(0),
    OPEN(1),
    STOPPING(2),
    CLOSED(3);

    private int id;

    ServerState(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
