/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.util.logging.Level;
import javax.vecmath.Point3i;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * 
 * 
 * @author Jacob Tyo
 * @version 12/08/2011
 */
public class CuboidHandler extends PlayerListener {
    public BattleZones plugin;
    public boolean isWaiting;
    public CommandSender sender;
    public int requesting;
    public Point3i pos1;
    public Point3i pos2;
    public String zoneName;
    public String worldName;
    
    public CuboidHandler(BattleZones plugin, CommandSender sender, String zoneName, String worldName)
    {
        init(plugin, sender, zoneName, worldName);
        make();
    }
    
    private void init(BattleZones plugin, CommandSender sender, String zoneName, String worldName) {
        this.plugin                     = plugin;
        this.sender                     = sender;
        this.zoneName                   = zoneName;
        this.worldName                  = worldName;
    }
    
    private void make() {
        plugin.manager.registerEvent(Event.Type.PLAYER_INTERACT, this, Event.Priority.Low, plugin);
    }
    
    public void requestCuboid()
    {
        if (sender.getServer().getWorld(worldName) == null) {
            Message.send(sender, Message.LEVEL_ERROR, "The world '" + worldName + "' does not exist...");
            return;
        }
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message.send(sender, Message.LEVEL_SPECIAL, "Selecting the bounds for " + zoneName);
                requestBlockPos(1);
                requestBlockPos(2);
                if (pos1.x > pos2.x) {
                    int buffer = pos2.x;
                    pos2.x = pos1.x;
                    pos1.x = buffer;
                }
                if (pos1.y < pos2.y) {
                    int buffer = pos2.y;
                    pos2.y = pos1.y;
                    pos1.y = buffer;
                }
                if (pos1.z > pos2.z) {
                    int buffer = pos2.z;
                    pos2.z = pos1.z;
                    pos1.z = buffer;
                }
                plugin.zoneConfig.addZone(sender, zoneName, worldName, true, pos1, pos2);
                ((Player) sender).getWorld().getBlockAt(pos1.x, pos1.y, pos1.z).setTypeId(57);
                ((Player) sender).getWorld().getBlockAt(pos2.x, pos2.y, pos2.z).setTypeId(41);
            }
        });
        requestThread.start();
    }
    
    public void requestBlockPos(int requesting)
    {
        isWaiting = true;
        this.requesting = requesting;
        Message.send(sender, Message.LEVEL_SPECIAL, "Punch a block to set position " + this.requesting + "...");
        while (isWaiting) {
            switch (requesting) {
                case 1: if (pos1 != null) isWaiting = false; break;
                case 2: if (pos2 != null) isWaiting = false; break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                BattleZones.LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        super.onPlayerInteract(event);
        if (!sender.getName().equals(event.getPlayer().getName())) return;
        if (isWaiting) {
            Block block = event.getClickedBlock();
            switch (requesting) {
                case 1:
                    pos1 = new Point3i(block.getX(), block.getY(), block.getZ());
                    Message.send(event.getPlayer(), Message.LEVEL_SUCCESS, "Position 1 set!");
                    break;
                case 2:
                    pos2 = new Point3i(block.getX(), block.getY(), block.getZ());
                    Message.send(event.getPlayer(), Message.LEVEL_SUCCESS, "Position 2 set!");
                    break;
            }
        }
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        super.onPlayerCommandPreprocess(event);
        if (sender.getName().equals(event.getPlayer().getName()) && isWaiting == true) {
            isWaiting = false;
            Message.send(sender, Message.LEVEL_ERROR, "Aborted...");
        }
    }
    
    

}
