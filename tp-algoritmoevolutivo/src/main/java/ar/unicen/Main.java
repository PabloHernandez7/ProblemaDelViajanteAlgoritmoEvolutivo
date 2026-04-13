package ar.unicen;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Configuración de hiperparámetros
        int nInd = 100;
        double probMut = 0.1;
        double probCruce = 0.8;
        int maxGen = 1000;
        Random rm = new Random();

        // 2. Lectura del archivo ATSP
        Scanner s = new Scanner(System.in);
        System.out.println("Escribir el nombre del archivo que desee cargar + su extensión: ");
        String nombreArchivo = s.nextLine();
        s.close();

        Path path = Paths.get(nombreArchivo);
        int[][] matrizCostos;
        int nCities;

        try {
            // Uso del parser para obtener la instancia del problema
            ATSPInstance inst = TSPLibATSPParser.parse(path);
            matrizCostos = inst.getCost();
            nCities = inst.getDimension();
            System.out.println("Archivo cargado: " + inst.getName() + " (Dimension: " + nCities + ")");
        } catch (IOException e) {
            System.out.println("Error crítico al buscar el archivo: " + e.getMessage());
            return;
        }

        // 3. Configuración de variantes a evaluar
        SeleccionPadresInterface selPadres = new SeleccionPadresTorneo(10, rm);
        CruceInterface cruce = new CruceOrden();
        MutacionInterface mutacion = new MutacionMezcla();
        SeleccionSobrevivientesInterface selSobrevivientes = new SeleccionSobrevivientesRuleta();

        // 4. Ejecución del Diseño Experimental
        int N = 5; // Mínimo pedido por la cátedra
        List<ResultadoCorrida> todasLasCorridas = new ArrayList<>();

        System.out.println("Iniciando evaluación experimental (" + N + " corridas)...");

        for (int i = 0; i < N; i++) {
            System.out.println("Ejecutando corrida " + (i + 1) + " de " + N + "...");

            // Generamos una población inicial nueva e independiente para cada corrida
            List<Individuo> poblacionInicial = generarPoblacionInicial(nCities, nInd);

            // Instanciamos el algoritmo nuevamente para asegurar que el estado (generaciones) empiece de cero
            AlgoritmoViajante algoritmo = new AlgoritmoViajante(
                    maxGen, probMut, probCruce, selPadres, cruce, mutacion, selSobrevivientes, matrizCostos, rm); 
                    // NOTA: Si modificaste tu constructor para pedir 'rm' al final, agrégalo aquí.

            ResultadoCorrida resultado = algoritmo.ejecutar(poblacionInicial);
            todasLasCorridas.add(resultado);
        }

        // 5. Consolidación de datos
        ExportadorResultados.exportarConsolidado(todasLasCorridas, "Evaluacion_Experimental.xlsx");
        System.out.println("Evaluación finalizada con éxito. Excel generado.");
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Genera una lista de N individuos con permutaciones aleatorias.
     */
    private static List<Individuo> generarPoblacionInicial(int nCities, int nInd) {
        List<Individuo> poblacion = new ArrayList<>();
        ArrayList<Integer> base = new ArrayList<>(nCities);
        
        for (int i = 0; i < nCities; i++) {
            base.add(i);
        }

        for (int i = 0; i < nInd; i++) {
            Individuo ind = new Individuo(new ArrayList<>(base));
            ind.permutar(); // Mezcla aleatoria
            poblacion.add(ind);
        }
        return poblacion;
    }
}