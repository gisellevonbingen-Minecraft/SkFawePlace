package giselle.skript.fawe_place;

import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.AbstractNonPlayerActor;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.Component;

public class PluginActor extends AbstractNonPlayerActor
{
	private static final UUID DEFAULT_ID = UUID.fromString("a233eb4b-4cab-42cd-9fd9-7e7b9a3f74bf");

	private final CommandSender sender;
	private final SkriptFAWE plugin;

	public PluginActor(SkriptFAWE plugin, CommandSender sender)
	{
		this.plugin = plugin;
		this.sender = sender;
	}

	@Override
	public UUID getUniqueId()
	{
		return DEFAULT_ID;
	}

	@Override
	public String getName()
	{
		return this.plugin.getName();
	}

	@Override
	@Deprecated
	public void printRaw(String msg)
	{
		for (String part : msg.split("\n"))
		{
			this.sender.sendMessage(part);
		}

	}

	@Override
	@Deprecated
	public void print(String msg)
	{
		for (String part : msg.split("\n"))
		{
			this.sender.sendMessage("§d" + part);
		}

	}

	@Override
	@Deprecated
	public void printDebug(String msg)
	{
		for (String part : msg.split("\n"))
		{
			this.sender.sendMessage("§7" + part);
		}
	}

	@Override
	@Deprecated
	public void printError(String msg)
	{
		for (String part : msg.split("\n"))
		{
			this.sender.sendMessage("§c" + part);
		}
	}

	@Override
	public void print(Component component)
	{
		this.sender.sendMessage(WorldEditText.reduceToText(component, this.getLocale()));
	}

	@Override
	public String[] getGroups()
	{
		return new String[0];
	}

	@Override
	public boolean hasPermission(String perm)
	{
		return true;
	}

	public void setPermission(String permission, boolean value)
	{

	}

	@Override
	public void checkPermission(String permission) throws AuthorizationException
	{

	}

	@Override
	public Locale getLocale()
	{
		return WorldEdit.getInstance().getConfiguration().defaultLocale;
	}

	public CommandSender getSender()
	{
		return this.sender;
	}

	@Override
	public SessionKey getSessionKey()
	{
		return new SessionKey()
		{
			@Nullable
			@Override
			public String getName()
			{
				return PluginActor.this.sender.getName();
			}

			@Override
			public boolean isActive()
			{
				if (PluginActor.this.sender instanceof Entity entity)
				{
					return entity.isValid() && !entity.isDead();
				}

				return true;
			}

			@Override
			public boolean isPersistent()
			{
				return true;
			}

			@Override
			public UUID getUniqueId()
			{
				return DEFAULT_ID;
			}

		};

	}

}
