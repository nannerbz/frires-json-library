package example.content;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class StatusStat {
    public static Stat
            status;

    public static void load() {
        status = new Stat("status", StatCat.function);
    }
}