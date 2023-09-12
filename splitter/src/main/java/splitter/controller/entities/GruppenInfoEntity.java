package splitter.controller.entities;

import java.util.List;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.aggregates.domains.gruppe.Person;

public record GruppenInfoEntity(Long gruppe,
                                String name,
                                List<String> personen,
                                Boolean geschlossen,
                                List<AusgabenInfoEntity> ausgaben) {
    public static GruppenInfoEntity create(Gruppe gruppe) {
        return new GruppenInfoEntity(gruppe.getId(),
                gruppe.getBeschreibung(),
                gruppe.getPersonen().stream()
                        .map(Person::name)
                        .toList(),
                gruppe.isGeschlossen(),
                gruppe.getAusgaben().stream()
                        .map(AusgabenInfoEntity::create).toList());
    }
}
