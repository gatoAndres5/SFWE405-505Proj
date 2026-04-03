package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Vendor;
import com.example.demo.repository.VendorRepository;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id).orElse(null);
    }

    public Vendor updateVendor(Long id, Vendor updatedVendor) {
        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null) {
            return null;
        }

        vendor.setName(updatedVendor.getName());
        vendor.setContactEmail(updatedVendor.getContactEmail());
        vendor.setContactPhone(updatedVendor.getContactPhone());

        return vendorRepository.save(vendor);
    }

    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }
}
