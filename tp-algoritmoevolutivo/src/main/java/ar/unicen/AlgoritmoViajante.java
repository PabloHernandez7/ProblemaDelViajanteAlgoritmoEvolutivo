package ar.unicen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlgoritmoViajante {
    private int maxGen;
    private double probMut;
    private double probCruce;
    private SeleccionPadresInterface seleccionPadres;
    private CruceInterface cruce;
    private MutacionInterface mutacion;
    private SeleccionSobrevivientesInterface seleccionSobrevivientes;
    private int[][] matrizCostos;
    private Random rm;

    public AlgoritmoViajante(int maxGen, double probMut, double probCruce,
            SeleccionPadresInterface seleccionPadres,
            CruceInterface cruce,
            MutacionInterface mutacion,
            SeleccionSobrevivientesInterface seleccionSobrevivientes,
            int[][] matrizCostos, Random rm) {
        this.maxGen = maxGen;
        this.probMut = probMut;
        this.probCruce = probCruce;
        this.seleccionPadres = seleccionPadres;
        this.cruce = cruce;
        this.mutacion = mutacion;
        this.seleccionSobrevivientes = seleccionSobrevivientes;
        this.matrizCostos = matrizCostos;
        this.rm = rm;
    }

    public ResultadoCorrida ejecutar(List<Individuo> poblacionInicial) {
        List<Individuo> poblacion = new ArrayList<>(poblacionInicial);
        List<Long> historial = new ArrayList<>();
        long solucionesGeneradas = poblacionInicial.size();

        for (int gen = 0; gen < maxGen; gen++) {
            List<Individuo> padres = seleccionPadres.select(poblacion, matrizCostos);
            List<Individuo> hijos = new ArrayList<>();

            for (int i = 0; i < padres.size() - 1; i += 2) {
                if (rm.nextDouble() < probCruce) {
                    hijos.add(cruce.cruzar(padres.get(i), padres.get(i + 1), rm));
                    solucionesGeneradas++;
                } else {
                    hijos.add(padres.get(i));
                    hijos.add(padres.get(i + 1));
                }
            }

            for (Individuo hijo : hijos) {
                if (rm.nextDouble() < probMut) {
                    mutacion.mutate(hijo, rm);
                }
            }

            poblacion = seleccionSobrevivientes.select(poblacion, hijos, poblacionInicial.size(), matrizCostos);

            // Registrar el mejor de esta generación para la curva de evolución
            historial.add(obtenerMejorFitness(poblacion));
        }

        return new ResultadoCorrida(historial.get(historial.size() - 1), solucionesGeneradas, historial);
    }

    private long obtenerMejorFitness(List<Individuo> p) {
        long mejor = p.get(0).getFitness(matrizCostos);
        for (Individuo ind : p) {
            if (ind.getFitness(matrizCostos) < mejor)
                mejor = ind.getFitness(matrizCostos);
        }
        return mejor;
    }
}