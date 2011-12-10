/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.util.Iterator;
import javax.vecmath.Point3i;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * 
 * 
 * @author Jacob Tyo
 * @version 12/08/2011
 */
public class BattleZonesMovementListener extends PlayerListener {
    public BattleZones plugin;
    public FileConfiguration config;
    
    public BattleZonesMovementListener(BattleZones plugin)
    {
        init(plugin);
    }

    private void init(BattleZones plugin)
    {
        this.plugin                     = plugin;
        config                          = plugin.zoneConfig.getConfig();
    }
    
    public boolean isIntersecting(Location pLocation, Point3i zonePos1, Point3i zonePos2)
    {
        if (pLocation.getBlockX() >= zonePos1.x && pLocation.getBlockX() <= zonePos2.x &&
            pLocation.getBlockY() <= zonePos1.y && pLocation.getBlockY() >= zonePos2.y &&
            pLocation.getBlockZ() >= zonePos1.z && pLocation.getBlockZ() <= zonePos2.z) return true;
        return false;
    }
    
    public boolean isSameBlock(Location to, Location from)
    {
        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) return true;
        return false;
    }

    
    
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        super.onPlayerMove(event);
        if (!plugin.isZonesSet) return;
        if (isSameBlock(event.getTo(), event.getFrom().getBlock().getLocation())) return;
        for (Iterator<String> it = plugin.nestedZones.iterator(); it.hasNext();) {
            String[] string = it.next().split("\\.");
            if (string[0].equals(event.getPlayer().getWorld().getName()) && config.getBoolean("zones." + string[0] + "." + string[1] + ".enabled")) {
                String root = "zones." + string[0] + "." + string[1] + ".";
                if (isIntersecting(event.getTo(), 
                        new Point3i(config.getInt(root + "x1"), config.getInt(root + "y1"), config.getInt(root + "z1")), 
                        new Point3i(config.getInt(root + "x2"), config.getInt(root + "y2"), config.getInt(root + "z2"))) &&
                    !isIntersecting(event.getFrom(), 
                        new Point3i(config.getInt(root + "x1"), config.getInt(root + "y1"), config.getInt(root + "z1")), 
                        new Point3i(config.getInt(root + "x2"), config.getInt(root + "y2"), config.getInt(root + "z2"))))
                {
                    Message.send(event.getPlayer(), "Entered: " + string[1] + ". PvP " + ChatColor.GREEN + "ON!");
                }
                else if (isIntersecting(event.getFrom(), 
                        new Point3i(config.getInt(root + "x1"), config.getInt(root + "y1"), config.getInt(root + "z1")), 
                        new Point3i(config.getInt(root + "x2"), config.getInt(root + "y2"), config.getInt(root + "z2"))) &&
                    !isIntersecting(event.getTo(), 
                        new Point3i(config.getInt(root + "x1"), config.getInt(root + "y1"), config.getInt(root + "z1")), 
                        new Point3i(config.getInt(root + "x2"), config.getInt(root + "y2"), config.getInt(root + "z2"))))
                {
                    Message.send(event.getPlayer(), "Leaving: " + string[1] + ". PvP " + ChatColor.RED + "OFF.");
                }
            }
        }
    }
    
}
