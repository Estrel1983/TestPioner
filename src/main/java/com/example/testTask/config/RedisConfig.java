package com.example.testTask.config;

import com.example.testTask.dto.Users;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
public class RedisConfig {
    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(ClientResources clientResources) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("redis_test", 6379);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .clientResources(clientResources)
                .build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    @Bean(name = "redisTemplateUsers")
    public RedisTemplate<String, Users> redisTemplateUsers(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Users> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<Users> serializer = new Jackson2JsonRedisSerializer<>(objectMapperWithJavaTimeSupport(), Users.class);
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
    @Bean(name = "redisTemplateUsersList")
    public RedisTemplate<String, List<Users>> redisTemplateUsersList(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<Users>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapperWithJavaTimeSupport());

        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
    private ObjectMapper objectMapperWithJavaTimeSupport() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Поддержка LocalDate, LocalDateTime и т.д.
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Читаемый ISO формат
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        return mapper;
    }
}
