package splitter.persistence;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import splitter.persistence.dto.Gruppendto;

public interface DBGruppenRepository extends CrudRepository<Gruppendto, Long> {

    public List<Gruppendto> findAll();

}
