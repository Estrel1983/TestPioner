package com.example.testTask.dao;

import com.example.testTask.dto.EmailData;
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
public class EmailDao {
    @PersistenceContext
    private EntityManager em;

    public Optional<Users> findUserByEmail(String email) {
        try {
            Users user = (Users) em.createQuery(Queries.GET_USER_BY_EMAIL_QUERY).setParameter("email", email).getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public List<EmailData> findEmailsByUser(Users user){
        return em.createQuery(Queries.GET_EMAILS_BY_USER_QUERY, EmailData.class).setParameter("user", user).getResultList();
    }

    @Transactional
    public EmailData save(EmailData emailData) {
        em.persist(emailData);
        return emailData;
    }

    public void delete(EmailData emailData) {
        em.remove(emailData);
    }
}
