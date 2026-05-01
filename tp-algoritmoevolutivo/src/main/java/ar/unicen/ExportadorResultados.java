package ar.unicen;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportadorResultados {

    public static void exportarConsolidado(List<ResultadoCorrida> corridas, int[][] matriz, String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // --- HOJA 1: RESUMEN ESTADÍSTICO (Ranking) ---
            Sheet sheetResumen = workbook.createSheet("Resumen Estadístico");
            Row header = sheetResumen.createRow(0);
            header.createCell(0).setCellValue("Métrica Evaluada");
            header.createCell(1).setCellValue("Valor");

            // 1. Calcular Fitness Promedio
            double promedioFitness = corridas.stream().mapToLong(ResultadoCorrida::getMejorFitness).average().orElse(0);
            
            // 2. Calcular Desviación Estándar (Muestral)
            double sumaDiferenciasCuadradas = 0.0;
            for (ResultadoCorrida rc : corridas) {
                sumaDiferenciasCuadradas += Math.pow(rc.getMejorFitness() - promedioFitness, 2);
            }
            double desviacionEstandar = 0.0;
            if (corridas.size() > 1) {
                // Dividimos por N-1 para la desviación estándar de una muestra
                desviacionEstandar = Math.sqrt(sumaDiferenciasCuadradas / (corridas.size() - 1));
            }

            // 3. Calcular Tiempo de Cómputo (Promedio de soluciones generadas)
            double promedioSoluciones = corridas.stream().mapToLong(ResultadoCorrida::getSolucionesGeneradas).average().orElse(0);

            // Escribir los resultados en la hoja
            sheetResumen.createRow(1).createCell(0).setCellValue("Valor de Fitness Promedio");
            sheetResumen.getRow(1).createCell(1).setCellValue(promedioFitness);
            
            sheetResumen.createRow(2).createCell(0).setCellValue("Desviación Estándar (Fitness)");
            sheetResumen.getRow(2).createCell(1).setCellValue(desviacionEstandar);

            sheetResumen.createRow(3).createCell(0).setCellValue("Tiempo de Cómputo (Promedio Soluciones)");
            sheetResumen.getRow(3).createCell(1).setCellValue(promedioSoluciones);
            
            sheetResumen.autoSizeColumn(0);
            sheetResumen.autoSizeColumn(1);

            // --- HOJA 2: EVOLUCIÓN DEL FITNESS ---
            Sheet sheetEvolucion = workbook.createSheet("Evolución Fitness");
            Row headerEvo = sheetEvolucion.createRow(0);
            headerEvo.createCell(0).setCellValue("Generación");
            
            for (int c = 0; c < corridas.size(); c++) {
                headerEvo.createCell(c + 1).setCellValue("Corrida " + (c + 1));
            }

            if (!corridas.isEmpty()) {
                int numGeneraciones = corridas.get(0).getHistorialFitness().size();
                for (int gen = 0; gen < numGeneraciones; gen++) {
                    Row row = sheetEvolucion.createRow(gen + 1);
                    row.createCell(0).setCellValue(gen + 1); 
                    
                    for (int c = 0; c < corridas.size(); c++) {
                        long fitnessEnEsaGen = corridas.get(c).getHistorialFitness().get(gen);
                        row.createCell(c + 1).setCellValue(fitnessEnEsaGen);
                    }
                }
            }

            // --- HOJA 3: DETALLE POBLACIONES ---
            Sheet sheetDetalle = workbook.createSheet("Detalle Poblaciones");
            String[] headersDetalle = {"Corrida", "Individuo Nro", "Costo (Fitness)", "Ruta (Permutación)"};
            
            Row headerRowDetalle = sheetDetalle.createRow(0);
            for (int i = 0; i < headersDetalle.length; i++) {
                headerRowDetalle.createCell(i).setCellValue(headersDetalle[i]);
            }

            int rowIdx = 1; 
            for (int numCorrida = 0; numCorrida < corridas.size(); numCorrida++) {
                ResultadoCorrida corrida = corridas.get(numCorrida);
                List<Individuo> poblacion = corrida.getPoblacionFinal();

                for (int i = 0; i < poblacion.size(); i++) {
                    Individuo ind = poblacion.get(i);
                    Row row = sheetDetalle.createRow(rowIdx++);
                    
                    row.createCell(0).setCellValue("Corrida " + (numCorrida + 1));
                    row.createCell(1).setCellValue(i + 1);
                    row.createCell(2).setCellValue(ind.getFitness(matriz));
                    row.createCell(3).setCellValue(obtenerRutaString(ind));
                }
            }

            for (int i = 0; i < headersDetalle.length; i++) {
                sheetDetalle.autoSizeColumn(i);
            }

            // --- GUARDAR ARCHIVO ---
            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
                workbook.write(fileOut);
            }
            System.out.println("Excel generado con éxito en: " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al exportar a Excel: " + e.getMessage());
        }
    }

    private static String obtenerRutaString(Individuo ind) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ind.getSizePermutacion(); i++) {
            sb.append(ind.getElementoPermutaciones(i));
            if (i < ind.getSizePermutacion() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }
}