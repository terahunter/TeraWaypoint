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
        if (args.length == 1) {
            Player player = (Player)sender;

            try {
                ResultSet result = plugin.db().query("SELECT * FROM waypoints WHERE name='" + args[0] + "';");
                if (result.getFetchSize() > 0) {
                    result.close();
                    sender.sendMessage("That name already exists!");
                    return false;
                }
                result.close();

                Location position = player.getLocation();
                plugin.db().query(String.format(
                        "INSERT INTO waypoints (name,x,y,z) VALUES ('%s',%f,%f,%f);",
                        args[0], position.getX(), position.getY(), position.getZ()
                ));
            } catch (SQLException e) {
                plugin.getLogger().warning("SQLException: " + e.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }
}
