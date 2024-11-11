/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */

package de.kamillionlabs.hateofluxdemos.controller;

import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateofluxdemos.dto.ShipmentDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Younes El Ouarti
 */
@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    @GetMapping("/{id}")
    public Mono<HalResourceWrapper<ShipmentDTO, Void>> getShipment(@PathVariable int id) {
        //This is just a stub for the SpringControllerLinkBuilder
        return Mono.empty();
    }
}
