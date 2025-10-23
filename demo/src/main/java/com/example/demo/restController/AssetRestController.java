package com.example.demo.restController;

import com.example.demo.entity.Asset;
import com.example.demo.service.AssetService;
import com.example.demo.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class AssetRestController {

    private final AssetService assetService;
    private final AuditService auditService;

    public AssetRestController(AssetService assetService, AuditService auditService) {
        this.assetService = assetService;
        this.auditService = auditService;
    }

    @GetMapping("/assets")
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    // Audit APIs đã được chuyển sang AuditRestController

    @GetMapping("/assets/{barcode}")
    public ResponseEntity<Asset> getAssetByBarcode(@PathVariable String barcode) {
        Asset asset = assetService.getAssetByBarcode(barcode);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/assets/department/{department}")
    public ResponseEntity<List<Asset>> getAssetsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(assetService.getAssetsByDepartment(department));
    }

    @GetMapping("/images/assets/{filename:.+}")
    public ResponseEntity<byte[]> getAssetImage(@PathVariable String filename) {
        try {
            byte[] imageBytes = assetService.getAssetImage(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/assets/capture")
    public ResponseEntity<Map<String, Object>> captureAsset(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("auditorName") String auditorName,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {

        try {
            Asset asset = auditService.processAssetImage(
                    imageFile, auditorName, latitude, longitude);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Asset captured successfully!");
            response.put("assetId", asset.getId());
            response.put("deviceNumber", asset.getDeviceNumber());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to capture asset: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}