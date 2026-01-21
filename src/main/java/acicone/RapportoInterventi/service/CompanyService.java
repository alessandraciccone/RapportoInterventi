package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.Company;
import acicone.RapportoInterventi.exceptions.BadRequestExceptions;
import acicone.RapportoInterventi.exceptions.NotFoundException;
import acicone.RapportoInterventi.payloads.CompanyRequestDTO;
import acicone.RapportoInterventi.payloads.CompanyResponseDTO;
import acicone.RapportoInterventi.payloads.CompanyUpdateDTO;
import acicone.RapportoInterventi.repositories.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    
    // CREATE COMPANY

    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO dto) {
        // Verifica se la ragione sociale esiste già
        if (companyRepository.findByRagioneSocialeIgnoreCase(dto.ragioneSociale()).isPresent()) {
            throw new BadRequestExceptions("Ragione sociale già esistente");
        }

        Company company = new Company();
        company.setRagioneSociale(dto.ragioneSociale());
        company.setEmail(dto.email());

        Company saved = companyRepository.save(company);
        return mapToResponseDTO(saved);
    }


    // GET COMPANY BY ID

    public CompanyResponseDTO getCompanyById(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Azienda non trovata"));
        return mapToResponseDTO(company);
    }


    // GET ALL COMPANIES

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    // SEARCH COMPANIES BY NAME

    public List<CompanyResponseDTO> searchCompaniesByName(String ragioneSociale) {
        List<Company> companies = companyRepository
                .findByRagioneSocialeContainingIgnoreCase(ragioneSociale);

        return companies.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    // UPDATE COMPANY

    @Transactional
    public CompanyResponseDTO updateCompany(UUID companyId, CompanyUpdateDTO dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Azienda non trovata"));

        // Aggiorna solo i campi non nulli
        if (dto.ragioneSociale() != null && !dto.ragioneSociale().isBlank()) {
            // Verifica che la nuova ragione sociale non esista già (esclusa l'azienda corrente)
            companyRepository.findByRagioneSocialeIgnoreCase(dto.ragioneSociale())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(companyId)) {
                            throw new BadRequestExceptions("Ragione sociale già esistente");
                        }
                    });
            company.setRagioneSociale(dto.ragioneSociale());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            company.setEmail(dto.email());
        }

        Company updated = companyRepository.save(company);
        return mapToResponseDTO(updated);
    }


    // DELETE COMPANY

    @Transactional
    public void deleteCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Azienda non trovata"));

        // Verifica se ci sono rapporti associati
        if (company.getReports() != null && !company.getReports().isEmpty()) {
            throw new BadRequestExceptions(
                    "Impossibile eliminare l'azienda: ci sono " +
                            company.getReports().size() + " rapporti associati"
            );
        }

        companyRepository.delete(company);
    }


    // CHECK IF COMPANY EXISTS

    public boolean companyExists(UUID companyId) {
        return companyRepository.existsById(companyId);
    }


    // GET COMPANY ENTITY (for internal use)

    public Company getCompanyEntity(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Azienda non trovata"));
    }


    // HELPER METHOD

    private CompanyResponseDTO mapToResponseDTO(Company company) {
        return new CompanyResponseDTO(
                company.getId(),
                company.getRagioneSociale(),
                company.getEmail()
        );
    }
}