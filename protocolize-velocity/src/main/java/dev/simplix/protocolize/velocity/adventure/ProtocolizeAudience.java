package dev.simplix.protocolize.velocity.adventure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.velocitypowered.api.proxy.Player;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.data.Sound;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;

/**
 * Date: 19.10.2021
 *
 * @author 4drian3d
 */
public class ProtocolizeAudience {
    private final Collection<ProtocolizePlayer> players = new HashSet<>();
    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();

    /**
     * Obtain a {@link ProtocolizeAudience} from an {@link Audience}
     * @param audience The Audience
     */
    public ProtocolizeAudience(Audience audience){
        Objects.requireNonNull(audience, "the audience cannot be null");
        if(audience instanceof Player){
            players.add(getPlayer(audience));
        } else if(audience instanceof ForwardingAudience.Single){
            Audience singleAudience = ((ForwardingAudience.Single)audience).audience();
            if(singleAudience instanceof Player){
                players.add(getPlayer(singleAudience));
            }
        } else if(audience instanceof ForwardingAudience){
            checkAndAddPlayers((ForwardingAudience)audience);
        }
    }

    /**
     * Obtain a {@link ProtocolizeAudience} from an {@link Audience}
     * @param audience The Audience
     * @return The {@link ProtocolizeAudience}
     */
    public static ProtocolizeAudience audience(Audience audience){
        return new ProtocolizeAudience(audience);
    }

    /**
     * Plays a {@link Sound} to the specified {@link ProtocolizeAudience}
     * @param sound The sound to be played
     * @param category The sound category
     * @param volume Sound volume
     * @param pitch Sound pitch
     */
    public void playSound(Sound sound, SoundCategory category, float volume, float pitch){
        for(ProtocolizePlayer player : players){
            player.playSound(sound, category, volume, pitch);
        }
    }

    /**
     * Opens an {@link Inventory} to the specified {@link ProtocolizeAudience}
     * @param inventory The inventory
     */
    public void openInventory(Inventory inventory){
        for(ProtocolizePlayer player : players){
            player.openInventory(inventory);
        }
    }

    /**
     * Closes the open inventories of the {@link ProtocolizeAudience}
     */
    public void closeInventory(){
        for(ProtocolizePlayer player : players){
            player.closeInventory();
        }
    }

    /**
     * Register an {@link Inventory}
     * @param windowId The id of the Inventory
     * @param inventory The Inventory
     */
    public void registerInventory(int windowId, Inventory inventory){
        for(ProtocolizePlayer player : players){
            player.registerInventory(windowId, inventory);
        }
    }

    /**
     * Get a {@link Player} from an {@link Audience}
     * @param audience The Player Audience
     * @return The Player
     */
    private static ProtocolizePlayer getPlayer(Audience audience){
        Player player = (Player) audience;
        return PLAYER_PROVIDER.player(player.getUniqueId());
    }

    private void checkAndAddPlayers(ForwardingAudience fAudience){
        for(Audience audience : fAudience.audiences()){
            if(audience instanceof Player){
                players.add(getPlayer(audience));
            } else if(audience instanceof ForwardingAudience.Single){
                Audience singleAudience = ((ForwardingAudience.Single)audience).audience();
                if(singleAudience instanceof Player){
                    players.add(getPlayer(singleAudience));
                }
            } else if(audience instanceof ForwardingAudience){
                checkAndAddPlayers((ForwardingAudience)audience);
            }
        }
    }
}
