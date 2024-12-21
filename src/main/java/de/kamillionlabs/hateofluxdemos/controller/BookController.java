/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 09.12.2024
 */
package de.kamillionlabs.hateofluxdemos.controller;

import de.kamillionlabs.hateoflux.http.HalListResponse;
import de.kamillionlabs.hateoflux.http.HalMultiResourceResponse;
import de.kamillionlabs.hateoflux.http.HalResourceResponse;
import de.kamillionlabs.hateoflux.model.hal.HalEmbeddedWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateofluxdemos.datatransferobject.AuthorDTO;
import de.kamillionlabs.hateofluxdemos.datatransferobject.BookDTO;
import de.kamillionlabs.hateofluxdemos.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Younes El Ouarti
 */
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/im-a-teapot")
    public HalResourceResponse<BookDTO, AuthorDTO> imATeapot() {
        return HalResourceResponse.of(HttpStatus.I_AM_A_TEAPOT);
    }


    @PostMapping("/book")
    public HalResourceResponse<BookDTO, AuthorDTO> createBook(@RequestBody BookDTO book) {
        Mono<HalResourceWrapper<BookDTO, AuthorDTO>> createdBook = bookService.addBook(book)
                .map(BookController::wrapBook);

        return HalResourceResponse.created(createdBook);
    }


    @GetMapping("/book/{id}")
    public HalResourceResponse<BookDTO, AuthorDTO> getBook(
            @PathVariable("id") int bookId,
            @RequestParam(name = "withAuthor", defaultValue = "false") boolean withAuthor) {

        Mono<BookDTO> bookMono = bookService.getBookById(bookId);

        // Conditionally retrieve the author only if requested
        Mono<AuthorDTO> authorMono = withAuthor
                ? bookMono.flatMap(book -> bookService.getAuthorByName(book.getAuthor()))
                : Mono.empty();

        // Determine the status code based on the presence of BookDTO and (optionally) AuthorDTO
        Mono<HttpStatus> statusMono = bookMono.hasElement().flatMap(bookExists -> {
            if (!bookExists) {
                // No book found
                return Mono.just(HttpStatus.NOT_FOUND);
            } else if (!withAuthor) {
                // BookDTO exists and author not requested: always 200
                return Mono.just(HttpStatus.OK);
            } else {
                // BookDTO exists and author was requested: check author presence
                return authorMono.hasElement().map(authorExists ->
                        authorExists ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT
                );
            }
        });

        // Construct the body (HalResourceWrapper)
        Mono<HalResourceWrapper<BookDTO, AuthorDTO>> bodyMono = bookMono.flatMap(book -> {
            Mono<HalResourceWrapper<BookDTO, AuthorDTO>> bookWithoutAuthor = Mono.just(wrapBook(book));
            if (!withAuthor) {
                return bookWithoutAuthor;
            } else {
                // With author requested
                return authorMono
                        .map(author -> wrapBook(book, author))
                        .switchIfEmpty(bookWithoutAuthor);
            }
        });
        return HalResourceResponse.of(bodyMono, statusMono)
                .withContentType("application/hal+json");
    }


    private static HalResourceWrapper<BookDTO, AuthorDTO> wrapBook(BookDTO b) {
        return HalResourceWrapper.wrap(b)
                .withLinks(Link.linkAsSelfOf("/book/" + b.getId()))
                .withEmbeddedResource(HalEmbeddedWrapper.empty());
    }

    private static HalResourceWrapper<BookDTO, AuthorDTO> wrapBook(BookDTO b, AuthorDTO a) {
        return HalResourceWrapper.wrap(b)
                .withLinks(Link.linkAsSelfOf("/book/" + b.getId()))
                .withEmbeddedResource(HalEmbeddedWrapper.wrap(a)
                        .withLinks(Link.linkAsSelfOf("/book/" + b.getId() + "/author")));
    }

    @GetMapping("/books")
    public HalListResponse<BookDTO, AuthorDTO> getAllBooksOfAuthor(@RequestParam String authorName) {
        Flux<HalResourceWrapper<BookDTO, AuthorDTO>> allBooks = bookService.getAllBooksByAuthorName(authorName)
                .map(BookController::wrapBook);

        // Empty Flux will result in an empty HalListWrapper with a self link, not an empty Mono
        Mono<HalListWrapper<BookDTO, AuthorDTO>> allBooksAsList = wrapBooks(authorName, allBooks);

        Mono<HttpStatus> status = allBooks.hasElements()
                .map(exists -> exists ? HttpStatus.OK : HttpStatus.NOT_FOUND);

        return HalListResponse.of(allBooksAsList, status);
    }

    @GetMapping("/books-as-stream")
    public HalMultiResourceResponse<BookDTO, AuthorDTO> getAllBooksOfAuthorAsStream(@RequestParam String authorName) {
        Flux<HalResourceWrapper<BookDTO, AuthorDTO>> allBooks = bookService.getAllBooksByAuthorName(authorName)
                .map(BookController::wrapBook);
        return HalMultiResourceResponse.of(allBooks, HttpStatus.OK);
    }

    private static Mono<HalListWrapper<BookDTO, AuthorDTO>> wrapBooks(String authorName,
                                                                      Flux<HalResourceWrapper<BookDTO, AuthorDTO>> allBooks) {
        //This is done automatically when assemblers are used
        return allBooks.collectList()
                .map(list -> {
                    HalListWrapper<BookDTO, AuthorDTO> result;
                    if (list.isEmpty()) {
                        result = HalListWrapper.empty(BookDTO.class);
                    } else {
                        result = HalListWrapper.wrap(list);
                    }
                    return result.withLinks(Link.linkAsSelfOf("/books{?authorName}").expand(authorName));
                });
    }

    @PutMapping("/book")
    public HalResourceResponse<BookDTO, AuthorDTO> updateBook(@RequestBody BookDTO book) {
        Mono<HalResourceWrapper<BookDTO, AuthorDTO>> updateBook = bookService.updateBook(book)
                .map(BookController::wrapBook);

        Mono<HttpStatus> statusCode = updateBook.hasElement()
                .map(exists -> exists ? HttpStatus.OK : HttpStatus.NOT_FOUND);

        return HalResourceResponse.of(updateBook, statusCode);
    }
}
