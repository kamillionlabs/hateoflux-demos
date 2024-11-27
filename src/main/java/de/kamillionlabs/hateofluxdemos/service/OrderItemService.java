/*
 * Copyright (c)  2024 kamillion-suite contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @since 11.11.2024
 */

package de.kamillionlabs.hateofluxdemos.service;

import de.kamillionlabs.hateofluxdemos.dto.OrderItemDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Younes El Ouarti
 */
@Component
public class OrderItemService {

    private final List<OrderItemDTO> database;

    public OrderItemService() {
        this.database = List.of(
                // Order ID: 1234, Total: 99.99
                new OrderItemDTO(1, 1234, "Wireless Mouse", 49.99),
                new OrderItemDTO(2, 1234, "USB-C Cable", 50.00),

                // Order ID: 1057, Total: 72.48
                new OrderItemDTO(3, 1057, "Bluetooth Keyboard", 72.48),

                // Order ID: 9510, Total: 199.99
                new OrderItemDTO(4, 9510, "Gaming Monitor", 199.99),

                // Order ID: 7258, Total: 34.00
                new OrderItemDTO(5, 7258, "HDMI Adapter", 34.00),

                // Order ID: 9550, Total: 149.99
                new OrderItemDTO(6, 9550, "External Hard Drive", 149.99),

                // Order ID: 7250, Total: 34.00
                new OrderItemDTO(7, 7250, "Wireless Charger", 34.00),

                // Order ID: 1230, Total: 99.99
                new OrderItemDTO(8, 1230, "Noise-Cancelling Headphones", 99.99),

                // Order ID: 1040, Total: 72.48
                new OrderItemDTO(9, 1040, "Smart LED Bulb", 72.48),

                // Order ID: 9540, Total: 199.99
                new OrderItemDTO(10, 9540, "4K Action Camera", 199.99),

                // Order ID: 7208, Total: 34.00
                new OrderItemDTO(11, 7208, "Portable Speaker", 34.00),

                // Order ID: 8508, Total: 149.99
                new OrderItemDTO(12, 8508, "Fitness Tracker", 149.99),

                // Order ID: 5208, Total: 34.00
                new OrderItemDTO(13, 5208, "USB Hub", 34.00),

                // Order ID: 5204, Total: 99.99
                new OrderItemDTO(14, 5204, "Wireless Earbuds", 99.99),

                // Order ID: 5007, Total: 10.00
                new OrderItemDTO(15, 5007, "Screen Protector", 10.00),

                // Order ID: 1070, Total: 199.99
                new OrderItemDTO(16, 1070, "Smartwatch", 199.99),

                // Order ID: 5078, Total: 34.00
                new OrderItemDTO(17, 5078, "USB Flash Drive", 34.00),

                // Order ID: 5058, Total: 149.99
                new OrderItemDTO(18, 5058, "Wireless Router", 149.99),

                // Order ID: 7058, Total: 34.00
                new OrderItemDTO(19, 7058, "Laptop Stand", 34.00)
        );
    }

    /**
     * Retrieves all OrderItemDTOs associated with a given order ID.
     *
     * @param orderId
     *         The ID of the order.
     * @return A Flux emitting the OrderItemDTOs for the specified order.
     */
    public Flux<OrderItemDTO> getOrderItemsByOrderId(int orderId) {
        return Flux.fromStream(
                database.stream()
                        .filter(item -> item.getOrderId() == orderId)
        );
    }

    /**
     * Retrieves an OrderItemDTO by its unique item ID.
     *
     * @param id
     *         The unique ID of the order item.
     * @return A Mono emitting the OrderItemDTO if found, or empty otherwise.
     */
    public Mono<OrderItemDTO> getOrderItemById(int id) {
        return database.stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }
}
