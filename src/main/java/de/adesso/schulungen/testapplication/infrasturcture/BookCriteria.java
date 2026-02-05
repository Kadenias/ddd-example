package de.adesso.schulungen.testapplication.infrasturcture;

import de.adesso.schulungen.testapplication.infrasturcture.BookEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
@Getter
@Setter
public class BookCriteria {

    private String title;
    private String author;

    public Specification<BookEntity> toSpecification() {
        return Specification.allOf(
                (title != null) ? byTitle(title) : null,
                (author != null) ? byAuthor(author) : null
        );
    }

    private Specification<BookEntity> byTitle(final String t) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), t);
    }

    private Specification<BookEntity> byAuthor(final String a) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("author"), a);
    }
}
