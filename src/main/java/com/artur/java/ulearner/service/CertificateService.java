package com.artur.java.ulearner.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.ColorConstants;
import com.artur.java.ulearner.dto.*;
import com.artur.java.ulearner.model.*;
import com.artur.java.ulearner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ProgressRepository progressRepository;

    @Transactional
    public CertificateDTO generateCertificate(String userEmail, Long courseId) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if certificate already exists
        var existingCert = certificateRepository.findByUserIdAndCourseId(user.getId(), courseId);
        if (existingCert.isPresent()) {
            return mapToCertificateDTO(existingCert.get());
        }

        // Check if course is completed
        Double completionPercentage = progressRepository.getCourseCompletionPercentage(user.getId(), courseId);
        if (completionPercentage == null || completionPercentage < 100.0) {
            throw new RuntimeException("Course not completed yet. Current progress: " +
                    (completionPercentage != null ? completionPercentage : 0) + "%");
        }

        // Generate unique certificate number
        String certificateNumber = generateCertificateNumber();
        LocalDateTime now = LocalDateTime.now();

        // Create certificate
        var certificate = Certificate.builder()
                .certificateNumber(certificateNumber)
                .user(user)
                .course(course)
                .issuedAt(now)
                .completedAt(now)
                .verified(true)
                .build();

        certificateRepository.save(certificate);

        return mapToCertificateDTO(certificate);
    }

    public byte[] generateCertificatePDF(String certificateNumber) {
        var certificate = certificateRepository.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            Paragraph title = new Paragraph("CERTIFICATE OF COMPLETION")
                    .setFontSize(28)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(100);
            document.add(title);

            // Subtitle
            Paragraph subtitle = new Paragraph("This is to certify that")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(40);
            document.add(subtitle);

            // Student Name
            String fullName = certificate.getUser().getFirstName() + " " + certificate.getUser().getLastName();
            Paragraph studentName = new Paragraph(fullName)
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(studentName);

            // Course completion text
            Paragraph courseText = new Paragraph("has successfully completed the course")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(courseText);

            // Course Name
            Paragraph courseName = new Paragraph(certificate.getCourse().getTitle())
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(courseName);

            // Date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            String dateStr = certificate.getIssuedAt().format(formatter);
            Paragraph date = new Paragraph("Issued on: " + dateStr)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(40);
            document.add(date);

            // Certificate Number
            Paragraph certNumber = new Paragraph("Certificate Number: " + certificate.getCertificateNumber())
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(60);
            document.add(certNumber);

            // Verification URL
            Paragraph verificationUrl = new Paragraph("Verify at: https://ulearner.com/verify/" + certificate.getCertificateNumber())
                    .setFontSize(10)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            document.add(verificationUrl);

            // Instructor Signature
            Paragraph signature = new Paragraph("_______________________")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(50);
            document.add(signature);

            String instructorName = certificate.getCourse().getInstructor().getFirstName() +
                    " " + certificate.getCourse().getInstructor().getLastName();
            Paragraph instructorText = new Paragraph(instructorName)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(instructorText);

            Paragraph instructorTitle = new Paragraph("Course Instructor")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(instructorTitle);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }

    public CertificateVerificationDTO verifyCertificate(String certificateNumber) {
        var certificate = certificateRepository.findByCertificateNumber(certificateNumber);

        if (certificate.isEmpty()) {
            return CertificateVerificationDTO.builder()
                    .valid(false)
                    .certificateNumber(certificateNumber)
                    .message("Certificate not found")
                    .build();
        }

        var cert = certificate.get();
        String studentName = cert.getUser().getFirstName() + " " + cert.getUser().getLastName();

        return CertificateVerificationDTO.builder()
                .valid(cert.getVerified())
                .certificateNumber(cert.getCertificateNumber())
                .studentName(studentName)
                .courseName(cert.getCourse().getTitle())
                .issuedAt(cert.getIssuedAt())
                .message(cert.getVerified() ? "Certificate is valid" : "Certificate has been revoked")
                .build();
    }

    public List<CertificateDTO> getUserCertificates(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return certificateRepository.findByUserId(user.getId()).stream()
                .map(this::mapToCertificateDTO)
                .collect(Collectors.toList());
    }

    private String generateCertificateNumber() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return "UL-" + uuid;
    }

    private CertificateDTO mapToCertificateDTO(Certificate certificate) {
        return CertificateDTO.builder()
                .id(certificate.getId())
                .certificateNumber(certificate.getCertificateNumber())
                .user(mapToUserDTO(certificate.getUser()))
                .course(mapToCourseDTO(certificate.getCourse()))
                .issuedAt(certificate.getIssuedAt())
                .completedAt(certificate.getCompletedAt())
                .pdfUrl("/certificates/" + certificate.getCertificateNumber() + "/pdf")
                .verified(certificate.getVerified())
                .build();
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }

    private CourseDTO mapToCourseDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .difficulty(course.getDifficulty())
                .build();
    }
}