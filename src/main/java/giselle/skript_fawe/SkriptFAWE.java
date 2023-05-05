package giselle.skript_fawe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.plugin.java.JavaPlugin;

import com.fastasyncworldedit.core.configuration.Settings;
import com.fastasyncworldedit.core.util.MainUtil;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.session.ClipboardHolder;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

public class SkriptFAWE extends JavaPlugin
{
	private static SkriptFAWE instance;

	public static SkriptFAWE instance()
	{
		return instance;
	}

	private PluginActor pluginActor;

	public SkriptFAWE()
	{
		instance = this;
	}

	@Override
	public void onEnable()
	{
		super.onEnable();

		SkriptAddon addon = Skript.registerAddon(this);

		try
		{
			addon.loadClasses(this.getClass().getPackageName(), "elements");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		this.pluginActor = new PluginActor(this, Bukkit.getConsoleSender());
	}

	public PluginActor getPluginActor()
	{
		return this.pluginActor;
	}

	public ClipboardHolder load(Actor actor, String fileName, String formatName) throws IOException, URISyntaxException
	{
		WorldEdit worldEdit = WorldEdit.getInstance();
		LocalConfiguration config = worldEdit.getConfiguration();

		// FAWE start
		ClipboardFormat format = null;
		InputStream in = null;
		try
		{
			URI uri;
			if (formatName.startsWith("url:"))
			{
				String t = fileName;
				fileName = formatName;
				formatName = t;
			}
			if (fileName.startsWith("url:"))
			{
				if (!actor.hasPermission("worldedit.schematic.load.web"))
				{
					throw new CommandException("worldedit.schematic.load.web");
				}

				UUID uuid = UUID.fromString(fileName.substring(4));
				URL webUrl = new URL(Settings.settings().WEB.URL);
				format = ClipboardFormats.findByAlias(formatName);
				URL url = new URL(webUrl, "uploads/" + uuid + "." + format.getPrimaryFileExtension());
				ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
				in = Channels.newInputStream(byteChannel);
				uri = url.toURI();
			}
			else
			{
				File saveDir = worldEdit.getWorkingDirectoryPath(config.saveDir).toFile();
				File dir = Settings.settings().PATHS.PER_PLAYER_SCHEMATICS ? new File(saveDir, actor.getUniqueId().toString()) : saveDir;
				File file;

				if (fileName.startsWith("#"))
				{
					format = ClipboardFormats.findByAlias(formatName);
					String[] extensions;

					if (format != null)
					{
						extensions = format.getFileExtensions().toArray(new String[0]);
					}
					else
					{
						extensions = ClipboardFormats.getFileExtensionArray();
					}

					file = actor.openFileOpenDialog(extensions);

					if (file == null)
					{
						throw new FileNotFoundException();
					}
					else if (!file.exists())
					{
						throw new FileNotFoundException(file.getPath());
					}

				}
				else
				{
					if (Settings.settings().PATHS.PER_PLAYER_SCHEMATICS && !actor.hasPermission("worldedit.schematic.load.other") && Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}").matcher(fileName).find())
					{
						throw new CommandException("worldedit.schematic.load.other");
					}

					if (fileName.matches(".*\\.[\\w].*"))
					{
						format = ClipboardFormats.findByExtension(fileName.substring(fileName.lastIndexOf('.') + 1));
					}
					else
					{
						format = ClipboardFormats.findByAlias(formatName);
					}

					file = MainUtil.resolve(dir, fileName, format, false);
				}

				if (file == null || !file.exists())
				{
					if (!fileName.contains("../"))
					{
						dir = worldEdit.getWorkingDirectoryPath(config.saveDir).toFile();
						file = MainUtil.resolve(dir, fileName, format, false);
					}

				}

				if (file == null || !file.exists() || !MainUtil.isInSubDirectory(saveDir, file))
				{
					throw new CommandException("Schematic " + fileName + " does not exist! (" + (file != null && file.exists()) + "|" + file + "|" + (file != null && !MainUtil.isInSubDirectory(saveDir, file)) + ")");
				}

				if (format == null)
				{
					format = ClipboardFormats.findByFile(file);

					if (format == null)
					{
						throw new CommandException("Unnown format:  " + formatName);
					}

				}

				in = new FileInputStream(file);
				uri = file.toURI();
			}

			return format.hold(actor, uri, in);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException ignored)
				{

				}

			}

		}

	}

}
