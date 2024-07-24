package example.content;

import example.world.types.SelectableReconstructor;
import example.world.types.StatusFieldBlock;
import example.world.types.WeatherCrafter;
import example.world.utils.DynamicUnitPlan;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.content.Weathers;
import mindustry.type.Category;
import mindustry.type.ItemStack;

public class ExampleBlocks {
    public static void load(){
        new WeatherCrafter("test1") {
            {
                localizedName = "test1";
                requirements(Category.units, ItemStack.with(Items.copper, 25));
                outputItem = new ItemStack(Items.pyratite, 2);
                consumeItems(ItemStack.with(Items.coal, 3, Items.silicon, 5, Items.lead, 1));
                weather = Weathers.rain;
            }
        };
        new StatusFieldBlock("test2") {
            {
                localizedName = "test2";
                requirements(Category.units, ItemStack.with(Items.copper, 25));
                consumeItems(ItemStack.with(Items.silicon, 1));
                consumePower(3);
                status = StatusEffects.overclock;
                statusDuration = 90;
                range = 15;
                craftTime = 90;
            }
        };
        new SelectableReconstructor("test3") {
            {
                localizedName = "test3";
                requirements(Category.units, ItemStack.with(Items.copper, 25));
                plans.add(new DynamicUnitPlan(UnitTypes.dagger, UnitTypes.crawler, 30, ItemStack.with(Items.coal, 10)));
                plans.add(new DynamicUnitPlan(UnitTypes.dagger, UnitTypes.nova, 60, ItemStack.with(Items.titanium, 10)));
            }
        };
    }
}
