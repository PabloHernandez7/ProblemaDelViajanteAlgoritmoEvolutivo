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
     * - perm [1,0] tenga costo 2 => fitness = 1/2 = 0 (por división entera)
     */
    private static int[][] matrizSimple() {
        return new int[][]{
            {0, 1},
            {2, 0}
        };
    }

    @Test
    void steady_reemplazaNPeroresDePoblacionPorMejoresHijos() {
        SeleccionSobrevivientesInterface sel = new SeleccionSobrevivientesSteady(2);
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

        // El parámetro 4 es el tamaño esperado de la población final
        List<Individuo> res = sel.select(poblacion, hijos, 4, m);

        // En steady, el tamaño final debe ser el de la población original
        assertEquals(poblacion.size(), res.size(), "El steady debe devolver una población del mismo tamaño");

        // Esperado: luego de reemplazar los 2 peores por los 2 buenos, deberían quedar todos con fitness=1
        long cantidadFitness1 = res.stream().filter(i -> i.getFitness(m) == 1).count();
        assertEquals(4, cantidadFitness1,
            "Deberían quedar todos con fitness=1 tras reemplazar los 2 peores por 2 hijos buenos");
    }

    @Test
    void steady_noLanzaError_yReemplazaSoloLosHijosDisponibles() {
        // Le pedimos que intente reemplazar 2 individuos (nSteady = 2)
        SeleccionSobrevivientesInterface sel = new SeleccionSobrevivientesSteady(2);
        int[][] m = matrizSimple();

        // Población inicial: 2 buenos y 2 malos
        List<Individuo> poblacion = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)), // bueno
            ind(Arrays.asList(1, 0)), // malo
            ind(Arrays.asList(1, 0)), // malo
            ind(Arrays.asList(0, 1))  // bueno
        ));

        // PERO solo le pasamos 1 hijo bueno (Menos cantidad que el nSteady)
        List<Individuo> hijos = new ArrayList<>(Arrays.asList(
            ind(Arrays.asList(0, 1)) // bueno
        ));

        // Esto antes explotaba con IllegalStateException. Ahora debe procesar sin problemas.
        List<Individuo> res = sel.select(poblacion, hijos, 4, m);

        assertEquals(4, res.size(), "El steady debe mantener el tamaño total de la población");

        // Verificamos la lógica matemática:
        // Tenía 2 buenos. Recibe 1 bueno nuevo que pisa a 1 malo. Debería terminar con 3 buenos.
        long cantidadFitness1 = res.stream().filter(i -> i.getFitness(m) == 1).count();
        assertEquals(3, cantidadFitness1, 
            "Debería haber 3 individuos con fitness=1 porque solo había 1 hijo para reemplazar a 1 malo");
    }
}