package de.adesso.schulungen.testapplication.web;

import de.adesso.schulungen.testapplication.infrasturcture.BookCriteria;
import de.adesso.schulungen.testapplication.infrasturcture.BookEntity;
import de.adesso.schulungen.testapplication.web.model.BookDto;
import de.adesso.schulungen.testapplication.infrasturcture.BookRepository;
import de.adesso.schulungen.testapplication.web.model.CreateBookDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        log.debug("BookController created");
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new book")
    public BookDto create(@RequestBody final CreateBookDto createBookDto) {
        final BookDto bookDto = new BookDto();
        bookDto.setId(createBookDto.metaInfos().id());
        bookDto.setTitle(createBookDto.metaInfos().title());
        bookDto.setAuthor(createBookDto.metaInfos().author());
        final BookEntity createdBook = bookRepository.save(bookDto.toDomain());
        return BookDto.fromDomain(createdBook);
    }

    @GetMapping("/reserve")
    @Operation(summary = "Reserves a book by ID. The book remains reservable for other user.")
    public BookDto reserve(final Long id) {
        return BookDto.fromDomain(bookRepository.getReferenceById(id));
    }

    @GetMapping("/order")
    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    @Operation(summary = "Order a book by ID. There is an infinite amount of the same book.")
    public BookDto order(final Long id) {
        return BookDto.fromDomain(bookRepository.getReferenceById(id));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all books")
    public List<BookDto> getAll() {
        return bookRepository.findAll().stream()
                .map(BookDto::fromDomain)
                .toList();
    }

    @GetMapping("/find")
    @Operation(summary = "Find books by criteria")
    public List<BookDto> find(@RequestBody final BookCriteria bookCriteria) throws InterruptedException {
        Thread.sleep(10000);
        return bookRepository.findAll(bookCriteria.toSpecification()).stream()
                .map(BookDto::fromDomain)
                .toList();
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing book", security = @SecurityRequirement(name = "bearerAuth"))
    public void update(final BookDto bookDto) {
        bookRepository.findById(bookDto.getId())
                .ifPresent(bookEntity -> {
                    bookEntity.setTitle(bookDto.getTitle());
                    bookEntity.setAuthor(bookDto.getAuthor());
                    bookRepository.save(bookEntity);
                });
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletes a book", security = @SecurityRequirement(name = "bearerAuth"))
    public void delete(final Long id) {
        bookRepository.deleteById(id);
    }
}
