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
import net.kyori.adventure.identity.Identity;

/**
 * Date: 19.10.2021
 *
 * @author 4drian3d
 */
public class ProtocolizeAudience {
    private AudienceType TYPE;
    private ProtocolizePlayer PLAYER;
    private Collection<ProtocolizePlayer> PLAYERS;
    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();

    private enum AudienceType{
        COLLECTION, SINGLE, NOTSUPPORTED
    }

    /**
     * Obtain a {@link ProtocolizeAudience} from an {@link Audience}
     * @param audience The Audience
     */
    public ProtocolizeAudience(Audience audience){
        if(audience instanceof RegisteredServer){
            this.TYPE = AudienceType.COLLECTION;
            this.PLAYERS = ((RegisteredServer)audience).getPlayersConnected().stream()
                .map(player -> PLAYER_PROVIDER.player(player.getUniqueId()))
                .collect(Collectors.toList());
        } else if(audience instanceof ProxyServer){
            this.TYPE = AudienceType.COLLECTION;
            this.PLAYERS = ((ProxyServer)audience).getAllPlayers().stream()
                .map(player -> PLAYER_PROVIDER.player(player.getUniqueId()))
                .collect(Collectors.toList());
        } else if(audience instanceof ForwardingAudience){
            this.TYPE = AudienceType.COLLECTION;
            for(Audience singleAudience : ((ForwardingAudience)audience).audiences()){
                if(singleAudience instanceof Player){
                    this.PLAYERS.add(PLAYER_PROVIDER.player(singleAudience.get(Identity.UUID)
                        .orElse(((Player)singleAudience).getUniqueId())));
                } else if(singleAudience instanceof RegisteredServer){
                    ((RegisteredServer)singleAudience).getPlayersConnected().stream()
                        .map(player -> PLAYER_PROVIDER.player(player.getUniqueId()))
                        .forEach(this.PLAYERS::add);
                } else if(singleAudience instanceof ForwardingAudience) {
                    for(Audience miniAudience : ((ForwardingAudience)singleAudience).audiences()){
                        if(miniAudience instanceof Player){
                            this.PLAYERS.add(PLAYER_PROVIDER.player(miniAudience.get(Identity.UUID)
                                .orElse(((Player)singleAudience).getUniqueId())));
                        } else if(miniAudience instanceof RegisteredServer){
                            ((RegisteredServer)miniAudience).getPlayersConnected().stream()
                                .map(player -> PLAYER_PROVIDER.player(player.getUniqueId()))
                                .forEach(this.PLAYERS::add);
                        } else {
                            /*
                            It does not make sense to include the ProxyServer in a FowardingAudience
                            */
                        }
                    }
                }
            }
        } else if(audience instanceof Player){
            this.TYPE = AudienceType.SINGLE;
            this.PLAYER = getPlayer(audience);
        } else {
            this.TYPE = AudienceType.NOTSUPPORTED;
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
        switch(TYPE){
            case COLLECTION:
                this.PLAYERS.forEach(pPlayer -> pPlayer.playSound(sound, category, volume, pitch));
                break;
            case SINGLE:
                this.PLAYER.playSound(sound, category, volume, pitch);
                break;
            case NOTSUPPORTED: break;
        }
    }

    /**
     * Opens an {@link Inventory} to the specified {@link ProtocolizeAudience}
     * @param inventory The inventory
     */
    public void openInventory(Inventory inventory){
        switch(TYPE){
            case COLLECTION:
                this.PLAYERS.forEach(pPlayer -> pPlayer.openInventory(inventory));
                break;
            case SINGLE:
                this.PLAYER.openInventory(inventory);
                break;
            case NOTSUPPORTED: break;
        }
    }

    /**
     * Closes the open inventories of the {@link ProtocolizeAudience}
     */
    public void closeInventory(){
        switch(TYPE){
            case COLLECTION:
                this.PLAYERS.forEach(pPlayer -> pPlayer.closeInventory());
                break;
            case SINGLE:
                this.PLAYER.closeInventory();
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
        switch(TYPE){
            case COLLECTION:
                this.PLAYERS.forEach(pPlayer -> pPlayer.registerInventory(windowId, inventory));
                break;
            case SINGLE:
                this.PLAYER.registerInventory(windowId, inventory);
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
}
