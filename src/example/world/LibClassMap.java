package example.world;
import example.world.types.RadiusDrill;
import example.world.types.SelectableReconstructor;
import example.world.types.StatusFieldBlock;
import example.world.types.WeatherCrafter;

import static mindustry.mod.ClassMap.classes;

public class LibClassMap {
    public static void load() {
        classes.put("WeatherCrafter", WeatherCrafter.class);
        classes.put("WeatherCrafterBuild", WeatherCrafter.WeatherCrafterBuild.class);
        classes.put("StatusFieldBlock", StatusFieldBlock.class);
        classes.put("StatusFieldBlockBuild", StatusFieldBlock.StatusFieldBlockBuild.class);
        classes.put("SelectableReconstructor", SelectableReconstructor.class);
        classes.put("SelectableReconstructorBuild", SelectableReconstructor.SelectableReconstructorBuild.class);
        classes.put("RadiusDrill", RadiusDrill.class);
        classes.put("RadiusDrillBuild", RadiusDrill.RadiusDrillBuild.class);
    }
}
