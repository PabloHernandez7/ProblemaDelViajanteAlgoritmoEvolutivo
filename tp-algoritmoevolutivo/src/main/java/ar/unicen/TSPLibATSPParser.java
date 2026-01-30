package ar.unicen;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TSPLibATSPParser {

    public static ATSPInstance parse(Path path) throws IOException {
        String name = null; //nombre del archivo
        Integer n = null; //dimension que tendra la matriz y arreglo de soluciones

        boolean inMatrix = false; //esto es para saber si ya podemos guardar la matriz de costos
        List<Integer> numbers = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); //convierte en string la linea, trim limpia espacios
                if (line.isEmpty()) continue;

                // Fin (a veces viene)
                if (line.equalsIgnoreCase("EOF")) break;

                if (!inMatrix) {
                    // Header
                    if (line.startsWith("NAME")) {
                        name = valueAfterColon(line); //busca : y copia todo lo posterior
                    } else if (line.startsWith("DIMENSION")) {
                        n = Integer.parseInt(valueAfterColon(line));
                    } else if (line.startsWith("EDGE_WEIGHT_SECTION")) {
                        if (n == null) {
                            throw new IllegalArgumentException("DIMENSION no encontrada antes de EDGE_WEIGHT_SECTION.");
                        }
                        inMatrix = true;
                    }
                } else {
                    // Matrix numbers (pueden venir separados por espacios)
                    // Algunas instancias meten cosas raras al final: protegemos con try/catch
                    String[] parts = line.split("\\s+"); 
                    for (String p : parts) {
                        if (p.trim().isEmpty()) continue; //si entra continue, pasa a otra iteracion
                        // por las dudas si aparece "EOF" pegado
                        if (p.equalsIgnoreCase("EOF")) {
                            inMatrix = false;
                            break;
                        }
                        numbers.add(Integer.parseInt(p));
                        if (numbers.size() == n * n) {
                            inMatrix = false; // ya tenemos todo
                            break;
                        }
                    }
                }
            }
        }

        if (name == null) name = path.getFileName().toString(); // fallback
        if (n == null) throw new IllegalArgumentException("DIMENSION no encontrada.");
        if (numbers.size() < n * n) {
            throw new IllegalArgumentException(
                "Matriz incompleta: esperados " + (n * n) + " valores, leídos " + numbers.size()
            );
        }

        int[][] cost = new int[n][n];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cost[i][j] = numbers.get(idx++);
            }
        }

        return new ATSPInstance(name, n, cost);
    }

    private static String valueAfterColon(String line) {
        int k = line.indexOf(':');
        if (k < 0) {
            // A veces viene "DIMENSION 43" (sin :)
            String[] parts = line.split("\\s+");
            return parts[parts.length - 1].trim();
        }
        return line.substring(k + 1).trim();
    }
}
