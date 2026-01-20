package acicone.RapportoInterventi.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String ragioneSociale;
    @Column(nullable = false)
    private String email;
    private String indirizzo;
    private String partitaIva;
    @OneToMany(mappedBy = "company")
private List<Report> reports;
    public Company(){};

    public Company(UUID id, String ragioneSociale, String email, String indirizzo, String partitaIva, List<Report> reports) {
        this.id = id;
        this.ragioneSociale = ragioneSociale;
        this.email = email;
        this.indirizzo = indirizzo;
        this.partitaIva = partitaIva;
        this.reports = reports;
    }

    public UUID getId() {
        return id;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", ragioneSociale='" + ragioneSociale + '\'' +
                ", email='" + email + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", partitaIva='" + partitaIva + '\'' +
                '}';
    }
}
