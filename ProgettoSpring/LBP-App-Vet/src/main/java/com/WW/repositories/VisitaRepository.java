package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.WW.entities.Visita;
import java.time.LocalDateTime;

/**
 * @author: cristian.pappalardo
 *          Repository per la tabella VISITA
 *          Last update: 28/06/2026
 */

public interface VisitaRepository extends JpaRepository<Visita, Integer> {

    // Trova una visita a partire dalla data della visita
    public Optional<Visita> findByDataVisita(LocalDateTime dataVisita);

    // Trova tutte le visite associate a un tipo di visita specifico
    public List<Visita> findByTipoVisitaId(Integer idTipoVisita);

    // Trova tutte le visite associate a un veterinario specifico
    public List<Visita> findByVeterinarioId(Integer idVeterinario);

    // Trova tutte le visite associate a un animale specifico
    public List<Visita> findByAnimaleId(Integer idAnimale);

    // Trova la visita associata a un pagamento specifico
    public Optional<Visita> findByPagamentoId(Integer idPagamento);

    // Trova tutte le visite di un veterinario in un determinato intervallo di tempo
       @Query("""
        SELECT v FROM Visita v
        WHERE v.veterinario.id = :veterinarioId
          AND v.isDeleted = false
          AND v.dataVisita BETWEEN :windowStart AND :windowEnd
        """)
    List<Visita> findVisiteVeterinarioNelPeriodo(
            @Param("veterinarioId") int veterinarioId,
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd);
}

