package example.content;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class WeathersStat {
    public static Stat
            weather;

    public static void load() {
        weather = new Stat("weather", StatCat.crafting);
    }
}