package com.landmarkist.www.landmarks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ListedBuildingIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ListedBuildingRestRepository repository;

    @Test
    public void cannotCreateViaHttp() throws Exception {
        String json = """
                      {"name": "Testington Towers"}
                      """;

        this.mockMvc.perform(post("/api/listedBuildings")
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void cannotUpdateViaHttp() throws Exception {
        ListedBuilding listedBuilding = repository.save(ListedBuilding.builder().name("Testington Towers").build());

        String json = """
                      {"name": "Testington Towers"}
                      """;

        this.mockMvc.perform(patch("/api/listedBuildings/" + listedBuilding.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(put("/api/listedBuildings/" + listedBuilding.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void cannotDeleteViaHttp() throws Exception {
        String id = "dfb76be6-8712-4d3b-98f6-eab608d116f2";
        this.mockMvc.perform(delete("/api/listedBuildings/" + id )).andExpect(status().isMethodNotAllowed());
    }
}
