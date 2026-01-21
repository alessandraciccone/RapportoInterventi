package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.Report;
import acicone.RapportoInterventi.exceptions.NotFoundException;
import acicone.RapportoInterventi.payloads.CompanyResponseDTO;
import acicone.RapportoInterventi.payloads.ReportResponseDTO;
import acicone.RapportoInterventi.repositories.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    // SEARCH REPORTS

    public List<ReportResponseDTO> searchReport(UUID userId, Integer anno, String ragione,
                                                LocalDate start, LocalDate end) {

        List<Report> reports = reportRepository.search(userId, anno, ragione, start, end);

        return reports.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    // GET REPORT BY ID

    public ReportResponseDTO getReportById(UUID reportId) {
        Report r = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Rapporto non trovato"));

        return mapToResponseDTO(r);
    }


    // DELETE REPORT

    public void deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new NotFoundException("Rapporto non trovato");
        }
        reportRepository.deleteById(reportId);
    }


    // HELPER METHOD

    private ReportResponseDTO mapToResponseDTO(Report r) {
        return new ReportResponseDTO(
                r.getId(),
                r.getUser().getId(),
                new CompanyResponseDTO(
                        r.getCompany().getId(),
                        r.getCompany().getRagioneSociale(),
                        r.getCompany().getEmail()
                ),
                r.getAnno(),
                r.getDataIntervento(),
                r.getOraInizio(),
                r.getOraFine(),
                r.getNote(),
                r.getFileUrl()
        );
    }
}