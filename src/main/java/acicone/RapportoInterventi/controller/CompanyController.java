package acicone.RapportoInterventi.controller;

import acicone.RapportoInterventi.payloads.CompanyRequestDTO;
import acicone.RapportoInterventi.payloads.CompanyResponseDTO;
import acicone.RapportoInterventi.payloads.CompanyUpdateDTO;
import acicone.RapportoInterventi.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // ======================
    // CREATE COMPANY
    // ======================
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponseDTO createCompany(@RequestBody @Valid CompanyRequestDTO dto) {
        return companyService.createCompany(dto);
    }

    // ======================
    // GET ALL COMPANIES
    // ======================
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    // ======================
    // GET COMPANY BY ID
    // ======================
    @GetMapping("/{companyId}")
    @PreAuthorize("isAuthenticated()")
    public CompanyResponseDTO getCompanyById(@PathVariable UUID companyId) {
        return companyService.getCompanyById(companyId);
    }

    // ======================
    // SEARCH COMPANIES BY NAME
    // ======================
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<CompanyResponseDTO> searchCompanies(@RequestParam String name) {
        return companyService.searchCompaniesByName(name);
    }

    // ======================
    // UPDATE COMPANY
    // ======================
    @PutMapping("/{companyId}")
    @PreAuthorize("isAuthenticated()")
    public CompanyResponseDTO updateCompany(
            @PathVariable UUID companyId,
            @RequestBody @Valid CompanyUpdateDTO dto) {
        return companyService.updateCompany(companyId, dto);
    }

    // ======================
    // DELETE COMPANY
    // ======================
    @DeleteMapping("/{companyId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable UUID companyId) {
        companyService.deleteCompany(companyId);
    }
}