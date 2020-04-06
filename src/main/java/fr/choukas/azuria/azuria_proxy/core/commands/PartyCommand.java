package fr.choukas.azuria.azuria_proxy.core.commands;

import fr.choukas.azuria.azuria_common.parties.Party;
import fr.choukas.azuria.azuria_common.parties.PartyInvitation;
import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.commands.BungeeCommandExecutor;
import fr.choukas.azuria.azuria_proxy.api.events.parties.PartyJoinEvent;
import fr.choukas.azuria.azuria_proxy.api.events.parties.PartyLeaveEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PartyCommand implements BungeeCommandExecutor {

    private AzuriaProxy proxy;

    public PartyCommand(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            AbstractPlayer dataSender = proxy.getPlayerManager().getPlayers().withUniqueId(((ProxiedPlayer) sender).getUniqueId()).get();

            switch (args[0]) {
                case "accept":
                    if (dataSender.getPartyInvitation() != null) {
                        Party party = dataSender.getPartyInvitation().getParty();

                        if (proxy.getPartyManager().getParties().getList().contains(party)) {
                            proxy.getProxy().getPluginManager().callEvent(new PartyJoinEvent(dataSender, party));

                            dataSender.setPartyInvitation(null);
                        } else {
                            dataSender.sendMessage(new TextComponent("La partie n'existe plus"));
                        }
                    } else {
                        dataSender.sendMessage(new TextComponent("Vous n'avez pas reçu d'invitation"));
                    }

                    break;

                case "deny":
                    if (dataSender.getPartyInvitation() != null) {
                        dataSender.setPartyInvitation(null);
                    } else {
                        dataSender.sendMessage(new TextComponent("Vous n'avez pas reçu d'invitation"));
                    }

                    break;

                case "disband":
                    if (dataSender.getParty() != null) {
                        if (dataSender.getParty().getOwner() == dataSender) {
                            Party party = dataSender.getParty();

                            party.disband();

                            proxy.getPartyManager().removeParty(party.getUniqueId());
                        } else {
                            dataSender.sendMessage(new TextComponent("Seul le propriétaire peut dissoudre la partie"));
                        }
                    } else {
                        dataSender.sendMessage(new TextComponent("Vous n'êtes dans aucune partie"));
                    }

                    break;

                case "kick":
                    if (dataSender.getParty() != null) {
                        if (dataSender.getParty().getOwner() == dataSender) {
                            if (!args[1].equals(dataSender.getName())) {
                                UUID targetUuid = ProxyServer.getInstance().getPlayer(args[1]).getUniqueId();
                                AbstractPlayer target = dataSender.getParty().getPlayers().withUniqueId(targetUuid).get();

                                if (target != null) {
                                    String message = "Vous avez été exclu de la partie";
                                    if (args.length == 3) {
                                        // If a message has been specified, add it
                                        message += " pour la raison suivante : " + args[2];
                                    }

                                    target.sendMessage(new TextComponent(message));

                                    proxy.getProxy().getPluginManager().callEvent(new PartyLeaveEvent(target, dataSender.getParty()));
                                } else {
                                    dataSender.sendMessage(new TextComponent("Ce joueur n'est pas dans la partie"));
                                }
                            } else {
                                dataSender.sendMessage(new TextComponent("Vous ne pouvez pas vous exclure vous-même"));
                            }
                        }
                    }

                    break;

                case "invite":
                    if (dataSender.getParty().getOwner() == dataSender) {
                        if (!args[1].equals(dataSender.getName())) {
                            UUID targetUuid = ProxyServer.getInstance().getPlayer(args[1]).getUniqueId();

                            if (targetUuid != null) {
                                AbstractPlayer target = proxy.getPlayerManager().getPlayers().withUniqueId(targetUuid).get();

                                if (target.getParty() == null) {
                                    if (target.getPartyInvitation() == null) {
                                        if (dataSender.getParty() == null) {
                                            // If dataSender hasn't a party, create it
                                            Party party = proxy.getPartyManager().createParty(dataSender);
                                            party.addPlayer(dataSender);
                                        }

                                        PartyInvitation invitation = new PartyInvitation(dataSender, dataSender.getParty());
                                        invitation.send(target);

                                        proxy.getSchedulerManager().runTaskLater(() -> {
                                            if (target.getPartyInvitation() != null) {
                                                target.setPartyInvitation(null);
                                                target.sendMessage(new TextComponent("L'invitation a expiré"));
                                                sender.sendMessage(new TextComponent("L'invitation a expiré"));
                                            }
                                        }, 60, TimeUnit.SECONDS);
                                    } else {
                                        dataSender.sendMessage(new TextComponent("Ce joueur a déjà reçu une invitation. Attendez qu'il y réponde"));
                                    }
                                } else {
                                    dataSender.sendMessage(new TextComponent("Ce joueur est déjà dans une partie"));
                                }
                            } else {
                                dataSender.sendMessage(new TextComponent("Ce joueur n'est pas connecté"));
                            }
                        } else {
                            dataSender.sendMessage(new TextComponent("Vous ne pouvez pas vous inviter vous même"));
                        }
                    } else {
                        dataSender.sendMessage(new TextComponent("Seul le propriétaire peut inviter un joueur dans la partie"));
                    }

                    break;

                case "list":
                    if (dataSender.getParty() != null) {
                        Party party = dataSender.getParty();

                        List<AbstractPlayer> players = party.getPlayers().getList();

                        dataSender.sendMessage(new TextComponent("Il y a " + players.size() + " joueurs dans votre partie :"));

                        StringBuilder playersListBuilder = new StringBuilder();
                        int i = 1;
                        for (AbstractPlayer player : players) {
                            playersListBuilder.append(player.getName());
                            if (i < party.getSize())
                                playersListBuilder.append(", ");
                            i++;
                        }

                        dataSender.sendMessage(new TextComponent(playersListBuilder.toString()));
                    } else {
                        dataSender.sendMessage(new TextComponent("Vous n'êtes dans aucune partie"));
                    }

                    break;
            }
        }
    }
}
