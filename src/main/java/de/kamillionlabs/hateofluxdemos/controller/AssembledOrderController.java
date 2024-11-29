/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 18.11.2024
 */
package de.kamillionlabs.hateofluxdemos.controller;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import de.kamillionlabs.hateoflux.utility.pair.PairFlux;
import de.kamillionlabs.hateofluxdemos.assembler.OrderAssembler;
import de.kamillionlabs.hateofluxdemos.datatransferobject.OrderDTO;
import de.kamillionlabs.hateofluxdemos.datatransferobject.ShipmentDTO;
import de.kamillionlabs.hateofluxdemos.service.OrderService;
import de.kamillionlabs.hateofluxdemos.service.ShipmentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static de.kamillionlabs.hateoflux.utility.SortDirection.ASCENDING;
import static de.kamillionlabs.hateoflux.utility.SortDirection.DESCENDING;

//@formatter:off
/**
 * @author Younes
 */
@RestController
@RequestMapping("/assembled")
@AllArgsConstructor
public class AssembledOrderController {

    @Autowired
    private OrderAssembler orderAssembler;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShipmentService shipmentService;


    /**
     * Cookbook example: Using an Assembler to Create a `HalListWrapper` For Resources With an Embedded Resource
     */
    @GetMapping("/orders-with-single-embedded-and-pagination")
    public Mono<HalListWrapper<OrderDTO, ShipmentDTO>> getOrdersWithShipmentAndPagination(
                                                                               @RequestParam(required = false) Long userId,     // 1
                                                                               Pageable pageable,                               // 2
                                                                               ServerWebExchange exchange) {                    // 3

        Flux<OrderDTO> orders = orderService.getOrders(userId, pageable);
        PairFlux<OrderDTO, ShipmentDTO> ordersWithShipment =
                PairFlux.zipWith(orders, (order -> shipmentService.getLastShipmentByOrderId(order.getId())));                   // 4

        Mono<Long> totalElements = orderService.countAllOrders(userId);                                                         // 5

        int pageSize = pageable.getPageSize();                                                                                  // 6
        long offset = pageable.getOffset();
        List<SortCriteria> sortCriteria = pageable.getSort().get()
                .map(o -> SortCriteria.by(o.getProperty(), o.getDirection().isAscending() ? ASCENDING : DESCENDING))
                .toList();
        return orderAssembler.wrapInListWrapper(ordersWithShipment, totalElements, pageSize, offset, sortCriteria, exchange);   // 7
    }
}
