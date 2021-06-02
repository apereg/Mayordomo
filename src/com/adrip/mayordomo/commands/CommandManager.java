package com.adrip.mayordomo.commands;

import java.lang.reflect.Method;
import java.util.LinkedList;

import com.adrip.mayordomo.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandManager {

	private static LinkedList<CommandGestor> commandsList = new LinkedList<>();

	private Member member;
	private Guild guild;
	private Message message;
	private TextChannel textChannel;
	private CommandType commandType;
	private String mainAlias;

	public CommandManager(Member member, Guild guild, Message message, TextChannel textChannel, String anyPrefix) {
		this.member = member;
		this.guild = guild;
		this.message = message;
		this.textChannel = textChannel;
		this.commandType = this.getCommandType(anyPrefix);
		this.mainAlias = this.getMainAlias(anyPrefix);
	}

	public boolean checkPermissions() {
		return Utils.hasPermission(member, CommandManager.isAnAdminCommand(this.commandType));
	}

	public static void registerCommands() {
		commandsList.add(new CommandGestor(CommandType.CREATE, new String[] { "create", "c", "crear" }, false, false));
		commandsList.add(new CommandGestor(CommandType.STATUS, new String[] { "status", "s", "estado" }, false, false));
		commandsList.add(new CommandGestor(CommandType.HELP, new String[] { "help", "h", "ayuda" }, false, false));
		commandsList.add(new CommandGestor(CommandType.CHANNELCAPACITY,
				new String[] { "channelcapacity", "capacity", "cc", "cap", "capacidad" }, false, false));
		commandsList.add(new CommandGestor(CommandType.CHANNELNAME,
				new String[] { "channelname", "name", "cn", "nombre" }, false, false));

		commandsList.add(new CommandGestor(CommandType.UNIQUE, new String[] { "setPrefix", "prefix" }, true, false));
		commandsList.add(new CommandGestor(CommandType.SETPREFIX, new String[] { "unique" }, true, false));

		commandsList.add(new CommandGestor(CommandType.ENABLE, new String[] { "enable", "on" }, true, true));
		commandsList.add(new CommandGestor(CommandType.DISABLE, new String[] { "disable", "off" }, true, true));
		commandsList.add(new CommandGestor(CommandType.TOGGLE, new String[] { "toggle", "switch" }, true, true));
	}

	public void executeCommand(String[] commandArgs, String commandPrefix) throws Exception {
		Commands command = new Commands(member, guild, message, textChannel, commandArgs, commandPrefix);
		String methodName = "command" + this.mainAlias.toUpperCase();
		Method method = command.getClass().getMethod(methodName);
		method.invoke(command);
	}

	public String getMainAlias(String commandPrefix) {
		for (CommandGestor command : commandsList) {
			if (command.isMyCommand(commandPrefix))
				return command.getMainAlias();
		}
		return null;
	}

	public CommandType getCommandType(String commandPrefix) {
		for (CommandGestor command : commandsList)
			if (command.isMyCommand(commandPrefix))
				return command.getCommandType();
		return null;
	}

	public static String[] getAllAliases(String commandPrefix) {
		for (CommandGestor command : commandsList)
			if (command.isMyCommand(commandPrefix))
				return command.getAliases();
		return new String[0];
	}

	public static boolean isAValidCommand(String commandPrefix) {
		return commandsList.stream().anyMatch(c -> c.isMyCommand(commandPrefix));
	}

	public static boolean isAnAdminCommand(CommandType commandType) {
		for (CommandGestor command : commandsList)
			if (command.getCommandType().equals(commandType))
				return command.isAdminCommand();
		return false;
	}

	public static boolean isAnAdminCommand(String commandPrefix) {
		for (CommandGestor command : commandsList)
			if (command.isMyCommand(commandPrefix))
				return command.isAdminCommand();
		return false;
	}

	public static boolean isAnActivationCommand(String commandPrefix) {
		for (CommandGestor command : commandsList)
			if (command.isMyCommand(commandPrefix))
				return command.isActivationCommand();
		return false;
	}

}