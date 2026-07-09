package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.LectorRequestDto;
import com.example.parcial.parcial2.domain.entities.Lector;
import com.example.parcial.parcial2.repositories.LectorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Service
public class LectorService {

    private final LectorRepository lectorRepository;

    public LectorService(LectorRepository lectorRepository) {
        this.lectorRepository = lectorRepository;
    }

    public Lector registerLector(LectorRequestDto dto) {
        String email = generateEmail(dto.getName(), dto.getLastname());

        Lector lector = new Lector();
        lector.setName(dto.getName());
        lector.setLastname(dto.getLastname());
        lector.setDui(dto.getDui());
        lector.setEmail(email);

        return lectorRepository.save(lector);
    }

    public Lector getLectorById(UUID id) {
        return lectorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lector no encontrado con id: " + id));
    }

    public List<Lector> getAllLectors() {
        return lectorRepository.findAll();
    }

    public Lector updateLector(UUID id, LectorRequestDto dto) {
        Lector lector = getLectorById(id);
        lector.setName(dto.getName());
        lector.setLastname(dto.getLastname());
        lector.setDui(dto.getDui());
        return lectorRepository.save(lector);
    }

    public void deleteLector(UUID id) {
        Lector lector = getLectorById(id);
        lector.setActive(false);
        lectorRepository.save(lector);
    }

    private String generateEmail(String name, String lastname) {
        String base = normalize(name) + "." + normalize(lastname);
        String email = base + "@library.com";

        int counter = 1;
        while (lectorRepository.findByEmail(email).isPresent()) {
            email = base + counter + "@library.com";
            counter++;
        }
        return email;
    }

    private String normalize(String text) {
        String normalized = Normalizer.normalize(text.trim().toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "");
    }
}