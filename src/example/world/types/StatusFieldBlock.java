package example.world.types;
import example.content.StatusStat;
import mindustry.content.StatusEffects;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.production.GenericCrafter;
import arc.math.*;

public class StatusFieldBlock extends GenericCrafter {
    public float range = 5;
    public StatusEffect status = StatusEffects.overclock;
    public float statusDuration = 1;
    public StatusFieldBlock(String name) {
        super(name);
    }
    @Override public void setStats() {
        super.setStats();
        stats.add(StatusStat.status, status.localizedName);
    }
    @Override public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        float offset = (float)((this.size - 1) * 4);
        Drawf.dashCircle((float)(x * 8) + offset, (float)(y * 8) + offset, 8*range , Pal.lighterOrange);
    }
    public class StatusFieldBlockBuild extends GenericCrafterBuild {
        @Override public void craft() {
            if (super.shouldConsume()) {
                super.consume();
                Groups.unit.each((u) -> {
                    if (u.team == this.team && Mathf.len(u.x - this.x, u.y - this.y) <= (8*range)) {
                        u.apply(status, statusDuration);
                    }
                });
                this.progress %= 1.0F;
            }
        }
    }
}
