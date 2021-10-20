package dev.simplix.protocolize.velocity.adventure;

import java.util.Collection;
import java.util.stream.Collectors;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

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
    private AudienceType type;
    private ProtocolizePlayer player;
    private Collection<ProtocolizePlayer> players;
    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();

    private enum AudienceType{
        COLLECTION, SINGLE, NOTSUPPORTED
    }

    public ProtocolizeAudience(Audience audience){
        if(audience instanceof RegisteredServer){
            this.type = AudienceType.COLLECTION;
            this.players = ((RegisteredServer)audience).getPlayersConnected().stream()
                .map(ProtocolizeAudience::getPlayer).collect(Collectors.toList());
        } else if(audience instanceof ProxyServer){
            this.type = AudienceType.COLLECTION;
            this.players = ((ProxyServer)audience).getAllPlayers().stream()
                .map(ProtocolizeAudience::getPlayer).collect(Collectors.toList());
        } else if(audience instanceof ForwardingAudience){
            this.type = AudienceType.COLLECTION;
            for(Audience singleAudience : ((ForwardingAudience)audience).audiences()){
                if(singleAudience instanceof Player){
                    players.add(getPlayer(singleAudience));
                } else if(singleAudience instanceof RegisteredServer){
                    ((RegisteredServer)singleAudience).getPlayersConnected().stream()
                        .map(ProtocolizeAudience::getPlayer)
                        .forEach(players::add);
                } else {
                    continue;
                }
            }
        } else if(audience instanceof Player){
            this.type = AudienceType.SINGLE;
            this.player = getPlayer(audience);
        } else {
            this.type = AudienceType.NOTSUPPORTED;
        }
    }

    /**
     * Plays a {@link Sound} to the specified {@link ProtocolizeAudience}
     * @param sound The sound to be played
     * @param category The sound category
     * @param volume Sound volume
     * @param pitch Sound pitch
     */
    public void playSound(Sound sound, SoundCategory category, float volume, float pitch){
        switch(type){
            case COLLECTION:
                players.forEach(pPlayer -> pPlayer.playSound(sound, category, volume, pitch));
                break;
            case SINGLE:
                player.playSound(sound, category, volume, pitch);
                break;
            case NOTSUPPORTED: break;
        }
    }

    /**
     * Opens an {@link Inventory} to the specified {@link ProtocolizeAudience}
     * @param inventory The inventory
     */
    public void openInventory(Inventory inventory){
        switch(type){
            case COLLECTION:
                players.forEach(pPlayer -> pPlayer.openInventory(inventory));
                break;
            case SINGLE:
                player.openInventory(inventory);
                break;
            case NOTSUPPORTED: break;
        }
    }

    /**
     * Closes the open inventories of the {@link ProtocolizeAudience}
     */
    public void closeInventory(){
        switch(type){
            case COLLECTION:
                players.forEach(pPlayer -> pPlayer.closeInventory());
                break;
            case SINGLE:
                player.closeInventory();
                break;
            case NOTSUPPORTED: break;
        }
    }

    /**
     * Register an {@link Inventory}
     * @param windowId The id of the Inventory
     * @param inventory The Inventory
     */
    public void registerInventory(int windowId, Inventory inventory){
        switch(type){
            case COLLECTION:
                players.forEach(pPlayer -> pPlayer.registerInventory(windowId, inventory));
                break;
            case SINGLE:
                player.registerInventory(windowId, inventory);
                break;
            case NOTSUPPORTED: break;
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

    /**
     * Obtain a {@link ProtocolizeAudience} from an {@link Audience}
     * @param audience The Audience
     * @return The {@link ProtocolizeAudience}
     */
    public static ProtocolizeAudience of(Audience audience){
        return new ProtocolizeAudience(audience);
    }
}
