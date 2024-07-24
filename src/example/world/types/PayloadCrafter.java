package example.world.types;
import arc.struct.Seq;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.payloads.Payload;

public class PayloadCrafter extends Constructor {
    public PayloadCrafter(String name) {
        super(name);
    }

    public class PayloadCrafterBuild extends ConstructorBuild {
        Seq<Payload> InputPayloads = new Seq<Payload>();
        
    }
}
