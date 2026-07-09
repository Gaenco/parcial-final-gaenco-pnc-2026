package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.BookRequestDto;
import com.example.parcial.parcial2.domain.dtos.GenreCountDto;
import com.example.parcial.parcial2.domain.entities.Book;
import com.example.parcial.parcial2.domain.entities.Genre;
import com.example.parcial.parcial2.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(BookRequestDto dto) {
        Genre genreEnum = Genre.valueOf(dto.getGenre().toUpperCase());

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(genreEnum);
        book.setIsbn(dto.getIsbn());
        book.setAvailableCount(dto.getAvailableCount());
        book.setAvailable(dto.getAvailableCount() > 0);

        return bookRepository.save(book);
    }

    public Book getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public List<Book> getAllBooks(String author, String genre) {
        Genre genreEnum = genre != null ? Genre.valueOf(genre.toUpperCase()) : null;

        if (author != null && genreEnum != null) {
            return bookRepository.findByAuthorAndGenre(author, genreEnum);
        } else if (author != null) {
            return bookRepository.findByAuthor(author);
        } else if (genreEnum != null) {
            return bookRepository.findByGenre(genreEnum);
        } else {
            return bookRepository.findAll();
        }
    }

    public Book updateBook(UUID id, BookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        if (dto.getGenre() != null) {
            book.setGenre(Genre.valueOf(dto.getGenre().toUpperCase()));
        }
        book.setIsbn(dto.getIsbn());
        book.setAvailable(dto.isAvailable());
        book.setAvailableCount(dto.getAvailableCount());
        return bookRepository.save(book);
    }

    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setActive(false);
        bookRepository.save(book);
    }

    public List<GenreCountDto> getGenresAvailable() {
        return bookRepository.countBooksByGenre();
    }
}
