package splitter.persistence.dto;

import java.util.List;
import org.springframework.data.annotation.Id;

public record Ausgabendto(@Id Long id,
                          String ersteller,
                          Double saldo,
                          String verwendungszweck,
                          List<String> teilhaber, //Personen namen
                          Long gruppenid) {
}
