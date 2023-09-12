package splitter.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import splitter.aggregates.domains.gruppe.Gruppe;
import splitter.controller.config.SecurityConfig;
import splitter.helper.WithMockOAuth2User;
import splitter.appservice.GruppenManagement;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class GruppenAnzeigeTest {

    @Nested
    @Import({SecurityConfig.class})
    @WebMvcTest
    class MainSeiteTests {

        @Autowired
        MockMvc mvc;

        @MockBean
        GruppenManagement manager;

        @Test
        @DisplayName("Unangemeldet wird man auf die Githubseite geleitet.")
        void test_0() throws Exception {
            mvc.perform(get("/")).andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("Angemeldet wird die Seite angezeigt.")
        void test_1() throws Exception {
            mvc.perform(get("/")).andExpect(status().isOk());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("Gruppe wird hinzugefügt")
        void test_2() throws Exception {
            mvc.perform(post("/").param("beschreibung","Urlaub").with(csrf())).andExpect(status().is3xxRedirection());
            verify(manager).createGruppe("username","Urlaub");
        }

    }

    @Nested
    @Import({SecurityConfig.class})
    @WithMockOAuth2User
    @WebMvcTest
    class DetailSeiteTests {

        @Autowired
        MockMvc mvc;

        @MockBean
        GruppenManagement manager;

        @Test
        @DisplayName("Detail Seite wird angezeigt")
        void test_0() throws Exception {
            Gruppe testGruppe = Gruppe.gruppeKreieren("username","Urlaub",123456L);
            when(manager.findGruppe(123456L)).thenReturn(testGruppe);
            mvc.perform(get("/detail/123456")).andExpect(model().attributeExists("gruppe")).andExpect(status().isOk());
        }

        @Test
        @DisplayName("Person wird hinzugefügt")
        void test_1() throws Exception {
            mvc.perform(post("/detail/123456/personen")
                            .param("name","PersonTestUser")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());
            verify(manager).addPersonToGruppe("PersonTestUser", 123456L);
        }

        @Test
        @DisplayName("Person wird entfernt")
        void test_2() throws Exception {
            mvc.perform(get("/detail/123456/personen_entfernen/PersonTestUser")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection());
            verify(manager).removePersonFromGruppe("PersonTestUser", 123456L);
        }

        @Test
        @DisplayName("Ausgabe wird hinzugefügt")
        void test_3() throws Exception {
            mvc.perform(post("/detail/12345/ausgaben")
                    .param("ersteller","testUser")
                            .param("teilhaber","testUser")
                            .param("saldo","100")
                            .param("verwendungszweck","Essen")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());
            verify(manager).addAusgabeToGruppe(
                    any(), eq(12345L));
        }

        @Test
        @DisplayName("AusgleichTransaktionen werden gezeigt")
        void test_4() throws Exception {
            mvc.perform(post("/detail/12345/ausgleichen")
                            .with(csrf()))
                    .andExpect(model().attribute("ausgleich", "true"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("Gruppe wird geschlossen")
        void test_5() throws Exception {
            mvc.perform(post("/detail/12345/gruppe_schließen")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection());
            verify(manager).schliessenDerGruppe( 12345L);
        }
    }


}
