package net.hyper_pigeon.polaroidcamera.persistent_state;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

public class ImagePersistentState extends PersistentState {
    private HashMap<String, ArrayList<byte[]>> imageTextures = new HashMap<>();
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

//        Iterator var3 = compoundTag.getKeys().iterator();
//
//        while(var3.hasNext()) {
//            String string = (String)var3.next();
//            imagePersistentState.imageTextures.put(string, compoundTag.getByteArray(string));
//        }
//
//        imagePersistentState.markDirty();
        return imagePersistentState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtCompound compoundTag = new NbtCompound();

//        this.imageTextures.forEach((string, bytes) -> {
//            compoundTag.putByteArray(string,bytes);
//        });
//        tag.put("images", compoundTag);
        return tag;
    }

    public boolean containsID(String id){
        return imageTextures.containsKey(id);
    }

    public void addByteArray(String id,byte[] bytes){
        ArrayList<byte[]> arrayList = new ArrayList<>();
        arrayList.add(bytes);
        this.imageTextures.put(id, arrayList);

        this.markDirty();
    }

    public void appendByteArray(String id, byte[] append){
        this.imageTextures.get(id).add(append);
        this.markDirty();
    }

    public byte[] getByteArray(String id) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        for(byte[] bytes : this.imageTextures.get(id)){
            byteArrayOutputStream.write(bytes);
        }

        return byteArrayOutputStream.toByteArray();

    }



    public void removeByteArray(String id){
        imageTextures.remove(id);
        markDirty();
    }

    public void removeByteArray(String id,byte[] bytes){
        this.imageTextures.remove(id);
        markDirty();
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