package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.EventAssignmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VendorRepository;

@Service
public class VendorService {

    VendorRepository vendorRepository;
    UserRepository userRepository;
    EventAssignmentRepository eventAssignmentRepository;

    public VendorService(VendorRepository vendorRepository, UserRepository userRepository, EventAssignmentRepository eventAssignmentRepository) {
        this.vendorRepository = vendorRepository;
        this.userRepository = userRepository;
        this.eventAssignmentRepository = eventAssignmentRepository;
    }

    public Vendor createVendor(Vendor vendor) {

        // check that vendor name is provided
        if (vendor.getName() == null || vendor.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor name is required.");
        }

        // check that contact name is provided
        if (vendor.getContactName() == null || vendor.getContactName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact name is required.");
        }

        // check that contact email is provided
        if (vendor.getContactEmail() == null || vendor.getContactEmail().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact email is required.");
        }
        // check that contact email format is valid
        if (!vendor.getContactEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid contact email format.");
        }

        // check that contact phone is provided
        if (vendor.getContactPhone() == null || vendor.getContactPhone().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact phone is required.");
        }

        // check if a vendor with this email already exists
        boolean emailAlreadyExists = vendorRepository.existsByContactEmail(vendor.getContactEmail());
        if (emailAlreadyExists == true) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email exists.");
        }

        vendor.setActive(true);
        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());

        Vendor savedVendor = vendorRepository.save(vendor);
        return savedVendor;
    }

    public List<Vendor> getAllVendors() {

        // get the current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        // admin can see all vendors
        if (currentUser.getRole() == UserRole.ADMIN) {
            List<Vendor> allVendors = vendorRepository.findAll();
            return allVendors;
        }

        // organizer can see all vendors
        if (currentUser.getRole() == UserRole.ORGANIZER) {
            List<Vendor> allVendors = vendorRepository.findAll();
            return allVendors;
        }

        // staff can see all vendors
        if (currentUser.getRole() == UserRole.STAFF) {
            List<Vendor> allVendors = vendorRepository.findAll();
            return allVendors;
        }

        // participant can see all vendors
        if (currentUser.getRole() == UserRole.PARTICIPANT) {
            List<Vendor> allVendors = vendorRepository.findAll();
            return allVendors;
        }

        // if none of the roles match throw forbidden
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, " Unauthorized");
    }

    public Vendor getVendorById(Long id) {

        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found.");
        }

        return vendor;
    }

    public Vendor updateVendor(Long id, Vendor updatedVendor) {

        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found.");
        }

        // check that vendor name is provided
        if (updatedVendor.getName() == null || updatedVendor.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor name is required.");
        }
        // check that contact email format is valid
        if (!vendor.getContactEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid contact email format.");
        }

        // check if the email was changed and if the new email is already taken
        if (!vendor.getContactEmail().equals(updatedVendor.getContactEmail())) {
            boolean emailAlreadyExists = vendorRepository.existsByContactEmail(updatedVendor.getContactEmail());
            if (emailAlreadyExists == true) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email exists.");
            }
        }

        vendor.setName(updatedVendor.getName());
        vendor.setContactName(updatedVendor.getContactName());
        vendor.setContactEmail(updatedVendor.getContactEmail());
        vendor.setContactPhone(updatedVendor.getContactPhone());
        vendor.setAddress(updatedVendor.getAddress());
        vendor.setAvailability(updatedVendor.getAvailability());
        vendor.setActive(updatedVendor.isActive());
        vendor.setUpdatedAt(LocalDateTime.now());

        Vendor savedVendor = vendorRepository.save(vendor);
        return savedVendor;
    }

    public Vendor deactivateVendor(Long id) {

        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found.");
        }

        vendor.setActive(false);
        vendor.setUpdatedAt(LocalDateTime.now());

        Vendor savedVendor = vendorRepository.save(vendor);
        return savedVendor;
    }

    public void deleteVendor(Long id) {

        Vendor vendor = vendorRepository.findById(id).orElse(null);

        if (vendor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found.");
        }

        vendorRepository.deleteById(id);
    }

}
