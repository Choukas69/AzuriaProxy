package fr.choukas.azuria.azuria_proxy.api.utils;

public abstract class BungeeRunnable extends Thread implements Runnable {

    public void cancel() {
        super.interrupt();
    }
}
