package net.hyper_pigeon.polaroidcamera.persistent_state;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Iterator;

public class ImagePersistentState extends PersistentState {
    private HashMap<String, byte[]> imageTextures = new HashMap<>();

    public ImagePersistentState(String key) {
        super(key);
    }

    public ImagePersistentState(){
        this("ImageInfo");
    }

    @Override
    public void fromTag(CompoundTag tag) {
        CompoundTag compoundTag = tag.getCompound("images");

        Iterator var3 = compoundTag.getKeys().iterator();

        while(var3.hasNext()) {
            String string = (String)var3.next();
            this.imageTextures.put(string, compoundTag.getByteArray(string));
        }

        this.markDirty();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag compoundTag = new CompoundTag();

        this.imageTextures.forEach((string, bytes) -> {
            compoundTag.putByteArray(string,bytes);
        });
        tag.put("images", compoundTag);
        return tag;
    }

    public boolean containsID(String id){
        return imageTextures.containsKey(id);
    }

    public void addByteArray(String id,byte[] bytes){
        this.imageTextures.put(id,bytes);
        this.markDirty();
    }

    public void appendByteArray(String id, byte[] append){
        this.imageTextures.put(id, ArrayUtils.addAll(imageTextures.get(id),append));
        this.markDirty();
    }

    public byte[] getByteArray(String id){
        return imageTextures.get(id);
    }

    public void removeByteArray(String id,byte[] bytes){
        this.imageTextures.remove(id);
    }

    public static ImagePersistentState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(() -> new ImagePersistentState("ImageInfo"), "ImageInfo");
    }

    public int getSize(){
        return imageTextures.size();
    }
}