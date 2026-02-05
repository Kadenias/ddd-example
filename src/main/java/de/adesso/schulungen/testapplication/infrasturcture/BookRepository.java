package de.adesso.schulungen.testapplication.infrasturcture;

import de.adesso.schulungen.testapplication.infrasturcture.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {
}
