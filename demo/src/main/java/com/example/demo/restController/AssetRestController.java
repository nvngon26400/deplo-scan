package com.example.demo.restController;

import com.example.demo.entity.Asset;
import com.example.demo.exception.ResourceNotFoundException;
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
@RequestMapping("v1/api/assets")
@Slf4j
public class AssetRestController {

    private final AssetService assetService;
    private final AuditService auditService;

    public AssetRestController(AssetService assetService, AuditService auditService) {
        this.assetService = assetService;
        this.auditService = auditService;
    }

    @GetMapping()
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<Asset> getAssetByBarcode(@PathVariable String barcode) {
        Asset asset = assetService.getAssetByBarcode(barcode);
        return ResponseEntity.ok(asset);
    }

    @PostMapping("/capture")
    public ResponseEntity<?> captureAsset(
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        Asset asset = auditService.processAssetImage(imageFile);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Asset>> getAssetsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(assetService.getAssetsByDepartment(department));
    }

    @GetMapping("/images/{filename:.+}")
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
}