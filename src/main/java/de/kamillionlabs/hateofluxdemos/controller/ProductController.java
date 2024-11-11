/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */

package de.kamillionlabs.hateofluxdemos.controller;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import de.kamillionlabs.hateofluxdemos.dto.ProductDTO;
import de.kamillionlabs.hateofluxdemos.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static de.kamillionlabs.hateoflux.utility.SortDirection.ASCENDING;
import static de.kamillionlabs.hateoflux.utility.SortDirection.DESCENDING;

/**
 * @author Younes El Ouarti
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/products")
public class ProductController {
    public static final String MIMETYPE_APPLICATION_HAL_JSON = "application/hal+json";


    @Autowired
    ProductService productService;

    @GetMapping(produces = {MIMETYPE_APPLICATION_HAL_JSON, MediaType.APPLICATION_JSON_VALUE})
    public Mono<HalListWrapper<ProductDTO, Void>> getProduct(
            @PageableDefault(page = 0, size = 20, sort = "currentPrice") Pageable pageable) {
        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();
        List<SortCriteria> sortCriteria = pageable.getSort().get()
                .map(o -> SortCriteria.by(o.getProperty(), o.getDirection().isAscending() ? ASCENDING : DESCENDING))
                .toList();

        return Flux.fromIterable(productService.getProducts())
                .map(p -> HalResourceWrapper.wrap(p)
                        .withLinks(
                                Link.linkAsSelfOf("http://example/" + p.getCode())
                        )
                ).collectList()
                .map(l -> {
                            long totalElements = 100L;
                            HalPageInfo p = HalPageInfo.assembleWithOffset(pageSize, totalElements, offset);
                            return HalListWrapper.wrap(l)
                                    .withLinks(
                                            Link.of("http://example/")
                                                    .deriveNavigationLinks(p, sortCriteria)
                                    );
                        }
                );
    }
}
