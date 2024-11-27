/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */
package de.kamillionlabs.hateofluxdemos.controller;

import de.kamillionlabs.hateoflux.model.hal.HalEmbeddedWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.IanaRelation;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import de.kamillionlabs.hateofluxdemos.assembler.OrderAssembler;
import de.kamillionlabs.hateofluxdemos.dto.OrderDTO;
import de.kamillionlabs.hateofluxdemos.dto.ShipmentDTO;
import de.kamillionlabs.hateofluxdemos.service.OrderService;
import de.kamillionlabs.hateofluxdemos.service.ShipmentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static de.kamillionlabs.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;
import static de.kamillionlabs.hateoflux.utility.SortDirection.ASCENDING;
import static de.kamillionlabs.hateoflux.utility.SortDirection.DESCENDING;

// @formatter:off
/**
 * @author Younes El Ouarti
 */
@RestController
@RequestMapping("/manual")
@AllArgsConstructor
public class ManualOrderController {

    @Autowired
    private OrderAssembler orderAssembler;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShipmentService shipmentService;

    /**
     * Cookbook example: Creating a HalResourceWrapper without an Embedded Resource
     */
    @GetMapping("/order-no-embedded/{orderId}")
    public Mono<HalResourceWrapper<OrderDTO, Void>> getOrder(@PathVariable int orderId) {    // 1

        Mono<OrderDTO> orderMono = orderService.getOrder(orderId);                           // 2

        return orderMono.map(order -> HalResourceWrapper.wrap(order)                         // 3
                .withLinks(                                                                  // 4
                        Link.of("orders/{orderId}/shipment")                                 // 5
                                .expand(orderId)                                             // 6
                                .withRel("shipment"),                                        // 7
                        Link.linkAsSelfOf("orders/" + orderId)                               // 8
                ));
    }

    /**
     * Cookbook example: Creating a HalResourceWrapper with an Embedded Resource
     */
    @GetMapping("/order-with-embedded/{orderId}")
    public Mono<HalResourceWrapper<OrderDTO, ShipmentDTO>> getOrderWithShipment(@PathVariable int orderId) {             //  1
        Mono<OrderDTO> orderMono = orderService.getOrder(orderId);                                                       //  2
        Mono<ShipmentDTO> shipmentMono = shipmentService.getLastShipmentByOrderId(orderId);                                  //  3
        return orderMono.zipWith(shipmentMono, (order, shipment) ->
                HalResourceWrapper.wrap(order)                                                                           //  4
                        .withLinks(                                                                                      //  5
                                Link.linkAsSelfOf("orders/" + orderId))                                                  //  6
                        .withEmbeddedResource(                                                                           //  7
                                HalEmbeddedWrapper.wrap(shipment)                                                        //  8
                                        .withLinks(                                                                      //  9
                                                linkTo(ShipmentController.class, c -> c.getShipment(shipment.getId()))   // 10
                                                        .withRel(IanaRelation.SELF)                                      // 11
                                                        .withHreflang("en-US")                                           // 12
                                        )
                        )
        );
    }


    /**
     * Cookbook example: Creating a HalListWrapper with Pagination
     */
    @GetMapping("/orders-with-pagination")
    public Mono<HalListWrapper<OrderDTO, Void>> getOrdersManualBuilt(@RequestParam Long userId,                       //  1
                                                                     Pageable pageable,                               //  2
                                                                     ServerWebExchange exchange) {                    //  3
        Flux<OrderDTO> ordersFlux = orderService.getOrdersByUserId(userId, pageable);                                 //  4
        Mono<Long> totalElementsMono = orderService.countAllOrdersByUserId(userId);                                   //  5

        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();
        List<SortCriteria> sortCriteria = pageable.getSort().get()                                                    //  6
                .map(o -> SortCriteria.by(o.getProperty(), o.getDirection().isAscending() ? ASCENDING : DESCENDING))  //  7
                .toList();

        return ordersFlux.map(
                        order -> HalResourceWrapper.wrap(order)                                                       //  8
                                .withLinks(
                                        Link.linkAsSelfOf("orders/" + order.getId())
                                                .prependBaseUrl(exchange)))
                .collectList()                                                                                        //  9
                .zipWith(totalElementsMono,(ordersList, totalElements) -> {                                           // 10
                            HalPageInfo pageInfo = HalPageInfo.assembleWithOffset(pageSize, totalElements, offset);   // 11
                            return HalListWrapper.wrap(ordersList)                                                    // 12
                                    .withLinks(Link.of("orders{?userId,someDifferentFilter}")                         // 13
                                            .expand(userId)                                                           // 14
                                            .prependBaseUrl(exchange)                                                 // 15
                                            .deriveNavigationLinks(pageInfo, sortCriteria))                           // 16
                                    .withPageInfo(pageInfo);                                                          // 17
                        }
                );
    }
}