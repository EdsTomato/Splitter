package splitter.aggregates.domains.gruppe;

import splitter.aggregates.domains.service.AusgleichCalculator;

import java.util.ArrayList;
import java.util.List;

public class Gruppe {

    private List<Person> personen = new ArrayList<Person>();
    private List<Ausgabe> ausgaben = new ArrayList<Ausgabe>();
    private List<Transaktion> transaktionen = new ArrayList<Transaktion>();
    private Long id;
    private Integer version;
    private Person ersteller;
    private String beschreibung;

    private Boolean isGeschlossen = false;

    private Gruppe(String erstellerName, String beschreibung, Long id) {
        ersteller = new Person(erstellerName, new Saldo(0));
        personen.add(ersteller);
        this.beschreibung = beschreibung;
        this.id = id;
    }

    public static Gruppe gruppeOhneErsteller(String erstellerName, String beschreibung, Long id) {
        Gruppe gruppe = new Gruppe(erstellerName, beschreibung, id);
        gruppe.getPersonen().remove(gruppe.getPersonbyname(erstellerName));
        return gruppe;
    }

    public Gruppe(String name, List<String> personen, Long id) throws IllegalAccessException {
        this("Ersteller", name, id);
        this.removePerson("Ersteller");
        for (String s : personen) {
            this.addPerson(new Person(s, new Saldo(0)));
        }
    }

    public static Gruppe gruppeOhneBeschreibung(String name) {
        return new Gruppe(name, "", 0L);
    }

    public static Gruppe gruppeKreieren(String name, String beschreibung, Long id) {
        return new Gruppe(name, beschreibung, id);
    }

    public void addPerson(Person person) {
        personen.add(person);
    }

    public void removePerson(String personName) {
        personen.remove(getPersonbyname(personName));
    }

    public void addAusgabe(Ausgabe ausgabe) throws IllegalAccessException {
        transaktionen.clear();
        ausgaben.add(ausgabe);
        AusgleichCalculator.saldoUpdate(ausgabe, this);
    }

    public void ausgleichen() {
        transaktionen.clear();
        transaktionen = AusgleichCalculator.ausgleich(personen);
    }

    

    public List<Person> getPersonen() {
        return this.personen;
    }

    public Person getPersonbyname(String name) {
        for (Person m : personen) {
            if (m.name().equals(name)) {
                return m;
            }
        }

        return null;
    }

    public List<Ausgabe> getAusgaben() {
        return this.ausgaben;
    }

    public List<Transaktion> getTransaktionen() {
        return transaktionen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public Long getId() {
        return id;
    }

    public String getErstellername() {
        return ersteller.name();
    }

    public Person getErsteller() {
        return ersteller;
    }

    public boolean isPerson(String personName) {
        return personen.stream().anyMatch(person -> person.name().equals(personName));
    }

    public void schliessen() {
        this.isGeschlossen = true;
    }

    public Boolean isGeschlossen() {
        return isGeschlossen;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
