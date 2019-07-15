/*
 *   Copyright (C) 2019 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.MarriageMaster.Bungee.SpecialInfoWorker;

import at.pcgamingfreaks.Bungee.Message.Message;
import at.pcgamingfreaks.Bungee.Message.MessageBuilder;
import at.pcgamingfreaks.MarriageMaster.Bungee.MarriageMaster;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Reflection;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class NoDatabaseWorker extends Command implements Listener
{
	private MarriageMaster plugin;
	private Message messageDBProblem;

	public NoDatabaseWorker(MarriageMaster plugin)
	{
		super("marry", "marry.reload");
		this.plugin = plugin;
		plugin.getProxy().getPluginManager().registerCommand(plugin, this);
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
		messageDBProblem = new MessageBuilder("Marriage Master", MessageColor.GOLD).append(" failed to connect to it's database!", MessageColor.RED).appendNewLine()
				.append("Please check your configuration and reload the plugin (", MessageColor.RED).append("/marry reload", MessageColor.BLUE).command("/marry reload").append(")!", MessageColor.RED).getMessage();
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onJoin(final PostLoginEvent event)
	{
		if(event.getPlayer() != null && event.getPlayer().hasPermission("marry.reload"))
		{
			plugin.getProxy().getScheduler().schedule(plugin, () -> {
				if(event.getPlayer() != null && event.getPlayer().hasPermission("marry.reload"))
				{
					messageDBProblem.send(event.getPlayer());
				}
			}, 10, TimeUnit.SECONDS);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] strings)
	{
		if(strings.length != 1 || !strings[0].equalsIgnoreCase("reload"))
		{
			commandSender.sendMessage(MessageColor.RED + "Only \"/marry reload\" is available at the moment!");
		}
		else
		{
			plugin.getProxy().getPluginManager().unregisterCommand(this);
			plugin.getProxy().getPluginManager().unregisterListener(this);
			try
			{
				plugin.getConfig().reload();
				Reflection.getMethod(plugin.getClass(), "load").invoke(plugin);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}