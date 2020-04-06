package fr.choukas.azuria.azuria_proxy.api.parties;

import fr.choukas.azuria.azuria_common.parties.AbstractPartyManager;
import fr.choukas.azuria.azuria_common.parties.Party;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;

import java.util.UUID;

public class PartyManager extends AbstractPartyManager {

    private AzuriaProxy proxy;

    public PartyManager(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void loadParty(UUID uuid) {
        Party party = proxy.getRedisManager().getPartiesManager().getParty(uuid);

        this.parties.put(uuid, party);
    }

    @Override
    public void unloadParty(UUID uuid) {
        this.parties.remove(uuid);

        proxy.getRedisManager().getPartiesManager().deleteParty(uuid);
    }
}
