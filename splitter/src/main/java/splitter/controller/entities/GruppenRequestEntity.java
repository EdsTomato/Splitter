package splitter.controller.entities;

import java.util.List;
import javax.validation.constraints.NotEmpty;

public record GruppenRequestEntity(@NotEmpty String name, @NotEmpty List<String> personen) {

}
