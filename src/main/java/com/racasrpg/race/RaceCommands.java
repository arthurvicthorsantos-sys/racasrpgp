package com.racasrpg.race;

import java.util.Arrays;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.racasrpg.RacasRpg;
import com.racasrpg.net.ModNetwork;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = RacasRpg.MODID)
public class RaceCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("raca")
                        // /raca -> abre o menu
                        .executes(ctx -> {
                            ModNetwork.openMenu(ctx.getSource().getPlayerOrException());
                            return 1;
                        })
                        .then(Commands.literal("menu")
                                .executes(ctx -> {
                                    ModNetwork.openMenu(ctx.getSource().getPlayerOrException());
                                    return 1;
                                }))
                        .then(Commands.literal("status")
                                .executes(ctx -> {
                                    RaceManager.sendStatus(ctx.getSource().getPlayerOrException());
                                    return 1;
                                }))
                        .then(Commands.literal("info")
                                .executes(ctx -> {
                                    RaceManager.sendInfo(ctx.getSource().getPlayerOrException());
                                    return 1;
                                }))
                        .then(Commands.literal("escolher")
                                .then(Commands.argument("raca", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                Arrays.stream(Race.values()).map(Race::id), builder))
                                        .executes(ctx -> choose(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "raca")))))
                        .then(Commands.literal("evoluir")
                                .executes(ctx -> {
                                    RaceManager.evolve(ctx.getSource().getPlayerOrException());
                                    return 1;
                                }))
                        .then(Commands.literal("resetar")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    RaceManager.reset(player);
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("Raça removida. Use /raca para escolher outra.")
                                                    .withStyle(ChatFormatting.YELLOW),
                                            false);
                                    return 1;
                                }))
        );
    }

    private static int choose(CommandSourceStack source, String id) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Race race = Race.byId(id);
        if (race == null) {
            source.sendFailure(Component.literal("Raça desconhecida. Opções: " + Race.allIds()));
            return 0;
        }
        if (!RaceManager.choose(player, race)) {
            source.sendFailure(Component.literal("Você já tem uma raça."));
            return 0;
        }
        return 1;
    }
}
