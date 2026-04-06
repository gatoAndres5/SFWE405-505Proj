package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Vendor;
import com.example.demo.service.VendorService;

@RestController
@RequestMapping("/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        return vendorService.createVendor(vendor);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/{id}")
    public Vendor getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable Long id, @RequestBody Vendor updatedVendor) {
        return vendorService.updateVendor(id, updatedVendor);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PatchMapping("/{id}/deactivate")
    public Vendor deactivateVendor(@PathVariable Long id) {
        return vendorService.deactivateVendor(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/{id}/bookings")
    public List<Booking> getBookings(@PathVariable Long id) {
        return vendorService.getBookings(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/{id}/availability")
    public boolean isAvailable(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        return vendorService.isAvailable(id, startDateTime, endDateTime);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @DeleteMapping("/{id}")
    public void deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
    }
}
