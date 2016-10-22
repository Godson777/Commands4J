package com.darichey.discord.api;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

class CommandDispatcher implements IListener<MessageReceivedEvent> {

	@Override
	public void handle(MessageReceivedEvent event) {
		String content = event.getMessage().getContent();
		CommandRegistry registry = CommandRegistry.getForClient(event.getClient());
		boolean isUserDisabled = registry.isUserDisabledInGuild(event.getMessage().getGuild(), event.getMessage().getAuthor()) || registry.isUserDisabled(event.getMessage().getAuthor());
		if (!event.getMessage().getChannel().isPrivate() && !event.getMessage().getAuthor().isBot() && !isUserDisabled) {
			if (content.startsWith(registry.getPrefixForGuild(event.getMessage().getGuild()) == null ? registry.getPrefix() : registry.getPrefixForGuild(event.getMessage().getGuild()))) {
				String commandName = content.substring(registry.getPrefixForGuild(event.getMessage().getGuild()) != null
						? registry.getPrefixForGuild(event.getMessage().getGuild()).length()
						: registry.getPrefix().length(), content.contains(" ") ? content.indexOf(" ") : content.length());
				Optional<Command> command = registry.getCommandByName(commandName, true);
				if (command.isPresent()) {
                    if (command.get().isCaseSensitive() && !commandName.equals(command.get().getName()))
                        return; // If it's case sensitive, check if the cases match

                    CommandContext context = new CommandContext(event.getMessage());

                    EnumSet<Permissions> userRequiredPermissions = command.get().getUserRequiredPermissions();
                    EnumSet<Permissions> botRequiredPermissions = command.get().getBotRequiredPermissions();
                    boolean userHasPermission = event.getMessage().getChannel().getModifiedPermissions(event.getMessage().getAuthor()).containsAll(userRequiredPermissions);
                    boolean botHasPermission = event.getMessage().getChannel().getModifiedPermissions(event.getClient().getOurUser()).containsAll(botRequiredPermissions);

                    if (userHasPermission) {
                        if (botHasPermission) {
                            command.get().onExecuted.accept(context);
                            if (command.get().deletesCommand()) {
                                RequestBuffer.request(() -> {
                                    try {
                                        event.getMessage().delete();
                                    } catch (MissingPermissionsException e) {
                                        command.get().onFailure.accept(context, FailureReason.BOT_MISSING_PERMISSIONS);
                                    } catch (DiscordException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        } else {
                            command.get().onFailure.accept(context, FailureReason.BOT_MISSING_DEFINED_PERMISSIONS);
                        }
                    } else {
                        command.get().onFailure.accept(context, FailureReason.AUTHOR_MISSING_PERMISSIONS);
                    }
                }
			}
		}
	}
}
