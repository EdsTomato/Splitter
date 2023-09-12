package splitter.persistence;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import splitter.persistence.dto.Ausgabendto;

public interface DBAusgabenRepository extends CrudRepository<Ausgabendto, Long> {

    List<Ausgabendto> findAll();

}
