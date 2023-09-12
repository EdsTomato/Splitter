package splitter.aggregates.domains.gruppe;

public record Transaktion(Person a, Person b, Saldo saldo) {

    @Override
    public String toString() {
        return a.name() + " muss " + b.name() + " " + saldo.getWert() + "€ überweisen.";
    }

}
