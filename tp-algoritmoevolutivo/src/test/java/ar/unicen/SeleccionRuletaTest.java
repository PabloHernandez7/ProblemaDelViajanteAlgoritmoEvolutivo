package ar.unicen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeleccionRuletaTest {

    private int[][] matriz;
    private List<Individuo> poblacion;

    @BeforeEach
    void setUp() {
        matriz = new int[][]{
            {0, 10, 20, 30},
            {10, 0, 15, 25},
            {20, 15, 0, 10},
            {30, 25, 10, 0}
        };

        // Individuo 1: ruta [0,1,2,3] → costo = 35 (mejor)
        // Individuo 2: ruta [0,2,1,3] → costo = 60 (peor)
        // Individuo 3: ruta [0,3,2,1] → costo = 55
        // Individuo 4: ruta [0,1,3,2] → costo = 45
        poblacion = new ArrayList<>();
        poblacion.add(new Individuo(Arrays.asList(0, 1, 2, 3)));
        poblacion.add(new Individuo(Arrays.asList(0, 2, 1, 3)));
        poblacion.add(new Individuo(Arrays.asList(0, 3, 2, 1)));
        poblacion.add(new Individuo(Arrays.asList(0, 1, 3, 2)));
    }

    @Test
    void testSelectDevuelveDosPadres() {
        SeleccionRuleta seleccion = new SeleccionRuleta();
        List<Individuo> padres = seleccion.select(poblacion, matriz);
        assertEquals(2, padres.size(), "Debe devolver exactamente 2 padres");
    }

    @Test
    void testSelectNullEnPadres() {
        SeleccionRuleta seleccion = new SeleccionRuleta();
        List<Individuo> padres = seleccion.select(poblacion, matriz);
        assertNotNull(padres.get(0), "El primer padre no debe ser null");
        assertNotNull(padres.get(1), "El segundo padre no debe ser null");
    }

    @Test
    void testSelectPadresPerteneceAPoblacion() {
        SeleccionRuleta seleccion = new SeleccionRuleta();
        List<Individuo> padres = seleccion.select(poblacion, matriz);
        assertTrue(poblacion.contains(padres.get(0)), "El padre 1 debe pertenecer a la población");
        assertTrue(poblacion.contains(padres.get(1)), "El padre 2 debe pertenecer a la población");
    }

    @Test
    void testMejorIndividuoEsSeleccionadoConMayorFrecuencia() {
        // El individuo con costo 35 (1/35 ≈ 0.0286) tiene mayor probabilidad
        // que el de costo 60 (1/60 ≈ 0.0167). En muchas tiradas debe aparecer más.
        SeleccionRuleta seleccion = new SeleccionRuleta();
        Individuo mejor = poblacion.get(0); // costo 35

        int conteoMejor = 0;
        int iteraciones = 1000;

        for (int i = 0; i < iteraciones; i++) {
            List<Individuo> padres = seleccion.select(poblacion, matriz);
            if (padres.get(0) == mejor) conteoMejor++;
            if (padres.get(1) == mejor) conteoMejor++;
        }

        // El mejor debería aparecer bastante más que 1/4 del total (probabilidad uniforme sería 500/2000)
        // Con inversión de fitness su prob real es ~29%, así que esperamos bastante más que 25%
        double frecuencia = (double) conteoMejor / (iteraciones * 2);
        assertTrue(frecuencia > 0.20,
            "El mejor individuo debería ser seleccionado con frecuencia mayor al 20%, fue: " + frecuencia);
    }

    @Test
    void testPoblacionDeUnElemento() {
        List<Individuo> unico = new ArrayList<>();
        unico.add(new Individuo(Arrays.asList(0, 1, 2, 3)));
        SeleccionRuleta seleccion = new SeleccionRuleta();
        List<Individuo> padres = seleccion.select(unico, matriz);
        assertEquals(2, padres.size());
        assertEquals(unico.get(0), padres.get(0));
        assertEquals(unico.get(0), padres.get(1));
    }

    @Test
    void testSeleccionConIndividuosIgualesFitness() {
        // Todos con el mismo costo → probabilidades iguales, no debe lanzar excepción
        List<Individuo> iguales = new ArrayList<>();
        iguales.add(new Individuo(Arrays.asList(0, 1, 2, 3))); // costo 35
        iguales.add(new Individuo(Arrays.asList(0, 1, 2, 3))); // costo 35
        iguales.add(new Individuo(Arrays.asList(0, 1, 2, 3))); // costo 35

        SeleccionRuleta seleccion = new SeleccionRuleta();
        assertDoesNotThrow(() -> seleccion.select(iguales, matriz));

        List<Individuo> padres = seleccion.select(iguales, matriz);
        assertEquals(2, padres.size());
    }
}
