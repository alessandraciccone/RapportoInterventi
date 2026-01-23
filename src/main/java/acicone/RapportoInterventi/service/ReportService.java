package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.Company;
import acicone.RapportoInterventi.entities.Report;
import acicone.RapportoInterventi.entities.ReportType;
import acicone.RapportoInterventi.entities.User;
import acicone.RapportoInterventi.exceptions.NotFoundException;
import acicone.RapportoInterventi.payloads.CompanyResponseDTO;
import acicone.RapportoInterventi.payloads.ReportRequestoDTO;
import acicone.RapportoInterventi.payloads.ReportResponseDTO;
import acicone.RapportoInterventi.payloads.ReportUpdateDTO;
import acicone.RapportoInterventi.repositories.CompanyRepository;
import acicone.RapportoInterventi.repositories.ReportRepository;
import acicone.RapportoInterventi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    public ReportService(
            ReportRepository reportRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            FileStorageService fileStorageService,
            EmailService emailService
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.fileStorageService = fileStorageService;
        this.emailService = emailService;
    }

    // ================= CREATE =================

    public ReportResponseDTO createReport(ReportRequestoDTO dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new NotFoundException("Azienda non trovata"));

        validateOrari(dto.oraInizio(), dto.oraFine());

        Report report = new Report();
        report.setUser(user);
        report.setCompany(company);
        report.setAnno(dto.anno());
        report.setDataIntervento(dto.dataIntervento());
        report.setOraInizio(dto.oraInizio());
        report.setOraFine(dto.oraFine());
        report.setNote(dto.note());
        report.setCreatedAt(LocalDate.now());

        int ore = calcolaOreLavorate(dto.oraInizio(), dto.oraFine());
        report.setOreLavorate(ore);

        if (dto.fileBase64() != null && !dto.fileBase64().isBlank()) {
            report.setType(ReportType.FILE);
            String fileUrl = fileStorageService.saveBase64(dto.fileBase64());
            report.setFileUrl(fileUrl);
        } else {
            report.setType(ReportType.FORM);
        }

        Report savedReport = reportRepository.save(report);

        // ================= INVIO EMAIL =================
        String subject = "Nuovo Report registrato: " + savedReport.getAnno();
        String body = """
                È stato registrato un nuovo report.

                Azienda: %s
                Utente: %s
                Data intervento: %s
                Ore lavorate: %d
                Note: %s
                """.formatted(
                savedReport.getCompany().getRagioneSociale(),
                savedReport.getUser().getCognome(),
                savedReport.getDataIntervento(),
                savedReport.getOreLavorate(),
                savedReport.getNote() != null ? savedReport.getNote() : "-"
        );

        if (savedReport.getCompany().getEmail() != null) {
            emailService.sendEmail(savedReport.getCompany().getEmail(), subject, body);
        }

        return mapToResponseDTO(savedReport);
    }

    // ================= UPDATE =================

    public ReportResponseDTO updateReport(UUID reportId, ReportUpdateDTO dto) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report non trovato"));

        LocalTime oraInizio = dto.oraInizio() != null ? dto.oraInizio() : report.getOraInizio();
        LocalTime oraFine = dto.oraFine() != null ? dto.oraFine() : report.getOraFine();

        validateOrari(oraInizio, oraFine);

        if (dto.anno() != null) report.setAnno(dto.anno());
        if (dto.dataIntervento() != null) report.setDataIntervento(dto.dataIntervento());
        if (dto.oraInizio() != null) report.setOraInizio(dto.oraInizio());
        if (dto.oraFine() != null) report.setOraFine(dto.oraFine());
        if (dto.note() != null) report.setNote(dto.note());

        report.setOreLavorate(calcolaOreLavorate(oraInizio, oraFine));

        if (dto.fileBase64() != null && !dto.fileBase64().isBlank()) {
            report.setType(ReportType.FILE);
            String fileUrl = fileStorageService.saveBase64(dto.fileBase64());
            report.setFileUrl(fileUrl);
        }

        return mapToResponseDTO(reportRepository.save(report));
    }

    // ================= SEARCH =================

    public List<ReportResponseDTO> searchReport(
            UUID userId, Integer anno, String ragione, LocalDate start, LocalDate end
    ) {
        return reportRepository.search(userId, anno, ragione, start, end)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ================= GET / DELETE =================

    public ReportResponseDTO getReportById(UUID reportId) {
        Report r = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Rapporto non trovato"));
        return mapToResponseDTO(r);
    }

    public void deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new NotFoundException("Rapporto non trovato");
        }
        reportRepository.deleteById(reportId);
    }

    // ================= HELPERS =================

    private void validateOrari(LocalTime inizio, LocalTime fine) {
        if (inizio != null && fine != null && !inizio.isBefore(fine)) {
            throw new IllegalArgumentException(
                    "Ora di inizio deve essere precedente all'ora di fine"
            );
        }
    }

    private int calcolaOreLavorate(LocalTime inizio, LocalTime fine) {
        return (int) Duration.between(inizio, fine).toHours();
    }

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
