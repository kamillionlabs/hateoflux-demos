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

import de.kamillionlabs.hateoflux.utility.PairList;
import de.kamillionlabs.hateofluxdemos.dto.ShipmentDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author Younes El Ouarti
 */
@Service
public class ShipmentService {

    PairList<Integer, ShipmentDTO> database = new PairList<>();

    public ShipmentService() {
        database.add(1234, new ShipmentDTO(127, "UPS", "154-ASD-1238724", "Completed"));
        database.add(1057, new ShipmentDTO(105, "UPS", "154-ASD-1284724", "Completed"));
        database.add(9510, new ShipmentDTO(951, "UPS", "154-ASD-1284724", "Completed"));
        database.add(7258, new ShipmentDTO(625, "UPS", "154-ASD-1284724", "Completed"));
        database.add(9550, new ShipmentDTO(655, "UPS", "154-ASD-4284784", "Completed"));
        database.add(7250, new ShipmentDTO(625, "UPS", "154-ASD-4238745", "Completed"));
        database.add(1230, new ShipmentDTO(123, "UPS", "154-ASD-1688724", "Completed"));
        database.add(1040, new ShipmentDTO(104, "UPS", "154-ASD-4238780", "Completed"));
        database.add(9540, new ShipmentDTO(994, "UPS", "154-ASD-4866514", "Completed"));
        database.add(7208, new ShipmentDTO(790, "UPS", "154-ASD-6868724", "Completed"));
        database.add(8508, new ShipmentDTO(890, "UPS", "154-ASD-2698720", "Completed"));
        database.add(5208, new ShipmentDTO(590, "UPS", "154-ASD-5938514", "Completed"));
        database.add(5204, new ShipmentDTO(590, "UPS", "154-ASD-5688724", "Completed"));
        database.add(5007, new ShipmentDTO(590, "UPS", "154-ASD-5968725", "Completed"));
        database.add(1070, new ShipmentDTO(197, "UPS", "154-ASD-4128724", "Completed"));
        database.add(5078, new ShipmentDTO(677, "UPS", "154-ASD-6848724", "Completed"));
        database.add(5058, new ShipmentDTO(675, "UPS", "154-ASD-3218735", "Completed"));
        database.add(7058, new ShipmentDTO(705, "UPS", "154-ASD-6158724", "Completed"));
    }

    public Mono<ShipmentDTO> getShipmentByOrderId(Integer orderId) {
        return database.stream().filter(pair -> pair.left().equals(orderId)).findFirst()
                .map(p -> Mono.just(p.right()))
                .orElse(Mono.empty());
    }
}
