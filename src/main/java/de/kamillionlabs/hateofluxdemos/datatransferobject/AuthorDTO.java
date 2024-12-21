/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 09.12.2024
 */
package de.kamillionlabs.hateofluxdemos.datatransferobject;

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
public class AuthorDTO {
    private int id;
    private String name;
    private String birthDate;
    private String mainGenre;

}
