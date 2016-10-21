package com.darichey.discord.api;

import java.util.EnumSet;

public enum FailureReason {

	/**
	 * If the author of the command-invoker is missing the Discord permissions required by
	 * {@link com.darichey.discord.api.Command#userRequiredPermissions}
	 */
	AUTHOR_MISSING_PERMISSIONS,

	/**
	 * If the bot is missing the Discord permissions required by
	 * {@link com.darichey.discord.api.Command#botRequiredPermissions}
	 */
	BOT_MISSING_DEFINED_PERMISSIONS,

	/**
	 * If the bot is missing Discord permissions (to send a message, etc.).
	 */
	BOT_MISSING_PERMISSIONS
}
