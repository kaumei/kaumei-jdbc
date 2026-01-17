/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service.impl;

import io.kaumei.jdbc.integration.jpa.entity.*;
import io.kaumei.jdbc.integration.jpa.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JpaCustomerDirectoryService implements CustomerDirectoryService {

    private final EntityManager entityManager;

    public JpaCustomerDirectoryService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CustomerDto createCustomer(CreateCustomerRequest request) {
        Objects.requireNonNull(request, "request");
        Customer customer = new Customer();
        customer.setName(request.name());
        entityManager.persist(customer);
        entityManager.flush();
        return toCustomerDto(customer);
    }

    @Override
    public RestaurantDto createRestaurant(CreateRestaurantRequest request) {
        Objects.requireNonNull(request, "request");
        Customer customer = requireCustomer(request.customerId());
        Restaurant restaurant = new Restaurant(request.name(), request.address(), customer);
        customer.getRestaurants().add(restaurant);
        entityManager.persist(restaurant);
        entityManager.flush();
        return toRestaurantDto(restaurant);
    }

    @Override
    public ServiceOnlineBookingDto createOnlineBookingService(CreateServiceOnlineBookingRequest request) {
        Objects.requireNonNull(request, "request");
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        ServiceOnlineBooking service = new ServiceOnlineBooking(restaurant);
        service.setEnabled(request.enabled());
        service.setMaxDaysBefore(request.maxDaysBefore());
        service.setMinPersons(request.minPersons());
        service.setNeedPhoneNumber(request.needPhoneNumber());
        restaurant.setOnlineBooking(service);
        entityManager.persist(service);
        entityManager.flush();
        return toServiceOnlineBookingDto(service);
    }

    @Override
    public ServiceAccountingDto createAccountingService(CreateServiceAccountingRequest request) {
        Objects.requireNonNull(request, "request");
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        ServiceAccounting service = new ServiceAccounting(restaurant);
        service.setEnabled(request.enabled());
        restaurant.setAccounting(service);
        entityManager.persist(service);
        entityManager.flush();
        return toServiceAccountingDto(service);
    }

    @Override
    public ServiceDeliveryIntegrationDto createDeliveryIntegrationService(
            CreateServiceDeliveryIntegrationRequest request) {
        Objects.requireNonNull(request, "request");
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        ServiceDeliveryIntegration service = new ServiceDeliveryIntegration(restaurant);
        service.setEnabled(request.enabled());
        service.setUberId(request.uberId());
        service.setLieferandoId(request.lieferandoId());
        service.setCustom(request.custom());
        restaurant.setDeliveryIntegration(service);
        entityManager.persist(service);
        entityManager.flush();
        return toServiceDeliveryIntegrationDto(service);
    }

    @Override
    public ServiceFeedbackDto createFeedbackService(CreateServiceFeedbackRequest request) {
        Objects.requireNonNull(request, "request");
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        ServiceFeedback service = new ServiceFeedback(restaurant);
        service.setEnabled(request.enabled());
        restaurant.setFeedback(service);
        entityManager.persist(service);
        entityManager.flush();
        return toServiceFeedbackDto(service);
    }

    @Override
    public ServiceStaffSchedulingDto createStaffSchedulingService(CreateServiceStaffSchedulingRequest request) {
        Objects.requireNonNull(request, "request");
        Restaurant restaurant = requireRestaurant(request.restaurantId());
        ServiceStaffScheduling service = new ServiceStaffScheduling(restaurant);
        service.setEnabled(request.enabled());
        restaurant.setStaffScheduling(service);
        entityManager.persist(service);
        entityManager.flush();
        return toServiceStaffSchedulingDto(service);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerRestaurantsDto> findCustomerByName(String customerName) {
        Objects.requireNonNull(customerName, "customerName");
        TypedQuery<Customer> query = entityManager.createQuery(
                "select distinct c from Customer c left join fetch c.restaurants where c.name = :name",
                Customer.class);
        query.setParameter("name", customerName);
        return query.getResultStream()
                .findFirst()
                .map(customer -> new CustomerRestaurantsDto(
                        toCustomerDto(customer),
                        customer.getRestaurants().stream()
                                .map(JpaCustomerDirectoryService::toRestaurantDto)
                                .collect(Collectors.toList())));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantDto> findRestaurantByName(String restaurantName) {
        Objects.requireNonNull(restaurantName, "restaurantName");
        TypedQuery<Restaurant> query = entityManager.createQuery(
                "select r from Restaurant r where r.name = :name",
                Restaurant.class);
        query.setParameter("name", restaurantName);
        return query.getResultStream().findFirst().map(JpaCustomerDirectoryService::toRestaurantDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantServicesDto> listServicesForCustomer(String customerName) {
        Objects.requireNonNull(customerName, "customerName");
        TypedQuery<Restaurant> query = entityManager.createQuery(
                "select distinct r from Restaurant r "
                        + "left join fetch r.onlineBooking "
                        + "left join fetch r.accounting "
                        + "left join fetch r.deliveryIntegration "
                        + "left join fetch r.feedback "
                        + "left join fetch r.staffScheduling "
                        + "where r.customer.name = :name",
                Restaurant.class);
        query.setParameter("name", customerName);
        return query.getResultStream()
                .map(JpaCustomerDirectoryService::toRestaurantServicesDto)
                .collect(Collectors.toList());
    }

    private Customer requireCustomer(Long id) {
        Customer customer = entityManager.find(Customer.class, id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer %d not found".formatted(id));
        }
        return customer;
    }

    private Restaurant requireRestaurant(Long id) {
        Restaurant restaurant = entityManager.find(Restaurant.class, id);
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant %d not found".formatted(id));
        }
        return restaurant;
    }

    private static CustomerDto toCustomerDto(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getName());
    }

    private static RestaurantDto toRestaurantDto(Restaurant restaurant) {
        return new RestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCustomer().getId());
    }

    private static ServiceOnlineBookingDto toServiceOnlineBookingDto(ServiceOnlineBooking service) {
        return new ServiceOnlineBookingDto(
                service.getRestaurant().getId(),
                service.isEnabled(),
                service.getMaxDaysBefore(),
                service.getMinPersons(),
                service.isNeedPhoneNumber());
    }

    private static ServiceAccountingDto toServiceAccountingDto(ServiceAccounting service) {
        return new ServiceAccountingDto(service.getRestaurant().getId(), service.isEnabled());
    }

    private static ServiceDeliveryIntegrationDto toServiceDeliveryIntegrationDto(
            ServiceDeliveryIntegration service) {
        return new ServiceDeliveryIntegrationDto(
                service.getRestaurant().getId(),
                service.isEnabled(),
                service.getUberId(),
                service.getLieferandoId(),
                service.getCustom());
    }

    private static ServiceFeedbackDto toServiceFeedbackDto(ServiceFeedback service) {
        return new ServiceFeedbackDto(service.getRestaurant().getId(), service.isEnabled());
    }

    private static ServiceStaffSchedulingDto toServiceStaffSchedulingDto(ServiceStaffScheduling service) {
        return new ServiceStaffSchedulingDto(service.getRestaurant().getId(), service.isEnabled());
    }

    private static RestaurantServicesDto toRestaurantServicesDto(Restaurant restaurant) {
        return new RestaurantServicesDto(
                restaurant.getName(),
                toAvailability(restaurant.getOnlineBooking()),
                toAvailability(restaurant.getAccounting()),
                toAvailability(restaurant.getDeliveryIntegration()),
                toAvailability(restaurant.getFeedback()),
                toAvailability(restaurant.getStaffScheduling()));
    }

    private static ServiceAvailability toAvailability(ServiceOnlineBooking service) {
        return service == null
                ? ServiceAvailability.ABSENT
                : (service.isEnabled() ? ServiceAvailability.ENABLED : ServiceAvailability.DISABLED);
    }

    private static ServiceAvailability toAvailability(ServiceAccounting service) {
        return service == null
                ? ServiceAvailability.ABSENT
                : (service.isEnabled() ? ServiceAvailability.ENABLED : ServiceAvailability.DISABLED);
    }

    private static ServiceAvailability toAvailability(ServiceDeliveryIntegration service) {
        return service == null
                ? ServiceAvailability.ABSENT
                : (service.isEnabled() ? ServiceAvailability.ENABLED : ServiceAvailability.DISABLED);
    }

    private static ServiceAvailability toAvailability(ServiceFeedback service) {
        return service == null
                ? ServiceAvailability.ABSENT
                : (service.isEnabled() ? ServiceAvailability.ENABLED : ServiceAvailability.DISABLED);
    }

    private static ServiceAvailability toAvailability(ServiceStaffScheduling service) {
        return service == null
                ? ServiceAvailability.ABSENT
                : (service.isEnabled() ? ServiceAvailability.ENABLED : ServiceAvailability.DISABLED);
    }
}
