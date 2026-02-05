package ar.unicen;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SeleccionSobrevivientesSteadyTest {

    private static Individuo ind(List<Integer> perm) {
        return new Individuo(perm);
    }

    /**
     * Matriz elegida para que:
     * - perm [0,1] tenga costo 1 => fitness = 1/1 = 1
     * - perm [1,0] tenga costo 2 => fitness = 1/2 = 0 (por división entera en tu Individuo)
     */
    private static int[][] matrizSimple() {
        return new int[][]{
            {0, 1},
            {2, 0}
        };
    }

    @Test
    void steady_reemplazaNPeroresDePoblacionPorMejoresHijos() {
        SeleccionSobrevivientesInterface sel = new SeleccionSobrevivientesSteady();
        int[][] m = matrizSimple();

        // Población: 2 buenos (fitness=1) y 2 malos (fitness=0)
        List<Individuo> poblacion = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)), // bueno
            ind(Arrays.asList(1, 0)), // malo
            ind(Arrays.asList(1, 0)), // malo
            ind(Arrays.asList(0, 1))  // bueno
        ));

        // Hijos: 2 buenos
        List<Individuo> hijos = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)), // bueno
            ind(Arrays.asList(0, 1))  // bueno
        ));

        List<Individuo> res = sel.select(poblacion, hijos, 2, m);

        // En steady, el tamaño final debe ser el de la población.
        assertEquals(poblacion.size(), res.size(), "El steady debe devolver una población del mismo tamaño");

        // Esperado: luego de reemplazar 2 peores por 2 buenos, deberían quedar 4 fitness=1
        long cantidadFitness1 = res.stream().filter(i -> i.getFitness(m) == 1).count();
        assertEquals(4, cantidadFitness1,
            "Deberían quedar todos con fitness=1 tras reemplazar los 2 peores por 2 hijos buenos (si el steady está bien)");
    }

    @Test
    void steady_lanzaError_siNoHaySuficientesHijos() {
        SeleccionSobrevivientesInterface sel = new SeleccionSobrevivientesSteady();
        int[][] m = matrizSimple();

        List<Individuo> poblacion = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)),
            ind(Arrays.asList(1, 0))
        ));

        List<Individuo> hijos = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)) // solo 1 hijo
        ));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            sel.select(poblacion, hijos, 2, m)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("hijos"));
    }

    @Test
    void steady_lanzaError_siNoHaySuficientePoblacion() {
        SeleccionSobrevivientesInterface sel = new SeleccionSobrevivientesSteady();
        int[][] m = matrizSimple();

        List<Individuo> poblacion = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)) // solo 1
        ));

        List<Individuo> hijos = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)),
            ind(Arrays.asList(0, 1))
        ));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            sel.select(poblacion, hijos, 2, m)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("poblacion"));
    }
}
