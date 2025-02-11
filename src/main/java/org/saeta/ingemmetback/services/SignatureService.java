package org.saeta.ingemmetback.services;

import org.saeta.ingemmetback.entities.Signature;
import org.saeta.ingemmetback.repositories.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
