package io.github.terahunter.terawaypoint;

import lib.PatPeter.SQLibrary.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 *
 */
public class TeraWaypoint extends JavaPlugin {
    private SQLite db;

    @Override
    public void onEnable() {
        try {
            setupDatabase();
        } catch (SQLException e) {
            // SQLite connection is required
            getLogger().severe("Could not setup SQLite: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getCommand("waypoint").setExecutor(new WaypointExecutor());
    }

    @Override
    public void onDisable() {
        db.close();
    }

    SQLite db() {
        return this.db;
    }


    // Database schema
    private static final String[][] tables = new String[][] {
            {"waypoints", "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE, " +
                    "dimension INTEGER, " +
                    "x DOUBLE, y DOUBLE, z DOUBLE"},
            {"teleporters", "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE, " +
                    "waypoint INTEGER, " +
                    "FOREIGN KEY(waypoint) REFERENCES waypoints(id)"}
    };

    /**
     * Open database and ensure the specified tables exist
     */
    private void setupDatabase() throws SQLException {
        db = new SQLite(Logger.getLogger("Minecraft"), "[TeraWaypoint:SQLite]",
                this.getDataFolder().getAbsolutePath(), "TeraWaypoint");
        db.open();

        for (String[] table: tables) {
            setupTable(table[0], table[1]);
        }
    }

    /**
     * Check if the specified table exists. If not, create it.
     */
    private void setupTable(String table, String columns) throws SQLException {
        if (!db.isTable(table)) {
            db.query("CREATE TABLE " + table + " (" + columns + ");");
        }
    }
}
