package acicone.RapportoInterventi.controller;

import acicone.RapportoInterventi.payloads.ReportRequestoDTO;
import acicone.RapportoInterventi.payloads.ReportResponseDTO;
import acicone.RapportoInterventi.payloads.ReportUpdateDTO;
import acicone.RapportoInterventi.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ========== CREATE ==========
    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(@Valid @RequestBody ReportRequestoDTO dto) {
        return ResponseEntity.ok(reportService.createReport(dto));
    }

    // ========== UPDATE ==========
    @PutMapping("/{reportId}")
    public ResponseEntity<ReportResponseDTO> updateReport(
            @PathVariable UUID reportId,
            @RequestBody ReportUpdateDTO dto
    ) {
        return ResponseEntity.ok(reportService.updateReport(reportId, dto));
    }

    // ========== GET ==========
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponseDTO> getReport(@PathVariable UUID reportId) {
        return ResponseEntity.ok(reportService.getReportById(reportId));
    }

    // ========== DELETE ==========
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    // ========== SEARCH ==========
    @GetMapping("/search")
    public ResponseEntity<List<ReportResponseDTO>> searchReports(
            @RequestParam UUID userId,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) String ragione,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end
    ) {
        return ResponseEntity.ok(reportService.searchReport(userId, anno, ragione, start, end));
    }
}
