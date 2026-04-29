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

@RestController
@RequestMapping("/vendors")
public class VendorController {

    VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        Vendor newVendor = vendorService.createVendor(vendor);
        return newVendor;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF', 'PARTICIPANT')")
    @GetMapping
    public List<Vendor> getAllVendors() {
        List<Vendor> vendorList = vendorService.getAllVendors();
        return vendorList;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF', 'PARTICIPANT')")
    @GetMapping("/{id}")
    public Vendor getVendorById(@PathVariable Long id) {
        Vendor vendor = vendorService.getVendorById(id);
        return vendor;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable Long id, @RequestBody Vendor updatedVendor) {
        Vendor vendor = vendorService.updateVendor(id, updatedVendor);
        return vendor;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PatchMapping("/{id}/deactivate")
    public Vendor deactivateVendor(@PathVariable Long id) {
        Vendor vendor = vendorService.deactivateVendor(id);
        return vendor;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
    }

}
