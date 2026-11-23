package es.udc.paproject.backend.test.integration.architecture.fixtures.model;

import es.udc.paproject.backend.model.entities.User;

/**
 * Clase de ejemplo que respeta la regla "el modelo no depende de rest":
 * solo usa clases del propio modelo.
 */
public class ModeloSinDependenciasDeRest {

    private User user;

    public User getUser() {
        return user;
    }
}
