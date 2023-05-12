package giselle.skript_fawe.elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.event.Event;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import giselle.skript.fawe_place.PluginActor;
import giselle.skript.fawe_place.SkriptFAWE;

public class PlaceEffect extends Effect
{
	static
	{
		Skript.registerEffect(PlaceEffect.class, "fawe_place %string% [as %-string%] %location% [rotate by %-number%]");
	}

	private Expression<String> fileName = null;
	private Expression<String> formatName = null;
	private Expression<Location> location = null;
	private Expression<Number> rotate = null;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult)
	{
		int i = 0;
		this.fileName = (Expression<String>) exprs[i++];
		this.formatName = (Expression<String>) exprs[i++];
		this.location = (Expression<Location>) exprs[i++];
		this.rotate = (Expression<Number>) exprs[i++];
		return true;
	}

	@Override
	public String toString(Event e, boolean debug)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("fawe_place ").append(this.fileName.toString(e, debug));

		if (this.formatName != null)
		{
			builder.append(" as ").append(this.formatName.toString(e, debug));
		}

		builder.append(" ").append(this.location.toString(e, debug));

		if (this.rotate != null)
		{
			builder.append(" rotate by ").append(this.rotate.toString(e, debug));
		}

		return builder.toString();
	}

	@Override
	protected void execute(Event e)
	{
		String fileName = this.fileName.getSingle(e);
		String formatName = Optional.ofNullable(this.formatName).map(f -> f.getSingle(e)).orElse("fast");
		Location location = this.location.getSingle(e);
		Number rotate = Optional.ofNullable(this.rotate).map(f -> f.getSingle(e)).orElse(null);

		SkriptFAWE plugin = SkriptFAWE.instance();
		PluginActor actor = plugin.getPluginActor();

		try
		{
			ClipboardHolder holder = plugin.load(actor, fileName, formatName);
			com.sk89q.worldedit.world.World fawe_world = FaweAPI.getWorld(location.getWorld().getName());
			BlockVector3 at = BlockVector3.at(location.getX(), location.getY(), location.getZ());

			if (rotate != null)
			{
				AffineTransform transform = new AffineTransform();
				transform = transform.rotateY(-rotate.doubleValue());
				holder.setTransform(transform.combine(holder.getTransform()));
			}

			PasteBuilder paste = holder.createPaste(fawe_world);
			Operation operation = paste.to(at).build();
			Operations.completeLegacy(operation);
		}
		catch (IOException | URISyntaxException ex)
		{
			throw new RuntimeException(ex);
		}

	}

}
