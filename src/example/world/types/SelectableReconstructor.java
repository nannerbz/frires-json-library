package example.world.types;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import example.world.utils.DynamicUnitPlan;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Iterator;

public class SelectableReconstructor extends Reconstructor {
    public Seq<DynamicUnitPlan> plans = new Seq<>();
    public boolean useInputIcons = false;

    public SelectableReconstructor(String name) {
        super(name);
        configurable = true;
        sync = true;
        clearOnDoubleTap = true;


        consume(new ConsumeItemDynamic(
                // items seq is already shrunk, it's safe to access
                (SelectableReconstructorBuild b) -> {
                    if (b.currentPlan == -1) return ItemStack.empty;
                    return plans.get(b.currentPlan).requirements;
                }
                )
        );

        config(Integer.class,(SelectableReconstructorBuild build,Integer i) -> {
            build.currentPlan = i;
        });

        configClear((SelectableReconstructorBuild build) -> {
            build.currentPlan = -1;
        });
    }

    @Override public void setStats() {
        this.stats.timePeriod = this.constructTime;
        super.setStats();
        this.stats.add(Stat.productionTime, this.constructTime / 60.0F, StatUnit.seconds);
        this.stats.add(Stat.output, (table) -> {
            table.row();
            Iterator var2 = this.upgrades.iterator();

            while(var2.hasNext()) {
                UnitType[] upgrade = (UnitType[])var2.next();
                if (upgrade[0].unlockedNow() && upgrade[1].unlockedNow()) {
                    table.table(Styles.grayPanel, (t) -> {
                        t.left();
                        t.image(upgrade[0].uiIcon).size(40.0F).pad(10.0F).left().scaling(Scaling.fit);
                        t.table((info) -> {
                            info.add(upgrade[0].localizedName).left();
                            info.row();
                        }).pad(10.0F).left();
                    }).fill().padTop(5.0F).padBottom(5.0F);
                    table.table(Styles.grayPanel, (t) -> {
                        t.image(Icon.right).color(Pal.darkishGray).size(40.0F).pad(10.0F);
                    }).fill().padTop(5.0F).padBottom(5.0F);
                    table.table(Styles.grayPanel, (t) -> {
                        t.left();
                        t.image(upgrade[1].uiIcon).size(40.0F).pad(10.0F).right().scaling(Scaling.fit);
                        t.table((info) -> {
                            info.add(upgrade[1].localizedName).right();
                            info.row();
                        }).pad(10.0F).right();
                    }).fill().padTop(5.0F).padBottom(5.0F);
                    table.row();
                }
            }

        });
    }

    public class SelectableReconstructorBuild extends ReconstructorBuild {
        public int currentPlan = -1;

        @Override public int getMaximumAccepted(Item item) {
            if (currentPlan == -1) return 0;
            for (var cur : plans.get(currentPlan).requirements) {
                if (cur.item == item) {
                    return Mathf.round((float) cur.amount * Vars.state.rules.unitCost(this.team));
                }
            }
            return 0;
        }

        @Override public Object config() {
            return currentPlan;
        }

        @Override public boolean shouldShowConfigure(Player player) {
            return true;
        }

        @Override public boolean acceptItem(Building source, Item item){
            return getMaximumAccepted(item) > items.get(item);
        }

        @Override public boolean shouldConsume(){
            if (payload == null) return false;
            return currentPlan != -1 && plans.get(currentPlan).unit == payload.unit.type;
        }

        @Override public void updateTile() {
            boolean valid = false;
            if (this.payload != null && currentPlan != -1) {
                var Plan = plans.get(currentPlan);
                if (Plan.unit != payload.unit.type) {
                    this.moveOutPayload();
                } else if (this.moveInPayload()) {
                    if (this.efficiency > 0.0F) {
                        valid = true;
                        this.progress += this.edelta() * Vars.state.rules.unitBuildSpeed(this.team);
                    }

                    if (this.progress >= Plan.time) {
                        ((UnitPayload)this.payload).unit = Plan.resultUnit.create(((UnitPayload)this.payload).unit.team());
                        if (((UnitPayload)this.payload).unit.isCommandable()) {
                            if (this.commandPos != null) {
                                ((UnitPayload)this.payload).unit.command().commandPosition(this.commandPos);
                            }

                            if (this.command != null) {
                                ((UnitPayload)this.payload).unit.command().command(this.command);
                            }
                        }

                        this.progress %= 1.0F;
                        Effect.shake(2.0F, 3.0F, this);
                        Fx.producesmoke.at(this);
                        this.consume();
                        Events.fire(new EventType.UnitCreateEvent(((UnitPayload)this.payload).unit, this));
                    }
                }
            }

            this.speedScl = Mathf.lerpDelta(this.speedScl, (float)Mathf.num(valid), 0.05F);
            this.time += this.edelta() * this.speedScl * Vars.state.rules.unitBuildSpeed(this.team);
        }

        @Override public void buildConfiguration(Table table){
            if (canSetCommand()) {
                super.buildConfiguration(table);
                return;
            }
                Seq<UnitType> units = Seq.with(plans).map(u -> u.resultUnit).retainAll(u -> u.unlockedNow() && !u.isBanned());

                if (units.any()) {
                    ItemSelection.buildTable(SelectableReconstructor.this, table, units, () -> currentPlan == -1 ? null : plans.get(currentPlan).resultUnit, unit -> configure(plans.indexOf(u -> u.resultUnit == unit)), selectionRows, selectionColumns);
                } else {
                    table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
                }
        }

        @Override public boolean acceptPayload(Building source, Payload payload) {
            if (currentPlan == -1) return false;
            if (this.payload == null && (this.enabled || source == this) && this.relativeTo(source) != this.rotation && payload instanceof UnitPayload) {
                UnitPayload pay = (UnitPayload)payload;
                UnitType upgrade = plans.get(currentPlan).unit;
                if (upgrade != null) {
                    if (!upgrade.unlockedNowHost() && !this.team.isAI()) {
                        pay.showOverlay(Icon.tree);
                    }

                    if (upgrade.isBanned()) {
                        pay.showOverlay(Icon.cancel);
                    }
                }

                return upgrade != null && (this.team.isAI() || upgrade.unlockedNowHost()) && !upgrade.isBanned();
            } else {
                return false;
            }
        }

        public float fraction() {
            if (currentPlan == -1) return 0;
            return this.progress / plans.get(currentPlan).time;
        }
    }
}
