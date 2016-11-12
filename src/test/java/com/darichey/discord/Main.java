package com.darichey.discord;

import com.darichey.discord.api.Command;
import com.darichey.discord.api.CommandRegistry;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	static JDA client;
	static String TOKEN = "";

	public static void main(String[] args) {
		try (BufferedReader reader = new BufferedReader(new FileReader("token.txt"))) {
			TOKEN = reader.readLine();
			client = new JDABuilder(AccountType.BOT).setToken(TOKEN).buildAsync();

			Command test = new Command("test")
					.withAliases("tast", "trst")
					.onExecuted(context ->
						context.getTextChannel().sendMessage("Pong!")
					);

			CommandRegistry.getForClient(client).register(test);

		} catch (LoginException | IOException | RateLimitedException e) {
			e.printStackTrace();
		}
	}
}
