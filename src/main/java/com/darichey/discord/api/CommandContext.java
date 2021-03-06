package com.darichey.discord.api;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The context of a command. Contains useful information about where and how a command was executed.
 */
public class CommandContext {

	private final MessageReceivedEvent event;
	private final Message message;
	private final String name;
	private final String[] args;
	private final CommandRegistry registry;
	private final TextChannel channel;
	private final Guild guild;
	private final User author;
	private final Member member;
	private final JDA jda;

	public CommandContext(MessageReceivedEvent event) {
		this.registry = CommandRegistry.getForClient(event.getJDA());
		this.event = event;
		this.message = event.getMessage();
		this.author = event.getAuthor();
		this.member = event.getMember();
		this.channel = event.getTextChannel();
		this.guild = event.getGuild();
		this.jda = event.getJDA();
		final String content = event.getMessage().getRawContent();
		this.name = content.substring(registry.getPrefix().length()).substring(0, content.contains(" ") ? content.indexOf(" ") : content.length() - 1);
		List<String> list = new ArrayList<>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(content.substring(registry.getPrefix().length() + name.length())); // Thanks @dec for regex
		while (m.find()) {
			list.add(m.group(1).replace("\"", ""));
		}
		this.args = list.toArray(new String[list.size()]);
	}

	/**
	 * @return The message object.
	 */
	public Message getMessage() {
		return this.message;
	}

	/**
	 * @return The command's name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The arguments, or an empty array if there aren't any.
	 */
	public String[] getArgs() {
		return this.args;
	}

	/**
	 * @return The CommandRegistry of the guild this context is for.
	 */
	public CommandRegistry getRegistry() {
		return this.registry;
	}

	/**
	 * @return The TextChannel of the message.
	 */
	public TextChannel getTextChannel() {
		return this.channel;
	}

	/**
	 * @return The Guild of the message.
	 */
	public Guild getGuild() {
		return this.guild;
	}

	/**
	 * @return The author of the message.
	 */
	public User getAuthor() {
		return this.author;
	}

	/**
	 * @return The Member object of the author.
	 */
	public Member getMember() {
		return this.member;
	}

	/**
	 * @return The JDA the guild is located in.
	 */
	public JDA getJDA() {
		return this.jda;
	}
}
