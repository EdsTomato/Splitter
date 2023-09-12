package splitter.controller.form;

import javax.validation.constraints.NotBlank;

public record PersonForm(@NotBlank(message = "Personname darf nicht leer sein!") String name) {
}
