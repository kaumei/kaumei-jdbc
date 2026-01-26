/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.hibernate;

import io.kaumei.jdbc.examples.spring.NamesService;
import io.kaumei.jdbc.examples.spring.ValueBudgeEntity;
import io.kaumei.jdbc.examples.spring.ValueBudgeId;
import org.hibernate.SessionFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NamesServiceHibernate implements NamesService {

    private final SessionFactory sessionFactory;

    public NamesServiceHibernate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int deleteAll() {
        return sessionFactory.getCurrentSession()
                .createMutationQuery("DELETE FROM ValueBudgeEntity")
                .executeUpdate();
    }

    @Override
    public int delete(String value) {
        return sessionFactory.getCurrentSession()
                .createMutationQuery("DELETE FROM ValueBudgeEntity where value = :value")
                .setParameter("value", value)
                .executeUpdate();
    }

    @Override
    public void insert(String value, @Nullable Integer budge) {
        var entity = new ValueBudgeEntity();
        entity.setId(new ValueBudgeId(value, budge));
        sessionFactory.getCurrentSession()
                .persist(entity);
    }

    @Override
    public int count() {
        return sessionFactory.getCurrentSession()
                .createQuery("select count(*) from ValueBudgeEntity", Integer.class)
                .getSingleResult();
    }

    @Override
    public List<NamesService.ValueBudge> selectAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from ValueBudgeEntity", ValueBudgeEntity.class)
                .getResultList().stream()
                .map(ValueBudgeEntity::toCustomer)
                .toList();
    }

}