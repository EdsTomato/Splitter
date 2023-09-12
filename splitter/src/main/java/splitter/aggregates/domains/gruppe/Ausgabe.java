package splitter.aggregates.domains.gruppe;

import java.util.ArrayList;
import java.util.List;

public class Ausgabe {

    private Person ersteller;
    private List<Person> teilhaber = new ArrayList<>(); //Owner ist Teil dieser Liste
    private Saldo saldo;

    private Long id;

    private String verwendungszweck;


    public Ausgabe(Person ersteller,
                   List<Person> teilhaber,
                   Saldo saldo,
                   String verwendungszweck) {
        this.ersteller = ersteller;
        this.teilhaber = teilhaber;
        this.saldo = saldo;
        this.verwendungszweck = verwendungszweck;
    }

    public Ausgabe(Gruppe gruppe,
                   String ersteller,
                   List<String> teilhaber,
                   Double saldo,
                   String verwendungszweck) {
        this.ersteller = gruppe.getPersonbyname(ersteller);
        for (String t : teilhaber) {
            this.teilhaber.add(gruppe.getPersonbyname(t));
        }
        this.saldo = new Saldo(saldo);
        this.verwendungszweck = verwendungszweck;
    }

    public Person getErsteller() {
        return ersteller;
    }

    public List<Person> getTeilhaber() {
        return teilhaber;
    }

    public Saldo getSaldo() {
        return saldo;
    }

    public Long getId() {
        return id;
    }

    public String getVerwendungszweck() {
        return verwendungszweck;
    }




}
