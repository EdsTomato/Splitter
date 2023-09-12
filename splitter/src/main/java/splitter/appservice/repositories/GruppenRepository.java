package splitter.appservice.repositories;

import java.util.List;
import splitter.aggregates.domains.gruppe.Gruppe;

public interface GruppenRepository {

    void save(Gruppe gruppe);

    List<Gruppe> findAll();

    Gruppe findById(Long id);

}
