package de.adesso.schulungen.testapplication.web.resolver;


import de.adesso.schulungen.testapplication.infrasturcture.BookEntity;
import de.adesso.schulungen.testapplication.infrasturcture.BookRepository;
import de.adesso.schulungen.testapplication.web.model.BookDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class BookQueryResolver {

    private final BookRepository bookRepository;

    public BookQueryResolver(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @QueryMapping
    public BookDto getById(@Argument Long id) {
        log.debug("AuthMutationResolver getById:" + id);
        final BookEntity bookEntity = bookRepository.getReferenceById(id);
        return BookDto.fromDomain(bookEntity);
    }

    @QueryMapping
    public List<BookDto> books() {
        log.debug("AuthMutationResolver books");
        return bookRepository.findAll().stream().map(BookDto::fromDomain).toList();
    }


    @MutationMapping
    public BookDto update(@Argument BookDto bookDto) {
        log.debug("Updating book with ID: {}", bookDto.getId());

        return bookRepository.findById(bookDto.getId())
                .map(bookEntity -> {
                    bookEntity.setTitle(bookDto.getTitle());
                    bookEntity.setAuthor(bookDto.getAuthor());
                    BookEntity updatedBook = bookRepository.save(bookEntity);
                    return BookDto.fromDomain(updatedBook); // Return updated book
                })
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookDto.getId())); // Throw an error instead of returning null
    }

}