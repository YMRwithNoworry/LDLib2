package com.lowdragmc.lowdraglib2.utils;

import com.google.common.base.Charsets;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2022/05/13
 * @implNote CustomResourcePack
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomResourcePack extends PathPackResources {
    private final PackType type;
    private final String namespace;
    private final Path root;

    public CustomResourcePack(File location, String namespace, PackType type) {
        super(namespace, location.toPath(), false);
        this.namespace = namespace;
        this.type = type;
        this.root = location.toPath();
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... pathSegments) {
        String fileName = String.join("/", pathSegments);
        if ("pack.mcmeta".equals(fileName)) {
            String description = "Generated resources for " + namespace;
            String fallback = "Mod resources.";
            String pack = String.format("{\"pack\":{\"pack_format\":" + SharedConstants.getCurrentVersion().getPackVersion(type) + ",\"description\":{\"translate\":\"%s\",\"fallback\":\"%s.\"}}}", description, fallback);
            return () -> IOUtils.toInputStream(pack, Charsets.UTF_8);
        }
        return super.getRootResource(pathSegments);
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        if (packType != type || !namespace.equals(location.getNamespace())) {
            return null;
        }
        var resolved = discoverResources(packType).get(location);
        return resolved == null ? null : () -> Files.newInputStream(resolved);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, PackResources.ResourceOutput output) {
        if (packType != type || !this.namespace.equals(namespace)) {
            return;
        }
        var normalizedPath = normalizeResourcePath(path);
        discoverResources(packType).forEach((location, file) -> {
            if (location.getNamespace().equals(namespace) && location.getPath().startsWith(normalizedPath)) {
                output.accept(location, () -> Files.newInputStream(file));
            }
        });
    }

    private Map<ResourceLocation, Path> discoverResources(PackType packType) {
        var discovered = new LinkedHashMap<ResourceLocation, Path>();
        var usedPaths = new LinkedHashMap<String, Path>();
        var namespaceRoot = root.resolve(packType.getDirectory()).resolve(namespace);
        if (!Files.isDirectory(namespaceRoot)) {
            return discovered;
        }
        try (var stream = Files.walk(namespaceRoot)) {
            stream.filter(Files::isRegularFile)
                    .sorted()
                    .forEach(file -> {
                        var rawPath = normalizeResourcePath(namespaceRoot.relativize(file).toString());
                        var resourcePath = createUniqueResourcePath(sanitizeResourcePath(rawPath), file, usedPaths);
                        discovered.put(new ResourceLocation(namespace, resourcePath), file);
                    });
        } catch (IOException ignored) {
            // Keep resource loading best-effort; broken external files should not break Minecraft reload.
        }
        return discovered;
    }

    private static String createUniqueResourcePath(String resourcePath, Path file, Map<String, Path> usedPaths) {
        var uniquePath = resourcePath;
        var suffix = 2;
        while (usedPaths.containsKey(uniquePath) && !usedPaths.get(uniquePath).equals(file)) {
            uniquePath = appendSuffix(resourcePath, suffix++);
        }
        usedPaths.put(uniquePath, file);
        return uniquePath;
    }

    private static String appendSuffix(String path, int suffix) {
        var slash = path.lastIndexOf('/');
        var dot = path.lastIndexOf('.');
        if (dot <= slash) {
            return path + "_" + suffix;
        }
        return path.substring(0, dot) + "_" + suffix + path.substring(dot);
    }

    private static String sanitizeResourcePath(String path) {
        var normalized = normalizeResourcePath(path).toLowerCase(Locale.ROOT);
        var builder = new StringBuilder(normalized.length());
        var previousUnderscore = false;
        for (var i = 0; i < normalized.length(); i++) {
            var c = normalized.charAt(i);
            if (isValidPathChar(c)) {
                builder.append(c);
                previousUnderscore = false;
            } else if (!previousUnderscore) {
                builder.append('_');
                previousUnderscore = true;
            }
        }
        return builder.toString()
                .replaceAll("/_+", "/")
                .replaceAll("_+/", "/")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
    }

    private static boolean isValidPathChar(char c) {
        return c == '/' || c == '_' || c == '-' || c == '.' ||
                (c >= 'a' && c <= 'z') ||
                (c >= '0' && c <= '9');
    }

    private static String normalizeResourcePath(String path) {
        return path.replace('\\', '/')
                .replaceAll("/+", "/")
                .replaceAll("^/+", "");
    }

}
