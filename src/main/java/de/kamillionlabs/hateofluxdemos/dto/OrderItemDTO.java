/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */

package de.kamillionlabs.hateofluxdemos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Younes El Ouarti
 */
@Data
@AllArgsConstructor
public class OrderItemDTO {
    private int id;
    private int orderId;
    private String name;
    private double price;
}
