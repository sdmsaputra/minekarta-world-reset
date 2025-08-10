package com.minekarta.worldreset;

import com.minekarta.worldreset.scheduler.SchedulerService;
import com.minekarta.worldreset.task.WorldResetTask;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MinekartaWorldReset extends JavaPlugin {

    private static final Logger logger = Logger.getLogger("MinekartaWorldReset");

    private String worldName;
    private String schedule;
    private boolean announce;
    private String announcementMessage;
    private int announcementTime;

    private MultiverseCore multiverseCore;
    private SchedulerService schedulerService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        if (!setupMultiverse()) {
            logger.severe("Multiverse-Core not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        schedulerService = new SchedulerService(this);
        schedulerService.start();
        getCommand("minekartaworldreset").setExecutor(new com.minekarta.worldreset.command.MWRCommand(this));
        logger.info("MinekartaWorldReset has been enabled!");
    }

    @Override
    public void onDisable() {
        schedulerService.stop();
        logger.info("MinekartaWorldReset has been disabled!");
    }

    private boolean setupMultiverse() {
        if (getServer().getPluginManager().getPlugin("Multiverse-Core") == null) {
            return false;
        }
        multiverseCore = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        return true;
    }

    public void triggerReset() {
        new WorldResetTask(this, multiverseCore).run();
    }

    public void loadConfig() {
        reloadConfig();
        worldName = getConfig().getString("world");
        schedule = getConfig().getString("schedule");
        announce = getConfig().getBoolean("announce");
        announcementMessage = getConfig().getString("announcement-message");
        announcementTime = getConfig().getInt("announcement-time");
        if (schedulerService != null) {
            schedulerService.reload();
        }
    }

    public SchedulerService getSchedulerService() {
        return schedulerService;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getSchedule() {
        return schedule;
    }

    public boolean isAnnounce() {
        return announce;
    }

    public String getAnnouncementMessage() {
        return announcementMessage;
    }

    public int getAnnouncementTime() {
        return announcementTime;
    }
}
