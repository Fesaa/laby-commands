package art.ameliah.brigadier.v1_20_1;

import art.ameliah.brigadier.core.linkers.BrigadierLinker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.SharedSuggestionProvider;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Implements(BrigadierLinker.class)
public class BrigadierLink extends BrigadierLinker {

  @Inject
  public BrigadierLink() {}

  @Override
  public void register() {

    Minecraft mc = Minecraft.getInstance();
    ClientPacketListener connection = mc.getConnection();
    if (connection == null) {
      System.out.println("CONNECTION WAS NULL");
      return;
    }

    this.registered = true;

    CommandDispatcher<SharedSuggestionProvider> dispatcher = connection.getCommands();

    LiteralArgumentBuilder<SharedSuggestionProvider> lit = LiteralArgumentBuilder.literal("foo");
    lit = lit.executes(c -> {
      System.out.println("Called foo with no arguments");
      return 1;
    });
    dispatcher.register(lit);
  }
}
