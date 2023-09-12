package splitter.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.persistence.dto.Gruppendto;
import splitter.persistence.dto.Mapper;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

@DataJdbcTest
public class DbTest {
    @Autowired
    DBAusgabenRepository ausgabenRepository;

    @Autowired
    DBGruppenRepository gruppenRepository;

    GruppenRepositoryImpl gRepo;

    AusgabenRepositoryImpl aRepo;

    public DbTest() {
        this.gRepo = new GruppenRepositoryImpl(gruppenRepository);
        this.aRepo = new AusgabenRepositoryImpl(ausgabenRepository);
    }

    @Test
    @DisplayName("save Gruppe")
    void saveGruppeTest() {
        DBGruppenRepository mockDb = mock(DBGruppenRepository.class);
        GruppenRepositoryImpl mockImpl = new GruppenRepositoryImpl(mockDb);

        Gruppe g = Gruppe.gruppeKreieren("user", "name", 123L);
        Gruppendto gDto = Mapper.gruppetoDto(g);

        mockImpl.save(g);

        verify(mockDb, times(1)).save(gDto);
    }

    @Test
    @DisplayName("return alle Gruppen")
    void alleGruppenTest() {
        DBGruppenRepository mockDb = mock(DBGruppenRepository.class);
        GruppenRepositoryImpl mockImpl = new GruppenRepositoryImpl(mockDb);

        mockImpl.findAll();

        verify(mockDb, times(1)).findAll();
    }

    @Test
    @DisplayName("findbyId Gruppe")
    void eineGruppeTest1() {
        DBGruppenRepository mockDb = mock(DBGruppenRepository.class);
        GruppenRepositoryImpl mockImpl = new GruppenRepositoryImpl(mockDb);

        try {
            mockImpl.findById(1234L);
        } catch (NoSuchElementException e) {
            verify(mockDb).findById(1234L);
        }
    }




}
