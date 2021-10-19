package me.basiqueevangelist.datapackify.trades;

import net.minecraft.util.Identifier;

public record TradeParsingError(Identifier file, Exception error) {

}
