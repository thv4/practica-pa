package es.udc.paproject.backend.test.integration.architecture.fixtures.model;

import es.udc.paproject.backend.rest.dtos.UserDto;

/**
 * Clase de ejemplo que viola a propósito la regla "el modelo no depende de rest".
 * Solo se usa para comprobar que la regla ArchUnit la detecta
 */
public class ModeloQueDependeDeRest {

    private UserDto userDto;

    public UserDto getUserDto() {
        return userDto;
    }
}
