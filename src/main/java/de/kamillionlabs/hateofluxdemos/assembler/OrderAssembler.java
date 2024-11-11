/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */
package de.kamillionlabs.hateofluxdemos.assembler;

import de.kamillionlabs.hateoflux.assembler.EmbeddingHalWrapperAssembler;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateofluxdemos.dto.OrderDTO;
import de.kamillionlabs.hateofluxdemos.dto.ShipmentDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Younes El Ouarti
 */
@Component
public class OrderAssembler implements EmbeddingHalWrapperAssembler<OrderDTO, ShipmentDTO> {


    @Override
    public Class<OrderDTO> getResourceTClass() {
        return OrderDTO.class;
    }

    @Override
    public Link buildSelfLinkForResource(OrderDTO resourceToWrap, ServerWebExchange exchange) {
        return Link.of("order/" + resourceToWrap.getId())
                .prependBaseUrl(exchange);
    }

    @Override
    public Link buildSelfLinkForEmbedded(ShipmentDTO embedded, ServerWebExchange exchange) {
        return Link.of("shipment/" + embedded.getId())
                .prependBaseUrl(exchange)
                .withHreflang("en-US");
    }

    @Override
    public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        return Link.of("order{?userId,someDifferentFilter}")
                .expand(queryParams)
                .prependBaseUrl(exchange);
    }
}
