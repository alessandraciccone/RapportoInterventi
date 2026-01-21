package acicone.RapportoInterventi.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    private String fileName;
    private String filePath;
private String fileContentType;
//form
    private String descrizione;
    @Column(nullable = false)
    private LocalDate  dataIntervento;
    @Column(nullable = false)
    private Integer oreLavorate;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private String note;
    private String fileUrl; // ad esempio la URL di un file salvato

    @Column(nullable = false)
    private Integer anno;

            @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    @Column(nullable = false)
    private LocalDate createdAt;
 public Report() {
 };

    public Report(UUID id, ReportType type, String fileName, String filePath, String fileContentType, String descrizione, LocalDate dataIntervento, Integer oreLavorate, LocalTime oraInizio, LocalTime oraFine, String note, String fileUrl, Integer anno, User user, Company company, LocalDate createdAt) {
        this.id = id;
        this.type = type;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileContentType = fileContentType;
        this.descrizione = descrizione;
        this.dataIntervento = dataIntervento;
        this.oreLavorate = oreLavorate;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.note = note;
        this.fileUrl = fileUrl;
        this.anno = anno;
        this.user = user;
        this.company = company;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getDataIntervento() {
        return dataIntervento;
    }

    public void setDataIntervento(LocalDate dataIntervento) {
        this.dataIntervento = dataIntervento;
    }


    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public void setOraFine(LocalTime oraFine) {
        this.oraFine = oraFine;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getOreLavorate() {
        return oreLavorate;
    }

    public void setOreLavorate(Integer oreLavorate) {
        this.oreLavorate = oreLavorate;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", type=" + type +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileContentType='" + fileContentType + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", dataIntervento=" + dataIntervento +
                ", oreLavorate=" + oreLavorate +
                ", anno=" + anno +

                ", createdAt=" + createdAt +
                '}';
    }


}

