package com.darichey.discord;

import com.darichey.discord.api.Command;
import com.darichey.discord.api.CommandRegistry;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;

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
			client = new JDABuilder().setBotToken(TOKEN).buildAsync();

			Command test = new Command("test")
					.withAliases("tast", "trst")
					.onExecuted(context ->
						sendMessage(context.getMessage().getTextChannel(), "Pong!")
					);

			CommandRegistry.getForClient(client).register(test);

		} catch (LoginException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(TextChannel channel, String message) {
		channel.sendMessage(message);
	}
}
