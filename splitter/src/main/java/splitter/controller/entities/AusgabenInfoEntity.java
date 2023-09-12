package splitter.controller.entities;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import splitter.aggregates.domains.gruppe.Ausgabe;
import splitter.aggregates.domains.gruppe.Person;

public record AusgabenInfoEntity(@NotEmpty String grund,
                                 @NotEmpty String glaeubiger,
                                 @NotNull Integer cent,
                                 @NotEmpty List<String> schuldner) {
    public static AusgabenInfoEntity create(Ausgabe ausgabe) {
        return new AusgabenInfoEntity(ausgabe.getVerwendungszweck(),
                ausgabe.getErsteller().name(),
                (int) (ausgabe.getSaldo().getWert() * 100),
                ausgabe.getTeilhaber().stream()
                        .map(Person::name).toList());
    }
}
