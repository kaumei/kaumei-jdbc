/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.kaumei.web;

import io.kaumei.jdbc.integration.jpa.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directory")
public class CustomerDirectoryController {

    private final CustomerDirectoryService service;

    public CustomerDirectoryController(CustomerDirectoryService service) {
        this.service = service;
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(service.createCustomer(request));
    }

    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantDto> createRestaurant(@RequestBody CreateRestaurantRequest request) {
        return ResponseEntity.ok(service.createRestaurant(request));
    }

    @PostMapping("/services/online-booking")
    public ResponseEntity<ServiceOnlineBookingDto> createOnlineBooking(
            @RequestBody CreateServiceOnlineBookingRequest request) {
        return ResponseEntity.ok(service.createOnlineBookingService(request));
    }

    @PostMapping("/services/accounting")
    public ResponseEntity<ServiceAccountingDto> createAccounting(
            @RequestBody CreateServiceAccountingRequest request) {
        return ResponseEntity.ok(service.createAccountingService(request));
    }

    @PostMapping("/services/delivery")
    public ResponseEntity<ServiceDeliveryIntegrationDto> createDelivery(
            @RequestBody CreateServiceDeliveryIntegrationRequest request) {
        return ResponseEntity.ok(service.createDeliveryIntegrationService(request));
    }

    @PostMapping("/services/feedback")
    public ResponseEntity<ServiceFeedbackDto> createFeedback(
            @RequestBody CreateServiceFeedbackRequest request) {
        return ResponseEntity.ok(service.createFeedbackService(request));
    }

    @PostMapping("/services/staff-scheduling")
    public ResponseEntity<ServiceStaffSchedulingDto> createStaffScheduling(
            @RequestBody CreateServiceStaffSchedulingRequest request) {
        return ResponseEntity.ok(service.createStaffSchedulingService(request));
    }

    @GetMapping("/customers/{name}")
    public ResponseEntity<CustomerRestaurantsDto> findCustomer(@PathVariable("name") String name) {
        return service.findCustomerByName(name).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/restaurants/{name}")
    public ResponseEntity<RestaurantDto> findRestaurant(@PathVariable("name") String name) {
        return service.findRestaurantByName(name).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customers/{name}/services")
    public ResponseEntity<List<RestaurantServicesDto>> listServices(@PathVariable("name") String name) {
        return ResponseEntity.ok(service.listServicesForCustomer(name));
    }
}
