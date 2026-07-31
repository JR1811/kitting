package net.shirojr.kitting.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.shirojr.kitting.component.KitComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KitCommands implements CommandRegistrationCallback {
    private static final SimpleCommandExceptionType DUPLICATE_ID =
            new SimpleCommandExceptionType(Text.literal("Identifier already exists"));
    private static final SimpleCommandExceptionType NO_TARGET =
            new SimpleCommandExceptionType(Text.literal("No target found"));
    private static final SimpleCommandExceptionType NO_USER =
            new SimpleCommandExceptionType(Text.literal("No user found"));
    private static final SimpleCommandExceptionType NO_SUCH_KIT =
            new SimpleCommandExceptionType(Text.literal("No such kit was found"));
    private static final SimpleCommandExceptionType DATA_LOSS =
            new SimpleCommandExceptionType(Text.literal("No loss of data allowed. If you clear self, make sure to store the data in the kit"));
    private static final SimpleCommandExceptionType NO_DATA_CHANGED =
            new SimpleCommandExceptionType(Text.literal("No data was changed"));
    private static final SimpleCommandExceptionType NO_DATA_FOUND =
            new SimpleCommandExceptionType(Text.literal("No data found"));

    private static final SuggestionProvider<ServerCommandSource> REGISTERED_KIT_SUGGESTER = (context, builder) -> {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return builder.buildFuture();
        KitComponent component = KitComponent.get(player);
        component.getRegisteredKits().forEach(identifier -> builder.suggest(identifier.toString()));
        return builder.buildFuture();
    };

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("kit").requires(source -> source.hasPermissionLevel(2))
                .then(literal("create")
                        .then(argument("id", IdentifierArgumentType.identifier())
                                .then(argument("initializeEmpty", BoolArgumentType.bool())
                                        .then(argument("clearTarget", BoolArgumentType.bool())
                                                .executes(context -> createKit(context, null))
                                                .then(argument("target", EntityArgumentType.player())
                                                        .executes(context ->
                                                                createKit(context, EntityArgumentType.getPlayer(context, "target"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(literal("apply")
                        .then(argument("id", IdentifierArgumentType.identifier())
                                .suggests(REGISTERED_KIT_SUGGESTER)
                                .executes(context -> KitCommands.applyKit(context, null))
                                .then(argument("targets", EntityArgumentType.players())
                                        .executes(context ->
                                                KitCommands.applyKit(context, EntityArgumentType.getPlayers(context, "targets"))
                                        )
                                )
                        )
                )
                .then(literal("print")
                        .executes(context -> KitCommands.printKits(context, null))
                        .then(argument("targets", EntityArgumentType.players())
                                .executes(context ->
                                        KitCommands.printKits(context, EntityArgumentType.getPlayers(context, "targets"))
                                )
                        )
                )
                .then(literal("share")
                        .then(argument("id", IdentifierArgumentType.identifier())
                                .suggests(REGISTERED_KIT_SUGGESTER)
                                .then(argument("targets", EntityArgumentType.players())
                                        .executes(KitCommands::shareKit)
                                )
                        )
                )
                .then(literal("remove")
                        .then(argument("id", IdentifierArgumentType.identifier())
                                .suggests(REGISTERED_KIT_SUGGESTER)
                                .executes(context -> KitCommands.removeKit(context, null))
                                .then(argument("targets", EntityArgumentType.players())
                                        .executes(context -> KitCommands.removeKit(context, EntityArgumentType.getPlayers(context, "targets")))
                                )
                        )
                )
        );
    }

    private static int printKits(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
        if (targets == null) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player == null) throw NO_TARGET.create();
            targets = Set.of(player);
        }
        if (targets.isEmpty()) {
            throw NO_TARGET.create();
        }
        HashSet<Identifier> allKits = new HashSet<>();
        targets.forEach(player -> allKits.addAll(KitComponent.get(player).getRegisteredKits()));
        if (allKits.isEmpty()) throw NO_DATA_FOUND.create();
        for (ServerPlayerEntity target : targets) {
            KitComponent component = KitComponent.get(target);
            List<Identifier> registeredKits = component.getRegisteredKits();
            if (registeredKits.isEmpty()) continue;
            MutableText printText = Text.empty().append(target.getName()).append(": ").styled(style -> style.withColor(Formatting.GREEN));
            for (int i = 0; i < registeredKits.size(); i++) {
                Identifier kitId = registeredKits.get(i);
                if (i != 0) {
                    printText.append(", ");
                }
                printText.append(Text.literal(kitId.toString()));
            }

            context.getSource().sendFeedback(() -> printText, false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int removeKit(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
        if (targets == null) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player == null) throw NO_TARGET.create();
            targets = Set.of(player);
        }
        if (targets.isEmpty()) {
            throw NO_TARGET.create();
        }

        Identifier id = IdentifierArgumentType.getIdentifier(context, "id");
        boolean anyChanged = false;
        for (ServerPlayerEntity target : targets) {
            KitComponent component = KitComponent.get(target);
            if (!component.getRegisteredKits().contains(id)) {
                context.getSource().sendFeedback(() -> Text.literal("Kit ID was not registered for " + target.getName().getString()), false);
                continue;
            }
            component.removeKit(id);
            context.getSource().sendFeedback(() -> Text.literal("Removed %s kit for %s".formatted(id.toString(), target.getName().getString())), true);
            anyChanged = true;
        }
        if (!anyChanged) throw NO_DATA_CHANGED.create();
        return Command.SINGLE_SUCCESS;
    }

    private static int applyKit(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
        if (targets == null) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player == null) {
                throw NO_TARGET.create();
            }
            targets = Set.of(player);
        }
        if (targets.isEmpty()) {
            throw NO_TARGET.create();
        }
        Identifier id = IdentifierArgumentType.getIdentifier(context, "id");
        for (ServerPlayerEntity target : targets) {
            KitComponent component = KitComponent.get(target);
            if (!component.getRegisteredKits().contains(id)) {
                context.getSource().sendFeedback(() -> Text.literal("Kit ID was not registered for " + target.getName().getString()), false);
                continue;
            }
            component.applyKit(id);
            context.getSource().sendFeedback(() -> Text.literal("Applied stored Kit to " + target.getName().getString()), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int createKit(CommandContext<ServerCommandSource> context, @Nullable ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == null) {
            player = context.getSource().getPlayer();
        }
        if (player == null) {
            throw NO_TARGET.create();
        }
        Identifier id = IdentifierArgumentType.getIdentifier(context, "id");
        KitComponent component = KitComponent.get(player);
        if (component.isRegisteredKit(id)) {
            throw DUPLICATE_ID.create();
        }
        boolean initializeEmpty = BoolArgumentType.getBool(context, "initializeEmpty");
        boolean clearTarget = BoolArgumentType.getBool(context, "clearTarget");
        if (initializeEmpty && clearTarget) {
            throw DATA_LOSS.create();
        }
        component.createKit(id);
        if (!initializeEmpty) {
            component.updateKit(id);
            context.getSource().sendFeedback(() -> Text.literal("Initialized Kit with current target's data"), false);
        }
        if (clearTarget) {
            component.clearLiveData(id);
        }
        context.getSource().sendFeedback(() -> Text.literal("Successfully created Kit"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int shareKit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            throw NO_USER.create();
        }
        Identifier id = IdentifierArgumentType.getIdentifier(context, "id");
        KitComponent originComponent = KitComponent.get(player);
        if (!originComponent.isRegisteredKit(id)) throw NO_SUCH_KIT.create();

        boolean anyShared = false;
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");
        for (ServerPlayerEntity target : targets) {
            KitComponent.ShareError shareError = originComponent.share(target, id);
            if (shareError == null) {
                anyShared = true;
                context.getSource().sendFeedback(() -> Text.literal("Shared %s kit with %s".formatted(id.toString(), target.getName().getString())), true);
            } else {
                context.getSource().sendFeedback(() -> shareError.getMessage(player, target), true);
            }
        }
        if (!anyShared) {
            throw NO_TARGET.create();
        }
        return Command.SINGLE_SUCCESS;
    }
}
