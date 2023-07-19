package art.ameliah.laby.addons.library.commands.core.commands;

import art.ameliah.laby.addons.library.commands.core.commands.customTypes.MyCustomCommandContext;
import art.ameliah.laby.addons.library.commands.core.models.CommandClass;
import art.ameliah.laby.addons.library.commands.core.models.annotations.Command;
import art.ameliah.laby.addons.library.commands.core.models.types.defaults.player.PlayerType;
import art.ameliah.laby.addons.library.commands.core.models.types.defaults.pos.PositionType;
import art.ameliah.laby.addons.library.commands.core.models.types.defaults.uuid.UUIDType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.Entity;
import net.labymod.api.client.world.block.BlockState;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.math.vector.FloatVector3;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class DefaultCustomTypes extends CommandClass<MyCustomCommandContext> {

  @Command
  public boolean entityLocation(MyCustomCommandContext ctx, PlayerType entity) {
    Entity entity1 = entity.get();
    if (entity1 != null) {
      ctx.displayClientMessage("Entity is @ location: " + entity1.position());
    } else {
      ctx.displayClientMessage("Entity was null");
    }
    return true;
  }

  @Command
  public boolean blockAt(MyCustomCommandContext ctx, PositionType pos) {
    assert pos != null;
    FloatVector3 vector = pos.get();
    if (vector == null) {
      ctx.displayClientMessage("Vector was null");
      return true;
    }
    BlockState block = Laby.labyAPI().minecraft().clientWorld()
        .getBlockState((int) vector.getX(), (int) vector.getY(),
            (int) vector.getZ());
    ctx.displayClientMessage("Found block: " + block.block() + " @ " + vector);
    return true;
  }

  @Command
  public boolean uuidToPlayer(MyCustomCommandContext ctx, UUIDType uuidType) {
    assert uuidType != null;
    UUID uuid = uuidType.get();
    ctx.displayClientMessage("Got uuid: " + uuid);
    Result<String> result =  Laby.labyAPI().labyNetController().loadNameByUniqueIdSync(uuid);
    if (result.hasException()) {
      ctx.displayClientMessage("Encountered an exception getting name");
    } else {
      ctx.displayClientMessage("Has name: " + result.get());
    }
    return true;
  }

  @Override
  public Component noPermissionComponent() {
    return Component.text("No perms", NamedTextColor.RED);
  }

  @Override
  public @NotNull Class<MyCustomCommandContext> getCommandContextClass() {
    return MyCustomCommandContext.class;
  }
}
