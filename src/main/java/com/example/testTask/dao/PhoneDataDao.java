package com.example.testTask.dao;

import com.example.testTask.dto.PhoneData;
import com.example.testTask.dto.Users;
import com.example.testTask.util.Queries;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PhoneDataDao {

    @PersistenceContext
    private EntityManager em;
    private final RedisTemplate<String, Users> redisTemplate;

    public Optional<Users> findUserByPhone(String phone) {
        try {
            Users cashedUser = redisTemplate.opsForValue().get(phone);
            if (cashedUser != null)
                return Optional.of(cashedUser);
            Users user = (Users) em.createQuery(Queries.GET_USER_BY_PHONE_QUERY).setParameter("phone", phone).getSingleResult();
            redisTemplate.opsForValue().set(phone, user);
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public List<PhoneData> findPhonesByUser(Users user){
        return em.createQuery(Queries.GET_PHONE_BY_USER_QUERY, PhoneData.class).setParameter("user", user).getResultList();
    }
    @Transactional
    public PhoneData save (PhoneData phoneData){
        em.persist(phoneData);
        return phoneData;
    }

    public void delete(PhoneData phoneData) {
        em.remove(phoneData);
        redisTemplate.delete(phoneData.getPhone());
    }
}
