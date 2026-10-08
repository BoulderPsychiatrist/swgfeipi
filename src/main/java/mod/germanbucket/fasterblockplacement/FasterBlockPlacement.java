package mod.germanbucket.fasterblockplacement;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FasterBlockPlacement.MODID)
public class FasterBlockPlacement {

    public static final String MODID = "fasterblockplacement";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public FasterBlockPlacement() {
    }
}
