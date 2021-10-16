package me.basiqueevangelist.datapackify.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import me.basiqueevangelist.datapackify.trades.VillagerTrades
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting

object DumpErrorsCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal("datapackify")
                .then(literal("dumperrors")
                    .requires {
                        it.hasPermissionLevel(2)
                    }
                    .executes(this::dumpErrors)
                )
        )
    }

    private fun dumpErrors(ctx: CommandContext<ServerCommandSource>): Int {
        if (VillagerTrades.VILLAGER_TRADES.errorList.isEmpty()) return 1

        val errList = VillagerTrades.VILLAGER_TRADES.errorList
        ctx.source.sendFeedback(LiteralText("The following errors have occurred during Datapackify reload!\n"), false)

        for (err in errList) {
            ctx.source.sendFeedback(LiteralText(err.file.toString() + " -> ")
                .formatted(Formatting.YELLOW)
                .append(LiteralText(err.error.toString())
                    .formatted(Formatting.RED)), false)
        }

        return 1
    }
}