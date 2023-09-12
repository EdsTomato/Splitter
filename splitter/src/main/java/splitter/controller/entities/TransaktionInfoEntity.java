package splitter.controller.entities;

import splitter.aggregates.domains.gruppe.Transaktion;

public record TransaktionInfoEntity(String von, String an, Integer cents) {
    public static TransaktionInfoEntity create(Transaktion transaktion) {
        return new TransaktionInfoEntity(transaktion.a().name(),
                transaktion.b().name(),
                (int) (transaktion.saldo().getWert() * 100));
    }
}
