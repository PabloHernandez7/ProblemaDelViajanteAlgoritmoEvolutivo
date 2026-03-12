package ar.unicen;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SeleccionPadresTorneo implements SeleccionPadresInterface {

    private final int tamañoTorneo; //el algoritmo dice el tamaño, es configurables
    private final Random random;

    public SeleccionPadresTorneo(int tamañoTorneo, Random rm) {
        this.tamañoTorneo = tamañoTorneo;
        this.random = rm;
    }

    @Override
    public List<Individuo> select(List<Individuo> poblacion, int[][] matriz) {
        if (tamañoTorneo > poblacion.size()) {
        throw new IllegalArgumentException(
            "El tamaño del torneo (" + tamañoTorneo + ") no puede superar " +
            "el tamaño de la poblacion (" + poblacion.size() + ")"
        );
    }
        return new ArrayList<>(Arrays.asList(seleccionarUno(poblacion,matriz),seleccionarUno(poblacion,matriz)));
    }

    private Individuo seleccionarUno(List<Individuo> poblacion, int[][] matriz) {
        Individuo mejor = null;
        List<Individuo> copia = new ArrayList<>(poblacion);
        Collections.shuffle(copia, random);
        for (int i = 0; i < tamañoTorneo; i++) {
            Individuo candidato = copia.get(i);
            if (mejor == null || candidato.getFitness(matriz) < mejor.getFitness(matriz)) {
                mejor = candidato;
            }
        }

        return mejor;
    }

    public int getTamañoTorneo() {
        return tamañoTorneo;
    }
}
