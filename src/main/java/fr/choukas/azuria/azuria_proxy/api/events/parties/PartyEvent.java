package fr.choukas.azuria.azuria_proxy.api.events.parties;

import fr.choukas.azuria.azuria_common.parties.Party;
import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import net.md_5.bungee.api.plugin.Event;

public abstract class PartyEvent extends Event {

    private AbstractPlayer player;
    private Party party;

    public PartyEvent(AbstractPlayer player, Party party) {
        this.player = player;
        this.party = party;
    }

    public AbstractPlayer getPlayer() {
        return this.player;
    }

    public Party getParty() {
        return this.party;
    }
}
