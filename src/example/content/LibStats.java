package example.content;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class LibStats {
    public static Stat
            status, weather, drillTier;

    public static void load() {
        status = new Stat("status", StatCat.function);
        weather = new Stat("weather", StatCat.crafting);
        drillTier = new Stat("drillTier", StatCat.crafting);
    }
}