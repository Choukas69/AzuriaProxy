package fr.choukas.azuria.azuria_proxy.api.events.parties;

import fr.choukas.azuria.azuria_common.parties.Party;
import fr.choukas.azuria.azuria_common.players.AbstractPlayer;

public class PartyJoinEvent extends PartyEvent {

    public PartyJoinEvent(AbstractPlayer player, Party party) {
        super(player, party);
    }
}
