package ar.unicen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SeleccionRuleta implements SeleccionPadresInterface {

    private final Random random;

    public SeleccionRuleta() {
        this.random = new Random();
    }

    @Override
    public List<Individuo> select(List<Individuo> poblacion, int[][] matriz) {
        double[] probabilidades = calcularProbabilidades(poblacion, matriz);
        return new ArrayList<>(Arrays.asList(seleccionarUno(poblacion, probabilidades),seleccionarUno(poblacion, probabilidades)));
    }    

    private double[] calcularProbabilidades(List<Individuo> poblacion, int[][] matriz) {
        double[] invertido = new double[poblacion.size()];
        double sumaTotal = 0.0;

        for (int i = 0; i < poblacion.size(); i++) {
            // Menor distancia = mayor fitness → invertimos para la ruleta
            invertido[i] = 1.0 / poblacion.get(i).getFitness(matriz);
            sumaTotal += invertido[i];
        }

        double[] probabilidades = new double[poblacion.size()];
        for (int i = 0; i < poblacion.size(); i++) {
            probabilidades[i] = invertido[i] / sumaTotal;
        }

        return probabilidades;
    }

    private Individuo seleccionarUno(List<Individuo> poblacion, double[] probabilidades) {
        double ruleta = random.nextDouble();
        double acumulado = 0.0;

        for (int i = 0; i < poblacion.size(); i++) {
            acumulado += probabilidades[i];
            if (ruleta <= acumulado) {
                return poblacion.get(i);
            }
        }

        // Fallback por errores de punto flotante
        return poblacion.get(poblacion.size() - 1);
    }
}
