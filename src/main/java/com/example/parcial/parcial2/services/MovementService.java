package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.MovementRequestDto;
import com.example.parcial.parcial2.domain.entities.Book;
import com.example.parcial.parcial2.domain.entities.Lector;
import com.example.parcial.parcial2.domain.entities.Movement;
import com.example.parcial.parcial2.domain.entities.MovementType;
import com.example.parcial.parcial2.repositories.BookRepository;
import com.example.parcial.parcial2.repositories.LectorRepository;
import com.example.parcial.parcial2.repositories.MovementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MovementService {

    private final MovementRepository movementRepository;
    private final LectorRepository lectorRepository;
    private final BookRepository bookRepository;

    public MovementService(MovementRepository movementRepository,
                           LectorRepository lectorRepository,
                           BookRepository bookRepository) {
        this.movementRepository = movementRepository;
        this.lectorRepository = lectorRepository;
        this.bookRepository = bookRepository;
    }

    public Movement borrowBook(MovementRequestDto dto) {
        Book book = bookRepository.findByIsbn(dto.getIsbn())
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado"));
        Lector lector = lectorRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Lector no encontrado"));

        if (book.getAvailableCount() <= 0) {
            throw new IllegalStateException("No hay copias disponibles de este libro");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setAvailable(book.getAvailableCount() > 0);
        bookRepository.save(book);

        return saveMovement(book, lector, MovementType.BORROWING);
    }

    public Movement returnBook(MovementRequestDto dto) {
        Book book = bookRepository.findByIsbn(dto.getIsbn())
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado"));
        Lector lector = lectorRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Lector no encontrado"));

        boolean tienePrestamoActivo = movementRepository
                .existsActiveBorrowing(book.getId(), lector.getId());

        if (!tienePrestamoActivo) {
            throw new IllegalStateException("Este lector no tiene un préstamo activo de este libro");
        }

        book.setAvailableCount(book.getAvailableCount() + 1);
        book.setAvailable(true);
        bookRepository.save(book);

        return saveMovement(book, lector, MovementType.RETURN);
    }

    private Movement saveMovement(Book book, Lector lector, MovementType type) {
        Movement movement = new Movement();
        movement.setBook(book);
        movement.setLector(lector);
        movement.setTimestamp(Instant.now());
        movement.setType(type);
        return movementRepository.save(movement);
    }
}
