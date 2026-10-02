package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.User.RoleType;
import es.udc.paproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.UserService;
import es.udc.paproject.backend.rest.common.JwtGenerator;
import es.udc.paproject.backend.rest.controllers.UserController;
import net.jqwik.api.Arbitraries;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "user";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String TOKEN = "service-token";
    private static final int MAX_LENGTH = 60;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtGenerator jwtGenerator;

    @BeforeEach
    public void setUp() {
        when(jwtGenerator.generate(any())).thenReturn(TOKEN);
    }

    private static User user() {
        User user = new User(USER_NAME, PASSWORD, "First", "Last", "user@test.com");
        user.setId(USER_ID);
        user.setRole(RoleType.ESPECTADOR);
        return user;
    }

    private static Map<String, Object> validSignUpBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("userName", USER_NAME);
        body.put("password", PASSWORD);
        body.put("firstName", "First");
        body.put("lastName", "Last");
        body.put("email", "user@test.com");
        return body;
    }

    private static Map<String, Object> validUpdateBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "First");
        body.put("lastName", "Last");
        body.put("email", "user@test.com");
        return body;
    }

    private static Map<String, Object> changePasswordBody(String oldPassword, String newPassword) {
        Map<String, Object> body = new HashMap<>();
        body.put("oldPassword", oldPassword);
        body.put("newPassword", newPassword);
        return body;
    }

    private static Map<String, Object> loginBody(String userName, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("userName", userName);
        body.put("password", password);
        return body;
    }

    private ResultActions perform(MockHttpServletRequestBuilder request, Object body) throws Exception {
        return mockMvc.perform(request
            .requestAttr("userId", USER_ID)
            .requestAttr("serviceToken", TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)));
    }

    private void mockSignUpAssignsIdAndRole() throws DuplicateInstanceException {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(USER_ID);
            user.setRole(RoleType.ESPECTADOR);
            return null;
        }).when(userService).signUp(any(User.class));
    }

    static Stream<String> validNameBoundaries() {
        return Stream.of("a", "a".repeat(MAX_LENGTH));
    }

    static Stream<String> invalidNameBoundaries() {
        return Stream.of("", "   ", "a".repeat(MAX_LENGTH + 1));
    }

    static Stream<String> invalidPasswordBoundaries() {
        return Stream.of("", "a".repeat(MAX_LENGTH + 1));
    }

    static Stream<String> invalidEmails() {
        return Stream.of("sin-arroba", "@dominio.com", "a@@dominio.com");
    }

    static Stream<String> randomValidNames() {
        return Arbitraries.strings().alpha()
            .ofMinLength(1).ofMaxLength(MAX_LENGTH)
            .sampleStream().limit(10);
    }

    static Stream<Long> randomForeignIds() {
        return Arbitraries.longs().between(USER_ID + 1, Long.MAX_VALUE)
            .sampleStream().limit(10);
    }

    @Test
    public void testPostSignUp_Ok() throws Exception {
        mockSignUpAssignsIdAndRole();

        perform(post("/users/signUp"), validSignUpBody())
            .andExpect(status().isCreated());
    }

    @Test
    public void testPostSignUp_Ok_ServiceToken() throws Exception {
        mockSignUpAssignsIdAndRole();

        perform(post("/users/signUp"), validSignUpBody())
            .andExpect(jsonPath("$.serviceToken").value(TOKEN));
    }

    @Test
    public void testPostSignUp_Ok_Location() throws Exception {
        mockSignUpAssignsIdAndRole();

        perform(post("/users/signUp"), validSignUpBody())
            .andExpect(header().string("Location", "http://localhost/users/" + USER_ID));
    }

    @Test
    public void testPostSignUp_Error_DuplicateInstanceException() throws Exception {
        doThrow(new DuplicateInstanceException("project.entities.user", USER_NAME))
            .when(userService).signUp(any(User.class));

        perform(post("/users/signUp"), validSignUpBody())
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("validNameBoundaries")
    public void testPostSignUp_Ok_FirstNameBoundary(String firstName) throws Exception {
        mockSignUpAssignsIdAndRole();
        Map<String, Object> body = validSignUpBody();
        body.put("firstName", firstName);

        perform(post("/users/signUp"), body)
            .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("invalidNameBoundaries")
    public void testPostSignUp_Error_FirstNameBoundary(String firstName) throws Exception {
        Map<String, Object> body = validSignUpBody();
        body.put("firstName", firstName);

        perform(post("/users/signUp"), body)
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("randomValidNames")
    public void testPostSignUp_Ok_RandomUserName(String userName) throws Exception {
        mockSignUpAssignsIdAndRole();
        Map<String, Object> body = validSignUpBody();
        body.put("userName", userName);

        perform(post("/users/signUp"), body)
            .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("invalidEmails")
    public void testPostSignUp_Error_InvalidEmail(String email) throws Exception {
        Map<String, Object> body = validSignUpBody();
        body.put("email", email);

        perform(post("/users/signUp"), body)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostSignUp_Error_MissingPassword() throws Exception {
        Map<String, Object> body = validSignUpBody();
        body.remove("password");

        perform(post("/users/signUp"), body)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostLogin_Ok() throws Exception {
        when(userService.login(USER_NAME, PASSWORD)).thenReturn(user());

        perform(post("/users/login"), loginBody(USER_NAME, PASSWORD))
            .andExpect(status().isOk());
    }

    @Test
    public void testPostLogin_Ok_ServiceToken() throws Exception {
        when(userService.login(USER_NAME, PASSWORD)).thenReturn(user());

        perform(post("/users/login"), loginBody(USER_NAME, PASSWORD))
            .andExpect(jsonPath("$.serviceToken").value(TOKEN));
    }

    @Test
    public void testPostLogin_Error_IncorrectLoginException() throws Exception {
        when(userService.login(USER_NAME, "wrong"))
            .thenThrow(new IncorrectLoginException(USER_NAME, "wrong"));

        perform(post("/users/login"), loginBody(USER_NAME, "wrong"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPostLogin_Error_MissingPassword() throws Exception {
        Map<String, Object> body = loginBody(USER_NAME, PASSWORD);
        body.remove("password");

        perform(post("/users/login"), body)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostLoginFromServiceToken_Ok() throws Exception {
        when(userService.loginFromId(USER_ID)).thenReturn(user());

        perform(post("/users/loginFromServiceToken"), Map.of())
            .andExpect(status().isOk());
    }

    @Test
    public void testPostLoginFromServiceToken_Ok_ServiceToken() throws Exception {
        when(userService.loginFromId(USER_ID)).thenReturn(user());

        perform(post("/users/loginFromServiceToken"), Map.of())
            .andExpect(jsonPath("$.serviceToken").value(TOKEN));
    }

    @Test
    public void testPostLoginFromServiceToken_Error_InstanceNotFoundException() throws Exception {
        when(userService.loginFromId(USER_ID))
            .thenThrow(new InstanceNotFoundException("project.entities.user", USER_ID));

        perform(post("/users/loginFromServiceToken"), Map.of())
            .andExpect(status().isNotFound());
    }

    @Test
    public void testPutUpdateProfile_Ok() throws Exception {
        when(userService.updateProfile(eq(USER_ID), anyString(), anyString(), anyString()))
            .thenReturn(user());

        perform(put("/users/{id}", USER_ID), validUpdateBody())
            .andExpect(status().isOk());
    }

    @Test
    public void testPutUpdateProfile_Ok_UpdatedData() throws Exception {
        User updated = user();
        updated.setFirstName("Nuevo");
        when(userService.updateProfile(eq(USER_ID), anyString(), anyString(), anyString()))
            .thenReturn(updated);
        Map<String, Object> body = validUpdateBody();
        body.put("firstName", "Nuevo");

        perform(put("/users/{id}", USER_ID), body)
            .andExpect(jsonPath("$.firstName").value("Nuevo"));
    }

    @Test
    public void testPutUpdateProfile_Ok_NullUserName() throws Exception {
        when(userService.updateProfile(eq(USER_ID), anyString(), anyString(), anyString()))
            .thenReturn(user());

        mockMvc.perform(put("/users/{id}", USER_ID)
                .requestAttr("userId", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"First\",\"lastName\":\"Last\",\"email\":\"user@test.com\",\"userName\":null}"))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("randomForeignIds")
    public void testPutUpdateProfile_Error_PermissionException(Long foreignId) throws Exception {
        perform(put("/users/{id}", foreignId), validUpdateBody())
            .andExpect(status().isForbidden());
    }

    @Test
    public void testPutUpdateProfile_Error_PermissionException_ServiceNotCalled() throws Exception {
        perform(put("/users/{id}", USER_ID + 1), validUpdateBody());

        verify(userService, never()).updateProfile(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    public void testPutUpdateProfile_Error_InstanceNotFoundException() throws Exception {
        when(userService.updateProfile(eq(USER_ID), anyString(), anyString(), anyString()))
            .thenThrow(new InstanceNotFoundException("project.entities.user", USER_ID));

        perform(put("/users/{id}", USER_ID), validUpdateBody())
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("validNameBoundaries")
    public void testPutUpdateProfile_Ok_LastNameBoundary(String lastName) throws Exception {
        when(userService.updateProfile(eq(USER_ID), anyString(), anyString(), anyString()))
            .thenReturn(user());
        Map<String, Object> body = validUpdateBody();
        body.put("lastName", lastName);

        perform(put("/users/{id}", USER_ID), body)
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("invalidNameBoundaries")
    public void testPutUpdateProfile_Error_LastNameBoundary(String lastName) throws Exception {
        Map<String, Object> body = validUpdateBody();
        body.put("lastName", lastName);

        perform(put("/users/{id}", USER_ID), body)
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidEmails")
    public void testPutUpdateProfile_Error_InvalidEmail(String email) throws Exception {
        Map<String, Object> body = validUpdateBody();
        body.put("email", email);

        perform(put("/users/{id}", USER_ID), body)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostChangePassword_Ok() throws Exception {
        perform(post("/users/{id}/changePassword", USER_ID), changePasswordBody(PASSWORD, NEW_PASSWORD))
            .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("validNameBoundaries")
    public void testPostChangePassword_Ok_NewPasswordBoundary(String newPassword) throws Exception {
        perform(post("/users/{id}/changePassword", USER_ID), changePasswordBody(PASSWORD, newPassword))
            .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("randomForeignIds")
    public void testPostChangePassword_Error_PermissionException(Long foreignId) throws Exception {
        perform(post("/users/{id}/changePassword", foreignId), changePasswordBody(PASSWORD, NEW_PASSWORD))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testPostChangePassword_Error_IncorrectPasswordException() throws Exception {
        doThrow(new IncorrectPasswordException())
            .when(userService).changePassword(USER_ID, "wrong", NEW_PASSWORD);

        perform(post("/users/{id}/changePassword", USER_ID), changePasswordBody("wrong", NEW_PASSWORD))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPostChangePassword_Error_InstanceNotFoundException() throws Exception {
        doThrow(new InstanceNotFoundException("project.entities.user", USER_ID))
            .when(userService).changePassword(USER_ID, PASSWORD, NEW_PASSWORD);

        perform(post("/users/{id}/changePassword", USER_ID), changePasswordBody(PASSWORD, NEW_PASSWORD))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("invalidPasswordBoundaries")
    public void testPostChangePassword_Error_NewPasswordBoundary(String newPassword) throws Exception {
        perform(post("/users/{id}/changePassword", USER_ID), changePasswordBody(PASSWORD, newPassword))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostChangePassword_Error_MissingOldPassword() throws Exception {
        Map<String, Object> body = changePasswordBody(PASSWORD, NEW_PASSWORD);
        body.remove("oldPassword");

        perform(post("/users/{id}/changePassword", USER_ID), body)
            .andExpect(status().isBadRequest());
    }

}