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
import de.kamillionlabs.hateofluxdemos.datatransferobject.OrderDTO;
import de.kamillionlabs.hateofluxdemos.datatransferobject.ShipmentDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Younes El Ouarti
 */
//@formatter:off
@Component
public class OrderAssembler implements EmbeddingHalWrapperAssembler<OrderDTO, ShipmentDTO> {                //1

    @Override
    public Class<OrderDTO> getResourceTClass() {                                                            //2
        return OrderDTO.class;                                                                              //3
    }

    @Override
    public Class<ShipmentDTO> getEmbeddedTClass() {                                                         //4
        return ShipmentDTO.class;
    }

    @Override
    public Link buildSelfLinkForResource(OrderDTO resourceToWrap, ServerWebExchange exchange) {             //5
        return Link.of("order/" + resourceToWrap.getId())                                                   //6
                .prependBaseUrl(exchange);                                                                  //7
    }

    @Override
    public Link buildSelfLinkForEmbedded(ShipmentDTO embedded, ServerWebExchange exchange) {                //8
        return Link.of("shipment/" + embedded.getId())
                .prependBaseUrl(exchange)
                .withHreflang("en-US");
    }

    @Override
    public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {                                  //9
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();                 //10
        return Link.of("order{?userId,someDifferentFilter}")                                               //11
                .expand(queryParams)
                .prependBaseUrl(exchange);
    }
}
