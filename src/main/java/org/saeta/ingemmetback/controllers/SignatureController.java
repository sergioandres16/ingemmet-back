package org.saeta.ingemmetback.controllers;

import org.saeta.ingemmetback.dto.SignatureFilter;
import org.saeta.ingemmetback.entities.Signature;
import org.saeta.ingemmetback.services.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;

    @GetMapping
    public ResponseEntity<List<Signature>> getAllSignatures() {
        return ResponseEntity.ok(signatureService.getAllSignatures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Signature> getSignatureById(@PathVariable Integer id) {
        Optional<Signature> signature = signatureService.getSignatureById(id);
        return signature.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Signature> createSignature(@RequestBody Signature signature) {
        return ResponseEntity.ok(signatureService.saveSignature(signature));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSignature(@PathVariable Integer id) {
        signatureService.deleteSignature(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exporta las firmas filtradas a Excel.
     * Se envía un objeto JSON con los filtros y se retorna un archivo .xlsx.
     */
    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportSignaturesToExcel(@RequestBody SignatureFilter filter) {
        // Llamamos a la lógica de generación en el servicio
        byte[] excelData = signatureService.exportSignaturesToExcel(filter);

        // Crea un recurso en memoria para adjuntarlo en la respuesta
        ByteArrayResource resource = new ByteArrayResource(excelData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signatures.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelData.length)
                .body(resource);
    }
}