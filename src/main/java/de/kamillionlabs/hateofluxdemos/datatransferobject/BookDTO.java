/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 09.12.2024
 */
package de.kamillionlabs.hateofluxdemos.datatransferobject;

import de.kamillionlabs.hateoflux.model.hal.Relation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * @author Younes El Ouarti
 */
@Jacksonized
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "customBooks", value = "customBook")
public class BookDTO {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String publishedDate;

    public BookDTO(String title, String author) {
        this.id = 37;
        this.title = title;
        this.author = author;
    }
}
