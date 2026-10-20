package io.grundner.wallbox.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Einstellungs-API und Fehlerfälle gemäß docs/api/report-api.md. */
@SpringBootTest(properties = "w5x.settings-file=build/tmp/settings-api-test/settings.json")
@AutoConfigureMockMvc
@DirtiesContext
class SettingsApiTest {

	@Autowired
	MockMvc mvc;

	@Test
	void settingsRoundTripWithoutPassword() throws Exception { // ST-4, AC-S2
		mvc.perform(put("/api/settings").contentType(MediaType.APPLICATION_JSON).content("""
						{"serialNumber":"504F94A22F32","username":"admin","password":"streng-geheim",
						 "locationLabel":"Neuhausen 22","wallboxId":"W"}"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.passwordSet").value(true))
				.andExpect(jsonPath("$.complete").value(true))
				.andExpect(content().string(not(containsString("streng-geheim"))));

		mvc.perform(get("/api/settings"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.serialNumber").value("504F94A22F32"))
				.andExpect(jsonPath("$.locationLabel").value("Neuhausen 22"))
				.andExpect(content().string(not(containsString("streng-geheim"))))
				.andExpect(content().string(not(containsString("\"password\""))));
	}

	@Test
	void invalidSerialNumberIs400() throws Exception { // AC-S4
		mvc.perform(put("/api/settings").contentType(MediaType.APPLICATION_JSON)
						.content("{\"serialNumber\":\"abc/def\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void reportWithIncompleteSettingsIs409() throws Exception { // ST-2, AC-7, AC-S5
		mvc.perform(put("/api/settings").contentType(MediaType.APPLICATION_JSON)
						.content("{\"serialNumber\":\"504F94A22F32\",\"username\":\"\",\"password\":\"\"}"))
				.andExpect(status().isOk());
		// Kennwort aus vorherigem Test bleibt (ST-5), Benutzer fehlt -> unvollständig
		mvc.perform(get("/api/report").param("year", "2024").param("month", "2"))
				.andExpect(status().isConflict());
	}
}
