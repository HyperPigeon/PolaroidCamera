package net.hyper_pigeon.image2map;

//https://github.com/TheEssem/Image2Map

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class Image2Map {
    class DitherModeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context,
                                                             SuggestionsBuilder builder) throws CommandSyntaxException {
            builder.suggest("none");
            builder.suggest("dither");
            return builder.buildFuture();
        }

    }

    public enum DitherMode {
        NONE,
        FLOYD;

        public static DitherMode fromString(String string) {
            if (string.equalsIgnoreCase("NONE"))
                return DitherMode.NONE;
            else if (string.equalsIgnoreCase("DITHER") || string.equalsIgnoreCase("FLOYD"))
                return DitherMode.FLOYD;
            throw new IllegalArgumentException("invalid dither mode");
        }
    }




}
