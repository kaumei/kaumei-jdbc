/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.kaumei.service;

import io.kaumei.jdbc.integration.jpa.service.*;
import io.kaumei.jdbc.integration.kaumei.repository.CustomerDirectoryJdbcRepository;
import io.kaumei.jdbc.integration.kaumei.repository.CustomerDirectoryJdbcRepository.RestaurantServicesRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
@Transactional
public class KaumeiCustomerDirectoryService implements CustomerDirectoryService {

    private final CustomerDirectoryJdbcRepository repository;

    public KaumeiCustomerDirectoryService(CustomerDirectoryJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public CustomerDto createCustomer(CreateCustomerRequest request) {
        requireNonNull(request, "request");
        var id = repository.insertCustomer(request.name());
        return new CustomerDto(id.id(), request.name());
    }

    @Override
    public RestaurantDto createRestaurant(CreateRestaurantRequest request) {
        requireNonNull(request, "request");
        requireCustomer(request.customerId());
        repository.insertRestaurant(request.name(), request.address(), request.customerId());
        return repository.findRestaurantByName(request.name()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Restaurant insert failed"));
    }

    @Override
    public ServiceOnlineBookingDto createOnlineBookingService(CreateServiceOnlineBookingRequest request) {
        requireNonNull(request, "request");
        RestaurantDto restaurant = requireRestaurant(request.restaurantId());
        repository.insertOnlineBooking(
                restaurant.id(),
                request.enabled(),
                request.maxDaysBefore(),
                request.minPersons(),
                request.needPhoneNumber());
        return repository.findOnlineBooking(restaurant.id()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Online booking insert failed"));
    }

    @Override
    public ServiceAccountingDto createAccountingService(CreateServiceAccountingRequest request) {
        requireNonNull(request, "request");
        RestaurantDto restaurant = requireRestaurant(request.restaurantId());
        repository.insertAccounting(restaurant.id(), request.enabled());
        return repository.findAccounting(restaurant.id()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Accounting insert failed"));
    }

    @Override
    public ServiceDeliveryIntegrationDto createDeliveryIntegrationService(
            CreateServiceDeliveryIntegrationRequest request) {
        requireNonNull(request, "request");
        RestaurantDto restaurant = requireRestaurant(request.restaurantId());
        repository.insertDelivery(
                restaurant.id(),
                request.enabled(),
                request.uberId(),
                request.lieferandoId(),
                request.custom());
        return repository.findDelivery(restaurant.id()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Delivery integration insert failed"));
    }

    @Override
    public ServiceFeedbackDto createFeedbackService(CreateServiceFeedbackRequest request) {
        requireNonNull(request, "request");
        RestaurantDto restaurant = requireRestaurant(request.restaurantId());
        repository.insertFeedback(restaurant.id(), request.enabled());
        return repository.findFeedback(restaurant.id()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Feedback insert failed"));
    }

    @Override
    public ServiceStaffSchedulingDto createStaffSchedulingService(CreateServiceStaffSchedulingRequest request) {
        requireNonNull(request, "request");
        RestaurantDto restaurant = requireRestaurant(request.restaurantId());
        repository.insertStaffScheduling(restaurant.id(), request.enabled());
        return repository.findStaffScheduling(restaurant.id()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Staff scheduling insert failed"));
    }

    @Override
    public Optional<CustomerRestaurantsDto> findCustomerByName(String customerName) {
        requireNonNull(customerName, "customerName");
        return repository.findCustomersByName(customerName).stream()
                .findFirst()
                .map(customer -> new CustomerRestaurantsDto(
                        customer, repository.findRestaurantsByCustomer(customer.id())));
    }

    @Override
    public Optional<RestaurantDto> findRestaurantByName(String restaurantName) {
        requireNonNull(restaurantName, "restaurantName");
        return repository.findRestaurantByName(restaurantName).stream().findFirst();
    }

    @Override
    public List<RestaurantServicesDto> listServicesForCustomer(String customerName) {
        requireNonNull(customerName, "customerName");
        return repository.fetchRestaurantServices(customerName).stream()
                .map(KaumeiCustomerDirectoryService::toServicesDto)
                .collect(Collectors.toList());
    }

    private CustomerDto requireCustomer(Long id) {
        return repository.findCustomerById(id).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Customer %d not found".formatted(id)));
    }

    private RestaurantDto requireRestaurant(Long id) {
        return repository.findRestaurantById(id).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Restaurant %d not found".formatted(id)));
    }

    private static RestaurantServicesDto toServicesDto(RestaurantServicesRow row) {
        return new RestaurantServicesDto(
                row.restaurantName(),
                toAvailability(row.onlineBooking()),
                toAvailability(row.accounting()),
                toAvailability(row.delivery()),
                toAvailability(row.feedback()),
                toAvailability(row.staffScheduling()));
    }

    private static ServiceAvailability toAvailability(Boolean enabled) {
        if (enabled == null) {
            return ServiceAvailability.ABSENT;
        }
        return enabled ? ServiceAvailability.ENABLED : ServiceAvailability.DISABLED;
    }
}
