package me.basiqueevangelist.datapackify.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.basiqueevangelist.datapackify.trades.VillagerTradeManager;
import me.basiqueevangelist.datapackify.trades.VillagerTrades;
import static net.minecraft.server.command.CommandManager.literal;

import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public final class DumpErrorsCommand {
    private DumpErrorsCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("datapackify")
                .then(literal("dumperrors")
                    .requires(x -> x.hasPermissionLevel(2))
                    .executes(DumpErrorsCommand::dumpErrors)
                )
        );
    }

    private static int dumpErrors(CommandContext<ServerCommandSource> ctx) {
        if (VillagerTradeManager.errorList.isEmpty()) return 1;

        var errList = VillagerTradeManager.errorList;
        ctx.getSource().sendFeedback(new LiteralText("The following errors have occurred during Datapackify reload!\n"), false);

        for (var err : errList) {
            ctx.getSource().sendFeedback(new LiteralText(err.file().toString() + " -> ")
                .formatted(Formatting.YELLOW)
                .append(new LiteralText(err.error().toString())
                    .formatted(Formatting.RED)), false);
        }

        return 1;
    }
}