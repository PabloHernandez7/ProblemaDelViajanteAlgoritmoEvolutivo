package ar.unicen;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportadorResultados {

    public static void exportarConsolidado(List<ResultadoCorrida> corridas, int[][] matriz, String nombreArchivo, Main.ConfiguracionAG config) {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            Sheet sheetResumen = workbook.createSheet("Resumen Estadístico");
            Row header = sheetResumen.createRow(0);
            header.createCell(0).setCellValue("Métrica Evaluada");
            header.createCell(1).setCellValue("Valor");

            double promedioFitness = corridas.stream().mapToLong(ResultadoCorrida::getMejorFitness).average().orElse(0);
            double promedioSoluciones = corridas.stream().mapToLong(ResultadoCorrida::getSolucionesGeneradas).average().orElse(0);
            
            double sumaDiferenciasCuadradas = 0.0;
            for (ResultadoCorrida rc : corridas) {
                sumaDiferenciasCuadradas += Math.pow(rc.getMejorFitness() - promedioFitness, 2);
            }
            double desviacionEstandar = 0.0;
            if (corridas.size() > 1) {
                desviacionEstandar = Math.sqrt(sumaDiferenciasCuadradas / (corridas.size() - 1));
            }

            sheetResumen.createRow(1).createCell(0).setCellValue("Valor de Fitness Promedio");
            sheetResumen.getRow(1).createCell(1).setCellValue(promedioFitness);
            
            sheetResumen.createRow(2).createCell(0).setCellValue("Desviación Estándar (Fitness)");
            sheetResumen.getRow(2).createCell(1).setCellValue(desviacionEstandar);

            sheetResumen.createRow(3).createCell(0).setCellValue("Tiempo de Cómputo (Promedio Soluciones)");
            sheetResumen.getRow(3).createCell(1).setCellValue(promedioSoluciones);

            sheetResumen.createRow(5).createCell(0).setCellValue("Mejor Fitness por Corrida");
            int rowIdxResumen = 6;
            for (int i = 0; i < corridas.size(); i++) {
                Row r = sheetResumen.createRow(rowIdxResumen++);
                r.createCell(0).setCellValue("Corrida " + (i + 1));
                r.createCell(1).setCellValue(corridas.get(i).getMejorFitness());
            }
            
            sheetResumen.autoSizeColumn(0);
            sheetResumen.autoSizeColumn(1);

            Sheet sheetConfig = workbook.createSheet("Configuración");
            Row headerCfg = sheetConfig.createRow(0);
            headerCfg.createCell(0).setCellValue("Parámetro");
            headerCfg.createCell(1).setCellValue("Valor");

            String[][] datosConfig = {
                {"Tamaño Población (nInd)", String.valueOf(config.nInd)},
                {"Generaciones (maxGen)", String.valueOf(config.maxGen)},
                {"Probabilidad Cruce", String.valueOf(config.probCruce)},
                {"Probabilidad Mutación", String.valueOf(config.probMut)},
                {"Selección de Padres", config.selPadres},
                {"K Torneo", String.valueOf(config.kTorneo)},
                {"Operador de Cruce", config.cruce},
                {"Operador de Mutación", config.mutacion},
                {"Selección de Sobrevivientes", config.selSobrevivientes},
                {"N Steady", String.valueOf(config.nSteady)}
            };

            for (int i = 0; i < datosConfig.length; i++) {
                Row r = sheetConfig.createRow(i + 1);
                r.createCell(0).setCellValue(datosConfig[i][0]);
                r.createCell(1).setCellValue(datosConfig[i][1]);
            }
            sheetConfig.autoSizeColumn(0);
            sheetConfig.autoSizeColumn(1);

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

            Sheet sheetDetalle = workbook.createSheet("Detalle Poblaciones");
            String[] headersDetalle = {"Corrida", "Individuo Nro", "Costo (Fitness)", "Ruta (Permutación)"};
            
            Row headerRowDetalle = sheetDetalle.createRow(0);
            for (int i = 0; i < headersDetalle.length; i++) {
                headerRowDetalle.createCell(i).setCellValue(headersDetalle[i]);
            }

            int rowIdxDetalle = 1; 
            for (int numCorrida = 0; numCorrida < corridas.size(); numCorrida++) {
                ResultadoCorrida corrida = corridas.get(numCorrida);
                List<Individuo> poblacion = corrida.getPoblacionFinal();

                for (int i = 0; i < poblacion.size(); i++) {
                    Individuo ind = poblacion.get(i);
                    Row row = sheetDetalle.createRow(rowIdxDetalle++);
                    
                    row.createCell(0).setCellValue("Corrida " + (numCorrida + 1));
                    row.createCell(1).setCellValue(i + 1);
                    row.createCell(2).setCellValue(ind.getFitness(matriz));
                    row.createCell(3).setCellValue(obtenerRutaString(ind));
                }
            }

            for (int i = 0; i < headersDetalle.length; i++) {
                sheetDetalle.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
                workbook.write(fileOut);
            }
            System.out.println("Excel generado con éxito en: " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al exportar a Excel: " + e.getMessage());
        }
    }

    public static void exportarRanking(List<ResumenConfiguracion> ranking, String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            ranking.sort((r1, r2) -> Double.compare(r1.promedioFitness, r2.promedioFitness));

            Sheet sheet = workbook.createSheet("Ranking de Algoritmos");
            Row header = sheet.createRow(0);
            
            String[] columnas = {
                "Puesto", "ID Conf", "Fitness Promedio", "Desv. Estándar", "Prom. Soluciones", 
                "Población", "Max Gen", "Prob. Cruce", "Prob. Mutación", 
                "Padres", "K", "Cruce", "Mutación", "Sobrevivientes", "N"
            };

            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowIdx = 1;
            for (int i = 0; i < ranking.size(); i++) {
                ResumenConfiguracion res = ranking.get(i);
                Row r = sheet.createRow(rowIdx++);
                
                r.createCell(0).setCellValue(i + 1); 
                r.createCell(1).setCellValue("Conf " + res.id);
                r.createCell(2).setCellValue(res.promedioFitness);
                r.createCell(3).setCellValue(res.desviacionEstandar);
                r.createCell(4).setCellValue(res.promedioSoluciones);
                
                r.createCell(5).setCellValue(res.config.nInd);
                r.createCell(6).setCellValue(res.config.maxGen);
                r.createCell(7).setCellValue(res.config.probCruce);
                r.createCell(8).setCellValue(res.config.probMut);
                r.createCell(9).setCellValue(res.config.selPadres);
                r.createCell(10).setCellValue(res.config.kTorneo);
                r.createCell(11).setCellValue(res.config.cruce);
                r.createCell(12).setCellValue(res.config.mutacion);
                r.createCell(13).setCellValue(res.config.selSobrevivientes);
                r.createCell(14).setCellValue(res.config.nSteady);
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
                workbook.write(fileOut);
            }
            
        } catch (IOException e) {
            System.err.println("Error al exportar el ranking: " + e.getMessage());
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