package com.minekarta.worldreset.task;

import com.minekarta.worldreset.MinekartaWorldReset;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.util.logging.Logger;

public class WorldResetTask implements Runnable {

    private static final Logger logger = Logger.getLogger("MinekartaWorldReset");

    private final MinekartaWorldReset plugin;
    private final MultiverseCore multiverseCore;

    public WorldResetTask(MinekartaWorldReset plugin, MultiverseCore multiverseCore) {
        this.plugin = plugin;
        this.multiverseCore = multiverseCore;
    }

    @Override
    public void run() {
        String worldName = plugin.getWorldName();
        MVWorldManager worldManager = multiverseCore.getMVWorldManager();

        if (!worldManager.isMVWorld(worldName)) {
            logger.warning("World '" + worldName + "' not found or not managed by Multiverse-Core.");
            return;
        }

        logger.info("Starting world reset for '" + worldName + "'...");

        // Unload the world
        if (worldManager.unloadWorld(worldName)) {
            logger.info("Successfully unloaded world '" + worldName + "'.");
        } else {
            logger.severe("Failed to unload world '" + worldName + "'.");
            return;
        }

        // Delete the world
        if (worldManager.deleteWorld(worldName)) {
            logger.info("Successfully deleted world '" + worldName + "'.");
        } else {
            logger.severe("Failed to delete world '" + worldName + "'.");
            // Even if deletion fails, we try to regenerate it.
        }

        // Create the world
        if (worldManager.addWorld(worldName, World.Environment.NORMAL, null, WorldType.NORMAL, true, null)) {
            logger.info("Successfully created world '" + worldName + "'.");
        } else {
            logger.severe("Failed to create world '" + worldName + "'.");
        }
    }
}
