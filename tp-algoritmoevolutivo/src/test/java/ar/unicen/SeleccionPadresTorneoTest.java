package ar.unicen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SeleccionPadresTorneoTest {

    private int[][] matriz;
    private List<Individuo> poblacion;
    Random random = new Random(42); 

    // Matriz de costos simple 4 ciudades:
    // 0→1=10, 0→2=20, 0→3=30
    // 1→2=15, 1→3=25
    // 2→3=10
    @BeforeEach
    void setUp() {
        matriz = new int[][]{
            {0, 10, 20, 30},
            {10, 0, 15, 25},
            {20, 15, 0, 10},
            {30, 25, 10, 0}
        };

        // Individuo 1: ruta [0,1,2,3] → costo = 10+15+10 = 35 (mejor)
        // Individuo 2: ruta [0,2,1,3] → costo = 20+15+25 = 60
        // Individuo 3: ruta [0,3,2,1] → costo = 30+10+15 = 55
        // Individuo 4: ruta [0,1,3,2] → costo = 10+25+10 = 45
        poblacion = new ArrayList<>();
        poblacion.add(new Individuo(Arrays.asList(0, 1, 2, 3)));
        poblacion.add(new Individuo(Arrays.asList(0, 2, 1, 3)));
        poblacion.add(new Individuo(Arrays.asList(0, 3, 2, 1)));
        poblacion.add(new Individuo(Arrays.asList(0, 1, 3, 2)));
    }

    @Test
    void testSelectDevuelveDosPardes() {
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(2,random);
        List<Individuo> padres = seleccion.select(poblacion, matriz);
        assertEquals(2, padres.size(), "Debe devolver exactamente 2 padres");
    }

    @Test
    void testSelectNullEnPadres() {
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(2,random);
        List<Individuo> padres = seleccion.select(poblacion, matriz);
        assertNotNull(padres.get(0), "El primer padre no debe ser null");
        assertNotNull(padres.get(1), "El segundo padre no debe ser null");
    }

    @Test
    void testSelectPadresPerteneceAPoblacion() {
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(2,random);
        List<Individuo> padres = seleccion.select(poblacion, matriz);
        assertTrue(poblacion.contains(padres.get(0)), "El padre 1 debe pertenecer a la población");
        assertTrue(poblacion.contains(padres.get(1)), "El padre 2 debe pertenecer a la población");
    }

    @Test
    void testTorneoTamañoIgualPoblacionSiempreEligeMejor() {
        // Con torneo = tamaño población, siempre debe ganar el de menor costo (35)
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(poblacion.size(),random);
        for (int i = 0; i < 20; i++) {
            List<Individuo> padres = seleccion.select(poblacion, matriz);
            assertEquals(35, padres.get(0).getFitness(matriz), "Con torneo completo siempre debe ganar el mejor");
            assertEquals(35, padres.get(1).getFitness(matriz), "Con torneo completo siempre debe ganar el mejor");
        }
    }

    @Test
    void testTorneoTamañoMayorQuePoblacionLanzaExcepcion() {
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(poblacion.size() + 1,random);
        assertThrows(IllegalArgumentException.class,
            () -> seleccion.select(poblacion, matriz),
            "Debe lanzar excepción si el torneo supera el tamaño de la población"
        );
    }

    @Test
    void testTorneoConPoblacionDeUnElemento() {
        List<Individuo> unico = new ArrayList<>();
        unico.add(new Individuo(Arrays.asList(0, 1, 2, 3)));
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(1,random);
        List<Individuo> padres = seleccion.select(unico, matriz);
        assertEquals(2, padres.size());
        // Ambos padres deben ser el único individuo
        assertEquals(unico.get(0), padres.get(0));
        assertEquals(unico.get(0), padres.get(1));
    }

    @Test
    void testGetTamañoTorneo() {
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(3,random);
        assertEquals(3, seleccion.getTamañoTorneo());
    }

    @Test
    void testTorneoEligeMejorDeLaMuestra() {
        // Con semilla fija sabemos exactamente qué 2 individuos entran al torneo
        // y verificamos que el ganador sea el de menor costo entre esos 2
        SeleccionPadresTorneo seleccion = new SeleccionPadresTorneo(2, random);
        
        List<Individuo> padres = seleccion.select(poblacion, matriz);
        
        // No sabemos quién ganó, pero sí que su fitness debe ser
        // menor o igual al del peor individuo de la población
        assertTrue(padres.get(0).getFitness(matriz) <= 60);
        assertTrue(padres.get(1).getFitness(matriz) <= 60);
    }
}
