package splitter.persistence;

import java.util.List;
import org.springframework.stereotype.Repository;
import splitter.aggregates.domains.gruppe.Ausgabe;
import splitter.persistence.dto.Mapper;
import splitter.appservice.repositories.AusgabenRepository;

@Repository
public class AusgabenRepositoryImpl implements AusgabenRepository {

    DBAusgabenRepository dbAusgabenRepository;

    public AusgabenRepositoryImpl(DBAusgabenRepository dbAusgabenRepository) {
        this.dbAusgabenRepository = dbAusgabenRepository;
    }

    @Override
    public void save(Ausgabe ausgabe, Long gruppenId) {
        dbAusgabenRepository.save(Mapper.ausgabetoDto(ausgabe, gruppenId));
    }

    @Override
    public List<Ausgabe> findAll(Long gruppenId) {
        return dbAusgabenRepository.findAll().stream()
                .filter(a -> gruppenId.equals(a.gruppenid()))
                .map(a -> Mapper.dtotoAusgabe(a))
                .toList();

    }

}
