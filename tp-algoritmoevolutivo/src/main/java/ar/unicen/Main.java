package ar.unicen;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static class ConfiguracionAG {
        int nInd;
        int maxGen;
        double probCruce;
        double probMut;
        String selPadres;
        int kTorneo;
        String cruce;
        String mutacion;
        String selSobrevivientes;
        int nSteady;
    }

    public static void main(String[] args) {
        Random rm = new Random();
        Scanner s = new Scanner(System.in);

        System.out.println("Escribir el nombre del archivo ATSP (ej: reto_15.atsp): ");
        String nombreArchivoATSP = s.nextLine();

        System.out.println("Escribir el nombre del archivo JSON de configuraciones (ej: configs.json): ");
        String nombreArchivoJSON = s.nextLine();
        s.close();

        Path pathATSP = Paths.get(nombreArchivoATSP);
        int[][] matrizCostos;
        int nCities;

        try {
            ATSPInstance inst = TSPLibATSPParser.parse(pathATSP);
            matrizCostos = inst.getCost();
            nCities = inst.getDimension();
            System.out.println("Instancia cargada: " + inst.getName() + " (" + nCities + " ciudades)");
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo ATSP: " + e.getMessage());
            return;
        }

        List<ConfiguracionAG> listaConfigs = leerConfiguracionesJSON(nombreArchivoJSON);
        if (listaConfigs.isEmpty()) {
            System.out.println("No se encontraron configuraciones válidas en el JSON.");
            return;
        }

        int N = 5; 

        String nombreBase = Paths.get(nombreArchivoATSP).getFileName().toString();
        String nombreProblema = nombreBase.contains(".") ? 
                                nombreBase.substring(0, nombreBase.lastIndexOf('.')) : 
                                nombreBase;

        String directorioSalida = "target/results/results_" + nombreProblema + "/";

        try {
            Files.createDirectories(Paths.get(directorioSalida));
        } catch (IOException e) {
            System.out.println("Error al crear el directorio de salida: " + e.getMessage());
            return;
        }

        List<ResumenConfiguracion> rankingGlobal = new ArrayList<>();

        for (int i = 0; i < listaConfigs.size(); i++) {
            ConfiguracionAG config = listaConfigs.get(i);
            System.out.println("\n   Iniciando Configuración " + (i + 1) + " de " + listaConfigs.size() + "   ");
            
            SeleccionPadresInterface selPadres = factorySelPadres(config.selPadres, rm, config.kTorneo);
            CruceInterface cruce = factoryCruce(config.cruce);
            MutacionInterface mutacion = factoryMutacion(config.mutacion);
            SeleccionSobrevivientesInterface selSobrevivientes = factorySelSobrevivientes(config.selSobrevivientes, config.nSteady);

            List<ResultadoCorrida> resultadosDeConfiguracion = new ArrayList<>();

            for (int j = 0; j < N; j++) {
                System.out.println("  Ejecutando corrida " + (j + 1) + " de " + N + "...");
                List<Individuo> poblacionInicial = generarPoblacionInicial(nCities, config.nInd);

                AlgoritmoViajante algoritmo = new AlgoritmoViajante(
                        config.maxGen, config.probMut, config.probCruce, 
                        selPadres, cruce, mutacion, selSobrevivientes, matrizCostos, rm);

                ResultadoCorrida resultado = algoritmo.ejecutar(poblacionInicial);
                resultadosDeConfiguracion.add(resultado);
            }

            double promF = resultadosDeConfiguracion.stream().mapToLong(ResultadoCorrida::getMejorFitness).average().orElse(0);
            double promS = resultadosDeConfiguracion.stream().mapToLong(ResultadoCorrida::getSolucionesGeneradas).average().orElse(0);
            double sumaDif = 0;
            for(ResultadoCorrida r : resultadosDeConfiguracion) {
                sumaDif += Math.pow(r.getMejorFitness() - promF, 2);
            }
            double desv = (N > 1) ? Math.sqrt(sumaDif / (N - 1)) : 0;

            rankingGlobal.add(new ResumenConfiguracion(i + 1, config, promF, desv, promS));

            String nombreArchivoExcel = generarNombreArchivo(config, i + 1);
            String rutaFinal = directorioSalida + nombreArchivoExcel;
            
            ExportadorResultados.exportarConsolidado(resultadosDeConfiguracion, matrizCostos, rutaFinal, config);
        }

        String rutaRanking = directorioSalida + "Ranking_Final_" + nombreProblema + ".xlsx";
        ExportadorResultados.exportarRanking(rankingGlobal, rutaRanking);

        System.out.println("\nProceso finalizado. Los archivos se guardaron en la carpeta: " + directorioSalida);
    }
  
    private static List<ConfiguracionAG> leerConfiguracionesJSON(String nombreArchivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(nombreArchivo)));
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ConfiguracionAG>>(){}.getType();
            return gson.fromJson(contenido, listType);
        } catch (Exception e) {
            System.err.println("Error leyendo el archivo JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static SeleccionPadresInterface factorySelPadres(String nombre, Random rm, int kTorneo) {
        if (nombre.equalsIgnoreCase("Torneo")) return new SeleccionPadresTorneo(kTorneo, rm); 
        if (nombre.equalsIgnoreCase("Ruleta")) return new SeleccionRuleta();
        throw new IllegalArgumentException("Seleccion de Padres no soportada: " + nombre);
    }

    private static CruceInterface factoryCruce(String nombre) {
        if (nombre.equalsIgnoreCase("Orden")) return new CruceOrden();
        if (nombre.equalsIgnoreCase("PMX")) return new CrucePMX();
        throw new IllegalArgumentException("Crossover no soportado: " + nombre);
    }

    private static MutacionInterface factoryMutacion(String nombre) {
        if (nombre.equalsIgnoreCase("Mezcla")) return new MutacionMezcla();
        if (nombre.equalsIgnoreCase("Intercambio")) return new MutacionIntercambio();
        throw new IllegalArgumentException("Mutación no soportada: " + nombre);
    }

    private static SeleccionSobrevivientesInterface factorySelSobrevivientes(String nombre, int nSteady) {
        if (nombre.equalsIgnoreCase("Ruleta")) return new SeleccionSobrevivientesRuleta();
        if (nombre.equalsIgnoreCase("Steady")) return new SeleccionSobrevivientesSteady(nSteady); 
        throw new IllegalArgumentException("Selección de Sobrevivientes no soportada: " + nombre);
    }

    private static String generarNombreArchivo(ConfiguracionAG c, int id) {
        return String.format("Conf%d_Pop%d_Gen%d_Cx%.2f_Mut%.2f_%s_%s.xlsx", 
                id, c.nInd, c.maxGen, c.probCruce, c.probMut, c.cruce, c.mutacion);
    }

    private static List<Individuo> generarPoblacionInicial(int nCities, int nInd) {
        List<Individuo> poblacion = new ArrayList<>();
        ArrayList<Integer> base = new ArrayList<>(nCities);
        for (int i = 0; i < nCities; i++) base.add(i);

        for (int i = 0; i < nInd; i++) {
            Individuo ind = new Individuo(new ArrayList<>(base));
            ind.permutar();
            poblacion.add(ind);
        }
        return poblacion;
    }
}