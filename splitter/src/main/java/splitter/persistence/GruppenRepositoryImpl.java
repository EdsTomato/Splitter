package splitter.persistence;

import java.util.List;
import org.springframework.stereotype.Repository;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.persistence.dto.Mapper;
import splitter.appservice.repositories.GruppenRepository;

@Repository
public class GruppenRepositoryImpl implements GruppenRepository {

    DBGruppenRepository dbGruppenRepository;

    public GruppenRepositoryImpl(DBGruppenRepository dbGruppenRepository) {
        this.dbGruppenRepository = dbGruppenRepository;
    }


    @Override
    public void save(Gruppe gruppe) {
        dbGruppenRepository.save(Mapper.gruppetoDto(gruppe));
    }

    @Override
    public List<Gruppe> findAll() {
        return dbGruppenRepository.findAll().stream()
                .map(Mapper::dtotoGruppe)
                .toList();
    }

    @Override
    public Gruppe findById(Long id) {
        return Mapper.dtotoGruppe(dbGruppenRepository.findById(id).get());
    }
}
