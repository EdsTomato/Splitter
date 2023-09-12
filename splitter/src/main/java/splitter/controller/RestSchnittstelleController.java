package splitter.controller;

import java.util.List;
import java.util.Random;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import splitter.aggregates.domains.gruppe.Ausgabe;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.controller.entities.*;
import splitter.appservice.GruppenManagement;


@RestController
public class RestSchnittstelleController {

    GruppenManagement manager;

    public RestSchnittstelleController(GruppenManagement manager) {
        this.manager = manager;
    }

    @PostMapping("/api/gruppen")
    public ResponseEntity gruppeErzeugen(
            @RequestBody @Valid GruppenRequestEntity request, BindingResult result)
            throws IllegalAccessException {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Random r = new Random();
        long x = r.nextLong(1000000L);
        Gruppe gruppe = new Gruppe(request.name(), request.personen(), x);
        manager.saveGruppe(gruppe);
        return new ResponseEntity<>(gruppe.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/api/user/{GITHUB-LOGIN}/gruppen")
    public ResponseEntity<List<GruppenUserInfoEntity>>
            alleGruppenEinerPersonAnzeigen(@PathVariable(name = "GITHUB-LOGIN") String name) {
        return new ResponseEntity<>(manager.getAllUserGruppen(name).stream()
                .map(GruppenUserInfoEntity::create)
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/api/gruppen/{ID}")
    public ResponseEntity informationenDerGruppe(@PathVariable(name = "ID") String idString) {
        try {
            Long id = Long.parseLong(idString);
            Gruppe gruppe = manager.findGruppe(id);
            if (gruppe == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(GruppenInfoEntity.create(gruppe), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/gruppen/{ID}/schliessen")
    public ResponseEntity schliessenEinerGruppe(
            @PathVariable(name = "ID") String idString) {
        try {
            Long id = Long.parseLong(idString);
            Gruppe gruppe = manager.findGruppe(id);
            if (gruppe == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            manager.schliessenDerGruppe(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/gruppen/{ID}/auslagen")
    public ResponseEntity eintragenVonAuslagen(@PathVariable(name = "ID") String idString,
                                               @RequestBody @Valid AusgabenInfoEntity request,
                                               BindingResult result) {
        try {
            if (result.hasErrors()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Long id = Long.parseLong(idString);
            Gruppe gruppe = manager.findGruppe(id);
            if (gruppe == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (gruppe.isGeschlossen()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            manager.addAusgabeToGruppe(new Ausgabe(gruppe,
                    request.glaeubiger(),
                    request.schuldner(),
                    (request.cent() / 100.0),
                    request.grund()), id);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/gruppen/{ID}/ausgleich")
    public ResponseEntity<List<TransaktionInfoEntity>> berechnenDerAusgleichszahlugen(
            @PathVariable(name = "ID") String idString) {
        try {
            Long id = Long.parseLong(idString);
            Gruppe gruppe = manager.findGruppe(id);
            if (gruppe == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            gruppe.ausgleichen();
            return new ResponseEntity<>(gruppe.getTransaktionen().stream()
                    .map(TransaktionInfoEntity::create)
                    .toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
