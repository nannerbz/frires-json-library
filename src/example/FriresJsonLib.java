package example;

import arc.*;
import arc.util.*;
import example.content.StatusStat;
import example.content.WeathersStat;
import example.world.LibClassMap;
import example.content.ExampleBlocks;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class FriresJsonLib extends Mod{

    public FriresJsonLib(){
        Log.info("Loaded Frire's Json Lib Constructor.");

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            //show dialog upon startup
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("Thank you for using Frire's Json Lib!");
                dialog.cont.add("Frire's Json Lib is a mod designed to make Json modding the best it can be, \n thank you for using it (or playing a mod that has it!)").row();
                //mod sprites are prefixed with the mod name (this mod is called 'example-java-mod' in its config)
                dialog.cont.image(Core.atlas.find("frires-json-lib-frire")).size(250f, 250f).pad(20f).row();
                dialog.cont.button("OK", dialog::hide).size(100f, 50f);
                dialog.show();
            });
        });
    }

    @Override
    public void loadContent(){
        Log.info("Loading Json Lib Data.");
        LibClassMap.load();
        WeathersStat.load();
        StatusStat.load();
        //ExampleBlocks.load();
    }
}
