BattleZones - PvP zones for CraftBukkit
=======================================

BattleZones allows for the creation of simple - open PvP arenas where players may enter and leave as they wish, without having to set up lobbies and matches.

BattleZones only enables PvP when a player is within an enabled zone. When the player leaves the zone, they are no longer able to PvP. This plugin offers a very simple way of providing any number of PvP areas to players across multiple worlds.

Official Site: http://dev.bukkit.org/server-mods/battlezones/


Commands
--------
- /bz help - Displays BattleZones help dialog.
- /bz add [worldName] [zoneName] - Creates a new PvP zone.
- /bz remove [worldName] [zoneName] - Removes an existing PvP zone.
- /bz enable [worldName] [zoneName] - Enables combat within an existing PvP zone.
- /bz disable [worldName] [zoneName] - Disables combat within an existing PvP zone.
- /bz list - Lists all PvP zones and their active states.


Permissions
-----------
Permissions for BattleZones commands to be called.
- `battlezones.bz`
Permissions for their respective commands.
- `battlezones.help` 
- `battlezones.add`
- `battlezones.remove`
- `battlezones.enable`
- `battlezones.disable`
- `battlezones.list`
Permissions for allowing PvP interaction. Whether inside a zone or not.
- `battlezones.pvp`