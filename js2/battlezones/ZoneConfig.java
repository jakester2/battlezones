/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import javax.vecmath.Point3i;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * This class handles zone file configurations.
 * 
 * @author Jacob Tyo
 * @version 12/10/2011
 */
public class ZoneConfig {
    private BattleZones plugin;
    private File configFile;
    private FileConfiguration config;
    
    /**
     * Create a new instance of {@code ZoneConfig}.
     * @param plugin Parent {@link BattleZones} instance.
     */
    public ZoneConfig(BattleZones plugin)
    {
        init(plugin);
        defaults();
    }

    /**
     * Initialize all variables.
     */
    private void init(BattleZones plugin)
    {
        this.plugin                 = plugin;
        configFile                  = new File(plugin.getDataFolder(), "zones.yml");
        config                      = YamlConfiguration.loadConfiguration(configFile);
    }
    
    /**
     * Set default values if no configuration exists.
     */
    private void defaults()
    {
        if (!getConfig().contains("zones")) {
            getConfig().createSection("zones");
            try {
                saveZones();
            } catch (IOException ex) {
                BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + " Error: Cannot save zone config. Exception: " + ex));
            }
            reloadZoneSet();
        }
    }
    
    /**
     * Reload the isZoneSet variable in the BattleZone object.
     */
    public void reloadZoneSet()
    {
        boolean zonesExist = false;
        for (Iterator<String> it = plugin.nestedZones.iterator(); it.hasNext();) {
            String[] string = it.next().split("\\.");
            if (getConfig().isSet("zones." + string[0] + "." + string[1])) zonesExist = true;
        }
        plugin.isZonesSet = zonesExist;
    }
    
    /**
     * Adds a new zone to the zone index then saves it to the zone configuration 
     * file.
     * @param sender Source of the command
     * @param zoneName Name of the new zone
     * @param worldName Name of the world to add the zone
     * @param initiallyEnabled Is the zone enabled on creation
     * @param point1 South-East point of the zone
     * @param point2 North-West point of the zone
     * @return {@code true} if the zone was successfully created, else {@code false}
     */
    public boolean addZone(CommandSender sender, String zoneName, String worldName, boolean initiallyEnabled, Point3i point1, Point3i point2)
    {
        String root = "zones." + worldName + "." + zoneName + ".";
        getConfig().set(root + "enabled", initiallyEnabled);
        getConfig().set(root + "x1", point1.x);
        getConfig().set(root + "y1", point1.y);
        getConfig().set(root + "z1", point1.z);
        getConfig().set(root + "x2", point2.x);
        getConfig().set(root + "y2", point2.y);
        getConfig().set(root + "z2", point2.z);
        try {
            saveZones();
        } catch (IOException ex) {
            BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + " Error: Cannot save zone config. Exception: " + ex));
            Message.send(sender, Message.LEVEL_ERROR, plugin.getDescription().getName() + " encountered an error. Please check the log.");
            return false;
        }
        plugin.indexZones(false);
        reloadZoneSet();
        BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + "Add: '" + zoneName + "' registered!"));
        Message.send(sender, Message.LEVEL_SUCCESS, "Add: '" + zoneName + "' successfully registered!");
        // Check if player is inside the zone that was created. If so, set PvP ON for the sender.
        if (plugin.movementListener.isIntersecting(((Player) sender).getLocation(),
                new Point3i(config.getInt(root + "x1"), config.getInt(root + "y1"), config.getInt(root + "z1")), 
                new Point3i(config.getInt(root + "x2"), config.getInt(root + "y2"), config.getInt(root + "z2"))))
        {
            plugin.pvpHandler.setPlayerPVP(((Player) sender), true);
        }
        return true;
    }
    
    /**
     * Removes a zone from the index then saves the zone configuration file.
     * @param sender Source of the command
     * @param zoneName Name of the zone to remove
     * @param worldName Name of the world to remove the zone
     * @return {@code true} if the zone was successfully removed, else {@code false}
     */
    public boolean removeZone(CommandSender sender, String zoneName, String worldName)
    {
        if (sender.getServer().getWorld(worldName) == null) {
            Message.send(sender, Message.LEVEL_ERROR, "The world '" + worldName + "' does not exist...");
            return false;
        }
        String root = "zones." + worldName + "." + zoneName + ".";
        if (getConfig().contains(root)) getConfig().set(root, null);
        else {
            if (sender != null) Message.send(sender, Message.LEVEL_ERROR, "The zone '" + zoneName + "' does not exist.");
            return false;
        }
        // Check if player was inside the zone that was remove and not in another zone. If so, set PvP OFF for the sender.
        if (plugin.movementListener.isIntersecting(((Player) sender).getLocation(),
                new Point3i(config.getInt(root + "x1"), config.getInt(root + "y1"), config.getInt(root + "z1")), 
                new Point3i(config.getInt(root + "x2"), config.getInt(root + "y2"), config.getInt(root + "z2"))))
        {
            plugin.pvpHandler.setPlayerPVP(((Player) sender), false);
        }
        try {
            saveZones();
        } catch (IOException ex) {
            BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + " Error: Cannot save zone config. Exception: " + ex));
            if (sender != null) Message.send(sender, Message.LEVEL_ERROR, plugin.getDescription().getName() + " encountered an error. Please check the log.");
            return false;
        }
        plugin.indexZones(false);
        reloadZoneSet();
        BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + "Remove: '" + zoneName + "' removed!"));
        if (sender != null) Message.send(sender, Message.LEVEL_SUCCESS, "Remove: '" + zoneName + "' successfully removed!");
        return true;
    }
    
    /**
     * Save the configuration file.
     * @throws IOException Throws if there is a problem saving the file.
     */
    public void saveZones() throws IOException
    {
        getConfig().options().header("Do NOT modify this document unless you know what you are doing.");
        getConfig().save(configFile);
    }

    /**
     * Returns the FileConfiguration Object.
     * @return FileConfiguration Object.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Returns whether there are any registered zones in the index or not.
     * @return {@code true} if no zones exist, else {@code false}
     */
    public boolean isEmpty()
    {
        boolean isEmpty = true;
        for (Iterator<String> it = plugin.nestedZones.iterator(); it.hasNext();) {
            String[] string = it.next().split("\\.");
            if (getConfig().isSet("zones." + string[0] + "." + string[1])) isEmpty = false;
        }
        return isEmpty;
    }
    
}
