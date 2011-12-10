/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import org.bukkit.entity.Player;

/**
 * 
 * 
 * @author Jacob Tyo
 * @version 12/08/2011
 */
public class PVPHandler {
    public BattleZones plugin;
    
    public PVPHandler(BattleZones plugin)
    {
        init(plugin);
    }
    
    private void init(BattleZones plugin) {
        this.plugin                     = plugin;
    }
    
    public boolean isGlobalPVPEnabled()
    {
        return false;
    }
    
    public void setGlobalPVP(boolean enabled)
    {
        
    }
    
    public boolean isPlayerPVPEnabled(Player player)
    {
        return false;
    }
    
    public boolean playerHasPVPPermissions(Player player)
    {
        return false;
    }
    
    public void setPlayerPVP(boolean enabled)
    {
        
    }

}
