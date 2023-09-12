package splitter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.controller.config.SecurityConfig;
import splitter.controller.entities.AusgabenInfoEntity;
import splitter.controller.entities.GruppenRequestEntity;
import splitter.appservice.GruppenManagement;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfig.class})
@WebMvcTest
public class RestControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    GruppenManagement manager;

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("gruppeErzeugen Test Status : CREATED")
    void test_0() throws Exception {
        mvc.perform(post("/api/gruppen")
                        .content(asJsonString(new GruppenRequestEntity("Test",List.of("user1","user2"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("gruppeErzeugen Test Status : BAD_REQUEST (Nicht valide Parameter)")
    void test_1() throws Exception {
        mvc.perform(post("/api/gruppen")
                        .content(asJsonString(new GruppenRequestEntity(null,List.of("user1","user2"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("alleGruppenEinerPersonAnzeigen Test Status : OK")
    void test_2() throws Exception {
        mvc.perform(get("/api/user/testUser/gruppen"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("informationenDerGruppe Test Status : OK")
    void test_3() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(Gruppe.gruppeOhneBeschreibung("TestErsteller"));
        mvc.perform(get("/api/gruppen/1234"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("informationenDerGruppe Test Status : NOT_FOUND (Ungültige ID)")
    void test_4() throws Exception {
        mvc.perform(get("/api/gruppen/lachs"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("informationenDerGruppe Test Status : NOT_FOUND (Gruppe existiert nicht)")
    void test_5() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(null);
        mvc.perform(get("/api/gruppen/1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("schliessenEinerGruppe Test Status : OK")
    void test_6() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(Gruppe.gruppeOhneBeschreibung("TestErsteller"));
        mvc.perform(post("/api/gruppen/1234/schliessen"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("schliessenEinerGruppe Test Status : NOT_FOUND (Ungültige ID)")
    void test_7() throws Exception {
        mvc.perform(post("/api/gruppen/lachs/schliessen"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("schliessenEinerGruppe Test Status : NOT_FOUND (Gruppe existiert nicht)")
    void test_8() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(null);
        mvc.perform(post("/api/gruppen/1234/schliessen"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("eintragenVonAuslagen Test Status : CREATED")
    void test_9() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(
                new Gruppe("Wandern",
                        List.of("testGlaeubiger","testSchuldner1","testSchuldner2"), 1234L));
        mvc.perform(post("/api/gruppen/1234/auslagen")
                        .content(asJsonString(new AusgabenInfoEntity("testGrund",
                                "testGlaeubiger",
                                1000,
                                List.of("testSchuldner1","testSchuldner2"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("eintragenVonAuslagen Test Status : BAD_REQUEST (Nicht valide Parameter)")
    void test_10() throws Exception {
        mvc.perform(post("/api/gruppen/1234/auslagen")
                        .content(asJsonString(new AusgabenInfoEntity("testGrund",
                                null,
                                1000,
                                null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("eintragenVonAuslagen Test Status : NOT_FOUND (Ungültige ID)")
    void test_11() throws Exception {
        mvc.perform(post("/api/gruppen/lachs/auslagen")
                        .content(asJsonString(new AusgabenInfoEntity("testGrund",
                                "testGlaeubiger",
                                1000,
                                List.of("testSchuldner1","testSchuldner2"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("eintragenVonAuslagen Test Status : NOT_FOUND (Gruppe existiert nicht)")
    void test_12() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(null);
        mvc.perform(post("/api/gruppen/1234/auslagen")
                        .content(asJsonString(new AusgabenInfoEntity("testGrund",
                                "testGlaeubiger",
                                1000,
                                List.of("testSchuldner1","testSchuldner2"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("eintragenVonAuslagen Test Status : CONFLICT (Gruppe ist geschlossen)")
    void test_13() throws Exception {
        Gruppe gruppe = new Gruppe("Wandern",
                List.of("testGlaeubiger","testSchuldner1","testSchuldner2"), 1234L);
        gruppe.schliessen();
        when(manager.findGruppe(1234L)).thenReturn(gruppe);
        mvc.perform(post("/api/gruppen/1234/auslagen")
                        .content(asJsonString(new AusgabenInfoEntity("testGrund",
                                "testGlaeubiger",
                                1000,
                                List.of("testSchuldner1","testSchuldner2"))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("berechnenDerAusgleichszahlugen Test Status : OK")
    void test_14() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(Gruppe.gruppeOhneBeschreibung("TestErsteller"));
        mvc.perform(get("/api/gruppen/1234/ausgleich"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("berechnenDerAusgleichszahlugen Test Status : NOT_FOUND (Ungültige ID)")
    void test_15() throws Exception {
        mvc.perform(get("/api/gruppen/lachs/ausgleich"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("berechnenDerAusgleichszahlugen Test Status : NOT_FOUND (Gruppe existiert nicht)")
    void test_16() throws Exception {
        when(manager.findGruppe(1234L)).thenReturn(null);
        mvc.perform(get("/api/gruppen/lachs/ausgleich"))
                .andExpect(status().isNotFound());
    }


}
