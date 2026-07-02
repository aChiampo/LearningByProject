package com.WW.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.Visita;

/**
 * @author: cristian.pappalardo
 * Modello di repository per la tabella VISITA
 * Last update: 28/06/2026
 */

public interface VisitaRepository extends JpaRepository<Visita, Integer> {

    //Trova tutte le visite associate a un tipo di visita specifico
    public List<Visita> findByTipoVisitaId(Integer idTipoVisita);
    //Trova tutte le visite associate a un veterinario specifico
    public List<Visita> findByIdVeterinario(Integer idVeterinario);
    //Trova tutte le visite associate a un animale specifico
    public List<Visita> findByIdAnimale(Integer idAnimale);
    //Trova la visita associata a un pagamento specifico
    public Visita findByIDPagamento(Integer idPagamento);  
    //Trova tutte le visite che devono essere pagate
    public List<Visita> findByPagatoTrue();
    //Trova tutte le visite che non sono ancora state pagate
    public List<Visita> findByPagatoFalse();
    public List<Visita> findByPagatoAndTipoVisitaId(boolean pagato, Integer idTipoVisita);


}
