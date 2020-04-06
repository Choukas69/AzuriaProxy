package fr.choukas.azuria.azuria_proxy.api.utils;

import fr.choukas.azuria.azuria_common.utils.AbstractSchedulerManager;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;

import java.util.concurrent.TimeUnit;

public class SchedulerManager extends AbstractSchedulerManager {

    private AzuriaProxy proxy;

    public SchedulerManager(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void runAsync(Runnable task) {
        proxy.getProxy().getScheduler().runAsync(proxy, task);
    }

    @Override
    public void runTaskLater(Runnable task, long delay, TimeUnit timeUnit) {
        proxy.getProxy().getScheduler().schedule(proxy, new BungeeRunnable() {
            @Override
            public void run() {
                task.run();
                this.cancel();
            }
        }, delay, timeUnit);
    }

    @Override
    public void runRepeatingTask(Runnable task, long delay, long period, TimeUnit timeUnit) {
        proxy.getProxy().getScheduler().schedule(proxy, task, delay, period, timeUnit);
    }
}
