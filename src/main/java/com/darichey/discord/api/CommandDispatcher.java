package com.darichey.discord.api;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.swing.text.html.Option;
import java.util.Optional;

class CommandDispatcher extends ListenerAdapter {

    @Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String content = event.getMessage().getContent();
		CommandRegistry registry = CommandRegistry.getForClient(event.getJDA());
		boolean isUserDisabled = registry.isUserDisabledInGuild(event.getGuild(), event.getMessage().getAuthor()) || registry.isUserDisabled(event.getMessage().getAuthor());
		if (!event.isFromType(ChannelType.PRIVATE) && !event.getMessage().getAuthor().isBot() && !isUserDisabled) {
			if (content.startsWith(registry.getPrefixForGuild(event.getGuild()) == null ? registry.getPrefix() : registry.getPrefixForGuild(event.getGuild()))) {
				String commandName = content.substring(registry.getPrefixForGuild(event.getGuild()) != null
						? registry.getPrefixForGuild(event.getGuild()).length()
						: registry.getPrefix().length(), content.contains(" ") ? content.indexOf(" ") : content.length());
				Command command = null;
                Optional<Command> standard = registry.getCommandByName(commandName, true);
                Optional<Command> custom = registry.getCustomCommandByName(commandName, event.getGuild(), true);
                if (standard.isPresent()) command = standard.get();
                if (custom != null && custom.isPresent()) command = custom.get();
				if (command != null) {
                    if (command.isCaseSensitive() && !commandName.equals(command.getName()))
                        return; // If it's case sensitive, check if the cases match

                    CommandContext context = new CommandContext(event);

                    Permission[] userRequiredPermissions = command.getUserRequiredPermissions();
                    Permission[] botRequiredPermissions = command.getBotRequiredPermissions();
                    boolean userHasPermission = (userRequiredPermissions == null || event.getMember().hasPermission(event.getTextChannel(), userRequiredPermissions));
                    boolean botHasPermission = (botRequiredPermissions == null || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), botRequiredPermissions));

                    if (userHasPermission) {
                        if (botHasPermission) {
                            command.onExecuted.accept(context);
                            if (command.deletesCommand()) {
                                try {
                                    event.getMessage().deleteMessage().queue();
                                } catch (PermissionException e) {
                                    command.onFailure.accept(context, FailureReason.BOT_MISSING_PERMISSIONS);
                                }
                            }
                        } else {
                            command.onFailure.accept(context, FailureReason.BOT_MISSING_DEFINED_PERMISSIONS);
                        }
                    } else {
                        command.onFailure.accept(context, FailureReason.AUTHOR_MISSING_PERMISSIONS);
                    }
                }
			}
		}
	}
}
