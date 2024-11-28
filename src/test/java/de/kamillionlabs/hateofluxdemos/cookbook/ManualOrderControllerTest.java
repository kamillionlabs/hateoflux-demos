/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */
package de.kamillionlabs.hateofluxdemos.cookbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateofluxdemos.controller.ManualOrderController;
import de.kamillionlabs.hateofluxdemos.datatransferobject.OrderDTO;
import de.kamillionlabs.hateofluxdemos.datatransferobject.ShipmentDTO;
import de.kamillionlabs.hateofluxdemos.service.OrderService;
import de.kamillionlabs.hateofluxdemos.service.ShipmentService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

/**
 * Contains test cases from the documentation. These can be used to debug a given example.
 *
 * @author Younes El Ouarti
 */
class ManualOrderControllerTest {

    private final OrderService orderService = new OrderService();

    private final ShipmentService shipmentService = new ShipmentService();

    private final ManualOrderController manualOrderController = new ManualOrderController(orderService,
            shipmentService);

    private final ObjectMapper objectMapper = new ObjectMapper();


    @SneakyThrows
    @Test
    void creating_a_halresourcewrapper_without_an_embedded_resource() {
        //alternative: Run the application and on command line execute:
        //curl "http://localhost:8080/manual/order-no-embedded/1234"

        HalResourceWrapper<OrderDTO, Void> order = manualOrderController.getOrder(1234)
                .block();

        String orderAsJson = objectMapper.writeValueAsString(order);

        JSONAssert.assertEquals("""
                {
                  "id": 1234,
                  "userId": 37,
                  "total": 99.99,
                  "status": "Processing",
                  "_links": {
                    "shipment": {
                      "href": "orders/1234/shipment"
                    },
                    "self": {
                      "href": "orders/1234"
                    }
                  }
                }
                """, orderAsJson, NON_EXTENSIBLE);
    }

    @SneakyThrows
    @Test
    void creating_a_halresourcewrapper_with_an_embedded_resource() {
        //alternative: Run the application and on command line execute:
        //curl "http://localhost:8080/manual/order-with-embedded/1234"

        HalResourceWrapper<OrderDTO, ShipmentDTO> order = manualOrderController.getOrderWithShipment(1234)
                .block();

        String orderAsJson = objectMapper.writeValueAsString(order);

        JSONAssert.assertEquals("""
                {
                  "id": 1234,
                  "userId": 37,
                  "total": 99.99,
                  "status": "Processing",
                  "_embedded": {
                    "shipment": {
                      "id": 127,
                      "carrier": "UPS",
                      "trackingNumber": "154-ASD-1238724",
                      "status": "Completed",
                      "_links": {
                        "self": {
                          "href": "/shipment/127",
                          "hreflang": "en-US"
                        }
                      }
                    }
                  },
                  "_links": {
                    "self": {
                      "href": "orders/1234"
                    }
                  }
                }
                """, orderAsJson, NON_EXTENSIBLE);
    }

    @SneakyThrows
    @Test
    void creating_a_hallistwrapper_with_pagination() {
        //alternative: Run the application and on command line execute:
        //curl "http://localhost:8080/manual/orders-with-pagination?userId=37&page=0&size=2&sort=id,desc"

        Sort sort = Sort.by(Sort.Order.desc("id"));
        MockServerHttpRequest request = MockServerHttpRequest
                .get("http://myservice:8080?userId=37&page=0&size=2&sort=id,desc")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        HalListWrapper<OrderDTO, Void> orders = manualOrderController.getOrdersManualBuilt(
                        37L,
                        PageRequest.of(0, 2, sort),
                        exchange)
                .block();

        String ordersAsJson = objectMapper.writeValueAsString(orders);

        JSONAssert.assertEquals("""
                {
                    "page": {
                      "size": 2,
                      "totalElements": 6,
                      "totalPages": 3,
                      "number": 0
                    },
                    "_embedded": {
                      "orderDTOs": [
                        {
                          "id": 1234,
                          "userId": 37,
                          "total": 99.99,
                          "status": "Processing",
                          "_links": {
                            "self": {
                              "href": "http://myservice:8080/orders/1234"
                            }
                          }
                        },
                        {
                          "id": 1057,
                          "userId": 37,
                          "total": 72.48,
                          "status": "Delivered",
                          "_links": {
                            "self": {
                              "href": "http://myservice:8080/orders/1057"
                            }
                          }
                        }
                      ]
                    },
                    "_links": {
                      "next": {
                        "href": "http://myservice:8080/orders?userId=37?page=1&size=2&sort=id,desc"
                      },
                      "self": {
                        "href": "http://myservice:8080/orders?userId=37?page=0&size=2&sort=id,desc"
                      },
                      "last": {
                        "href": "http://myservice:8080/orders?userId=37?page=2&size=2&sort=id,desc"
                      }
                    }
                  }
                """, ordersAsJson, NON_EXTENSIBLE);
    }
}