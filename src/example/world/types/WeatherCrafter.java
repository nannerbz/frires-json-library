package example.world.types;
import example.content.WeathersStat;
import mindustry.content.Weathers;
import mindustry.type.Weather;
import mindustry.world.blocks.production.GenericCrafter;

public class WeatherCrafter extends GenericCrafter {
    public Weather weather = Weathers.rain;
    public WeatherCrafter(String name) {
        super(name);
    }
    @Override public void setStats() {
        super.setStats();
        stats.add(WeathersStat.weather, weather.localizedName);
    }
    public class WeatherCrafterBuild extends GenericCrafterBuild {
        @Override public void update() {
            if (weather.isActive()) {
                super.update();
            }
        }
    }
}

