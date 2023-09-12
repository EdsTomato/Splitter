package splitter.persistence.dto;

import java.util.List;
import splitter.aggregates.domains.gruppe.*;

public class Mapper {

    public static Gruppendto gruppetoDto(Gruppe gruppe) {
        List<String> namen = gruppe.getPersonen().stream()
                .map(Person::name)
                .toList();
        return new Gruppendto(gruppe.getId(),
                gruppe.getErstellername(),
                gruppe.getBeschreibung(),
                gruppe.isGeschlossen(),
                namen,
                gruppe.getVersion());

    }

    public static Ausgabendto ausgabetoDto(Ausgabe ausgabe, Long gruppenId) {
        List<String> namen = ausgabe.getTeilhaber().stream()
                .map(Person::name)
                .toList();
        return new Ausgabendto(null,
                ausgabe.getErsteller().name(),
                ausgabe.getSaldo().getWert(),
                ausgabe.getVerwendungszweck(),
                namen,
                gruppenId);
    }

    public static Gruppe dtotoGruppe(Gruppendto gruppendto) {
        Gruppe gruppe = Gruppe.gruppeKreieren(gruppendto.erstellername(),
                gruppendto.beschreibung(),
                gruppendto.id());
        if (!gruppendto.mitglieder().contains(gruppendto.erstellername())) {
            gruppe = Gruppe.gruppeOhneErsteller(gruppendto.erstellername(),
                    gruppendto.beschreibung(),
                    gruppendto.id());
        }
        List<String> personenNamen = gruppendto.mitglieder().stream()
                .filter(n -> !n.equals(gruppendto.erstellername()))
                .toList();
        for (String p : personenNamen) {
            gruppe.addPerson(new Person(p, new Saldo(0)));
        }
        if (gruppendto.isgeschlossen()) {
            gruppe.schliessen();
        }
        gruppe.setVersion(gruppendto.version());
        return gruppe;
    }

    public static Ausgabe dtotoAusgabe(Ausgabendto ausgabendto) {
        return new Ausgabe(new Person(ausgabendto.ersteller(), new Saldo(0)),
                ausgabendto.teilhaber().stream()
                        .map(n -> new Person(n, new Saldo(0)))
                        .toList(),
                new Saldo(ausgabendto.saldo()),
                ausgabendto.verwendungszweck());
    }

}
