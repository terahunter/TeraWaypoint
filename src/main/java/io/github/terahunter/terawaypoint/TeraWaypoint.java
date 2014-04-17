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
            getLogger().severe("Could not setup SQLite: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        db.close();
    }


    private static final String[][] tables = new String[][] {
            {"waypoints", "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE, " +
                    "dimension INTEGER, " +
                    "x INTEGER, y INTEGER, z INTEGER"},
            {"teleporters", "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE, " +
                    "waypoint INTEGER, " +
                    "FOREIGN KEY(waypoint) REFERENCES waypoints(id)"}
    };

    private void setupDatabase() throws SQLException {
        db = new SQLite(Logger.getLogger("Minecraft"), "[TeraWaypoint:SQLite]",
                this.getDataFolder().getAbsolutePath(), "TeraWaypoint");
        db.open();

        for (String[] table: tables) {
            setupTable(table[0], table[1]);
        }
    }

    private void setupTable(String table, String columns) throws SQLException {
        if (!db.isTable(table)) {
            db.query("CREATE TABLE " + table + " (" + columns + ");");
        }
    }
}
