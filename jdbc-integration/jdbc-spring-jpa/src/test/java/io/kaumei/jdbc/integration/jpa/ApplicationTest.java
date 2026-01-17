/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa;

import io.kaumei.jdbc.integration.jpa.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

    static long id = System.currentTimeMillis();

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUpClient() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void restWorkflowAgainstRunningApp() {

        var restaurantCount = 100;
        var customerName = "customer-restWorkflowAgainstRunningApp";

        CustomerDto customer = restClient.post()
                .uri("/api/directory/customers")
                .body(new CreateCustomerRequest(customerName))
                .retrieve()
                .body(CustomerDto.class);
        assertThat(customer).isNotNull();

        for (int i = 0; i < restaurantCount; i++) {
            String name = "restaurant-" + i;
            RestaurantDto restaurant = restClient.post()
                    .uri("/api/directory/restaurants")
                    .body(new CreateRestaurantRequest(name, "Main Street " + i, customer.id()))
                    .retrieve()
                    .body(RestaurantDto.class);
            assertThat(restaurant).isNotNull();

            restClient.post()
                    .uri("/api/directory/services/online-booking")
                    .body(new CreateServiceOnlineBookingRequest(restaurant.id(), true, 30, 2, true))
                    .retrieve()
                    .body(ServiceOnlineBookingDto.class);
            restClient.post()
                    .uri("/api/directory/services/accounting")
                    .body(new CreateServiceAccountingRequest(restaurant.id(), true))
                    .retrieve()
                    .body(ServiceAccountingDto.class);
            restClient.post()
                    .uri("/api/directory/services/delivery")
                    .body(new CreateServiceDeliveryIntegrationRequest(
                            restaurant.id(), i % 2 == 0, "uber-" + i, "lieferando-" + i, "custom-" + i))
                    .retrieve()
                    .body(ServiceDeliveryIntegrationDto.class);
            restClient.post()
                    .uri("/api/directory/services/feedback")
                    .body(new CreateServiceFeedbackRequest(restaurant.id(), i % 3 == 0))
                    .retrieve()
                    .body(ServiceFeedbackDto.class);
            restClient.post()
                    .uri("/api/directory/services/staff-scheduling")
                    .body(new CreateServiceStaffSchedulingRequest(restaurant.id(), true))
                    .retrieve()
                    .body(ServiceStaffSchedulingDto.class);
        }

        CustomerRestaurantsDto hydrated = restClient.get()
                .uri("/api/directory/customers/{name}", customerName)
                .retrieve()
                .body(CustomerRestaurantsDto.class);
        assertThat(hydrated).isNotNull();
        assertThat(hydrated.restaurants()).hasSize(restaurantCount);

        RestaurantServicesDto[] services = restClient.get()
                .uri("/api/directory/customers/{name}/services", customerName)
                .retrieve()
                .body(RestaurantServicesDto[].class);
        assertThat(services).isNotNull();
        assertThat(services).hasSize(restaurantCount);
        for (RestaurantServicesDto dto : services) {
            assertThat(dto.onlineBooking()).isEqualTo(ServiceAvailability.ENABLED);
            assertThat(dto.accounting()).isEqualTo(ServiceAvailability.ENABLED);
            assertThat(dto.staffScheduling()).isEqualTo(ServiceAvailability.ENABLED);
        }
    }

    @Test
    void customer() {
        CustomerDto customer = restClient.post()
                .uri("/api/directory/customers")
                .body(new CreateCustomerRequest("customer-one"))
                .retrieve()
                .body(CustomerDto.class);
        System.out.println(customer);

        customer = restClient.post()
                .uri("/api/directory/customers")
                .body(new CreateCustomerRequest("customer-two"))
                .retrieve()
                .body(CustomerDto.class);
        System.out.println(customer);


        assertThat(customer).isNotNull();
    }

    @Disabled("manual performance test")
    @Test
    void customerLoad() {
        for (int i = 0; i < 6; i++) {
            customerLoad(5);
        }
    }

    void customerLoad(int seconds) {
        long end = System.currentTimeMillis() + seconds * 1000L;
        int count = 0;
        while (System.currentTimeMillis() < end) {
            count++;
            CustomerDto customer = restClient.post()
                    .uri("/api/directory/customers")
                    .body(new CreateCustomerRequest("customer-" + (id++)))
                    .retrieve()
                    .body(CustomerDto.class);
            assertThat(customer).isNotNull();
        }
        int countPerSecond = count / seconds;
        System.out.println("jpa: count per second: " + countPerSecond + " (time: " + seconds + ")");
    }

}
