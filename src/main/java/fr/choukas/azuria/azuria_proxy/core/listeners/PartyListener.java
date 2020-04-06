package fr.choukas.azuria.azuria_proxy.core.listeners;

import fr.choukas.azuria.azuria_common.parties.Party;
import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.events.parties.PartyJoinEvent;
import fr.choukas.azuria.azuria_proxy.api.events.parties.PartyLeaveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyListener implements Listener {

    private AzuriaProxy proxy;

    public PartyListener(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @EventHandler
    public void onPartyJoin(PartyJoinEvent event) {
        AbstractPlayer player = event.getPlayer();
        Party party = event.getParty();

        party.broadcast(new TextComponent(player.getName() + " a rejoint la partie !"));

        party.addPlayer(player);
    }

    @EventHandler
    public void onPartyLeave(PartyLeaveEvent event) {
        AbstractPlayer player = event.getPlayer();
        Party party = event.getParty();

        party.removePlayer(player);

        if (party.getOwner() == player) {
            // Disband the party if the player was the party owner
            party.disband();

            proxy.getPartyManager().unloadParty(party.getUniqueId());
        } else {
            if (party.getSize() == 0) {
                // Remove the party from parties list and cache if there is anyone else in the party
                proxy.getPartyManager().unloadParty(party.getUniqueId());
            } else {
                party.broadcast(new TextComponent(player.getName() + " a quitté la partie"));
            }
        }
    }
}
