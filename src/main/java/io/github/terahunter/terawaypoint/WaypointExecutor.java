package io.github.terahunter.terawaypoint;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class WaypointExecutor implements CommandExecutor {
    private TeraWaypoint plugin = TeraWaypoint.getPlugin(TeraWaypoint.class);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) { return false; }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            try {
                ResultSet result = plugin.db().query("SELECT * FROM waypoints WHERE name='" + args[0] + "';");
                // Specified name should not exist (SQL query should return empty)
                boolean nameExists = result.getFetchSize() > 0;
                result.close();

                if (nameExists) {
                    sender.sendMessage("That name already exists!");
                } else {
                    // Register waypoint using player's position
                    Location position = player.getLocation();
                    plugin.db().query(String.format(
                            "INSERT INTO waypoints (name,x,y,z) VALUES ('%s',%f,%f,%f);",
                            args[0], position.getX(), position.getY(), position.getZ()
                    ));
                }

            } catch (SQLException e) {
                plugin.getLogger().warning("SQLException: " + e.getMessage());
            }

        } else {  // sender not instanceof Player
            sender.sendMessage("Only a player can use that!");
        }

        return true;
    }
}
