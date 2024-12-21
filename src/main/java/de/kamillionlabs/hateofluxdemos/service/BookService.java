/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 09.12.2024
 */
package de.kamillionlabs.hateofluxdemos.service;

import de.kamillionlabs.hateofluxdemos.datatransferobject.AuthorDTO;
import de.kamillionlabs.hateofluxdemos.datatransferobject.BookDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author Younes El Ouarti
 */
@Service
public class BookService {

    public static AuthorDTO UNKNOWN_AUTHOR = new AuthorDTO(-1, "n/a", "n/a", "n/a");

    public enum AuthorName {
        JOSHUA_BLOCH("Joshua Bloch"), //1
        ROBERT_MARTIN("Robert C. Martin"), //1
        BRIAN_GOETZ("Brian Goetz"), //2
        ERICH_GAMMA("Erich Gamma"), //3
        MARTIN_FOWLER("Martin Fowler"), //1
        MICHAEL_FEATHERS("Michael Feathers"), //1
        ANDREW_HUNT("Andrew Hunt"); //1

        private final String name;

        AuthorName(String name) {
            this.name = name;
        }
    }


    public enum BookTitle {
        EFFECTIVE_JAVA("Effective Java"),
        CLEAN_CODE("Clean Code"),
        JAVA_CONCURRENCY_IN_PRACTICE("Java Concurrency in Practice"),
        JAVA_PUZZLERS("Java Puzzlers"),
        DESIGN_PATTERNS("Design Patterns: Elements of Reusable Object-Oriented Software"),
        HEAD_FIRST_DESIGN_PATTERNS("Head First Design Patterns"),
        PATTERN_ORIENTED_SOFTWARE_ARCHITECTURE("Pattern-Oriented Software Architecture Volume 1"),
        REFACTORING("Refactoring: Improving the Design of Existing Code"),
        THE_PRAGMATIC_PROGRAMMER("The Pragmatic Programmer"),
        WORKING_EFFECTIVELY_WITH_LEGACY_CODE("Working Effectively with Legacy Code");

        private final String title;

        BookTitle(String title) {
            this.title = title;
        }
    }

    private final LinkedMultiValueMap<AuthorDTO, BookDTO> database;


    public BookService() {
        database = new LinkedMultiValueMap<>();

        BookDTO book1 = BookDTO.builder()
                .id(1)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .publishedDate("2018-01-06")
                .build();

        AuthorDTO author1 = AuthorDTO.builder()
                .id(1)
                .name("Joshua Bloch")
                .birthDate("1961-08-28")
                .mainGenre("Programming")
                .build();

        database.addAll(author1, List.of(book1));

        BookDTO book2 = BookDTO.builder()
                .id(2)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .publishedDate("2008-08-01")
                .build();

        AuthorDTO author2 = AuthorDTO.builder()
                .id(2)
                .name("Robert C. Martin")
                .birthDate("1952-12-05")
                .mainGenre("Software Engineering")
                .build();

        database.addAll(author2, List.of(book2));

        BookDTO book3 = BookDTO.builder()
                .id(3)
                .title("Java Concurrency in Practice")
                .author("Brian Goetz")
                .isbn("978-0321349606")
                .publishedDate("2006-05-19")
                .build();

        BookDTO book4 = BookDTO.builder()
                .id(4)
                .title("Java Puzzlers")
                .author("Brian Goetz")
                .isbn("978-0321336781")
                .publishedDate("2005-07-24")
                .build();

        AuthorDTO author3 = AuthorDTO.builder()
                .id(3)
                .name("Brian Goetz")
                .birthDate("1969-05-22")
                .mainGenre("Programming Languages")
                .build();

        database.addAll(author3, List.of(book3, book4));

        BookDTO book5 = BookDTO.builder()
                .id(5)
                .title("Design Patterns: Elements of Reusable Object-Oriented Software")
                .author("Erich Gamma")
                .isbn("978-0201633610")
                .publishedDate("1994-10-31")
                .build();

        BookDTO book6 = BookDTO.builder()
                .id(6)
                .title("Head First Design Patterns")
                .author("Erich Gamma")
                .isbn("978-0596007126")
                .publishedDate("2004-10-25")
                .build();

        BookDTO book7 = BookDTO.builder()
                .id(7)
                .title("Pattern-Oriented Software Architecture Volume 1")
                .author("Erich Gamma")
                .isbn("978-0471958697")
                .publishedDate("1995-10-25")
                .build();

        AuthorDTO author4 = AuthorDTO.builder()
                .id(4)
                .name("Erich Gamma")
                .birthDate("1961-03-13")
                .mainGenre("Software Architecture")
                .build();

        database.addAll(author4, List.of(book5, book6, book7));

        BookDTO book8 = BookDTO.builder()
                .id(8)
                .title("Refactoring: Improving the Design of Existing Code")
                .author("Martin Fowler")
                .isbn("978-0201485677")
                .publishedDate("1999-07-08")
                .build();

        AuthorDTO author5 = AuthorDTO.builder()
                .id(5)
                .name("Martin Fowler")
                .birthDate("1963-12-18")
                .mainGenre("Software Engineering")
                .build();

        database.addAll(author5, List.of(book8));

        BookDTO book9 = BookDTO.builder()
                .id(9)
                .title("The Pragmatic Programmer")
                .author("Andrew Hunt")
                .isbn("978-0201616224")
                .publishedDate("1999-10-30")
                .build();

        AuthorDTO author6 = AuthorDTO.builder()
                .id(6)
                .name("Andrew Hunt")
                .birthDate("1964-06-15")
                .mainGenre("Software Development")
                .build();

        database.addAll(author6, List.of(book9));


        BookDTO book10 = BookDTO.builder()
                .id(10)
                .title("Working Effectively with Legacy Code")
                .author("Michael Feathers")
                .isbn("978-0131177055")
                .publishedDate("2004-09-22")
                .build();

        AuthorDTO author7 = AuthorDTO.builder()
                .id(7)
                .name("Michael Feathers")
                .birthDate("1966-01-27")
                .mainGenre("Software Maintenance")
                .build();

        database.addAll(author7, List.of(book10));

        BookDTO book11 = BookDTO.builder()
                .id(11)
                .title("It Somehow Works!")
                .isbn("811-3458521698")
                .publishedDate("1970-01-01")
                .build();

        database.add(UNKNOWN_AUTHOR, book11);

        BookDTO book12 = BookDTO.builder()
                .id(12)
                .title("This is for updating and nothing else!")
                .isbn("662-3204682647")
                .publishedDate("1970-02-02")
                .build();

        database.add(UNKNOWN_AUTHOR, book12);
    }

    public Mono<BookDTO> getBookById(int id) {
        return database.entrySet().stream()
                .filter(pair -> pair.getValue().stream().anyMatch(book -> Objects.equals(book.getId(), id)))
                .findFirst()
                .map(multiPair -> multiPair.getValue().get(0))
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    public Mono<BookDTO> getBookByTitle(BookTitle bookTitle) {
        return getBookByTitle(bookTitle.title);
    }

    public Mono<BookDTO> getBookByTitle(String bookTitle) {
        return database.entrySet().stream()
                .filter(pair -> pair.getValue().stream().anyMatch(book -> Objects.equals(book.getTitle(), bookTitle)))
                .findFirst()
                .map(multiPair -> multiPair.getValue().get(0))
                .map(Mono::just)
                .orElse(Mono.empty());
    }


    public Flux<BookDTO> getAllBooksByAuthorName(AuthorName authorName) {
        return getAllBooksByAuthorName(authorName.name);
    }

    public Flux<BookDTO> getAllBooksByAuthorName(String authorName) {
        return database.entrySet().stream()
                .filter(pair -> pair.getKey().getName().strip().equalsIgnoreCase(authorName))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(Flux::fromIterable)
                .orElse(Flux.empty());
    }


    public Mono<AuthorDTO> getAuthorByName(AuthorName authorName) {
        return getAuthorByName(authorName.name);
    }

    public Mono<AuthorDTO> getAuthorByName(String authorName) {
        return database.entrySet().stream()
                .filter(kv -> kv.getKey().getName().strip().equalsIgnoreCase(authorName))
                .findFirst()
                .map(Map.Entry::getKey)
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    public Mono<Void> addAuthor(AuthorDTO author) {
        Objects.requireNonNull(author, "Author cannot be null");
        // Check if author already exists
        boolean exists = database.entrySet().stream()
                .anyMatch(kv -> kv.getKey().getName().equalsIgnoreCase(author.getName()));
        if (exists) {
            return Mono.error(new IllegalArgumentException("Author already exists"));
        }
        database.addAll(author, new ArrayList<>());
        return Mono.empty();
    }

    public Mono<BookDTO> addBook(BookDTO book) {
        Objects.requireNonNull(book, "Book cannot be null");
        Objects.requireNonNull(book.getAuthor(), "Book must have an author");

        // Find the author in the database
        Optional<Map.Entry<AuthorDTO, List<BookDTO>>> authorPairOpt = database.entrySet().stream()
                .filter(pair -> pair.getKey().getName().equalsIgnoreCase(book.getAuthor()))
                .findFirst();

        if (authorPairOpt.isPresent()) {
            Map.Entry<AuthorDTO, List<BookDTO>> authorPair = authorPairOpt.get();
            // Check if the book already exists
            boolean bookExists = authorPair.getValue().stream()
                    .anyMatch(existingBook -> existingBook.getTitle().equalsIgnoreCase(book.getTitle()));
            if (bookExists) {
                return Mono.error(new IllegalArgumentException("Book already exists for this author"));
            }
            authorPair.getValue().add(book);
        } else {
            database.add(UNKNOWN_AUTHOR, book);
        }
        return Mono.just(book);
    }

    /**
     * Adds a new author along with their books to the database.
     *
     * @param author
     *         The AuthorDTO object to be added.
     * @param books
     *         The list of BookDTO objects to be associated with the author.
     * @return Mono<Void> indicating completion or error.
     */
    public Mono<Void> addAuthorWithBooks(AuthorDTO author, List<BookDTO> books) {
        Objects.requireNonNull(author, "Author cannot be null");
        Objects.requireNonNull(books, "Books list cannot be null");

        // Check if author already exists
        boolean exists = database.entrySet().stream()
                .anyMatch(pair -> pair.getKey().getName().equalsIgnoreCase(author.getName()));
        if (exists) {
            return Mono.error(new IllegalArgumentException("Author already exists"));
        }

        // Ensure all books have the correct author name
        for (BookDTO book : books) {
            if (!book.getAuthor().equalsIgnoreCase(author.getName())) {
                return Mono.error(new IllegalArgumentException(
                        "Book author does not match the provided author"));
            }
        }

        database.addAll(author, new ArrayList<>(books));
        return Mono.empty();
    }

    public Mono<BookDTO> updateBook(BookDTO updatedBook) {
        Objects.requireNonNull(updatedBook, "Updated book cannot be null");
        String titleOfUpdateBook = updatedBook.getTitle();
        Objects.requireNonNull(titleOfUpdateBook, "Title cannot be null");

        for (var pair : database.entrySet()) {
            List<BookDTO> books = pair.getValue();
            for (int i = 0; i < books.size(); i++) {
                BookDTO currentBook = books.get(i);
                if (currentBook.getTitle().equalsIgnoreCase(titleOfUpdateBook)) {
                    //only change isbn and pub date for simplicity
                    BookDTO bookToUpdate = BookDTO.builder()
                            .id(currentBook.getId())
                            .title(currentBook.getTitle())
                            .author(updatedBook.getAuthor() != null ?
                                    updatedBook.getAuthor() : currentBook.getAuthor())
                            .isbn(updatedBook.getIsbn())  //can change ISBN
                            .publishedDate(updatedBook.getPublishedDate() != null ?
                                    updatedBook.getPublishedDate() : currentBook.getPublishedDate())
                            .build();
                    books.set(i, bookToUpdate);
                    return Mono.just(bookToUpdate);
                }
            }
        }
        return Mono.empty();
    }

}
