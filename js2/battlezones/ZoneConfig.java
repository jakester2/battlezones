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

/**
 *
 * @author Jacob Tyo
 * @version 12/08/2011
 */
public class ZoneConfig {
    private BattleZones plugin;
    private File configFile;
    private FileConfiguration config;
    
    public ZoneConfig(BattleZones plugin)
    {
        init(plugin);
        defaults();
    }

    private void init(BattleZones plugin)
    {
        this.plugin                 = plugin;
        configFile                  = new File(plugin.getDataFolder(), "zones.yml");
        config                      = YamlConfiguration.loadConfiguration(configFile);
    }
    
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
    
    public void reloadZoneSet()
    {
        boolean zonesExist = false;
        for (Iterator<String> it = plugin.nestedZones.iterator(); it.hasNext();) {
            String[] string = it.next().split("\\.");
            if (getConfig().isSet("zones." + string[0] + "." + string[1])) zonesExist = true;
        }
        plugin.isZonesSet = zonesExist;
    }
    
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
        return true;
    }
    
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
