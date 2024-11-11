/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */

package de.kamillionlabs.hateofluxdemos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * @author Younes El Ouarti
 */
@Jacksonized
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    String code;

    String name;

    BigDecimal currentPrice;
}
