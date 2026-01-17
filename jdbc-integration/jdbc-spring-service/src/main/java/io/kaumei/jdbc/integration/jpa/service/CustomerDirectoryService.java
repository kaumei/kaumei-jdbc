/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

import java.util.List;
import java.util.Optional;

/**
 * High-level service contract for the Spring JPA sample flows.
 */
public interface CustomerDirectoryService {

    /**
     * Creates a new customer aggregate from the provided request DTO.
     */
    CustomerDto createCustomer(CreateCustomerRequest request);

    /**
     * Creates a restaurant for an existing customer.
     */
    RestaurantDto createRestaurant(CreateRestaurantRequest request);

    /**
     * Provisions the online booking service for a restaurant.
     */
    ServiceOnlineBookingDto createOnlineBookingService(CreateServiceOnlineBookingRequest request);

    /**
     * Provisions the accounting service for a restaurant.
     */
    ServiceAccountingDto createAccountingService(CreateServiceAccountingRequest request);

    /**
     * Provisions the delivery integration service for a restaurant.
     */
    ServiceDeliveryIntegrationDto createDeliveryIntegrationService(CreateServiceDeliveryIntegrationRequest request);

    /**
     * Provisions the feedback service for a restaurant.
     */
    ServiceFeedbackDto createFeedbackService(CreateServiceFeedbackRequest request);

    /**
     * Provisions the staff scheduling service for a restaurant.
     */
    ServiceStaffSchedulingDto createStaffSchedulingService(CreateServiceStaffSchedulingRequest request);

    /**
     * Fetches a customer and their restaurants by customer name.
     */
    Optional<CustomerRestaurantsDto> findCustomerByName(String customerName);

    /**
     * Fetches a restaurant by its name.
     */
    Optional<RestaurantDto> findRestaurantByName(String restaurantName);

    /**
     * Lists the service availability for every restaurant of the supplied customer.
     */
    List<RestaurantServicesDto> listServicesForCustomer(String customerName);
}
