/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Jacob Tyo
 * @version 12/08/2011
 */
public class BattleZones extends JavaPlugin {
    public static final Logger LOG = Logger.getLogger("Minecraft");
    
    public ArrayList<String> nestedZones;
    public BattleZonesCommandExecutor executor;
    public BattleZonesMovementListener movementListener;
    public boolean isZonesSet;
    public PluginManager manager;
    public PrefConfig prefConfig;
    public PVPHandler pvpHandler;
    public ZoneConfig zoneConfig;
    
    @Override
    public void onDisable() {
        LOG.info((Message.getPrefix() + "Plugin Disabled!"));
    }

    @Override
    public void onEnable() {
        init();
        run();
    }

    private void init() {
        LOG.log(Level.INFO, (Message.getPrefix() + "Initializing..."));
        nestedZones                         = new ArrayList<String>();
        prefConfig                          = new PrefConfig(this);
        zoneConfig                          = new ZoneConfig(this);
        executor                            = new BattleZonesCommandExecutor(this);
        movementListener                    = new BattleZonesMovementListener(this);
        manager                             = this.getServer().getPluginManager();
        pvpHandler                          = new PVPHandler(this);
    }

    private void run() {
        getCommand("bz").setExecutor(executor);
        indexZones(true);
        zoneConfig.reloadZoneSet();
        manager.registerEvent(Event.Type.PLAYER_MOVE, movementListener, Event.Priority.Low, this);
        LOG.log(Level.INFO, (Message.getPrefix() + getDescription().getName() + " Enabled! Version: " + getDescription().getVersion()));
    }

    public void indexZones(boolean isStartup) {
        Set<String> rootSet = zoneConfig.getConfig().getConfigurationSection("zones").getKeys(false);
        if (rootSet != null) {
            LOG.log(Level.INFO, (Message.getPrefix() + (isStartup ? "Indexing" : "Reindexing") + " zones..."));
            nestedZones.clear();
            for (Iterator<String> it = rootSet.iterator(); it.hasNext();) {
                String worldName = it.next();
                Set<String> zoneSet = zoneConfig.getConfig().getConfigurationSection("zones." + worldName).getKeys(false);
                for (Iterator<String> zones = zoneSet.iterator(); zones.hasNext();) {
                    String zoneName = zones.next();
                    nestedZones.add(worldName + "." + zoneName);
                }
            }
        }
    }
    
}
