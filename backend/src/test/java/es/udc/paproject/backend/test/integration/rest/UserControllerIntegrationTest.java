package es.udc.paproject.backend.test.integration.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.User.RoleType;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.rest.controllers.UserController;
import es.udc.paproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.paproject.backend.rest.dtos.ChangePasswordParamsDto;
import es.udc.paproject.backend.rest.dtos.LoginParamsDto;
import es.udc.paproject.backend.rest.dtos.UserDto;
import jakarta.transaction.Transactional;

@SpringBootTest 
@AutoConfigureMockMvc 
@ActiveProfiles("test")
@Transactional 
public class UserControllerIntegrationTest {

    /** The Constant PASSWORD. */
	private final static String PASSWORD = "password";

	/** The non existent id. */
	private static final Long NON_EXISTENT_ID = 9_000_042L;

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The password encoder. */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

	/** The user dao. */
	@Autowired
	private UserDao userDao;

	/** The user controller. */
	@Autowired
	private UserController userController;

	/**
	 * Creates the authenticated user.
	 *
	 * @param userName the user name
	 * @param roleType the role type
	 * @return the authenticated user dto
	 * @throws IncorrectLoginException the incorrect login exception
	 */
	private AuthenticatedUserDto createAuthenticatedUser(String userName, RoleType roleType)
			throws IncorrectLoginException {

		User user = new User(userName, PASSWORD, "newUser", "user", "user@test.com");

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(roleType);

		userDao.save(user);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUserName(user.getUserName());
		loginParams.setPassword(PASSWORD);

		return userController.login(loginParams);

	}

	/**
	 * Test post login ok.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testPostLogin_Ok() throws Exception {

		AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.ESPECTADOR);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUserName(user.getUserDto().getUserName());
		loginParams.setPassword(PASSWORD);

		mockMvc.perform(post("/users/login").header("Authorization", "Bearer " + user.getServiceToken())
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(loginParams)))
				.andExpect(status().isOk());

	}

    /** 
     * @throws Exception
     */
    @Test
    public void testPostLogin_Error() throws Exception {

        AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.ESPECTADOR);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUserName(user.getUserDto().getUserName());
		loginParams.setPassword("IncorrectPassword");

		mockMvc.perform(post("/users/login").header("Authorization", "Bearer " + user.getServiceToken())
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(loginParams)))
				.andExpect(status().isUnauthorized());
        
    }

    @Test
    public void testPutUpdateProfile_Ok() throws Exception {

        AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.ESPECTADOR);

        UserDto userDto = new UserDto();
        userDto.setFirstName("New First Name");
        userDto.setLastName("New Last Name");
        userDto.setEmail("new.email@example.com");

        mockMvc.perform(put("/users/{id}", user.getUserDto().getId())
                .header("Authorization", "Bearer " + user.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPutUpdateProfile_Error_PermissionException() throws Exception {

        AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.ESPECTADOR);

        UserDto userDto = new UserDto();
        userDto.setFirstName("New First Name");
        userDto.setLastName("New Last Name");
        userDto.setEmail("new.email@example.com");

        mockMvc.perform(put("/users/{id}", NON_EXISTENT_ID)
                .header("Authorization", "Bearer " + user.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testPostChangePassword_Ok() throws Exception {

        AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.ESPECTADOR);

        ChangePasswordParamsDto changePasswordParamsDto = new ChangePasswordParamsDto();
        changePasswordParamsDto.setOldPassword(PASSWORD);
        changePasswordParamsDto.setNewPassword("newPassword");

        mockMvc.perform(post("/users/{id}/changePassword", user.getUserDto().getId())
                .header("Authorization", "Bearer " + user.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(changePasswordParamsDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testPostChangePassword_Error_PermissionException() throws Exception {

        AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.ESPECTADOR);

        ChangePasswordParamsDto changePasswordParamsDto = new ChangePasswordParamsDto();
        changePasswordParamsDto.setOldPassword(PASSWORD);
        changePasswordParamsDto.setNewPassword("newPassword");

        mockMvc.perform(post("/users/{id}/changePassword", NON_EXISTENT_ID)
                .header("Authorization", "Bearer " + user.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(changePasswordParamsDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testPostChangePassword_Error_IncorrectPasswordException() throws Exception {
        
        AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.ESPECTADOR);

        ChangePasswordParamsDto changePasswordParamsDto = new ChangePasswordParamsDto();
        changePasswordParamsDto.setOldPassword("IncorrectPassword");
        changePasswordParamsDto.setNewPassword("newPassword");

        mockMvc.perform(post("/users/{id}/changePassword", user.getUserDto().getId())
                .header("Authorization", "Bearer " + user.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(changePasswordParamsDto)))
                .andExpect(status().isUnauthorized());
    }
    
}
