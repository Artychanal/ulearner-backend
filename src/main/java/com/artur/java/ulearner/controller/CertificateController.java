package com.artur.java.ulearner.controller;

import com.artur.java.ulearner.dto.*;
import com.artur.java.ulearner.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/generate/{courseId}")
    public ResponseEntity<CertificateDTO> generateCertificate(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(certificateService.generateCertificate(
                authentication.getName(), courseId
        ));
    }

    @GetMapping("/{certificateNumber}/pdf")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String certificateNumber) {
        byte[] pdfBytes = certificateService.generateCertificatePDF(certificateNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificate-" + certificateNumber + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/verify/{certificateNumber}")
    public ResponseEntity<CertificateVerificationDTO> verifyCertificate(
            @PathVariable String certificateNumber
    ) {
        return ResponseEntity.ok(certificateService.verifyCertificate(certificateNumber));
    }

    @GetMapping("/my-certificates")
    public ResponseEntity<List<CertificateDTO>> getMyCertificates(Authentication authentication) {
        return ResponseEntity.ok(certificateService.getUserCertificates(authentication.getName()));
    }
}