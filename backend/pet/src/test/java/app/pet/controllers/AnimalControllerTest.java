package app.pet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private String jwt;

    @BeforeEach
    void login() throws Exception {
        var loginJson = mapper.writeValueAsString(
            Map.of("username", "nicolasgraeb2@gmail.com", "password", "haslo123")
        );

        var tokenJson = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        jwt = "Bearer " + mapper.readTree(tokenJson).get("accessToken").asText();
        assertThat(jwt).contains("Bearer ");
    }

    @Test
    void canListAvailableAnimals() throws Exception {
        mvc.perform(get("/api/animals")
                .header("Authorization", jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void canCreateReadUpdateAndDeleteAnimal() throws Exception {
        // create
        MockMultipartFile image = new MockMultipartFile(
            "image", "pic.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1,2,3}
        );
        var result = mvc.perform(multipart("/api/animals")
                .file(image)
                .param("name", "Testy")
                .param("species", "cat")
                .param("sex", "MALE")
                .param("age", "5")
                .param("description", "friendly")
                .param("status", "AVAILABLE")
                .header("Authorization", jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

        Long id = mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // read
        mvc.perform(get("/api/animals/{id}", id)
                .header("Authorization", jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Testy"));

        // update
        var updated = Map.of(
            "id", id,
            "name", "Testy2",
            "species", "cat",
            "sex", "MALE",
            "age", 6,
            "description", "very friendly",
            "status", "AVAILABLE"
        );
        mvc.perform(put("/api/animals/{id}", id)
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updated)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Testy2"));

        // delete
        mvc.perform(delete("/api/animals/{id}", id)
                .header("Authorization", jwt))
            .andExpect(status().isNoContent());
    }
}
