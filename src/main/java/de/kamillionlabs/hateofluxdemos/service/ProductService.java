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
 * @since 29.10.2024
 */

package de.kamillionlabs.hateofluxdemos.service;

import de.kamillionlabs.hateofluxdemos.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Younes El Ouarti
 */
@Service
public class ProductService {

    public List<ProductDTO> getProducts() {
        return List.of(
                new ProductDTO("SOME-CODE-1", "This is a dummy product 1", BigDecimal.valueOf(1.99)),
                new ProductDTO("SOME-CODE-2", "This is a dummy product 2", BigDecimal.valueOf(4.99))
                //                new ProductDTO("SOME-CODE-3", "This is a dummy product 3", BigDecimal.valueOf(5.99)),
                //                new ProductDTO("SOME-CODE-4", "This is a dummy product 4", BigDecimal.valueOf(0.99)),
                //                new ProductDTO("SOME-CODE-5", "This is a dummy product 5", BigDecimal.valueOf(0.99)),
                //                new ProductDTO("SOME-CODE-6", "This is a dummy product 6", BigDecimal.valueOf(0.49)),
                //                new ProductDTO("SOME-CODE-7", "This is a dummy product 7", BigDecimal.valueOf(1.19)),
                //                new ProductDTO("SOME-CODE-8", "This is a dummy product 8", BigDecimal.valueOf(2.49)),
                //                new ProductDTO("SOME-CODE-9", "This is a dummy product 9", BigDecimal.valueOf(7.99)),
                //                new ProductDTO("SOME-CODE-10", "This is a dummy product 10", BigDecimal.valueOf(10
                //                .49))
        );
    }
}
