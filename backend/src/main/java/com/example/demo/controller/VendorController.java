package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Vendor;
import com.example.demo.service.VendorService;

/**
 * REST controller for managing Vendor entities.
 *
 * Provides endpoints for creating, retrieving, updating,
 * deactivating, and deleting vendors in the system.
 *
 * Access to endpoints is controlled via role-based authorization.
 */
@RestController
@RequestMapping("/vendors")
public class VendorController {

    VendorService vendorService;

    /**
     * Constructor for VendorController.
     *
     * @param vendorService service layer handling vendor business logic
     */
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Creates a new vendor.
     *
     * Accessible by ADMIN and ORGANIZER roles.
     *
     * @param vendor the vendor object to create
     * @return the created vendor with generated ID and default values
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        Vendor newVendor = vendorService.createVendor(vendor);
        return newVendor;
    }

    /**
     * Retrieves all vendors.
     *
     * Accessible by ADMIN, ORGANIZER, STAFF, and PARTICIPANT roles.
     *
     * @return list of all vendors
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF', 'PARTICIPANT')")
    @GetMapping
    public List<Vendor> getAllVendors() {
        List<Vendor> vendorList = vendorService.getAllVendors();
        return vendorList;
    }

    /**
     * Retrieves a vendor by its ID.
     *
     * Accessible by ADMIN, ORGANIZER, STAFF, and PARTICIPANT roles.
     *
     * @param id the ID of the vendor
     * @return the vendor matching the given ID
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF', 'PARTICIPANT')")
    @GetMapping("/{id}")
    public Vendor getVendorById(@PathVariable Long id) {
        Vendor vendor = vendorService.getVendorById(id);
        return vendor;
    }

    /**
     * Updates an existing vendor.
     *
     * Accessible by ADMIN and ORGANIZER roles.
     *
     * @param id the ID of the vendor to update
     * @param updatedVendor the updated vendor data
     * @return the updated vendor
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable Long id, @RequestBody Vendor updatedVendor) {
        Vendor vendor = vendorService.updateVendor(id, updatedVendor);
        return vendor;
    }

    /**
     * Deactivates a vendor by setting its active status to false.
     *
     * Accessible by ADMIN and ORGANIZER roles.
     *
     * @param id the ID of the vendor to deactivate
     * @return the updated vendor with active set to false
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PatchMapping("/{id}/deactivate")
    public Vendor deactivateVendor(@PathVariable Long id) {
        Vendor vendor = vendorService.deactivateVendor(id);
        return vendor;
    }

    /**
     * Deletes a vendor by its ID.
     *
     * Accessible by ADMIN role only.
     *
     * @param id the ID of the vendor to delete
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
    }
}
