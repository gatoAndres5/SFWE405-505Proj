package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Vendor;
import com.example.demo.service.VendorService;

@WebMvcTest(VendorController.class)
public class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;

    @Test
    void createVendorTest() throws Exception {
        Vendor sarahVendor = new Vendor();
        sarahVendor.setId(1L);
        sarahVendor.setName("Sarah Events");

        when(vendorService.createVendor(any(Vendor.class))).thenReturn(sarahVendor);

        mockMvc.perform(post("/vendors")
                .with(user("sarah").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Sarah Events",
                          "contactName": "Sarah",
                          "contactPhone": "1234567890",
                          "contactEmail": "sarah@test.com",
                          "address": "Baghdad Street",
                          "active": true
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sarah Events"));
    }

    @Test
    void getAllVendorsTest() throws Exception {
        Vendor sarahVendor = new Vendor();
        sarahVendor.setId(1L);
        sarahVendor.setName("Sarah Events");

        List<Vendor> vendors = new ArrayList<>();
        vendors.add(sarahVendor);

        when(vendorService.getAllVendors()).thenReturn(vendors);

        mockMvc.perform(get("/vendors")
                .with(user("sarah").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sarah Events"));
    }

    @Test
    void getVendorByIdTest() throws Exception {
        Vendor sarahVendor = new Vendor();
        sarahVendor.setId(1L);
        sarahVendor.setName("Sarah Events");

        when(vendorService.getVendorById(1L)).thenReturn(sarahVendor);

        mockMvc.perform(get("/vendors/1")
                .with(user("sarah").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sarah Events"));
    }

    @Test
    void updateVendorTest() throws Exception {
        Vendor sarahVendor = new Vendor();
        sarahVendor.setId(1L);
        sarahVendor.setName("Sarah Updated");

        when(vendorService.updateVendor(eq(1L), any(Vendor.class))).thenReturn(sarahVendor);

        mockMvc.perform(put("/vendors/1")
                .with(user("sarah").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Sarah Updated",
                          "contactName": "Sarah",
                          "contactPhone": "2223334444",
                          "contactEmail": "updated@sarah.com",
                          "address": "Baghdad Mall",
                          "active": true
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sarah Updated"));
    }

    @Test
    void deactivateVendorTest() throws Exception {
        Vendor sarahVendor = new Vendor();
        sarahVendor.setId(1L);
        sarahVendor.setActive(false);

        when(vendorService.deactivateVendor(1L)).thenReturn(sarahVendor);

        mockMvc.perform(patch("/vendors/1/deactivate")
                .with(user("sarah").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void getBookingsTest() throws Exception {
        when(vendorService.getBookings(1L)).thenReturn(new ArrayList<Booking>());

        mockMvc.perform(get("/vendors/1/bookings")
                .with(user("sarah").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void isAvailableTest() throws Exception {
        LocalDateTime start = LocalDateTime.parse("2026-04-07T10:00:00");
        LocalDateTime end = LocalDateTime.parse("2026-04-07T12:00:00");

        when(vendorService.isAvailable(1L, start, end)).thenReturn(true);

        mockMvc.perform(get("/vendors/1/availability")
                .with(user("sarah").roles("ADMIN"))
                .param("startDateTime", "2026-04-07T10:00:00")
                .param("endDateTime", "2026-04-07T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deleteVendorTest() throws Exception {
        doNothing().when(vendorService).deleteVendor(1L);

        mockMvc.perform(delete("/vendors/1")
                .with(user("sarah").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
