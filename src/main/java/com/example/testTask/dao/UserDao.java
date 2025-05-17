package com.example.testTask.dao;

import com.example.testTask.dto.Users;
import com.example.testTask.util.Queries;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDao {
    @PersistenceContext
    private EntityManager em;
    private final RedisTemplate<String, List<Users>> redisTemplate;

    public List<Users> findUser(String name, LocalDate dateOfBirth, int page, int size) {
        String redisKey = String.format("%s|%s|%d|%d", name, dateOfBirth, page, size);
        List<Users> cashedList = redisTemplate.opsForValue().get(redisKey);
        if (cashedList != null)
            return cashedList;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Users> cq = cb.createQuery(Users.class);
        Root<Users> root = cq.from(Users.class);
        List<Predicate> predicates = new ArrayList<>();
        if (dateOfBirth != null)
            predicates.add(cb.greaterThan(root.get("dateOfBirth"), dateOfBirth));
        if (name != null && !name.isBlank())
            predicates.add(cb.like(root.get("name"), name + "%"));
        cq.where(predicates.toArray(new Predicate[0]));
        List<Users> result = em.createQuery(cq).setFirstResult(page * size).setMaxResults(size).getResultList();
        redisTemplate.opsForValue().set(redisKey, result);
        return result;
    }

    public Optional<Users> findById(Long id) {
        try {
            Users user = (Users) em.createQuery(Queries.GET_USER_BY_ID_QUERY).setParameter("id", id).getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
