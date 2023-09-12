package splitter.aggregates.domains.gruppe;

public class Saldo {
    private double wert;

    public Saldo(double wert) {
        this.wert = wert;
    }

    public double getWert() {
        return wert;
    }

    public void addSaldo(double wert) {
        this.wert = this.wert + wert;
        this.wert = Math.round(this.wert * 100) / 100.0;
    }
}
