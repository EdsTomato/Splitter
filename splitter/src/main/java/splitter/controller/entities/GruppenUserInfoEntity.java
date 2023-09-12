package splitter.controller.entities;

import java.util.List;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.aggregates.domains.gruppe.Person;

public record GruppenUserInfoEntity(Long gruppe, String name, List<String> personen) {
    public static GruppenUserInfoEntity create(Gruppe gruppe) {
        List<String> personen = gruppe.getPersonen().stream().map(Person::name).toList();
        return new GruppenUserInfoEntity(gruppe.getId(), gruppe.getBeschreibung(), personen);
    }
}
