package splitter.aggregates.domains.service;

import splitter.aggregates.domains.gruppe.*;

import java.util.ArrayList;
import java.util.List;

public class AusgleichCalculator {

    private static List<Person> copyPersonen(List<Person> personen) {
        List<Person> copyPersonen = new ArrayList<>();
        for (Person m : personen) {
            copyPersonen.add(new Person(m.name(), new Saldo(m.saldo().getWert())));
        }
        return copyPersonen;
    }

    public static List<Transaktion> ausgleich(List<Person> personen) {
        List<Person> copyPersonen = copyPersonen(personen);
        List<Transaktion> transaktionen = new ArrayList<Transaktion>();
        for (Person a : copyPersonen) {
            double wertA = a.saldo().getWert();
            if (wertA < 0) {
                for (Person b : copyPersonen) {
                    double wertB = b.saldo().getWert();
                    if (wertA != 0 && !b.equals(a) && wertB > 0) {
                        if (Math.abs(wertA) == wertB) {
                            a.saldo().addSaldo(wertB);
                            b.saldo().addSaldo(wertA);
                            transaktionen.add(new Transaktion(a, b, new Saldo(wertB)));
                        } else if (Math.abs(wertA) > wertB) {
                            a.saldo().addSaldo(wertB);
                            b.saldo().addSaldo(-1 * wertB);
                            transaktionen.add(new Transaktion(a, b, new Saldo(wertB)));
                        } else if (Math.abs(wertA) < wertB) {
                            a.saldo().addSaldo(-1 * wertA);
                            b.saldo().addSaldo(wertA);
                            transaktionen.add(new Transaktion(a, b, new Saldo(Math.abs(wertA))));
                        }
                        wertA = a.saldo().getWert();
                        wertB = b.saldo().getWert();
                    }
                }
            }
        }
        return transaktionen;
    }

    public static void saldoUpdate(Ausgabe ausgabe, Gruppe gruppe) {
        int n = ausgabe.getTeilhaber().size();
        gruppe.getPersonbyname(ausgabe.getErsteller().name())
                .saldo().addSaldo(ausgabe.getSaldo().getWert());
        ausgabe.getTeilhaber().forEach(b -> {
                    gruppe.getPersonbyname(b.name())
                            .saldo().addSaldo(-1 * ((1.0 / n) * ausgabe.getSaldo().getWert()));
                }
        );
    }


}
