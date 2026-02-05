package de.adesso.schulungen.testapplication.infrasturcture;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "book")
public class BookEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    public BookEntity(final Long id, final String title, final String author) {
        if(Objects.isNull(id) || (id < 0)) {
            throw new IllegalArgumentException("Parameter id must not be null");
        }
        Objects.requireNonNull(title,"Parameter title must not be null");
        Objects.requireNonNull(author, "Parameter author must not be null");
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public BookEntity() {

    }
}
