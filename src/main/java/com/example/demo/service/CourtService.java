package com.example.demo.service;

import com.example.demo.dto.CourtRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Category;
import com.example.demo.model.Court;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;
    private final CategoryRepository categoryRepository;

    public List<Court> getAllCourts() {
        return courtRepository.findAll();
    }

    public Court getCourtById(Long id) {
        return courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với ID: " + id));
    }

    // Dùng DTO để tạo mới
    public Court addCourt(CourtRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục ID: " + request.getCategoryId()));

        Court court = Court.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .imageUrl(request.getImageUrl())
                .pricePerHour(request.getPricePerHour())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .category(category)
                .build();

        return courtRepository.save(court);
    }

    // Dùng DTO để cập nhật
    public Court updateCourt(Long id, CourtRequest request) {
        Court existingCourt = getCourtById(id);
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục ID: " + request.getCategoryId()));

        existingCourt.setName(request.getName());
        existingCourt.setDescription(request.getDescription());
        existingCourt.setAddress(request.getAddress());
        existingCourt.setImageUrl(request.getImageUrl());
        existingCourt.setPricePerHour(request.getPricePerHour());
        existingCourt.setOpeningTime(request.getOpeningTime());
        existingCourt.setClosingTime(request.getClosingTime());
        existingCourt.setCategory(category);

        return courtRepository.save(existingCourt);
    }

    public void deleteCourt(Long id) {
        Court court = getCourtById(id);
        courtRepository.delete(court);
    }

    public Court createCourt(Court court) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCourt'");
    }

    public Court updateCourt(Long id, Court courtDetails) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCourt'");
    }
}