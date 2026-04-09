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

/**
 * REST controller for managing Vendor-related operations.
 * 
 * Provides endpoints for creating, retrieving, updating, deactivating,
 * and deleting vendors, as well as checking availability and retrieving bookings.
 * 
 * Access to all endpoints is restricted to users with ADMIN or ORGANIZER roles.
 */
@RestController
@RequestMapping("/vendors")
public class VendorController {

    private final VendorService vendorService;

    /**
     * Constructor for VendorController.
     * 
     * @param vendorService the service layer handling vendor business logic
     */
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Creates a new vendor in the system.
     * 
     * @param vendor the vendor object containing required details
     * @return the created vendor
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        return vendorService.createVendor(vendor);
    }

    /**
     * Retrieves all vendors in the system.
     * 
     * @return a list of all vendors
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    /**
     * Retrieves a vendor by its unique ID.
     * 
     * @param id the ID of the vendor
     * @return the vendor if found, otherwise null
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/{id}")
    public Vendor getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    /**
     * Updates an existing vendor's information.
     * 
     * @param id the ID of the vendor to update
     * @param updatedVendor the updated vendor data
     * @return the updated vendor
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable Long id, @RequestBody Vendor updatedVendor) {
        return vendorService.updateVendor(id, updatedVendor);
    }

    /**
     * Deactivates a vendor by setting its active status to false.
     * 
     * @param id the ID of the vendor to deactivate
     * @return the updated vendor with active set to false
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PatchMapping("/{id}/deactivate")
    public Vendor deactivateVendor(@PathVariable Long id) {
        return vendorService.deactivateVendor(id);
    }

    /**
     * Retrieves all bookings associated with a specific vendor.
     * 
     * @param id the ID of the vendor
     * @return a list of bookings for the vendor
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/{id}/bookings")
    public List<Booking> getBookings(@PathVariable Long id) {
        return vendorService.getBookings(id);
    }

    /**
     * Checks if a vendor is available within a given time range.
     * 
     * @param id the ID of the vendor
     * @param startDateTime the start of the requested time interval
     * @param endDateTime the end of the requested time interval
     * @return true if the vendor is available, false otherwise
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/{id}/availability")
    public boolean isAvailable(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        return vendorService.isAvailable(id, startDateTime, endDateTime);
    }

    /**
     * Deletes a vendor from the system.
     * 
     * @param id the ID of the vendor to delete
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @DeleteMapping("/{id}")
    public void deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
    }
}