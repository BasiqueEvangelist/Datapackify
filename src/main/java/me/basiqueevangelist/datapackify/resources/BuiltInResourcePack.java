package me.basiqueevangelist.datapackify.resources;

import me.basiqueevangelist.datapackify.Datapackify;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuiltInResourcePack extends AbstractFileResourcePack {
    private static final Path BASE_PATH = FabricLoader.getInstance().getModContainer(Datapackify.NAMESPACE).get().getPath("builtin_data");

    public BuiltInResourcePack() {
        super(null);
    }

    @Override
    protected InputStream openFile(String name) throws IOException {
        return Files.newInputStream(BASE_PATH.resolve(name));
    }

    @Override
    protected boolean containsFile(String name) {
        return Files.exists(BASE_PATH.resolve(name));
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        Path nsPath = BASE_PATH.resolve(type.getDirectory()).resolve(namespace);
        Path walkPath = nsPath.resolve(prefix.replace("/", BASE_PATH.getFileSystem().getSeparator()));

        if (!Files.exists(walkPath))
            return Collections.emptyList();

        List<Identifier> ids = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(walkPath, maxDepth)) {
            walk
                .filter(x -> {
                    String fileName = x.getFileName().toString();
                    return pathFilter.test(fileName) && !fileName.endsWith(".mcmeta");
                })
                .filter(Files::isRegularFile)
                .map(nsPath::relativize)
                .map(x -> x.toString().replace(BASE_PATH.getFileSystem().getSeparator(), "/"))
                .forEach(x -> ids.add(new Identifier(namespace, x)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ids;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        try (var stream = Files.list(BASE_PATH.resolve(type.getDirectory()))) {
            return stream
                .filter(Files::isDirectory)
                .map(x -> x.getFileName().toString())
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public String getName() {
        return "Datapackify Built-in Resources";
    }
}
