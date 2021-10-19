package me.basiqueevangelist.datapackify.trades;

import com.google.common.collect.Lists;
import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.basiqueevangelist.datapackify.Datapackify;
import me.basiqueevangelist.datapackify.mixins.TradeOffersAccessor;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.StreamSupport;

public class VillagerTradeManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger("Datapackify/VillagerTradeManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final List<TradeParsingError> errorList = new ArrayList<>();

    public VillagerTradeManager() {
        super(GSON, "villager_trades");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Datapackify.NAMESPACE, "villager_trades");
    }

    @Override
    public void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        errorList.clear();

        Map<VillagerProfession, Int2ObjectMap<List<TradeOffers.Factory>>> trades = new HashMap<>();
        Int2ObjectMap<List<TradeOffers.Factory>> wanderingTrades = new Int2ObjectOpenHashMap<>();

        for (var prof : Registry.VILLAGER_PROFESSION) {
            trades.put(prof, new Int2ObjectOpenHashMap<>());
        }

        for (var entry : loader.entrySet()) {
            try {
                JsonObject obj = JsonHelper.asObject(entry.getValue(), "<file>");
                var tradeOfferList = StreamSupport.stream(JsonHelper.getArray(obj, "trades").spliterator(), false).map(VillagerTradeManager::parseTrade).toArray(TradeOffers.Factory[]::new);
                Identifier profId = new Identifier(JsonHelper.getString(obj, "profession"));

                Int2ObjectMap<List<TradeOffers.Factory>> map;
                if (profId.equals(new Identifier("wandering_trader"))) {
                    map = wanderingTrades;
                } else {
                    var profession = Registry.VILLAGER_PROFESSION.getOrEmpty(profId).orElseThrow(() ->
                        new IllegalArgumentException("Invalid profession " + profId)
                    );
                    map = trades.get(profession);
                }
                var careerLevel = JsonHelper.getInt(obj, "career_level");
                if (map.containsKey(careerLevel)) {
                    Collections.addAll(map.get(careerLevel), tradeOfferList);
                } else {
                    map.put(careerLevel, new ArrayList<>(List.of(tradeOfferList)));
                }
            } catch (IllegalArgumentException | JsonParseException e) {
                LOGGER.error("Encountered error while parsing {}", entry.getKey(), e);
                errorList.add(new TradeParsingError(entry.getKey(), e));
            }
        }
        LOGGER.info("Loaded " + (trades.size() + wanderingTrades.size()) + " villager trades.");
        Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> finalMap = new HashMap<>();
        for (var entry : trades.entrySet()) {
            Int2ObjectMap<TradeOffers.Factory[]> profMap = new Int2ObjectOpenHashMap<>();
            for (var entry2 : entry.getValue().int2ObjectEntrySet()) {
                profMap.put(entry2.getIntKey(), entry2.getValue().toArray(new TradeOffers.Factory[0]));
            }
            finalMap.put(entry.getKey(), profMap);
        }
        TradeOffersAccessor.setTrades(finalMap);
        Int2ObjectMap<TradeOffers.Factory[]> finalWanderingMap = new Int2ObjectOpenHashMap<>();
        for (var entry : wanderingTrades.int2ObjectEntrySet()) {
            finalWanderingMap.put(entry.getIntKey(), entry.getValue().toArray(new TradeOffers.Factory[0]));
        }
        TradeOffersAccessor.setWanderingTrades(finalWanderingMap);
    }

    private static TradeOffers.Factory parseTrade(JsonElement trade) {
        JsonObject obj = JsonHelper.asObject(trade, "trade");
        var type = new Identifier(JsonHelper.getString(obj, "type"));
        Optional<IOfferFactoryType> res = VillagerTrades.REGISTRY.getOrEmpty(type);
        if (res.isEmpty()) {
            throw new IllegalArgumentException("Invalid factory type" + type);
        }

        return res.get().deserialize(obj);
    }
}