package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.VendorRepository;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public Vendor createVendor(Vendor vendor) {
        if (vendor == null ||
            isBlank(vendor.getName()) ||
            isBlank(vendor.getContactName()) ||
            isBlank(vendor.getContactEmail()) ||
            isBlank(vendor.getContactPhone()) ||
            vendor.getAddress() == null ||
            isBlank(vendor.getAddress().getStreet()) ||
            isBlank(vendor.getAddress().getCity()) ||
            isBlank(vendor.getAddress().getState()) ||
            isBlank(vendor.getAddress().getZipCode()) ||
            isBlank(vendor.getAddress().getCountry())) {
            throw new IllegalArgumentException("Missing required vendor fields.");
        }

        if (!isValidEmail(vendor.getContactEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // duplicate email now returns 409 instead of 400
        if (vendorRepository.existsByContactEmail(vendor.getContactEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vendor already exists.");
        }

        vendor.setActive(true);
        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());

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

        if (updatedVendor == null ||
            isBlank(updatedVendor.getName()) ||
            isBlank(updatedVendor.getContactName()) ||
            isBlank(updatedVendor.getContactEmail()) ||
            isBlank(updatedVendor.getContactPhone()) ||
            updatedVendor.getAddress() == null ||
            isBlank(updatedVendor.getAddress().getStreet()) ||
            isBlank(updatedVendor.getAddress().getCity()) ||
            isBlank(updatedVendor.getAddress().getState()) ||
            isBlank(updatedVendor.getAddress().getZipCode()) ||
            isBlank(updatedVendor.getAddress().getCountry())) {
            throw new IllegalArgumentException("Missing required vendor fields.");
        }

        if (!isValidEmail(updatedVendor.getContactEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // duplicate email on update now returns 409
        if (!vendor.getContactEmail().equals(updatedVendor.getContactEmail()) &&
            vendorRepository.existsByContactEmail(updatedVendor.getContactEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vendor already exists.");
        }

        vendor.setName(updatedVendor.getName());
        vendor.setContactName(updatedVendor.getContactName());
        vendor.setContactPhone(updatedVendor.getContactPhone());
        vendor.setContactEmail(updatedVendor.getContactEmail());
        vendor.setAddress(updatedVendor.getAddress());
        vendor.setAvailability(updatedVendor.getAvailability());
        vendor.setActive(updatedVendor.isActive());
        vendor.setUpdatedAt(LocalDateTime.now());

        return vendorRepository.save(vendor);
    }

    public Vendor deactivateVendor(Long id) {
        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null) {
            return null;
        }

        vendor.setActive(false);
        vendor.setUpdatedAt(LocalDateTime.now());

        return vendorRepository.save(vendor);
    }

    public List<Booking> getBookings(Long id) {
        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null) {
            return null;
        }

        return vendor.getBookings();
    }

    public boolean isAvailable(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null || !vendor.isActive()) {
            return false;
        }

        List<Booking> bookings = vendor.getBookings();

        for (Booking booking : bookings) {
            if (booking.getStartDateTime().isBefore(endDateTime) &&
                booking.getEndDateTime().isAfter(startDateTime)) {
                return false;
            }
        }

        return true;
    }

    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}