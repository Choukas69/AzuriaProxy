package fr.choukas.azuria.azuria_proxy.api.events.parties;

import fr.choukas.azuria.azuria_common.parties.Party;
import fr.choukas.azuria.azuria_common.players.AbstractPlayer;

public class PartyLeaveEvent extends PartyEvent {

    public PartyLeaveEvent(AbstractPlayer player, Party party) {
        super(player, party);
    }
}
