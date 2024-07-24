package example.world.utils;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.units.UnitFactory;

public class DynamicUnitPlan extends UnitFactory.UnitPlan {
    public UnitType resultUnit;
    public DynamicUnitPlan(UnitType unit, UnitType resultUnit, float time, ItemStack[] requirements) {
        super(unit, time, requirements);
        this.resultUnit = resultUnit;
    }
}
