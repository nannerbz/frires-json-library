package example.world.types;

import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePayloadDynamic;
import mindustry.world.modules.ItemModule;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.tilesize;

public class PayloadCrafter extends Constructor {
    public class PayloadRecipy {
        Seq<PayloadStack> inputPayload;
        Block outputPayload;


        public PayloadRecipy() {}
        public PayloadRecipy(Seq<PayloadStack> input,Block output) {
            outputPayload = output;
            inputPayload = input;
        }
    }

    public Seq<PayloadRecipy> payloadRecipies = new Seq<>();

    protected @Nullable ConsumePayloadDynamic consPay;

    public PayloadCrafter(String name) {
        super(name);

        sync = true;
        configurable = true;
        clearOnDoubleTap = true;
        config(Integer.class,(PayloadCrafterBuild building,Integer val) -> {
            building.currentRecipy = val;
        });
        configClear((PayloadCrafterBuild building) -> {
            building.currentRecipy = -1;
        });

        consumeBuilder.clear();
        consume(
                consPay = new ConsumePayloadDynamic(
                        (PayloadCrafterBuild building) -> {
                            if (building.currentRecipy == -1)
                                return new Seq<PayloadStack>();
                            var payloads = payloadRecipies.get(building.currentRecipy).inputPayload;

                            return payloads;
                        }
                )
        );
    }

    public class PayloadCrafterBuild extends ConstructorBuild {

        public int currentRecipy = -1;

        PayloadSeq payloads = new PayloadSeq();

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            if (this.payload != null) return false;
            if (currentRecipy == -1) return false;
            if (payload instanceof BuildPayload pblock) {
                var cur = payloadRecipies.get(currentRecipy).inputPayload;
                AtomicInteger limit = new AtomicInteger(0);
                cur.each(c -> {
                    if (c.item == pblock.block())
                        limit.addAndGet(c.amount);
                });
                return limit.get() > payloads.get(pblock.block());
            }
            return false;
        }

        @Override
        public Block recipe() {
            if (currentRecipy == -1) return null;
            return payloadRecipies.get(currentRecipy).outputPayload;
        }

        @Override
        public void updateTile() {
            
            if (payload != null) {
                payload.update(null, this);
                if (currentRecipy != -1 && payloadRecipies.get(currentRecipy).inputPayload.contains(stac -> stac.item == payload.block())) {
                    if (moveInPayload()) {
                        payloads.add(payload.block());
                        payload = null;
                    }
                } else
                    moveOutPayload();
            }
            boolean produce = currentRecipy != -1 && efficiency() > 0 && payload == null;

            if(produce){
                progress += buildSpeed * edelta();

                if(progress >= payloadRecipies.get(currentRecipy).outputPayload.buildCost){
                    consume();
                    payload = new BuildPayload(recipe(), team);
                    payload.block().placeEffect.at(x, y, payload.size() / tilesize);
                    payVector.setZero();
                    progress %= 1f;
                }
            }

            heat = Mathf.lerpDelta(heat, Mathf.num(produce), 0.15f);
            time += heat * delta();
        }
        @Override
        public float progress() {
            if (currentRecipy == -1) return 0;
            return progress / payloadRecipies.get(currentRecipy).outputPayload.buildCost;
        }
        @Override
        public PayloadSeq getPayloads() {
            return payloads;
        }
        @Override
        public float efficiency() {
            return super.efficiency() * consPay.efficiency(this);
        }

        @Override
        public void buildConfiguration(Table table){
            Seq<Block> resList = Seq.with(payloadRecipies).map(p -> p.outputPayload);
            ItemSelection.buildTable(PayloadCrafter.this, table, resList, () -> currentRecipy == -1 ? null : resList.get(currentRecipy), (v) -> {currentRecipy = resList.indexOf(v); Call.tileConfig(Vars.player, this, currentRecipy);}, selectionRows, selectionColumns);
        }
    }
}
