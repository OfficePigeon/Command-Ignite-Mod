package fun.wich;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

public class IgniteCommand implements ModInitializer {
	@Override public void onInitialize() { CommandRegistrationCallback.EVENT.register((IgniteCommand::register)); }
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(CommandManager.literal("ignite")
				.requires(source -> source.hasPermissionLevel(2))
				.executes((context) -> ignite(context.getSource(), ImmutableList.of(context.getSource().getEntityOrThrow()), 10))
				.then(CommandManager.argument("targets", EntityArgumentType.entities())
						.executes((context) -> ignite(context.getSource(), EntityArgumentType.getEntities(context, "targets"), 10))
						.then((CommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000))
								.executes(context -> ignite(context.getSource(), EntityArgumentType.getEntities(context, "targets"), IntegerArgumentType.getInteger(context, "seconds")))))));
		dispatcher.register(CommandManager.literal("extinguish")
				.requires((source) -> source.hasPermissionLevel(2))
				.executes((context) -> extinguish(context.getSource(), ImmutableList.of(context.getSource().getEntityOrThrow())))
				.then(CommandManager.argument("targets", EntityArgumentType.entities())
						.executes((context) -> extinguish(context.getSource(), EntityArgumentType.getEntities(context, "targets")))));
	}
	private static int ignite(ServerCommandSource source, Collection<? extends Entity> targets, int duration) {
		for (Entity entity : targets) entity.setOnFireFor(duration);
		if (targets.size() == 1) source.sendFeedback(new TranslatableText("commands.ignite.success.single", targets.iterator().next().getDisplayName()), true);
		else source.sendFeedback(new TranslatableText("commands.ignite.success.multiple", targets.size()), true);
		return targets.size();
	}
	private static int extinguish(ServerCommandSource source, Collection<? extends Entity> targets) {
		for (Entity entity : targets) entity.extinguish();
		if (targets.size() == 1) source.sendFeedback(new TranslatableText("commands.extinguish.success.single", targets.iterator().next().getDisplayName()), true);
		else source.sendFeedback(new TranslatableText("commands.extinguish.success.multiple", targets.size()), true);
		return targets.size();
	}
}