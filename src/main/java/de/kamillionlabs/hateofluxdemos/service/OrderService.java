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
 * @since 06.11.2024
 */

package de.kamillionlabs.hateofluxdemos.service;

import de.kamillionlabs.hateofluxdemos.datatransferobject.OrderDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Younes El Ouarti
 */
@Component
public class OrderService {

    private final List<OrderDTO> database;

    public OrderService() {
        this.database = List.of(
                new OrderDTO(1234, 37L, 99.99, "Processing"),
                new OrderDTO(1057, 37L, 72.48, "Delivered"),
                new OrderDTO(9510, 37L, 199.99, "Returned"),
                new OrderDTO(7258, 37L, 34.00, "Delivered"),
                new OrderDTO(9550, 38L, 149.99, "Delivered"),
                new OrderDTO(7250, 39L, 34.00, "Created"),
                new OrderDTO(1230, 39L, 99.99, "Delivered"),
                new OrderDTO(1040, 37L, 72.48, "Delivered"),
                new OrderDTO(9540, 87L, 199.99, "Returned"),
                new OrderDTO(7208, 87L, 34.00, "Delivered"),
                new OrderDTO(8508, 88L, 149.99, "Delivered"),
                new OrderDTO(5208, 89L, 34.00, "Delivered"),
                new OrderDTO(5204, 87L, 99.99, "Processing"),
                new OrderDTO(5007, 37L, 10.00, "Delivered"),
                new OrderDTO(1070, 17L, 199.99, "Returned"),
                new OrderDTO(5078, 17L, 34.00, "Returned"),
                new OrderDTO(5058, 38L, 149.99, "Delivered"),
                new OrderDTO(7058, 98L, 34.00, "Delivered")
        );
    }


    public Mono<OrderDTO> getOrder(int id) {
        return database.stream().filter(order -> order.getId() == id).findFirst()
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }

    public Flux<OrderDTO> getOrdersByUserId(long userId) {
        return Flux.fromStream(database.stream().filter(order -> order.getUserId() == userId));
    }

    public Flux<OrderDTO> getOrdersByUserId(long userId, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        return Flux.fromStream(database.stream().filter(order -> order.getUserId() == userId)
                .limit(pageSize));
    }

    public Flux<OrderDTO> getOrders(Long userId, Pageable pageable) {
        //for simplicity, we ignore the sorting
        int pageSize = pageable.getPageSize();
        return userId == null ? Flux.fromStream(database.stream().limit(pageSize))
                : getOrdersByUserId(userId).take(pageSize);
    }

    public Mono<Long> countAllOrdersByUserId(long userId) {
        return Mono.just(database.stream().filter(order -> order.getUserId() == userId)
                .count());
    }

    public Mono<Long> countAllOrders(Long userId) {

        return userId == null ? Mono.just((long) database.size()) : countAllOrdersByUserId(userId);
    }
}