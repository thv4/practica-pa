package es.udc.paproject.backend.test.model.services.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.model.services.Block;

class BlockUnitTest {

    private static final List<String> ITEMS = List.of("a", "b");

    @Test
    void getItemsDevuelveLosElementos() {
        assertEquals(ITEMS, new Block<>(ITEMS, true).getItems());
    }

    @Test
    void getExistMoreItemsDevuelveSiHayMasElementos() {
        assertTrue(new Block<>(ITEMS, true).getExistMoreItems());
    }

    @Test
    void equalsConLaMismaInstanciaEsCierto() {
        Block<String> block = new Block<>(ITEMS, true);
        assertTrue(block.equals(block));
    }

    @Test
    void equalsConNuloEsFalso() {
        assertFalse(new Block<>(ITEMS, true).equals(null));
    }

    @Test
    void equalsConObjetoDeOtraClaseEsFalso() {
        assertFalse(new Block<>(ITEMS, true).equals("otro"));
    }

    @Test
    void equalsConDistintoExistMoreItemsEsFalso() {
        assertFalse(new Block<>(ITEMS, true).equals(new Block<>(ITEMS, false)));
    }

    @Test
    void equalsConItemsNulosFrenteANoNulosEsFalso() {
        assertFalse(new Block<String>(null, true).equals(new Block<>(ITEMS, true)));
    }

    @Test
    void equalsConAmbosItemsNulosEsCierto() {
        assertTrue(new Block<String>(null, true).equals(new Block<String>(null, true)));
    }

    @Test
    void equalsConItemsDistintosEsFalso() {
        assertFalse(new Block<>(ITEMS, true).equals(new Block<>(List.of("c"), true)));
    }

    @Test
    void equalsConMismosItemsYExistMoreItemsEsCierto() {
        assertTrue(new Block<>(ITEMS, true).equals(new Block<>(List.of("a", "b"), true)));
    }

    @Test
    void hashCodeEsIgualParaBloquesIguales() {
        assertEquals(new Block<>(ITEMS, true).hashCode(), new Block<>(List.of("a", "b"), true).hashCode());
    }

    @Test
    void hashCodeEsIgualParaBloquesConItemsNulos() {
        assertEquals(new Block<String>(null, false).hashCode(), new Block<String>(null, false).hashCode());
    }

    @Test
    void hashCodeDistingueExistMoreItems() {
        assertNotEquals(new Block<>(ITEMS, true).hashCode(), new Block<>(ITEMS, false).hashCode());
    }
}
