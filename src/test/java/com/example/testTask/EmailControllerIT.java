package com.example.testTask;

import com.example.testTask.dao.EmailDao;
import com.example.testTask.dto.EmailData;
import com.example.testTask.dto.Users;
import com.example.testTask.dto.requests.EmailRequest;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "file:src/test/resources/application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmailControllerIT {
        @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager em;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private RedisTemplate<String, Users> redisTemplate;
    @Mock
    private ValueOperations<String, Users> valueOperations;
    @Autowired
    EmailDao emailDao;

    private Users authUser;

    static {
        postgres.start();
    }
    private String token;
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @BeforeAll
    void setUpOnce()throws Exception{

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        authUser = emailDao.createUserForTest("admin", "admin123", LocalDate.of(1999,3,3));
        EmailData email = emailDao.createEmailForTest("admin@admin.com", authUser);

        String tokenStr = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                            {
                                                "email":"admin@admin.com",
                                                "password":"admin123"
                                            }
                                    """)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        token = JsonPath.read(tokenStr, "$.token");
    }
    @BeforeEach
    @Transactional
    void setUp() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
    }
    @BeforeEach
    void setupSecurityContext() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authUser.getId(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @Order(1)
    void testAddNewEmail_success() throws Exception{
        EmailRequest request = new EmailRequest();
        request.setNewEmail("new@email.test");
        mockMvc.perform(post("/email")
                        .header("Authorization", "Bearer " + token )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@email.test"));
    }
    @Test
    @Order(2)
    void testAddNewInvalidEmail_badRequest() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setNewEmail("test.com");
        mockMvc.perform(post("/email")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Order(3)
    void testAddTheSameEmail_serverError() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setNewEmail("admin@admin.com");
        mockMvc.perform(post("/email")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
    @Test
    @Order(4)
    void testChangeEmail_success() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setOldEmail("new@email.test");
        request.setNewEmail("change@email.test");
        mockMvc.perform(patch("/email")
                .header("Authorization", "Bearer " + token )
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email changed successfully"));

        emailDao.findUserByEmail("change@email.test");
    }
    @Test
    @Order(5)
    void testDeleteEmail_success() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setOldEmail("change@email.test");
        mockMvc.perform(delete("/email")
                        .header("Authorization", "Bearer " + token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(emailDao.findUserByEmail("change@email.test").isPresent());
    }
    @Test
    @Order(6)
    void testChangeEmailInvalidEmail_badRequest() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setOldEmail("admin@admin.com");
        request.setNewEmail("change@.test");
        mockMvc.perform(patch("/email")
                        .header("Authorization", "Bearer " + token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Order(7)
    void testChangeEmailTheSameEmail_serverError() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setOldEmail("admin@admin.com");
        request.setNewEmail("admin@admin.com");
        mockMvc.perform(patch("/email")
                        .header("Authorization", "Bearer " + token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
    @Order(8)
    void testDeleteEmailTheOnly_conflict() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setOldEmail("admin@admin.com");
        mockMvc.perform(delete("/email")
                        .header("Authorization", "Bearer " + token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        emailDao.findUserByEmail("admin@admin.com");
    }
    @Order(9)
    void testDeleteEmailInvalidEmail_badRequest() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setOldEmail("admin@admin");
        mockMvc.perform(delete("/email")
                        .header("Authorization", "Bearer " + token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        emailDao.findUserByEmail("admin@admin.com");
    }
    @Order(10)
    void testDeleteEmailWrongEmail_serverError() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setOldEmail("wrong@admin.wr");
        mockMvc.perform(delete("/email")
                        .header("Authorization", "Bearer " + token )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        emailDao.findUserByEmail("admin@admin.com");
    }
}
