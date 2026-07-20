package com.WW.dto.output;

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
}
