package es.udc.paproject.backend.test.model.services.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PermissionCheckerImpl;

@ExtendWith(MockitoExtension.class)
class PermissionCheckerImplUnitTest {

    private static final Long USER_ID = 1L;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private PermissionCheckerImpl permissionChecker;

    @Test
    void checkUserExistenteDevuelveElUsuario() throws Exception {
        User user = new User("viewer", "password", "First", "Last", "viewer@test.com");
        when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        assertSame(user, permissionChecker.checkUser(USER_ID));
    }

    @Test
    void checkUserInexistenteLanzaInstanceNotFound() {
        when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> permissionChecker.checkUser(USER_ID));
    }
}
