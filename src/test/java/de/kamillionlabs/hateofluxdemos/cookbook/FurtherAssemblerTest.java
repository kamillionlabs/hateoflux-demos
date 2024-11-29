/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 28.11.2024
 */
package de.kamillionlabs.hateofluxdemos.cookbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.utility.pair.MultiRightPairFlux;
import de.kamillionlabs.hateoflux.utility.pair.PairFlux;
import de.kamillionlabs.hateofluxdemos.assembler.OrderAssembler;
import de.kamillionlabs.hateofluxdemos.datatransferobject.OrderDTO;
import de.kamillionlabs.hateofluxdemos.datatransferobject.ShipmentDTO;
import de.kamillionlabs.hateofluxdemos.service.OrderService;
import de.kamillionlabs.hateofluxdemos.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

/**
 * @author Younes El Ouarti
 */
public class FurtherAssemblerTest {

    private final OrderAssembler orderAssembler = new OrderAssembler();

    private final OrderService orderService = new OrderService();

    private final ShipmentService shipmentService = new ShipmentService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
            .get("https://www.example.com")
            .build());

    @Test
    void creating_a_halresourcewrapper_with_a_resource_and_no_embedded() throws Exception {
        // Given Input
        Mono<OrderDTO> resource = orderService.getOrder(1234);
        Mono<ShipmentDTO> embedded = Mono.empty();

        // Assembler call
        Mono<HalResourceWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInResourceWrapper(resource,
                embedded, exchange);

        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());
        JSONAssert.assertEquals("""
                {
                  "id": 1234,
                  "userId": 37,
                  "total": 99.99,
                  "status": "Processing",
                  "_links": {
                    "self": {
                      "href": "https://www.example.com/order/1234"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    void creating_a_halresourcewrapper_with_a_resource_and_a_single_embedded() throws Exception {
        // Given Input
        Mono<OrderDTO> resource = orderService.getOrder(1234);
        Mono<ShipmentDTO> embedded = shipmentService.getShipment(127);

        // Assembler call
        Mono<HalResourceWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInResourceWrapper(resource,
                embedded, exchange);

        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());
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
                           "href": "https://www.example.com/shipment/127",
                           "hreflang": "en-US"
                         }
                       }
                     }
                   },
                   "_links": {
                     "self": {
                       "href": "https://www.example.com/order/1234"
                     }
                   }
                 }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    void creating_a_halresourcewrapper_with_a_resource_and_a_list_of_embedded() throws Exception {
        // Given Input
        Mono<OrderDTO> resource = orderService.getOrder(1234);
        Flux<ShipmentDTO> embeddedList = shipmentService.getShipments(3287, 4125);

        // Assembler call
        Mono<HalResourceWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInResourceWrapper(resource,
                embeddedList, exchange);

        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());
        JSONAssert.assertEquals("""
                {
                   "id": 1234,
                   "userId": 37,
                   "total": 99.99,
                   "status": "Processing",
                   "_embedded": {
                     "shipments": [
                       {
                         "id": 3287,
                         "carrier": "DHL",
                         "trackingNumber": "562-DHL-9182736",
                         "status": "Pending",
                         "_links": {
                           "self": {
                             "href": "https://www.example.com/shipment/3287",
                             "hreflang": "en-US"
                           }
                         }
                       },
                       {
                         "id": 4125,
                         "carrier": "USPS",
                         "trackingNumber": "739-USP-1827364",
                         "status": "Completed",
                         "_links": {
                           "self": {
                             "href": "https://www.example.com/shipment/4125",
                             "hreflang": "en-US"
                           }
                         }
                       }
                     ]
                   },
                   "_links": {
                     "self": {
                       "href": "https://www.example.com/order/1234"
                     }
                   }
                 }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    void creating_a_halresourcewrapper_with_a_resource_and_an_empty_list_of_embedded() throws Exception {
        // Given Input
        Mono<OrderDTO> resource = orderService.getOrder(1234);
        Flux<ShipmentDTO> embeddedList = Flux.empty();

        // Assembler call
        Mono<HalResourceWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInResourceWrapper(resource,
                embeddedList, exchange);

        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());
        JSONAssert.assertEquals("""
                {
                   "id": 1234,
                   "userId": 37,
                   "total": 99.99,
                   "status": "Processing",
                   "_embedded": {
                     "shipments": []
                   },
                   "_links": {
                     "self": {
                       "href": "https://www.example.com/order/1234"
                     }
                   }
                 }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    void creating_an_empty_hallistwrapper() throws Exception {
        //Given Input
        PairFlux<OrderDTO, ShipmentDTO> emptyPairFlux = PairFlux.empty();
        MultiRightPairFlux<OrderDTO, ShipmentDTO> emptyMultiRightPairFlux = MultiRightPairFlux.empty();

        //Assembler call
        // Option 1
        HalListWrapper<OrderDTO, ShipmentDTO> resultOp1 = orderAssembler.createEmptyListWrapper(OrderDTO.class,
                exchange);
        // Option 2
        Mono<HalListWrapper<OrderDTO, ShipmentDTO>> resultOp2 = orderAssembler.wrapInListWrapper(emptyPairFlux,
                exchange);
        // Option 3
        Mono<HalListWrapper<OrderDTO, ShipmentDTO>> resultOp3 =
                orderAssembler.wrapInListWrapper(emptyMultiRightPairFlux, exchange);


        // Then
        String actualJsonOp1 = objectMapper.writeValueAsString(resultOp1);
        String actualJsonOp2 = objectMapper.writeValueAsString(resultOp2.block());
        String actualJsonOp3 = objectMapper.writeValueAsString(resultOp3.block());

        JSONAssert.assertEquals(actualJsonOp1, actualJsonOp2, NON_EXTENSIBLE);
        JSONAssert.assertEquals(actualJsonOp2, actualJsonOp3, NON_EXTENSIBLE);

        JSONAssert.assertEquals("""
                {
                  "_embedded": {
                    "orderDTOs": []
                  },
                  "_links": {
                    "self": {
                      "href": "https://www.example.com/order"
                    }
                  }
                }
                """, actualJsonOp1, NON_EXTENSIBLE);

    }

    @Test
    void creating_a_hallistwrapper_with_resources_each_having_a_single_embedded() throws Exception {
        //Given input
        Flux<OrderDTO> orders = orderService.getOrdersByUserId(38L);
        PairFlux<OrderDTO, ShipmentDTO> resourcesWithEmbedded;

        resourcesWithEmbedded = PairFlux.from(orders)
                .with(order -> shipmentService.getLastShipmentByOrderId(order.getId()));

        //Assembler call
        Mono<HalListWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInListWrapper(resourcesWithEmbedded,
                exchange);

        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());
        JSONAssert.assertEquals("""
                {
                    "_embedded": {
                      "orderDTOs": [
                        {
                          "id": 9550,
                          "userId": 38,
                          "total": 149.99,
                          "status": "Delivered",
                          "_embedded": {
                            "shipment": {
                              "id": 3105,
                              "carrier": "FedEx",
                              "trackingNumber": "759-FDX-1029384",
                              "status": "Out for Delivery",
                              "_links": {
                                "self": {
                                  "href": "https://www.example.com/shipment/3105",
                                  "hreflang": "en-US"
                                }
                              }
                            }
                          },
                          "_links": {
                            "self": {
                              "href": "https://www.example.com/order/9550"
                            }
                          }
                        },
                        {
                          "id": 5058,
                          "userId": 38,
                          "total": 149.99,
                          "status": "Delivered",
                          "_embedded": {
                            "shipment": {
                              "id": 5032,
                              "carrier": "FedEx",
                              "trackingNumber": "357-FDX-2938475",
                              "status": "In Transit",
                              "_links": {
                                "self": {
                                  "href": "https://www.example.com/shipment/5032",
                                  "hreflang": "en-US"
                                }
                              }
                            }
                          },
                          "_links": {
                            "self": {
                              "href": "https://www.example.com/order/5058"
                            }
                          }
                        }
                      ]
                    },
                    "_links": {
                      "self": {
                        "href": "https://www.example.com/order"
                      }
                    }
                  }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    void creating_a_hallistwrapper_with_resources_each_having_a_single_embedded_with_paging() throws Exception {
        //Given Input
        int pageNumber = 0;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageNumber, pageSize); // This would usually be provided by Spring
        // automatically
        Flux<OrderDTO> orders = orderService.getOrdersByUserId(37L, pageable);

        PairFlux<OrderDTO, ShipmentDTO> resourcesWithEmbedded;
        resourcesWithEmbedded = PairFlux.from(orders)
                .with(order -> shipmentService.getLastShipmentByOrderId(order.getId()));

        Mono<Long> totalNumberOfElements = orderService.countAllOrdersByUserId(37L);


        //Assembler call
        Mono<HalListWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInListWrapper(resourcesWithEmbedded,
                totalNumberOfElements,
                pageSize,
                pageable.getOffset(),
                null, //for simplicity, we do not sort
                exchange);

        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());
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
                        "_embedded": {
                          "shipment": {
                            "id": 127,
                            "carrier": "UPS",
                            "trackingNumber": "154-ASD-1238724",
                            "status": "Completed",
                            "_links": {
                              "self": {
                                "href": "https://www.example.com/shipment/127",
                                "hreflang": "en-US"
                              }
                            }
                          }
                        },
                        "_links": {
                          "self": {
                            "href": "https://www.example.com/order/1234"
                          }
                        }
                      },
                      {
                        "id": 1057,
                        "userId": 37,
                        "total": 72.48,
                        "status": "Delivered",
                        "_embedded": {
                          "shipment": {
                            "id": 105,
                            "carrier": "UPS",
                            "trackingNumber": "154-ASD-1284724",
                            "status": "Completed",
                            "_links": {
                              "self": {
                                "href": "https://www.example.com/shipment/105",
                                "hreflang": "en-US"
                              }
                            }
                          }
                        },
                        "_links": {
                          "self": {
                            "href": "https://www.example.com/order/1057"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "next": {
                      "href": "https://www.example.com/order?page=1&size=2"
                    },
                    "self": {
                      "href": "https://www.example.com/order?page=0&size=2"
                    },
                    "last": {
                      "href": "https://www.example.com/order?page=2&size=2"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    void creating_a_hallistwrapper_with_resources_each_having_a_list_of_embedded() throws Exception {
        //Given Input
        Flux<OrderDTO> ordersWithReturns = orderService.getOrdersByUserId(17L);
        MultiRightPairFlux<OrderDTO, ShipmentDTO> resourcesWithEmbedded;
        resourcesWithEmbedded = MultiRightPairFlux.from(ordersWithReturns)
                .with(order -> shipmentService.getShipmentsByOrderId(order.getId()));

        //Assembler call
        Mono<HalListWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInListWrapper(resourcesWithEmbedded,
                exchange);


        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());

        JSONAssert.assertEquals("""
                {
                  "_embedded": {
                    "orderDTOs": [
                      {
                        "id": 1070,
                        "userId": 17,
                        "total": 199.99,
                        "status": "Returned",
                        "_embedded": {
                          "shipments": [
                            {
                              "id": 2551,
                              "carrier": "UPS",
                              "trackingNumber": "610-UPS-3748291",
                              "status": "Completed",
                              "_links": {
                                "self": {
                                  "href": "https://www.example.com/shipment/2551",
                                  "hreflang": "en-US"
                                }
                              }
                            },
                            {
                              "id": 3904,
                              "carrier": "DHL",
                              "trackingNumber": "680-DHL-9182736",
                              "status": "Completed",
                              "_links": {
                                "self": {
                                  "href": "https://www.example.com/shipment/3904",
                                  "hreflang": "en-US"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "self": {
                            "href": "https://www.example.com/order/1070"
                          }
                        }
                      },
                      {
                        "id": 5078,
                        "userId": 17,
                        "total": 34.0,
                        "status": "Returned",
                        "_embedded": {
                          "shipments": [
                            {
                              "id": 3750,
                              "carrier": "USPS",
                              "trackingNumber": "755-USP-8374652",
                              "status": "Completed",
                              "_links": {
                                "self": {
                                  "href": "https://www.example.com/shipment/3750",
                                  "hreflang": "en-US"
                                }
                              }
                            },
                            {
                              "id": 4203,
                              "carrier": "FedEx",
                              "trackingNumber": "920-FDX-5647382",
                              "status": "Completed",
                              "_links": {
                                "self": {
                                  "href": "https://www.example.com/shipment/4203",
                                  "hreflang": "en-US"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "self": {
                            "href": "https://www.example.com/order/5078"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "self": {
                      "href": "https://www.example.com/order"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    void creating_a_hallistwrapper_with_resources_each_having_a_list_of_embedded_with_some_being_empty() throws Exception {
        //Given Input
        Flux<OrderDTO> ordersWithAndWithoutShipments = orderService.getOrdersByUserId(39L);
        MultiRightPairFlux<OrderDTO, ShipmentDTO> resourcesWithEmbedded;
        resourcesWithEmbedded = MultiRightPairFlux.from(ordersWithAndWithoutShipments)
                .with(order -> shipmentService.getShipmentsByOrderId(order.getId()));

        //Assembler call
        Mono<HalListWrapper<OrderDTO, ShipmentDTO>> result = orderAssembler.wrapInListWrapper(resourcesWithEmbedded,
                exchange);


        // Then
        String actualJson = objectMapper.writeValueAsString(result.block());
        JSONAssert.assertEquals("""
                {
                  "_embedded": {
                    "orderDTOs": [
                      {
                        "id": 7250,
                        "userId": 39,
                        "total": 34.0,
                        "status": "Created",
                        "_embedded": {
                          "shipments": []
                        },
                        "_links": {
                          "self": {
                            "href": "https://www.example.com/order/7250"
                          }
                        }
                      },
                      {
                        "id": 1230,
                        "userId": 39,
                        "total": 99.99,
                        "status": "Delivered",
                        "_embedded": {
                          "shipments": [
                            {
                              "id": 4005,
                              "carrier": "FedEx",
                              "trackingNumber": "634-FDX-8473621",
                              "status": "Delivered",
                              "_links": {
                                "self": {
                                  "href": "https://www.example.com/shipment/4005",
                                  "hreflang": "en-US"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "self": {
                            "href": "https://www.example.com/order/1230"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "self": {
                      "href": "https://www.example.com/order"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }
}
