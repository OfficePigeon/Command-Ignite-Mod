package fun.wich;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;

public class IgniteCommand implements ModInitializer {
	@Override public void onInitialize() { CommandRegistrationCallback.EVENT.register(IgniteCommand::register); }
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("ignite")
				.requires(source -> source.hasPermissionLevel(2))
				.executes((context) -> execute(context.getSource(), ImmutableList.of(context.getSource().getEntityOrThrow()), 10))
				.then(CommandManager.argument("targets", EntityArgumentType.entities())
						.executes((context) -> execute(context.getSource(), EntityArgumentType.getEntities(context, "targets"), 10))
						.then((CommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000))
								.executes(context -> execute(context.getSource(), EntityArgumentType.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "seconds")))))));
	}
	private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, int duration) {
		for (Entity entity : targets) entity.setOnFireFor(duration);
		if (targets.size() == 1) source.sendFeedback(Text.translatable("commands.ignite.success.single", targets.iterator().next().getDisplayName()), true);
		else source.sendFeedback(Text.translatable("commands.ignite.success.multiple", targets.size()), true);
		return targets.size();
	}
}