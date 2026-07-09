package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.Visita;

/**
 * @author: cristian.pappalardo
 * Repository per la tabella VISITA
 * Last update: 28/06/2026
 */

public interface VisitaRepository extends JpaRepository<Visita, Integer> {

    //Trova tutte le visite associate a un tipo di visita specifico
    public List<Visita> findByTipoVisitaId(Integer idTipoVisita);
    //Trova tutte le visite associate a un veterinario specifico
    public List<Visita> findByVeterinarioId(Integer idVeterinario);
    //Trova tutte le visite associate a un animale specifico
    public List<Visita> findByAnimaleId(Integer idAnimale);
    //Trova la visita associata a un pagamento specifico
    public Optional<Visita> findByPagamentoId(Integer idPagamento);  
}
