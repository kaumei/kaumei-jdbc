/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.kaumei.repository;

import io.kaumei.jdbc.annotation.JdbcConstructorAnnotations;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import io.kaumei.jdbc.annotation.config.JdbcReturnGeneratedValues;
import io.kaumei.jdbc.integration.jpa.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JdbcConstructorAnnotations(Autowired.class)
public interface CustomerDirectoryJdbcRepository {

    record CustomerId(long id) {
    }

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("insert into customers(name) values (:name)")
    CustomerId insertCustomer(String name);

    @JdbcSelect("select id, name from customers where name = :name")
    List<CustomerDto> findCustomersByName(String name);

    @JdbcSelect("select id, name from customers where id = :id")
    List<CustomerDto> findCustomerById(Long id);

    @JdbcUpdate("insert into restaurants(name, address, customer_id) values (:name, :address, :customerId)")
    int insertRestaurant(String name, String address, Long customerId);

    @JdbcSelect("select id, name, address, customer_id from restaurants where name = :name")
    List<RestaurantDto> findRestaurantByName(String name);

    @JdbcSelect("select id, name, address, customer_id from restaurants where id = :id")
    List<RestaurantDto> findRestaurantById(Long id);

    @JdbcSelect("select id, name, address, customer_id from restaurants where customer_id = :customerId")
    List<RestaurantDto> findRestaurantsByCustomer(Long customerId);

    @JdbcUpdate("""
            insert into service_online_booking (id, enabled, max_days_before, min_persons, need_phone_number)
            values (:restaurantId, :enabled, :maxDaysBefore, :minPersons, :needPhoneNumber)
            """)
    int insertOnlineBooking(Long restaurantId, boolean enabled, int maxDaysBefore, Integer minPersons, boolean needPhoneNumber);

    @JdbcSelect("""
            select id as restaurant_Id,
                   enabled,
                   max_days_before,
                   min_persons,
                   need_phone_number
            from service_online_booking
            where id = :restaurantId
            """)
    List<ServiceOnlineBookingDto> findOnlineBooking(Long restaurantId);

    @JdbcUpdate("""
            insert into service_accounting (id, enabled)
            values (:restaurantId, :enabled)
            """)
    int insertAccounting(Long restaurantId, boolean enabled);

    @JdbcSelect("""
            select id as restaurant_id,
                   enabled
            from service_accounting
            where id = :restaurantId
            """)
    List<ServiceAccountingDto> findAccounting(Long restaurantId);

    @JdbcUpdate("""
            insert into service_delivery_integration (id, enabled, uber_id, lieferando_id, custom)
            values (:restaurantId, :enabled, :uberId, :lieferandoId, :custom)
            """)
    int insertDelivery(Long restaurantId, boolean enabled, String uberId, String lieferandoId, String custom);

    @JdbcSelect("""
            select id as restaurant_Id,
                   enabled,
                   uber_id,
                   lieferando_id,
                   custom
            from service_delivery_integration
            where id = :restaurantId
            """)
    List<ServiceDeliveryIntegrationDto> findDelivery(Long restaurantId);

    @JdbcUpdate("""
            insert into service_feedback (id, enabled)
            values (:restaurantId, :enabled)
            """)
    int insertFeedback(Long restaurantId, boolean enabled);

    @JdbcSelect("""
            select id as restaurant_Id,
                   enabled
            from service_feedback
            where id = :restaurantId
            """)
    List<ServiceFeedbackDto> findFeedback(Long restaurantId);

    @JdbcUpdate("""
            insert into service_staff_scheduling (id, enabled)
            values (:restaurantId, :enabled)
            """)
    int insertStaffScheduling(Long restaurantId, boolean enabled);

    @JdbcSelect("""
            select id as restaurant_Id,
                   enabled
            from service_staff_scheduling
            where id = :restaurantId
            """)
    List<ServiceStaffSchedulingDto> findStaffScheduling(Long restaurantId);

    @JdbcSelect("""
            select r.name as restaurant_Name,
                   ob.enabled as online_Booking,
                   acc.enabled as accounting,
                   di.enabled as delivery,
                   fb.enabled as feedback,
                   ss.enabled as staff_Scheduling
            from restaurants r
            join customers c on c.id = r.customer_id
            left join service_online_booking ob on ob.id = r.id
            left join service_accounting acc on acc.id = r.id
            left join service_delivery_integration di on di.id = r.id
            left join service_feedback fb on fb.id = r.id
            left join service_staff_scheduling ss on ss.id = r.id
            where c.name = :customerName
            order by r.name
            """)
    List<RestaurantServicesRow> fetchRestaurantServices(String customerName);

    record RestaurantServicesRow(
            String restaurantName,
            Boolean onlineBooking,
            Boolean accounting,
            Boolean delivery,
            Boolean feedback,
            Boolean staffScheduling) {
    }
}
