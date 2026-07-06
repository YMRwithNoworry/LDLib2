package com.lowdragmc.lowdraglib2.editor.resource;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.utils.ResourceHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import org.jetbrains.annotations.Nullable;
import java.io.DataInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PackFileResourceProvider<T> extends ResourceProvider<T>  {
    private static final String RESOURCE_ROOT = "resources";
    private final Map<IResourcePath, ResourceLocation> resourceLocations = new LinkedHashMap<>();

    public PackFileResourceProvider(ResourceInstance<T> resourceInstance) {
        super(resourceInstance);
        PackResourceManager.INSTANCE.registerProvider(this);
    }

    void clearCachedResources() {
        contents.clear();
        resourceLocations.clear();
    }

    @Override
    public String getName() {
        return "mod_resources";
    }

    @Override
    public ResourceProviderType getType() {
        return FileResourceProvider.TYPE;
    }

    @Override
    public boolean supportResourcePath(IResourcePath path) {
        return path instanceof FilePath filePath &&
                filePath.location != null &&
                filePath.file.getName().endsWith(resourceInstance.resource.getFileExtension());
    }

    @Override
    public IResourcePath createSubPath(String name) {
        return new FilePath(new ResourceLocation(
                LDLib2.MOD_ID,
                RESOURCE_ROOT + "/" + name + resourceInstance.resource.getFileExtension()));
    }

    @Override
    public String getResourceName(IResourcePath path) {
        if (path instanceof FilePath filePath) {
            var fileName = filePath.file.getName();
            var suffix = resourceInstance.resource.getFileExtension();
            if (fileName.endsWith(suffix)) {
                return fileName.substring(0, fileName.length() - suffix.length());
            }
        }
        return super.getResourceName(path);
    }

    @Override
    public boolean addResource(IResourcePath path, T resource) {
        return false;
    }

    @Override
    public @Nullable T removeResource(IResourcePath path) {
        return null;
    }

    @Override
    public boolean canRemove(IResourcePath path) {
        return false;
    }

    @Override
    public boolean canRename(IResourcePath path) {
        return false;
    }

    @Override
    public boolean canEdit(IResourcePath path) {
        return false;
    }

    @Override
    public boolean canCopy(IResourcePath path) {
        return false;
    }

    @Override
    public boolean supportAdd() {
        return false;
    }

    @Nullable
    private T getResourceByLocation(ResourceLocation location) {
        for (PackResources pack : ResourceHelper.getResourceManager().listPacks().toList()) {
            var resource = pack.getResource(PackType.CLIENT_RESOURCES, location);
            if (resource != null) {
                try {
                    try (var stream = resource.get()) {
                        try (var inputStream = new DataInputStream(stream)) {
                            var tag = NbtIo.read(inputStream);
                            return deserializeNBT(tag, Platform.getFrozenRegistry());
                        }
                    }
                } catch (Exception e) {
                    LDLib2.LOGGER.warn("Failed to read resource {} from {}: ", location, resource, e);
                }
            }
        }
        return null;
    }

    @Nullable
    private T deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        if (nbt.getString("type").equals(resourceInstance.resource.getName())) {
            return resourceInstance.resource.deserializeResource(nbt.get("data"), provider);
        }
        return null;
    }

    @Override
    public T getResource(IResourcePath path) {
        if (supportResourcePath(path)) {
            if (!contents.containsKey(path)) {
                var location = path instanceof FilePath filePath ? filePath.location : null;
                if (location != null) {
                    contents.put(path, getResourceByLocation(location));
                }
            }
            return contents.get(path);
        }
        return null;
    }

    @Override
    public boolean checkAndUpdateResourceProvider() {
        var discovered = new LinkedHashMap<IResourcePath, ResourceLocation>();
        var resources = ResourceHelper.getResourceManager().listResources(RESOURCE_ROOT,
                location -> location.getPath().endsWith(resourceInstance.resource.getFileExtension()));
        resources.keySet().stream()
                .sorted(java.util.Comparator.comparing(ResourceLocation::toString))
                .forEach(location -> discovered.put(new FilePath(location), location));

        if (discovered.keySet().equals(resourceLocations.keySet())) {
            return false;
        }

        resourceLocations.clear();
        resourceLocations.putAll(discovered);
        contents.clear();
        for (var entry : resourceLocations.entrySet()) {
            var resource = getResourceByLocation(entry.getValue());
            if (resource != null) {
                contents.put(entry.getKey(), resource);
            }
        }
        resourceInstance.clearCache();
        return true;
    }
}
