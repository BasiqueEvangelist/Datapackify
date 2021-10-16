package me.basiqueevangelist.datapackify.trades

import net.minecraft.util.Identifier

data class TradeParsingError(
    val file: Identifier,
    val error: Exception
) {

}
