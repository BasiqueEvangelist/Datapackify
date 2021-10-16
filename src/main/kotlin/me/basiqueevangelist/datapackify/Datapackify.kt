package me.basiqueevangelist.datapackify

import me.basiqueevangelist.datapackify.commands.DumpErrorsCommand
import me.basiqueevangelist.datapackify.potionrecipes.PotionRecipes
import me.basiqueevangelist.datapackify.trades.VillagerTrades
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

object Datapackify : ModInitializer {
    const val NAMESPACE = "datapackify"

    override fun onInitialize() {
        VillagerTrades.init()
        PotionRecipes.init()

        CommandRegistrationCallback.EVENT.register { dispatcher, dedicated ->
            DumpErrorsCommand.register(dispatcher)
        }
    }
}