/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 11.11.2024
 */
package de.kamillionlabs.hateofluxdemos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HateofluxDemosApplication {

    /**
     * Run this to start the service.
     *
     * @param args
     *         n/a
     */
    public static void main(String[] args) {
        SpringApplication.run(HateofluxDemosApplication.class, args);
    }

}
