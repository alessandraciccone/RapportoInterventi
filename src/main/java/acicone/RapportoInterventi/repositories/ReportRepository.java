package acicone.RapportoInterventi.repositories;

import acicone.RapportoInterventi.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByUserIdAndAnnoOrderByDataInterventoDesc(UUID userId, Integer anno);
    List<Report>findByUserIdAndCompany_RagioneSocialeContainingIgnoreCase(UUID userId, String ragioneSociale);
List <Report> findByUserIdAndDataInterventoBetween(UUID userId, LocalDate start, LocalDate end);

    List<Report> findByUserIdAndAnnoAndCompany_RagioneSocialeContainingIgnoreCase(UUID userId, Integer anno, String ragioneSociale);

    //ADMIN SEARCH
    List<Report> findByAnno(Integer anno);
    List<Report> findByUser_Id(UUID userId);
    @Query("""
        SELECT r FROM Report r
        WHERE r.user.id = :userId
          AND (:anno IS NULL OR r.anno = :anno)
          AND (:ragione IS NULL OR LOWER(r.company.ragioneSociale)
               LIKE LOWER(CONCAT('%', :ragione, '%')))
          AND (:start IS NULL OR r.dataIntervento >= :start)
          AND (:end IS NULL OR r.dataIntervento <= :end)
        ORDER BY r.dataIntervento DESC
    """)
    List<Report> search(
            @Param("userId") UUID userId,
            @Param("anno") Integer anno,
            @Param("ragione") String ragione,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}