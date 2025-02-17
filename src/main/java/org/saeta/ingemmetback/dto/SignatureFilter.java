package org.saeta.ingemmetback.dto;

import lombok.Data;

@Data
public class SignatureFilter {
    private String selectedColumn;   // "dni" o "nombre"
    private String searchTerm;       // cadena de búsqueda
    private String dateFilter;       // fecha exacta
    private String startDateFilter;  // fecha mínima
    private String endDateFilter;    // fecha máxima
}