package mycoolmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCoolMod implements ModInitializer {

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("mycoolmod");

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
          if (handler.player.hasVehicle() && handler.player.getVehicle() instanceof PlayerEntity) {
            handler.player.stopRiding();
          }
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
            if(!world.isClient && hand == Hand.MAIN_HAND && entity instanceof PlayerEntity && player.getStackInHand(hand).isEmpty()) {
                ServerPlayerEntity passanger = (ServerPlayerEntity) entity;
                while (passanger.getFirstPassanger() != null && passanger.getFirstPassanger() != player) {
                    passanger = (ServerPlayerEntity) passanger.getFirstPassanger();
                }
                player.startRiding(passanger);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
    }
}
