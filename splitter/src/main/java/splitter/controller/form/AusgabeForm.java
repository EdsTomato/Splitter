package splitter.controller.form;

import java.util.List;
import javax.validation.constraints.*;

public record AusgabeForm(@NotBlank(message = "Es muss ein Ersteller gewählt werden!")
                          @Size(max = 100)
                          String ersteller,
                          @NotEmpty(message = "Es muss mindestens ein Teilhaber gewählt werden!")
                          @Size(max = 100)
                          List<String> teilhaber,
                          @NotNull(message = "Der Betrag darf nicht leer sein!")
                          @Min(value = 0, message = "Ein negativer Betrag ist nicht Erlaubt!")
                          Double saldo,
                          @NotBlank(message = "Verwendungszweck darf nicht leer sein!")
                          @Size(max = 100)
                          String verwendungszweck){}
