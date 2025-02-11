package org.saeta.ingemmetback.controllers;

import org.saeta.ingemmetback.entities.Signature;
import org.saeta.ingemmetback.services.SignatureService;
import lombok.RequiredArgsConstructor;
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
}
