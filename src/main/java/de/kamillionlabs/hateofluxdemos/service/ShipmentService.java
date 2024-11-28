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
 * @since 07.11.2024
 */

package de.kamillionlabs.hateofluxdemos.service;

import de.kamillionlabs.hateoflux.utility.pair.Pair;
import de.kamillionlabs.hateoflux.utility.pair.PairList;
import de.kamillionlabs.hateofluxdemos.datatransferobject.ShipmentDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

/**
 * @author Younes El Ouarti
 */
@Service
public class ShipmentService {

    PairList<Integer, ShipmentDTO> database = new PairList<>();

    public ShipmentService() {
        //shipment by order id
        database.add(1234, new ShipmentDTO(127, "UPS", "154-ASD-1238724", "Completed"));
        database.add(1057, new ShipmentDTO(105, "UPS", "154-ASD-1284724", "Completed"));
        database.add(2457, new ShipmentDTO(2589, "FedEx", "845-FDX-3748291", "In Transit"));
        database.add(3124, new ShipmentDTO(3287, "DHL", "562-DHL-9182736", "Pending"));
        database.add(3982, new ShipmentDTO(4125, "USPS", "739-USP-1827364", "Completed"));
        database.add(2765, new ShipmentDTO(2890, "UPS", "124-UPS-5647382", "Cancelled"));
        database.add(5058, new ShipmentDTO(5032, "FedEx", "357-FDX-2938475", "In Transit"));
        database.add(2301, new ShipmentDTO(2450, "DHL", "468-DHL-8374652", "Delivered"));
        database.add(3550, new ShipmentDTO(3675, "USPS", "591-USP-7463829", "Processing"));
        database.add(4203, new ShipmentDTO(4350, "UPS", "682-UPS-9182736", "Shipped"));
        database.add(9550, new ShipmentDTO(3105, "FedEx", "759-FDX-1029384", "Out for Delivery"));
        database.add(5058, new ShipmentDTO(4302, "DHL", "830-DHL-5647382", "Delayed"));
        database.add(2543, new ShipmentDTO(2701, "USPS", "912-USP-5647382", "In Transit"));
        database.add(1230, new ShipmentDTO(4005, "FedEx", "634-FDX-8473621", "Delivered"));
        database.add(3102, new ShipmentDTO(3250, "UPS", "812-UPS-2938475", "Processing"));
        database.add(2750, new ShipmentDTO(2903, "DHL", "479-DHL-9182736", "Shipped"));
        database.add(3321, new ShipmentDTO(3490, "USPS", "605-USP-3748291", "Completed"));
        database.add(2905, new ShipmentDTO(3050, "FedEx", "738-FDX-5647382", "Cancelled"));
        database.add(4123, new ShipmentDTO(4250, "UPS", "845-UPS-1827364", "In Transit"));
        database.add(2678, new ShipmentDTO(2830, "DHL", "529-DHL-8374652", "Pending"));
        database.add(3500, new ShipmentDTO(3652, "USPS", "670-USP-9182736", "Out for Delivery"));
        database.add(4101, new ShipmentDTO(4305, "FedEx", "753-FDX-5647382", "Delayed"));
        database.add(2250, new ShipmentDTO(2401, "UPS", "590-UPS-3748291", "Processing"));
        database.add(3850, new ShipmentDTO(4002, "DHL", "645-DHL-9182736", "Shipped"));
        database.add(3100, new ShipmentDTO(3255, "USPS", "705-USP-8374652", "Delivered"));
        database.add(2755, new ShipmentDTO(2908, "FedEx", "812-FDX-5647382", "In Transit"));
        database.add(4200, new ShipmentDTO(4355, "UPS", "890-UPS-1827364", "Pending"));
        database.add(2950, new ShipmentDTO(3102, "DHL", "670-DHL-9182736", "Completed"));
        database.add(5078, new ShipmentDTO(3750, "USPS", "755-USP-8374652", "Completed"));
        database.add(5078, new ShipmentDTO(4203, "FedEx", "920-FDX-5647382", "Completed"));
        database.add(1070, new ShipmentDTO(2551, "UPS", "610-UPS-3748291", "Completed"));
        database.add(1070, new ShipmentDTO(3904, "DHL", "680-DHL-9182736", "Completed"));

    }

    public Flux<ShipmentDTO> getShipmentsByOrderId(Integer orderId) {
        return Flux.fromIterable(database.stream().filter(pair -> pair.left().equals(orderId))
                .map(Pair::right)
                .toList());
    }

    public Mono<ShipmentDTO> getLastShipmentByOrderId(Integer orderId) {
        return database.stream().filter(pair -> pair.left().equals(orderId))
                .map(Pair::getRight)
                .max(Comparator.comparingInt(ShipmentDTO::getId))
                .map(Mono::just)
                .orElse(Mono.empty());
    }


    public Flux<ShipmentDTO> getShipments(Integer... shipmentIds) {
        return Flux.fromStream(
                database.stream()
                        .filter(pair -> Set.of(shipmentIds).contains(pair.right().getId()))
                        .map(Pair::right)
        );
    }

    public Mono<ShipmentDTO> getShipment(Integer shipmentId) {
        return database.stream()
                .filter(pair -> Objects.equals(shipmentId, pair.right().getId()))
                .findFirst()
                .map(p -> Mono.just(p.right()))
                .orElse(Mono.empty());
    }


}
