package de.adesso.schulungen.testapplication.web.model;

import de.adesso.schulungen.testapplication.infrasturcture.BookEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BookDto {

    private Long id;
    private String title;
    private String author;

    public static BookDto fromDomain(final BookEntity bookEntity) {
        final BookDto bookDto = new BookDto();
        bookDto.setId(bookEntity.getId());
        bookDto.setTitle(bookEntity.getTitle());
        bookDto.setAuthor(bookEntity.getAuthor());
        return bookDto;
    }

    public BookEntity toDomain() {
        return new BookEntity(id, title, author);
    }
}
