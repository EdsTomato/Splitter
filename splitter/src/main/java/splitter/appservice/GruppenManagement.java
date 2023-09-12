package splitter.appservice;

import static splitter.aggregates.domains.gruppe.Gruppe.gruppeKreieren;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitter.aggregates.domains.gruppe.*;
import splitter.appservice.repositories.AusgabenRepository;
import splitter.appservice.repositories.GruppenRepository;

@Service
public class GruppenManagement {

    /*HashMap<Long, Gruppe> gruppen = new HashMap<>();

    public void createGruppe(String name, String beschreibung) {
        Random r = new Random();
        long x = r.nextLong(1000000L);
        Gruppe gruppe = gruppeKreieren(name, beschreibung, x);
        gruppen.put(x, gruppe);
    }

    public Gruppe saveGruppe(Gruppe gruppe) {
        gruppen.put(gruppe.getId(), gruppe);
        return gruppe;
    }

    public void deleteGruppe(Long id) {
        gruppen.remove(id);
    }

    public List<Gruppe> allGruppen() {
        return gruppen.values().stream().toList();
    }

    public Gruppe findGruppe(Long id) {
        try {
            return gruppen.get(id);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<Gruppe> getAllUserGruppen(String name) {
        return allGruppen().stream()
                .filter(g -> g.isPerson(name))
                .toList();
    }

    public List<Gruppe> getGeschlosseneGruppen(String name) {
        return allGruppen().stream()
                .filter(g -> g.isPerson(name))
                .filter(g -> g.isGeschlossen())
                .toList();
    }

    public List<Gruppe> getOffeneGruppen(String name) {
        return allGruppen().stream()
                .filter(g -> g.isPerson(name))
                .filter(g -> !g.isGeschlossen())
                .toList();
    }

    public void schliessenDerGruppe(Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        currentGruppe.ausgleichen();
        currentGruppe.schliessen();
        saveGruppe(currentGruppe);
    }

    public void addPersonToGruppe(String person, Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        if (currentGruppe.isGeschlossen()) {
            throw new IllegalAccessException("Gruppe ist geschlossen!");
        }
        if (!currentGruppe.getAusgaben().isEmpty()) {
            throw new IllegalAccessException("Gruppe ist bereits gestartet!");
        }
        if (currentGruppe.isPerson(person)) {
            throw new IllegalAccessException("Die Person ist bereits in der Gruppe.");
        }
        currentGruppe.addPerson(new Person(person, new Saldo(0)));
        saveGruppe(currentGruppe);
    }

    public void removePersonFromGruppe(String person, Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        if (currentGruppe.isGeschlossen()) {
            throw new IllegalAccessException("Gruppe ist geschlossen!");
        }
        if (!currentGruppe.getAusgaben().isEmpty()) {
            throw new IllegalAccessException("Gruppe ist bereits gestartet!");
        }
        if (!currentGruppe.isPerson(person)) {
            throw new IllegalAccessException("Die Person ist nicht Teil der Gruppe.");
        }
        currentGruppe.removePerson(person);
        if(currentGruppe.getPersonen().isEmpty()) {
            deleteGruppe(id);
        } else {
            saveGruppe(currentGruppe);
        }
    }

    public void addAusgabeToGruppe(Ausgabe ausgabe, Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        if (currentGruppe.isGeschlossen()) {
            throw new IllegalAccessException("Gruppe ist geschlossen!");
        }
        if (!currentGruppe.isPerson(ausgabe.getErsteller().name())) {
            throw new IllegalAccessException("Unbekannter Ersteller");
        }
        if (!ausgabe.getTeilhaber()
                .stream()
                .allMatch(teilhaber -> teilhaber != null &&
                currentGruppe.isPerson(teilhaber.name()))
        ) {
            throw new IllegalAccessException("Unbekannter Teilhaber");
        }
        currentGruppe.addAusgabe(ausgabe);
        saveGruppe(currentGruppe);
    }

    public void ausgleichenDerGruppe(Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        if (currentGruppe.isGeschlossen()) {
            throw new IllegalAccessException("Gruppe ist geschlossen!");
        }
        currentGruppe.ausgleichen();
        saveGruppe(currentGruppe);
    }*/

    GruppenRepository gruppenRepository;
    AusgabenRepository ausgabenRepository;

    public GruppenManagement(GruppenRepository gruppenRepository,
                             AusgabenRepository ausgabenRepository) {
        this.gruppenRepository = gruppenRepository;
        this.ausgabenRepository = ausgabenRepository;
    }

    @Transactional
    public void saveGruppe(Gruppe gruppe) {
        gruppenRepository.save(gruppe);
    }

    public void createGruppe(String name, String beschreibung) {
        Gruppe gruppe = gruppeKreieren(name, beschreibung, null);
        saveGruppe(gruppe);
    }

    public List<Gruppe> allGruppen() {
        return gruppenRepository.findAll();
    }

    public Gruppe findGruppe(Long gruppenId) {
        Gruppe gruppe = gruppenRepository.findById(gruppenId);
        ausgabenRepository.findAll(gruppenId).forEach(a -> {
            try {
                gruppe.addAusgabe(a);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        if (gruppe.isGeschlossen()) {
            gruppe.ausgleichen();
        }
        return gruppe;
    }

    public List<Ausgabe> ausgabenVonGruppe(Long id) {
        return ausgabenRepository.findAll(id);
    }

    public List<Gruppe> getAllUserGruppen(String name) {
        return allGruppen().stream()
                .filter(g -> g.isPerson(name))
                .toList();
    }

    public List<Gruppe> getGeschlosseneGruppen(String name) {
        return allGruppen().stream()
                .filter(g -> g.isPerson(name))
                .filter(Gruppe::isGeschlossen)
                .toList();
    }

    public List<Gruppe> getOffeneGruppen(String name) {
        return allGruppen().stream()
                .filter(g -> g.isPerson(name))
                .filter(g -> !g.isGeschlossen())
                .toList();
    }

    public void schliessenDerGruppe(Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        currentGruppe.schliessen();
        saveGruppe(currentGruppe);
    }

    public void addPersonToGruppe(String person, Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        if (currentGruppe.isGeschlossen()) {
            throw new IllegalAccessException("Gruppe ist geschlossen!");
        }
        if (!currentGruppe.getAusgaben().isEmpty()) {
            throw new IllegalAccessException("Gruppe ist bereits gestartet!");
        }
        if (currentGruppe.isPerson(person)) {
            throw new IllegalAccessException("Die Person ist bereits in der Gruppe.");
        }
        currentGruppe.addPerson(new Person(person, new Saldo(0)));
        saveGruppe(currentGruppe);
    }

    public void removePersonFromGruppe(String person, Long id) throws IllegalAccessException {
        Gruppe currentGruppe = findGruppe(id);
        if (currentGruppe.isGeschlossen()) {
            throw new IllegalAccessException("Gruppe ist geschlossen!");
        }
        if (!currentGruppe.getAusgaben().isEmpty()) {
            throw new IllegalAccessException("Gruppe ist bereits gestartet!");
        }
        if (!currentGruppe.isPerson(person)) {
            throw new IllegalAccessException("Die Person ist nicht Teil der Gruppe.");
        }
        currentGruppe.removePerson(person);
        saveGruppe(currentGruppe);
    }

    @Transactional
    public void addAusgabeToGruppe(Ausgabe ausgabe, Long gruppenId) throws IllegalAccessException {
        Gruppe gruppe = findGruppe(gruppenId);
        if (gruppe.isGeschlossen()) {
            throw new IllegalAccessException("Gruppe ist geschlossen!");
        }
        if (!gruppe.isPerson(ausgabe.getErsteller().name())) {
            throw new IllegalAccessException("Unbekannter Ersteller");
        }
        if (!ausgabe.getTeilhaber()
                .stream()
                .allMatch(teilhaber -> teilhaber != null && gruppe.isPerson(teilhaber.name()))
        ) {
            throw new IllegalAccessException("Unbekannter Teilhaber");
        }
        ausgabenRepository.save(ausgabe, gruppenId);
    }
}
