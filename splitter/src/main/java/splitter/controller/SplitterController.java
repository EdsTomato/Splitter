package splitter.controller;


import javax.validation.Valid;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import splitter.aggregates.domains.gruppe.*;
import splitter.controller.form.AusgabeForm;
import splitter.controller.form.GruppenForm;
import splitter.controller.form.PersonForm;
import splitter.appservice.GruppenManagement;

@Controller
public class SplitterController {

    GruppenManagement gruppenManager;

    public SplitterController(GruppenManagement gruppenManager) {
        this.gruppenManager = gruppenManager;
    }

    @GetMapping("/")
    public String index(Model model, OAuth2AuthenticationToken t, GruppenForm gruppenForm) {
        String name = t.getPrincipal().getAttribute("login");
        model.addAttribute("myOffeneGruppenList",
                gruppenManager.getOffeneGruppen(name));
        model.addAttribute("myGeschlosseneGruppenList",
                gruppenManager.getGeschlosseneGruppen(name));
        return "main";
    }

    @PostMapping("/")
    public String createGruppe(@Valid GruppenForm gruppenForm, BindingResult bindingResult,
                               OAuth2AuthenticationToken t, Model model) {
        if (bindingResult.hasErrors()) {
            return index(model, t, gruppenForm);
        }
        String name = t.getPrincipal().getAttribute("login");
        gruppenManager.createGruppe(name, gruppenForm.beschreibung());
        return "redirect:/";
    }

    @GetMapping("/detail/{id}")
    public String gruppenDetail(Model model,
                                @PathVariable Long id,
                                @RequestParam(name = "ausgleich",
                                        defaultValue = "false") Boolean ausgleich,
                                OAuth2AuthenticationToken t,
                                PersonForm personForm,
                                AusgabeForm ausgabeForm) {
        Gruppe gruppe = gruppenManager.findGruppe(id);
        String user = t.getPrincipal().getAttribute("login");
        if (!gruppe.isPerson(user)) {
            return "redirect:/";
        }
        if (ausgleich) {
            gruppe.ausgleichen();
        }
        model.addAttribute("userName", t.getPrincipal().getAttribute("login"));
        model.addAttribute("gruppe", gruppe);
        return "detail";
    }

    @PostMapping("/detail/{id}/gruppe_schließen")
    public String gruppeSchliessen(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes)
            throws IllegalAccessException {
        redirectAttributes.addAttribute("ausgleich", true);
        gruppenManager.schliessenDerGruppe(id);
        return "redirect:/detail/" + id;
    }

    @PostMapping("/detail/{id}/personen")
    public String addPerson(@Valid PersonForm personForm,
                              BindingResult bindingResult,
                              @PathVariable Long id,
                              Model model,
                              OAuth2AuthenticationToken t,
                              AusgabeForm ausgabeForm) throws IllegalAccessException {
        if (bindingResult.hasErrors()) {
            return gruppenDetail(model, id, false, t, personForm, ausgabeForm);
        }
        gruppenManager.addPersonToGruppe(personForm.name(), id);
        return "redirect:/detail/" + id;
    }

    @GetMapping("/detail/{id}/personen_entfernen/{personName}")
    public String removePerson(@PathVariable Long id, @PathVariable String personName,
                               OAuth2AuthenticationToken t) throws IllegalAccessException {
        gruppenManager.removePersonFromGruppe(personName, id);
        if (t.getPrincipal().getAttribute("login").equals(personName)) {
            return "redirect:/";
        }
        return "redirect:/detail/" + id;
    }

    @PostMapping("/detail/{id}/ausgaben")
    public String addAusgabe(@Valid AusgabeForm ausgabeForm,
                             BindingResult bindingResult,
                             @PathVariable Long id,
                             Model model,
                             OAuth2AuthenticationToken t,
                             PersonForm personForm,
                             RedirectAttributes redirectAttributes) throws IllegalAccessException {
        if (bindingResult.hasErrors()) {
            return gruppenDetail(model, id, false, t, personForm, ausgabeForm);
        }
        redirectAttributes.addAttribute("ausgleich", false);
        gruppenManager.addAusgabeToGruppe(new Ausgabe(
                new Person(ausgabeForm.ersteller(), new Saldo(0)),
                ausgabeForm.teilhaber().stream()
                        .map(n -> new Person(n, new Saldo(0)))
                        .toList(),
                new Saldo(ausgabeForm.saldo()),
                ausgabeForm.verwendungszweck()), id);
        return "redirect:/detail/" + id;
    }

    @PostMapping("/detail/{id}/ausgleichen")
    public String ausgleich(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("ausgleich", true);
        return "redirect:/detail/" + id;
    }

}
