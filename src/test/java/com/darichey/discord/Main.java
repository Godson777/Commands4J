package com.darichey.discord;

import com.darichey.discord.api.Command;
import com.darichey.discord.api.CommandRegistry;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
						context.getTextChannel().sendMessage("Pong!").queue()
					);

			Command wew = new Command("wew")
					.onExecuted(context -> context.getTextChannel().sendMessage("lad").queue());

			CommandRegistry.getForClient(client).register(test);

			client.addEventListener(new ListenerAdapter() {
				@Override
				public void onReady(ReadyEvent event) {
					client.getGuilds().forEach(guild -> {
						if (guild.getId().equals("221910104495095808")) CommandRegistry.getForClient(client).customRegister(wew, guild);
					});
				}
			});

		} catch (LoginException | IOException | RateLimitedException e) {
			e.printStackTrace();
		}
	}
}
