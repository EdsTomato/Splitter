package splitter.persistence.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import splitter.aggregates.domains.gruppe.Ausgabe;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.persistence.AusgabenRepositoryImpl;
import splitter.persistence.DBAusgabenRepository;
import splitter.persistence.DBGruppenRepository;
import splitter.persistence.GruppenRepositoryImpl;
import splitter.appservice.GruppenManagement;
import splitter.appservice.repositories.AusgabenRepository;
import splitter.appservice.repositories.GruppenRepository;

import java.util.List;

@DataJdbcTest
public class GruppenManagementTest {

    @Autowired
    DBGruppenRepository gruppenRepository;

    @Autowired
    DBAusgabenRepository ausgabenRepository;

    GruppenRepository gRepo;

    AusgabenRepository aRepo;

    @BeforeEach
    void init() {
        gRepo = new GruppenRepositoryImpl(gruppenRepository);
        aRepo = new AusgabenRepositoryImpl(ausgabenRepository);
    }


    @Test
    @DisplayName("create Gruppe")
    void createGruppeTest1() {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        manager.createGruppe("name", "beschreibung");
        assertThat(manager.getOffeneGruppen("name").size()).isEqualTo(1);
    }

    @Test
    @DisplayName("save Gruppe")
    void gruppeTest1() {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("user", "name", 1234L);
        manager.saveGruppe(gruppe);
        assertThat(manager.findGruppe(1234L)).isNotNull();
    }

    @Test
    @DisplayName("return alle Gruppen")
    void alleGruppenTest1() {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        manager.createGruppe("name", "beschreibung");
        manager.createGruppe("name", "beschreibung");
        assertThat(manager.allGruppen().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Neu erstellte Gruppen sind immer offen")
    void geschlosseneGruppenTest1() {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        manager.createGruppe("name", "beschreibung");
        manager.createGruppe("name", "beschreibung");
        assertThat(manager.getGeschlosseneGruppen("name").size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Gruppe wird geschlossen")
    void geschlosseneGruppenTest2() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        manager.schliessenDerGruppe(1234L);
        assertThat(manager.getGeschlosseneGruppen("name").size()).isEqualTo(1);
    }

    @Test
    @DisplayName("es wird nur eine Gruppe geschlossen (ID abhängig)")
    void geschlosseneGruppenTest3() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name1", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        manager.schliessenDerGruppe(1234L);
        assertThat(manager.getGeschlosseneGruppen("name2").size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Wenn die Gruppe geschlossen, dann ist sie unveränderbar")
    void geschlosseneGruppeTest4() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name1", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        manager.schliessenDerGruppe(1234L);
        assertThrows(IllegalAccessException.class, () ->
                manager.addPersonToGruppe("name2", 1234L));
        assertThrows(IllegalAccessException.class, () ->
                manager.removePersonFromGruppe("name1", 1234L));
        assertThrows(IllegalAccessException.class, () ->
                manager.addAusgabeToGruppe(new Ausgabe(
                        gruppe,
                        "name1",
                        List.of("name1"),
                        100.0,
                        "Essen"), 1234L));
    }

    @Test
    @DisplayName("offene Gruppen")
    void offeneGruppenTest1() {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        manager.createGruppe("name", "beschreibung");
        manager.createGruppe("name", "beschreibung");
        assertThat(manager.getOffeneGruppen("name").size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Gruppe ist nach dem schließen nicht mehr offen")
    void offeneGruppenTest2() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        manager.schliessenDerGruppe(1234L);
        assertThat(manager.getOffeneGruppen("name").size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Add Person")
    void addPersonToGruppeTest1() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name1", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        manager.addPersonToGruppe("name2", 1234L);
        assertThat(manager.findGruppe(1234L).getPersonbyname("name2")).isNotNull();
    }

    @Test
    @DisplayName("Kann kein Person doppelt hinzufügen!")
    void addPersonToGruppeTwiceTest2() {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name1", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        assertThrows(IllegalAccessException.class, () -> manager.addPersonToGruppe("name1", 1234L));
    }

    @Test
    @DisplayName("Remove Person")
    void removePersonFromGruppeTest1() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name1", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        manager.addPersonToGruppe("name2", 1234L);
        manager.removePersonFromGruppe("name2", 1234L);
        assertThat(manager.findGruppe(1234L).getPersonbyname("name2")).isNull();
    }

    @Test
    @DisplayName("Remove Person was nicht existiert")
    void removePersonFromGruppeTest2() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name1", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        assertThrows(IllegalAccessException.class, () -> manager.removePersonFromGruppe("name2", 1234L));
    }

    @Test
    @DisplayName("add Ausgaben")
    void addAusgabenToGruppeTest1() throws IllegalAccessException {
        GruppenManagement manager = new GruppenManagement(gRepo,aRepo);
        Gruppe gruppe = Gruppe.gruppeKreieren("name1", "beschreibung", 1234L);
        manager.saveGruppe(gruppe);
        manager.addAusgabeToGruppe(new Ausgabe(gruppe,
                "name1",
                List.of("name1"),
                100.0,
                        "Essen"),
                1234L);
        assertThat(manager.ausgabenVonGruppe(1234L).size()).isEqualTo(1);
    }



}

