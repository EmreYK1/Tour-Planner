package com.tourplanner.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.dto.TourResponse;
import com.tourplanner.exception.TourNotFoundException;
import com.tourplanner.model.TransportType;
import com.tourplanner.security.JwtService;
import com.tourplanner.service.TourService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

// Integrations-/MockMvc-Tests für TourController.
// Schicht: nur Web-Layer (@WebMvcTest) – TourService wird gemockt.
@WebMvcTest(TourController.class)
class TourControllerTest {

    private static final String BASE_URL = "/api/tours";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TourService tourService;

    // JwtService muss als Bean vorhanden sein, damit JwtAuthFilter im Kontext instanziiert werden kann.
    @MockBean
    private JwtService jwtService;

    // ── Fixture-Helfer ─────────────────────────────────────────────────────────

    private static TourResponse tourResponse(long id, String name) {
        return new TourResponse(
                id, name, "Scenic route",
                "Vienna", "Salzburg",
                TransportType.CAR,
                295.0, 9000L, "", null, 2, "Mittel");
    }

    private static CreateTourRequest validCreateRequest() {
        return new CreateTourRequest(
                "Vienna → Salzburg", "Scenic route through the Alps",
                "Vienna", "Salzburg",
                TransportType.CAR,
                295.0, 9000L, null,
                null, null, null, null);
    }

    // ── GET /api/tours ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    void listTours_noSearchParam_returns200WithJsonArray() throws Exception {
        List<TourResponse> tours = List.of(
                tourResponse(1L, "Vienna → Salzburg"),
                tourResponse(2L, "Linz → Graz"));

        when(tourService.search(null)).thenReturn(tours);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    // ── GET /api/tours?search=wien ─────────────────────────────────────────────

    @Test
    @WithMockUser
    void listTours_withSearchParam_returnsFilteredList() throws Exception {
        TourResponse wienTour = tourResponse(3L, "Wien Tour");

        when(tourService.search("wien")).thenReturn(List.of(wienTour));

        mockMvc.perform(get(BASE_URL).param("search", "wien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Wien Tour"));
    }

    // ── POST /api/tours ────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    void createTour_validBody_returns201WithCreatedTour() throws Exception {
        CreateTourRequest request = validCreateRequest();
        TourResponse created = tourResponse(10L, request.name());

        when(tourService.create(any(CreateTourRequest.class))).thenReturn(created);

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value(request.name()));
    }

    @Test
    @WithMockUser
    void createTour_blankName_returns400() throws Exception {
        CreateTourRequest invalidRequest = new CreateTourRequest(
                "", "description",
                "Vienna", "Salzburg",
                TransportType.CAR, 10.0, 3600L, null,
                null, null, null, null);

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/tours/{id} ────────────────────────────────────────────────────

    @Test
    @WithMockUser
    void getTourById_existingId_returns200WithTour() throws Exception {
        TourResponse tour = tourResponse(5L, "Graz → Linz");

        when(tourService.findById(5L)).thenReturn(Optional.of(tour));

        mockMvc.perform(get(BASE_URL + "/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Graz → Linz"));
    }

    @Test
    @WithMockUser
    void getTourById_unknownId_returns404() throws Exception {
        when(tourService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/tours/{id} ─────────────────────────────────────────────────

    @Test
    @WithMockUser
    void deleteTour_foreignOrNonExistentTour_returns404() throws Exception {
        doThrow(new TourNotFoundException(42L))
                .when(tourService).delete(42L);

        mockMvc.perform(delete(BASE_URL + "/{id}", 42L).with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/tours/{id} ────────────────────────────────────────────────────

    @Test
    @WithMockUser
    void updateTour_validBody_returns200WithUpdatedTour() throws Exception {
        CreateTourRequest request = validCreateRequest();
        TourResponse updated = tourResponse(7L, request.name());

        when(tourService.update(anyLong(), any(CreateTourRequest.class))).thenReturn(updated);

        mockMvc.perform(put(BASE_URL + "/{id}", 7L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7));
    }
}
