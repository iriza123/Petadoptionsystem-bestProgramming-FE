package com.petadoption.system.controller;

import com.petadoption.system.dto.ApiResponse;
import com.petadoption.system.model.AdoptionRequest;
import com.petadoption.system.service.AdoptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdoptionController {

    private final AdoptionService adoptionService;

    /**
     * Submit adoption request
     * POST /api/adoptions
     * Body: { "userId": 1, "petId": 2, "reason": "I love dogs..." }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AdoptionRequest>> submitRequest(
            @RequestBody Map<String, Object> requestBody
    ) {
        Long userId = Long.valueOf(requestBody.get("userId").toString());
        Long petId = Long.valueOf(requestBody.get("petId").toString());
        String reason = requestBody.get("reason").toString();

        AdoptionRequest request = adoptionService.submitRequest(userId, petId, reason);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Adoption request submitted successfully", request));
    }

    /**
     * Get user's adoption requests
     * GET /api/adoptions/my-requests/{userId}
     */
    @GetMapping("/my-requests/{userId}")
    public ResponseEntity<ApiResponse<List<AdoptionRequest>>> getUserRequests(
            @PathVariable Long userId
    ) {
        List<AdoptionRequest> requests = adoptionService.getUserRequests(userId);
        return ResponseEntity.ok(ApiResponse.success("Requests retrieved successfully", requests));
    }

    /**
     * Get all pending requests (Admin)
     * GET /api/adoptions/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<AdoptionRequest>>> getPendingRequests() {
        List<AdoptionRequest> requests = adoptionService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success("Pending requests retrieved successfully", requests));
    }

    /**
     * Get all requests (Admin)
     * GET /api/adoptions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdoptionRequest>>> getAllRequests() {
        List<AdoptionRequest> requests = adoptionService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success("All requests retrieved successfully", requests));
    }

    /**
     * Approve adoption request (Admin)
     * PUT /api/adoptions/{id}/approve
     * Body: { "adminNotes": "Approved after home visit" }
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<AdoptionRequest>> approveRequest(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String adminNotes = body != null ? body.get("adminNotes") : null;
        AdoptionRequest request = adoptionService.approveRequest(id, adminNotes);
        return ResponseEntity.ok(ApiResponse.success("Adoption request approved", request));
    }

    /**
     * Reject adoption request (Admin)
     * PUT /api/adoptions/{id}/reject
     * Body: { "adminNotes": "Insufficient experience" }
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<AdoptionRequest>> rejectRequest(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String adminNotes = body != null ? body.get("adminNotes") : null;
        AdoptionRequest request = adoptionService.rejectRequest(id, adminNotes);
        return ResponseEntity.ok(ApiResponse.success("Adoption request rejected", request));
    }
}
