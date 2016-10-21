package com.darichey.discord.api;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class CommandRegistry {

    private static HashMap<IDiscordClient, CommandRegistry> registries = new HashMap<>();
    private List<Command> commands = new ArrayList<>();
    private HashMap<IGuild, List<Command>> customCommands = new HashMap<>();
    private String prefix = "$";
    private HashMap<IGuild, String> prefixes = new HashMap<>();
    private HashMap<IGuild, List<IUser>> disabledUsersInGuilds = new HashMap<>();
    private HashMap<IGuild, List<IUser>> testMode = new HashMap<>();
    private List<IUser> disabledUsers = new ArrayList<>();

	/**
	 * Get the CommandRegistry associated with the client, or create a new one if not present.
	 * @param client The client object to associate with.
	 * @return The CommandRegistry for the client.
	 */
	public static CommandRegistry getForClient(IDiscordClient client) {
		if (!registries.containsKey(client)) {
			registries.put(client, new CommandRegistry());
			client.getDispatcher().registerListener(new CommandDispatcher());
		}
		return registries.get(client);
	}

	/**
	 * Private so you have to use {@link CommandRegistry#getForClient(IDiscordClient)}
	 */
	private CommandRegistry() {}

	/**
	 * Register a command.
	 * @param command The command.
	 */
	public void register(Command command) {
		if (!commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findFirst().isPresent()) {
			commands.add(command);
		} else {
			throw new IllegalArgumentException("Attempt to register two commands with the same name: " + command.getName());
		}
	}

	/**
	 * Register a custom command to a specific server.
	 * @param command The custom command.
	 * @param guild The guild the command gets assigned to.
	 */
	public void customRegister(Command command, IGuild guild) {
		if (!customCommands.containsKey(guild)) {
			customCommands.put(guild, new ArrayList<>());
		}
		if (!commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findFirst().isPresent()
				&& !customCommands.get(guild).stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findFirst().isPresent()) {
			List<Command> cc = customCommands.get(guild);
			cc.add(command);
			customCommands.replace(guild, cc);
		} else {
			throw new IllegalArgumentException("Attempt to register two custom commands with the same name: " + command.getName());
		}
	}

	/**
	 * Registers an array of commands. Purely for convenience.
	 * @param commands Array/Varargs of commands to register
	 */
	public void registerAll(Command... commands) {
		for (Command command : commands) register(command);
	}

	/**
	 * Get a command by its name.
	 * @param name The command name.
	 * @param includeAlias If aliases can be used to search, otherwise it has to be the original name.
	 * @return An optional value of the command.
	 */
	public Optional<Command> getCommandByName(String name, boolean includeAlias) {
		return commands.stream().filter(c ->
				c.getName().equalsIgnoreCase(name) || (includeAlias && c.getAliases().contains(name))
		).findFirst();
	}

	/**
	 * Get a custom command by its name in a specified guild.
	 * @param name The command name.
	 * @param guild The guild the command should be in.
	 * @param includeAlias If aliases can be used to search, otherwise it has to be the original name.
	 * @return An optional value of the custom command.
	 */
	public Optional<Command> getCustomCommandByName(String name, IGuild guild, boolean includeAlias) {
		if (customCommands.containsKey(guild))
			return customCommands.get(guild).stream().filter(c -> c.getName().equalsIgnoreCase(name) || (includeAlias && c.getAliases().contains(name))).findFirst();
		else return null;
	}

	/**
	 * Get all commands registered.
	 * @return A list of commands.
	 */
	public List<Command> getCommands() {
		return commands;
	}

	/**
	 * Get all custom commands registered in a specified guild.
	 * @param guild The guild the commands should be in.
	 * @return A list of custom commands.
	 */
	public List<Command> getCustomCommands(IGuild guild) {
		if (customCommands.containsKey(guild)) return customCommands.get(guild);
		else return new ArrayList<>();
	}

	/**
	 * Sets the prefix that commands registered to this registry will be activated by.
	 * @param prefix The new prefix.
	 */
	public void setPrefix(String prefix) {
		if (prefix == null) throw new IllegalArgumentException("The new prefix cannot be null!");
		this.prefix = prefix;
	}

	/**
	 * @return The prefix that commands registered to this registry will be activated by.
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * Sets the prefix that commands registered to this registry will be activated by in a specified guild.
	 * @param guild The guild.
	 * @param prefix The new prefix.
	 */
	public void setPrefixForGuild(IGuild guild, String prefix) {
		if (!prefixes.containsKey(guild)) {
			prefixes.put(guild, prefix);
		} else {
			prefixes.replace(guild, prefix);
		}
	}

	/**
	 * Deletes the prefix in a specified guild, but only if the guild has a custom prefix.
	 * @param guild The guild.
	 */
	public void deletePrefixForGuild(IGuild guild) {
		if (prefixes.containsKey(guild)) {
			prefixes.remove(guild);
		}
	}

	/**
	 * @param guild The guild to get the prefix for.
	 * @return The prefix that commands registered to this registry will be activated by in a specified guild.
	 */
	public String getPrefixForGuild(IGuild guild) {
		if (prefixes.containsKey(guild)) {
			return prefixes.get(guild);
		} else {
			return null;
		}
	}

	/**
	 * Disables a user's ability to invoke commands in a specified guild.
	 * @param guild The guild.
	 * @param user The user.
	 */
	public void disableUserInGuild(IGuild guild, IUser user) {
		if (!disabledUsersInGuilds.containsKey(guild)) {
			disabledUsersInGuilds.put(guild, new ArrayList<>());
		}

		List<IUser> users = disabledUsersInGuilds.get(guild);

		if (!users.contains(user)) {
			users.add(user);
			disabledUsersInGuilds.replace(guild, users);
		} else {
			throw new IllegalArgumentException("Attempted to disable commands for a user in a guild that already had commands disabled!");
		}
	}

	/**
	 * Enables a user's ability to invoke commands in a specified guild, does nothing if the user can already use commands.
	 * @param guild The guild.
	 * @param user The user.
	 */
	public void enableUserInGuild(IGuild guild, IUser user) {
		if (disabledUsersInGuilds.containsKey(guild)) {
			List<IUser> users = disabledUsersInGuilds.get(guild);
			if (users.contains(user)) {
				users.remove(user);
				disabledUsersInGuilds.replace(guild, users);
			}
		}
	}

	/**
	 * Disables a user's ability to invoke commands globally.
	 * @param user The user.
	 */
	public void disableUser(IUser user) {
		if (!disabledUsers.contains(user)) {
			disabledUsers.add(user);
		} else {
			throw new IllegalArgumentException("Attempted to disable commands for a user that already had commands disabled!");
		}
	}

	/**
	 * Enables a user's ability to invoke commands globally, does nothing if the user can already use commands.
	 * @param user The user.
	 */
	public void enableUser(IUser user) {
		if (!disabledUsers.contains(user)) {
			disabledUsers.add(user);
		} else {
			throw new IllegalArgumentException("Attempted to disable commands for a user that already had commands disabled!");
		}
	}

	/**
	 * Checks if a user's ability to invoke commands is disabled in a specified server.
	 * @param guild The guild.
	 * @param user The user.
	 * @return false if user can invoke commands, true if not.
	 */
	public boolean isUserDisabledInGuild(IGuild guild, IUser user) {
		List<IUser> users;
		if (!disabledUsersInGuilds.containsKey(guild)) {
			return false;
		} else {
			users = disabledUsersInGuilds.get(guild);
			return users.contains(user);
		}
	}

	/**
	 * Checks if a user's ability to invoke commands is disabled globally.
	 * @param user The user.
	 * @return false if user can invoke commands, true if not.
	 */
	public boolean isUserDisabled(IUser user) {
		return disabledUsers.contains(user);
	}
}
