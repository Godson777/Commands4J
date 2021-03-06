package com.darichey.discord.api;


import net.dv8tion.jda.core.Permission;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public class Command {

	private final String name;
	private String description = "";
	private String usage = "";
	private Set<String> aliases = new HashSet<>();
	private boolean caseSensitive = false;
	private boolean deleteCommand = false;
	private CommandCategory category;
	private Permission[] userRequiredPermissions;
	private Permission[] botRequiredPermissions;

	Consumer<CommandContext> onExecuted = context -> {};
	BiConsumer<CommandContext, FailureReason> onFailure = (context, failureReason) -> {};

	/**
	 * Initialize with the command's name.
	 * @param name The name of the command.
	 */
	public Command(String name) {
		this.name = name;
	}

	/**
	 * The function to execute when the command is called.
	 * @param function The function to execute.
	 * @return This command instance.
	 */
	public Command onExecuted(Consumer<CommandContext> function) {
		this.onExecuted = function;
		return this;
	}

	/**
	 * The function to execute when the command fails.
	 * @param function The function to execute.
	 * @return This command instance.
	 */
	public Command onFailure(BiConsumer<CommandContext, FailureReason> function) {
		this.onFailure = function;
		return this;
	}

	/**
	 * An arbitrary value for the description of the command. This isn't used by the API.
	 * @param description The description.
	 * @return This command instance.
	 */
	public Command withDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * An arbitrary value for the category of the command. This isn't used by the API.
	 * @param category The category.
	 * @return This command instance.
	 */
	public Command withCategory(CommandCategory category) {
		this.category = category;
		return this;
	}

	/**
	 * An arbitrary value for the usage of the command. This isn't used by the API.
	 * @param usage The usage.
	 * @return This command instance.
	 */
	public Command withUsage(String usage) {
		this.usage = usage;
		return this;
	}

	/**
	 * Aliases that will also trigger this command (besides the name). No two commands may have the same alias.
	 * @param aliases The aliases.
	 * @return This command instance.
	 */
	public Command withAliases(Set<String> aliases) {
		this.aliases = aliases;
		return this;
	}

	/**
	 * Aliases that will also trigger this command (besides the name). No two commands may have the same alias.
	 * @param aliases The aliases.
	 * @return This command instance.
	 */
	public Command withAliases(String... aliases) {
		Collections.addAll(this.aliases, aliases);
		return this;
	}

	/**
	 * If true, the command will trigger regardless of case. (i.e. both !ping and !PiNg will trigger the command)
	 * @param caseSensitive Case sensitive or not.
	 * @return This command instance.
	 */
	public Command caseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		return this;
	}

	/**
	 * If true, the message that triggered this command will automatically be deleted.
	 * @param deleteCommand To delete or not.
	 * @return This command instance.
	 */
	public Command deleteCommand(boolean deleteCommand) {
		this.deleteCommand = deleteCommand;
		return this;
	}

	/**
	 * The set of permissions a person requires to execute this command. Failing to meet these requirements will result in {@link FailureReason#AUTHOR_MISSING_PERMISSIONS}
	 * @param userRequiredPermissions The required permissions.
	 * @return This command instance.
	 */
	public Command userRequiredPermissions(Permission... userRequiredPermissions) {
		this.userRequiredPermissions = userRequiredPermissions;
		return this;
	}

	/**
	 * The set of permissions a person requires to execute this command. Failing to meet these requirements will result in {@link FailureReason#AUTHOR_MISSING_PERMISSIONS}
	 * @param botRequiredPermissions The required permissions.
	 * @return This command instance.
	 */
	public Command botRequiredPermissions(Permission... botRequiredPermissions) {
		this.botRequiredPermissions = botRequiredPermissions;
		return this;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getUsage() {
		return usage;
	}

	public CommandCategory getCategory() {
		return category;
	}

	public Set<String> getAliases() {
		return aliases;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean deletesCommand() {
		return deleteCommand;
	}

	public Permission[] getUserRequiredPermissions() {
		return userRequiredPermissions;
	}

	public Permission[] getBotRequiredPermissions() {
		return botRequiredPermissions;
	}

}
