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
import de.kamillionlabs.hateoflux.utility.Pair;
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

/**
 * @author Younes El Ouarti
 */
@RestController
@RequestMapping
@AllArgsConstructor
public class OrderController {

    @Autowired
    private OrderAssembler orderAssembler;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShipmentService shipmentService;

    /**
     * Cookbook example: Creating a HalResourceWrapper without an Embedded Resource
     */
    @GetMapping("/{orderId}")
    public Mono<HalResourceWrapper<OrderDTO, Void>> getOrder(@PathVariable int orderId) {
        Mono<OrderDTO> orderMono = orderService.getOrder(orderId);
        return orderMono.map(order -> HalResourceWrapper.wrap(order)
                .withLinks(
                        Link.of("orders/{orderId}/shipment")
                                .expand(orderId)
                                .withRel("shipment"),
                        Link.linkAsSelfOf("orders/" + orderId)
                ));
    }

    /**
     * Cookbook example: Creating a HalResourceWrapper with an Embedded Resource
     */
    @GetMapping("/order-with-embedded/{orderId}")
    public Mono<HalResourceWrapper<OrderDTO, ShipmentDTO>> getOrderWithShipment(@PathVariable int orderId) {
        Mono<OrderDTO> orderMono = orderService.getOrder(orderId);
        Mono<ShipmentDTO> shipmentMono = shipmentService.getShipmentByOrderId(orderId);
        return orderMono.zipWith(shipmentMono, (order, shipment) ->
                HalResourceWrapper.wrap(order)
                        .withLinks(
                                Link.linkAsSelfOf("orders/" + orderId))
                        .withEmbeddedResource(
                                HalEmbeddedWrapper.wrap(shipment)
                                        .withLinks(
                                                linkTo(ShipmentController.class, c -> c.getShipment(shipment.getId()))
                                                        .withRel(IanaRelation.SELF)
                                                        .withHreflang("en-US")
                                        )
                        )
        );
    }

    /**
     * Cookbook example: Creating a HalListWrapper with Pagination
     */
    @GetMapping("/orders-with-pagination-manual")
    public Mono<HalListWrapper<OrderDTO, Void>> getOrdersManualBuilt(@RequestParam Long userId,
                                                                     Pageable pageable,
                                                                     ServerWebExchange exchange) {
        Flux<OrderDTO> ordersFlux = orderService.getOrdersByUserId(userId, pageable);
        Mono<Long> totalElementsMono = orderService.countAllOrdersByUserId(userId);
        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();
        List<SortCriteria> sortCriteria = pageable.getSort().get()
                .map(o -> SortCriteria.by(o.getProperty(), o.getDirection().isAscending() ? ASCENDING : DESCENDING))
                .toList();
        return ordersFlux.map(
                        order -> HalResourceWrapper.wrap(order)
                                .withLinks(
                                        Link.linkAsSelfOf("orders/" + order.getId())
                                                .prependBaseUrl(exchange)))
                .collectList()
                .zipWith(totalElementsMono,
                        (ordersList, totalElements) -> {
                            HalPageInfo pageInfo = HalPageInfo.assembleWithOffset(pageSize, totalElements, offset);
                            return HalListWrapper.wrap(ordersList)
                                    .withLinks(Link.of("orders{?userId,someDifferentFilter}")
                                            .expand(userId)
                                            .prependBaseUrl(exchange)
                                            .deriveNavigationLinks(pageInfo, sortCriteria))
                                    .withPageInfo(pageInfo);
                        }
                );
    }

    /**
     * Cookbook example: Using an Assembler to Create a HalListWrapper With Embedded Resources
     */
    @GetMapping("/orders-using-assembler")
    public Mono<HalListWrapper<OrderDTO, ShipmentDTO>> getOrdersUsingAssembler(@RequestParam(required = false) Long userId,
                                                                               Pageable pageable,
                                                                               ServerWebExchange exchange) {

        Flux<Pair<OrderDTO, ShipmentDTO>> ordersWithShipment = orderService.getOrders(userId, pageable)
                .flatMap(order ->
                        shipmentService.getShipmentByOrderId(order.getId())
                                .map(shipment -> Pair.of(order, shipment)));
        Mono<Long> totalElements = orderService.countAllOrders(userId);

        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();
        List<SortCriteria> sortCriteria = pageable.getSort().get()
                .map(o -> SortCriteria.by(o.getProperty(), o.getDirection().isAscending() ? ASCENDING : DESCENDING))
                .toList();
        return orderAssembler.wrapInListWrapper(ordersWithShipment, totalElements, pageSize, offset, sortCriteria, exchange);
    }
}
