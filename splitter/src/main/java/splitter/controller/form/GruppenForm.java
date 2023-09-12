package splitter.controller.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public record GruppenForm(
        @NotBlank(message = "Die Gruppenbeschreibung darf nicht leer sein!") String beschreibung) {
}
