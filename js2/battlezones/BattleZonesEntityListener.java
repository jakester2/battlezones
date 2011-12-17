/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

/**
 * Listener that fires when a player interacts with another entity. This class is
 * designed to control PvP interaction between two {@link Players}.
 * 
 * @author Jacob Tyo
 * @version 12/10/2011
 */
public class BattleZonesEntityListener extends EntityListener {
    public BattleZones plugin;
    public FileConfiguration zoneConfig;

    /**
     * Create a new instance of {@code BattleZonesEntityListener}.
     * @param plugin Parent {@link BattleZones} instance.
     */
    public BattleZonesEntityListener(BattleZones plugin)
    {
        init(plugin);
    }

    /**
     * Initialize all variables.
     */
    private void init(BattleZones plugin) {
        this.plugin                 = plugin;
        zoneConfig                  = plugin.zoneConfig.getConfig();
    }

    /**
     * Called when an entity is damaged. Detects whether both the attacker and
     * attackee are able to PvP. If not, cancel the interaction.
     * @param e Relevant event details
     */
    @Override
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;
        if (e instanceof EntityDamageByEntityEvent)
        {
            Player attacker = ((Player) ((EntityDamageByEntityEvent) e).getDamager()).getPlayer();  BattleZones.LOG.log(Level.CONFIG, ("Attacker: " + attacker.getName()));
            Player attackee = ((Player) ((EntityDamageByEntityEvent) e).getEntity()).getPlayer();   BattleZones.LOG.log(Level.CONFIG, ("Attackee: " + attackee.getName()));
            boolean isAttackerPVP = plugin.pvpHandler.isPlayerPVPEnabled(attacker);                 BattleZones.LOG.log(Level.CONFIG, ("isAttackerPvP: " + isAttackerPVP));
            boolean isAttackeePVP = plugin.pvpHandler.isPlayerPVPEnabled(attackee);                 BattleZones.LOG.log(Level.CONFIG, ("isAttackeePvP: " + isAttackeePVP));
            if (!isAttackerPVP || isAttackeePVP)
            {
                e.setCancelled(true);
                Message.send(attacker, Message.LEVEL_ERROR, "You are not in a PvP zone.");
            }
            else if (isAttackerPVP || !isAttackeePVP)
            {
                e.setCancelled(true);
                Message.send(attacker, Message.LEVEL_ERROR, "You cannot attack this person.");
            }
            Message.send(attacker, Message.LEVEL_SUCCESS, "Successful attack!");
        }
    }
    
}
