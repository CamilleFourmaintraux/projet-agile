package main.java;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Its a class which contains all obstacles of a map.
 */
public class MapSpawnConfig {
    /**
     * A list of all the objects and their stats (when they spawn, at what speed and where).
     */
    private ArrayList<ObstacleSpawn> spawns = new ArrayList<>();

    public MapSpawnConfig(ArrayList<ObstacleSpawn> spawns) {
        this.spawns = spawns;
    }

    public ArrayList<ObstacleSpawn> getSpawns() { return spawns; }
    
    /**
     * Reads the CSV file, and for each line it's an obstacle meant to spawn at a specific position and time.
     * The parameters of the CSV file are the following:
     * 1. name of the obstacle, which is the name of the CSV file of this particular obstacle.
     * 2. the speed at which the obstacle moves from the right to the left. It's an arbitrary number yet to be defined properly. (TODO)
     * 3. the y-shift from the top of the map (so the height of the obstacle)
     * 4. at what time the obstacle is going to spawn after the beginning of the level.
     * @param path The path to the CSV file.
     * @param delimiter The delimiter to use in the CSV file.
     * @return An instance of MapSpawnConfig.
     */
    public static MapSpawnConfig fromCSV(String path, String delimiter) {
        final ArrayList<ObstacleSpawn> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                Scanner scanner = new Scanner(line).useDelimiter(",");

                String name = scanner.next();
                double speed = Double.parseDouble(scanner.next());
                double y = Double.parseDouble(scanner.next());
                double delta = Double.parseDouble(scanner.next());
                
                ObstacleSpawn obstacle = new ObstacleSpawn(name, speed, y, delta);
                list.add(obstacle);
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MapSpawnConfig(list);
    }

    public static MapSpawnConfig fromCSV(String path) {
        return MapSpawnConfig.fromCSV(path, ",");
    }
}