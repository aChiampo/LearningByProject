package com.WW.dto.output;

import com.WW.entities.Animale;

public record AnimaleOutputDTO(
        String nome,
        String specie,
        String razza,
        String sesso,
        Double peso,
        String microchip,
        String note,
        String dataNascita) {

    // Constructor for cases where all fields are provided
    public AnimaleOutputDTO(String nome, String specie, String razza, String sesso, Double peso, String microchip,
            String note, String dataNascita) {
        this.nome = nome;
        this.specie = specie;
        this.razza = razza;
        this.sesso = sesso;
        this.peso = peso;
        this.microchip = microchip;
        this.note = note;
        this.dataNascita = dataNascita;
    }

    // Constructor for cases where only the basic information is needed
    public AnimaleOutputDTO(String nome, String specie, String razza, String sesso) {
        this(nome, specie, razza, sesso, null, null, null, null);
    }

    /**
     * Static method to convert an Animale entity to an AnimaleOutputDTO
     * 
     * @author: Cristian Pappalardo
     * @param animale the Animale entity to convert
     * @return an instance of AnimaleOutputDTO with the data from the Animale entity
     */
    public static AnimaleOutputDTO fromEntity(Animale animale) {
        return new AnimaleOutputDTO(
                animale.getNome(),
                animale.getSpecie(),
                animale.getRazza(),
                animale.getSesso(),
                animale.getPeso(),
                animale.getMicrochip(),
                animale.getNote(),
                animale.getDataNascita().toString());
    }

    /**
     * Static method to convert an Animale entity to an AnimaleOutputDTO for use in
     * Visita context
     * 
     * @author: Cristian Pappalardo
     * @param animale
     * @return an instance of AnimaleOutputDTO with the basic data from the Animale
     *         entity
     */
    public static AnimaleOutputDTO fromEntityForVisita(Animale animale) {
        return new AnimaleOutputDTO(
                animale.getNome(),
                animale.getSpecie(),
                animale.getRazza(),
                animale.getSesso());
    }

}
