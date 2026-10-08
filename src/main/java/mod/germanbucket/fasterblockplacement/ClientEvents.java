package mod.germanbucket.fasterblockplacement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class ClientEvents {

    public static final KeyBinding TOGGLE_INSTAPLACE_BIND = new KeyBinding(
            "key." + FasterBlockPlacement.MODID + ".enableinstaplace",
            KeyConflictContext.IN_GAME,
            InputMappings.getInputByCode(GLFW.GLFW_KEY_G, -1),
            "key.categories." + FasterBlockPlacement.MODID
    );

    private static int ticker = 20;

    // Minecraft#rightClickDelayTimer (SRG name field_71467_ac). Accessed via reflection instead of a mixin.
    private static Field rightClickDelayField;
    private static boolean reflectionFailed = false;

    private static void resetRightClickDelay(Minecraft mc) {
        if (reflectionFailed) {
            return;
        }
        try {
            if (rightClickDelayField == null) {
                rightClickDelayField = ObfuscationReflectionHelper.findField(Minecraft.class, "field_71467_ac");
            }
            rightClickDelayField.setInt(mc, 0);
        } catch (Throwable t) {
            reflectionFailed = true;
            FasterBlockPlacement.LOGGER.error("Could not access Minecraft#rightClickDelayTimer, instant placement disabled", t);
        }
    }

    @Mod.EventBusSubscriber(modid = FasterBlockPlacement.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ClientRegistry.registerKeyBinding(TOGGLE_INSTAPLACE_BIND);
        }
    }

    @Mod.EventBusSubscriber(modid = FasterBlockPlacement.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();

            if (ticker > 0) {
                ticker--;
            }

            if (TOGGLE_INSTAPLACE_BIND.isKeyDown() && mc.player != null && ticker <= 0) {
                ticker = 20;
                TOGGLE_INSTAPLACE_BIND.isPressed();

                if (Config.isInstantPlacing) {
                    mc.player.sendStatusMessage(new TranslationTextComponent("message." + FasterBlockPlacement.MODID + ".disabledinstaplace"), true);
                    mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                    Config.isInstantPlacing = false;
                } else {
                    mc.player.sendStatusMessage(new TranslationTextComponent("message." + FasterBlockPlacement.MODID + ".enabledinstaplace"), true);
                    mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 1.0F, 1.0F);
                    Config.isInstantPlacing = true;
                }
            }

            // Vanilla sets the delay to 4 after every use; zero it at the end of each tick
            // so the next tick's right-click check passes immediately.
            if (Config.isInstantPlacing && mc.player != null) {
                resetRightClickDelay(mc);
            }
        }
    }
}
