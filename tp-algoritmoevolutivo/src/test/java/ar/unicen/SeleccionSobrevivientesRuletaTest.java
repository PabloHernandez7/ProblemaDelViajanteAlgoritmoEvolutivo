package ar.unicen;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SeleccionSobrevivientesRuletaTest {

    private static Individuo ind(List<Integer> perm) {
        return new Individuo(perm);
    }

    @Test
    void ruleta_conN0_devuelveListaVacia() {
        SeleccionSobrevivientesInterface sel = new SeleccionSobrevivientesRuleta();

        List<Individuo> poblacion = Arrays.asList(ind(Arrays.asList(0, 1)));
        List<Individuo> hijos = Arrays.asList(ind(Arrays.asList(1, 0)));

        int[][] m = {
            {0, 1},
            {2, 0}
        };

        List<Individuo> res = sel.select(poblacion, hijos, 0, m);

        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    @Test
    void ruleta_conNMayorACero_noDebeColgarse() {
        // Este test está hecho para detectar el bug actual (posible loop infinito).
        // Con tu implementación actual es muy probable que falle por timeout.
        SeleccionSobrevivientesInterface sel = new SeleccionSobrevivientesRuleta();

        List<Individuo> poblacion = Arrays.asList(
            ind(Arrays.asList(0, 1)),
            ind(Arrays.asList(1, 0))
        );

        List<Individuo> hijos = Arrays.asList(
            ind(Arrays.asList(0, 1)),
            ind(Arrays.asList(1, 0))
        );

        int[][] m = {
            {0, 1},
            {2, 0}
        };

        assertTimeoutPreemptively(Duration.ofMillis(200), () -> {
            List<Individuo> res = sel.select(poblacion, hijos, 1, m);
            assertNotNull(res);
            assertEquals(1, res.size());
        });
    }
}