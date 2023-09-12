package splitter.appservice.repositories;

import java.util.List;
import splitter.aggregates.domains.gruppe.Ausgabe;

public interface AusgabenRepository {
    void save(Ausgabe ausgabe, Long gruppenId);

    List<Ausgabe> findAll(Long gruppenId);

}
