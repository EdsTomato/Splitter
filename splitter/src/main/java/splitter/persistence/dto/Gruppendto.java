package splitter.persistence.dto;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

public record Gruppendto(@Id Long id,
                         String erstellername,
                         String beschreibung,
                         Boolean isgeschlossen,
                         List<String> mitglieder,
                         @Version Integer version) {//Personen namen
}
