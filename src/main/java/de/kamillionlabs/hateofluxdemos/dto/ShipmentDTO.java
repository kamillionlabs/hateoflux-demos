/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */

package de.kamillionlabs.hateofluxdemos.dto;

import de.kamillionlabs.hateoflux.model.hal.Relation;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Relation(itemRelation = "shipment", collectionRelation = "shipments")
public class ShipmentDTO {
    int id;
    String carrier;
    String trackingNumber;
    String status;
}
