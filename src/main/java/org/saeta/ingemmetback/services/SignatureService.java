package org.saeta.ingemmetback.services;

import org.saeta.ingemmetback.dto.SignatureFilter;
import org.saeta.ingemmetback.entities.Signature;
import org.saeta.ingemmetback.repositories.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignatureService {
    private final SignatureRepository signatureRepository;

    public List<Signature> getAllSignatures() {
        return signatureRepository.findAll();
    }

    public Optional<Signature> getSignatureById(Integer id) {
        return signatureRepository.findById(id);
    }

    public Signature saveSignature(Signature signature) {
        return signatureRepository.save(signature);
    }

    public void deleteSignature(Integer id) {
        signatureRepository.deleteById(id);
    }

    /**
     * Genera un archivo Excel en memoria con las firmas que cumplan los filtros recibidos.
     * Retorna un arreglo de bytes para que el controlador lo envíe como download.
     */
    public byte[] exportSignaturesToExcel(SignatureFilter filter) {

        // 1. Obtener todas las firmas
        List<Signature> allSignatures = signatureRepository.findAll();

        // 2. Filtrar en memoria (similar a tu front)
        List<Signature> filtered = allSignatures.stream()
                .filter(sig -> {
                    // Extraemos la parte de fecha (yyyy-MM-dd)
                    String sigDateOnly = (sig.getFechahora() != null)
                            ? sig.getFechahora().toLocalDate().toString()
                            : "";

                    // 2.1 Filtro por columna (dni / nombre) + searchTerm
                    if (filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
                        if ("dni".equalsIgnoreCase(filter.getSelectedColumn())) {
                            if (!sig.getDni().toLowerCase().contains(filter.getSearchTerm().toLowerCase())) {
                                return false;
                            }
                        } else {
                            // Por defecto, filtra por nombre
                            if (!sig.getNombre().toLowerCase().contains(filter.getSearchTerm().toLowerCase())) {
                                return false;
                            }
                        }
                    }

                    // 2.2 Filtro por "fecha exacta" (dateFilter)
                    if (filter.getDateFilter() != null && !filter.getDateFilter().isEmpty()) {
                        if (!sigDateOnly.startsWith(filter.getDateFilter())) {
                            return false;
                        }
                    }

                    // 2.3 Filtro por rango de fechas
                    if (filter.getStartDateFilter() != null && !filter.getStartDateFilter().isEmpty()) {
                        if (sigDateOnly.compareTo(filter.getStartDateFilter()) < 0) {
                            return false;
                        }
                    }
                    if (filter.getEndDateFilter() != null && !filter.getEndDateFilter().isEmpty()) {
                        if (sigDateOnly.compareTo(filter.getEndDateFilter()) > 0) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        // 3. Crear el workbook y la hoja con Apache POI
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Firmas");

            // Creación de estilo para la cabecera (opcional)
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // 3.1 Cabecera
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("DNI");
            headerRow.createCell(2).setCellValue("RutaFirma");
            headerRow.createCell(3).setCellValue("Nombre");
            headerRow.createCell(4).setCellValue("FechaHora");

            for (Cell cell : headerRow) {
                cell.setCellStyle(headerStyle);
            }

            // 3.2 Rellenar las filas con los datos filtrados
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int rowNum = 1;
            for (Signature sig : filtered) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(sig.getId() == null ? "" : sig.getId().toString());
                row.createCell(1).setCellValue(sig.getDni() == null ? "" : sig.getDni());
                row.createCell(2).setCellValue(sig.getRutafir() == null ? "" : sig.getRutafir());
                row.createCell(3).setCellValue(sig.getNombre() == null ? "" : sig.getNombre());
                row.createCell(4).setCellValue(
                        sig.getFechahora() == null
                                ? ""
                                : sig.getFechahora().format(formatter)
                );
            }

            // Auto-ajustar columnas (opcional)
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // 4. Escribir el Workbook a un ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de Firmas", e);
        }
    }
}