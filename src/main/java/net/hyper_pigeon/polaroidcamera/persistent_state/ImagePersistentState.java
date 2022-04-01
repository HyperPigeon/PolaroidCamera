package net.hyper_pigeon.polaroidcamera.persistent_state;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

public class ImagePersistentState extends PersistentState {
    private HashMap<String, byte[]> imageTextures = new HashMap<>();
    public final String key;

    public ImagePersistentState(String key) {
        super();
        this.key = key;
    }

    public ImagePersistentState(){
        this("ImageInfo");
    }


    public static ImagePersistentState readNbt(NbtCompound tag) {

        ImagePersistentState imagePersistentState = new ImagePersistentState();
        NbtCompound compoundTag = tag.getCompound("images");

        Iterator var3 = compoundTag.getKeys().iterator();

        while(var3.hasNext()) {
            String string = (String)var3.next();
            imagePersistentState.imageTextures.put(string, compoundTag.getByteArray(string));
        }

        imagePersistentState.markDirty();
        return imagePersistentState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtCompound compoundTag = new NbtCompound();

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

    public void removeByteArray(String id){
        imageTextures.remove(id);
    }

    public void removeByteArray(String id,byte[] bytes){
        this.imageTextures.remove(id);
    }

    public static ImagePersistentState get(ServerWorld world) {
        return (ImagePersistentState) world.getPersistentStateManager().getOrCreate((nbtCompound) -> {
            return readNbt(nbtCompound);
        },ImagePersistentState::new, "ImageInfo");
    }

    public int getSize(){
        return imageTextures.size();
    }
}