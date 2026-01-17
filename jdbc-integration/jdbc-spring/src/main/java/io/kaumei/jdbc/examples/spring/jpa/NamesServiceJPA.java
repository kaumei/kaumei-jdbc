/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.jpa;

import io.kaumei.jdbc.examples.spring.NamesService;
import io.kaumei.jdbc.examples.spring.ValueBudgeEntity;
import io.kaumei.jdbc.examples.spring.ValueBudgeId;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NamesServiceJPA implements NamesService {

    private final EntityManager entityManager;

    public NamesServiceJPA(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int deleteAll() {
        return entityManager
                .createQuery("DELETE FROM ValueBudgeEntity")
                .executeUpdate();
    }

    @Override
    public int delete(String value) {
        return entityManager
                .createQuery("DELETE FROM ValueBudgeEntity where value = :value")
                .setParameter("value", value)
                .executeUpdate();
    }


    @Override
    public void insert(String value, @Nullable Integer budge) {
        var entity = new ValueBudgeEntity();
        entity.setId(new ValueBudgeId(value, budge));
        entityManager.persist(entity);
    }

    @Override
    public int count() {
        return entityManager
                .createQuery("select count(*) from ValueBudgeEntity", Integer.class)
                .getSingleResult();
    }

    @Override
    public List<NamesService.ValueBudge> selectAll() {
        return entityManager
                .createQuery("from ValueBudgeEntity", ValueBudgeEntity.class)
                .getResultList().stream()
                .map(ValueBudgeEntity::toCustomer)
                .toList();
    }
}