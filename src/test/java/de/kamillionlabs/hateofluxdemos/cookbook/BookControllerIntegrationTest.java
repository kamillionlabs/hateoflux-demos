/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 09.12.2024
 */
package de.kamillionlabs.hateofluxdemos.cookbook;


import de.kamillionlabs.hateofluxdemos.datatransferobject.BookDTO;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

/**
 * @author Younes El Ouarti
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class BookControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testImATeapot() {
        webTestClient.post()
                .uri("/im-a-teapot")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT)
                .expectBody()
                .isEmpty();
    }


    @Test
    public void testCreateBook() {
        BookDTO newBook = BookDTO.builder()
                .id(700)
                .title("New Book Title")
                .author("Andrew Hunt")
                .isbn("123-4567890123")
                .publishedDate("2024-12-09")
                .build();


        String expectedJson = """
                {
                  "id": 700,
                  "title": "New Book Title",
                  "author": "Andrew Hunt",
                  "isbn": "123-4567890123",
                  "publishedDate": "2024-12-09",
                  "_links": {
                    "self": {
                      "href": "/book/700"
                    }
                  }
                }
                """;

        webTestClient.post()
                .uri("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String actualJson = response.getResponseBody();
                    try {
                        JSONAssert.assertEquals(expectedJson, actualJson, false);
                    } catch (Exception e) {
                        throw new AssertionError("JSON comparison failed", e);
                    }
                });
    }

    @Test
    public void testGetBookFound() {
        String expectedJson = """
                    {
                      "id": 1,
                      "title": "Effective Java",
                      "author": "Joshua Bloch",
                      "isbn": "978-0134685991",
                      "publishedDate": "2018-01-06",
                      "_links": {
                        "self": {
                          "href": "/book/1"
                        }
                      }
                    }
                """;

        webTestClient.get()
                .uri("/book/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String actualJson = response.getResponseBody();
                    try {
                        JSONAssert.assertEquals(expectedJson, actualJson, false);
                    } catch (Exception e) {
                        throw new AssertionError("JSON comparison failed", e);
                    }
                });
    }

    @Test
    public void testGetBookNotFound() {
        webTestClient.get()
                .uri("/book/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    public void testGetAllBooksByAuthorFound() {
        String expectedJson = """
                {
                  "_embedded": {
                    "customBooks": [
                      {
                        "id": 1,
                        "title": "Effective Java",
                        "author": "Joshua Bloch",
                        "isbn": "978-0134685991",
                        "publishedDate": "2018-01-06",
                        "_links": {
                          "self": {
                            "href": "/book/1"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "self": {
                      "href": "/books?authorName=Joshua+Bloch"
                    }
                  }
                }
                """;

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("authorName", "Joshua Bloch")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String actualJson = response.getResponseBody();
                    try {
                        JSONAssert.assertEquals(expectedJson, actualJson, false);
                    } catch (Exception e) {
                        throw new AssertionError("JSON comparison failed", e);
                    }
                });
    }

    @Test
    public void testGetAllBooksByAuthorFoundAsStream() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books-as-stream")
                        .queryParam("authorName", "Brian Goetz")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        // Assert the first JSON object
                        JSONAssert.assertEquals("""  
                                {
                                  "id": 3,
                                  "title": "Java Concurrency in Practice",
                                  "author": "Brian Goetz",
                                  "isbn": "978-0321349606",
                                  "publishedDate": "2006-05-19",
                                  "_links": {
                                    "self": {
                                      "href": "/book/3"
                                    }
                                  }
                                }
                                """, jsonArray.get(0).toString(), NON_EXTENSIBLE);
                        // Assert the second JSON object
                        JSONAssert.assertEquals("""
                                {
                                  "id": 4,
                                  "title": "Java Puzzlers",
                                  "author": "Brian Goetz",
                                  "isbn": "978-0321336781",
                                  "publishedDate": "2005-07-24",
                                  "_links": {
                                    "self": {
                                      "href": "/book/4"
                                    }
                                  }
                                }
                                """, jsonArray.get(1).toString(), NON_EXTENSIBLE);
                    } catch (Exception e) {
                        throw new AssertionError("JSON comparison failed", e);
                    }
                });
    }

    @Test
    public void testGetAllBooksByAuthorNotFound() {
        String expectedJson = """
                    {
                      "_embedded": {
                        "customBooks": []
                      },
                      "_links": {
                        "self": {
                          "href": "/books?authorName=Unknown+Author"
                        }
                      }
                    }
                """;

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("authorName", "Unknown Author")
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String actualJson = response.getResponseBody();
                    try {
                        JSONAssert.assertEquals(expectedJson, actualJson, false);
                    } catch (Exception e) {
                        throw new AssertionError("JSON comparison failed", e);
                    }
                });
    }

    @Test
    public void testUpdateBookSuccess() {
        BookDTO updatedBook = BookDTO.builder()
                .title("This is for updating and nothing else!")
                .isbn("111-1111111111")
                .publishedDate("2018-12-31")
                .build();

        String expectedJson = """
                    {
                      "id": 12,
                      "title": "This is for updating and nothing else!",
                      "isbn": "111-1111111111",
                      "publishedDate": "2018-12-31",
                      "_links": {
                        "self": {
                          "href": "/book/12"
                        }
                      }
                    }
                """;

        webTestClient.put()
                .uri("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedBook)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String actualJson = response.getResponseBody();
                    try {
                        JSONAssert.assertEquals(expectedJson, actualJson, false);
                    } catch (Exception e) {
                        throw new AssertionError("JSON comparison failed", e);
                    }
                });
    }

    @Test
    public void testUpdateBookNotFound() {
        BookDTO updatedBook = BookDTO.builder()
                .title("Non-Existent Book")
                .author("Unknown Author")
                .isbn("000-0000000000")
                .publishedDate("2024-12-09")
                .build();


        webTestClient.put()
                .uri("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedBook)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }
}