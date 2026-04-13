package ar.unicen;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportadorResultados {

    public static void exportarAExcel(List<Individuo> poblacion, Individuo mejor, int[][] matriz,
            String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 1. Hoja de Población Final
            Sheet sheetPoblacion = workbook.createSheet("Población Final");
            String[] columnasPoblacion = { "ID", "Costo (Fitness)", "Ruta Completa" };

            Row headerRow = sheetPoblacion.createRow(0);
            for (int i = 0; i < columnasPoblacion.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnasPoblacion[i]);
            }

            int rowNum = 1;
            for (int i = 0; i < poblacion.size(); i++) {
                Individuo ind = poblacion.get(i);
                Row row = sheetPoblacion.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(ind.getFitness(matriz));
                row.createCell(2).setCellValue(obtenerRutaString(ind));
            }

            // 2. Hoja del Mejor Individuo
            Sheet sheetMejor = workbook.createSheet("Mejor Resultado");
            Row rowMejorCosto = sheetMejor.createRow(0);
            rowMejorCosto.createCell(0).setCellValue("Costo Total:");
            rowMejorCosto.createCell(1).setCellValue(mejor.getFitness(matriz));

            Row rowRutaHeader = sheetMejor.createRow(2);
            rowRutaHeader.createCell(0).setCellValue("Orden de Visita");
            rowRutaHeader.createCell(1).setCellValue("ID Ciudad");

            for (int i = 0; i < mejor.getSizePermutacion(); i++) {
                Row row = sheetMejor.createRow(i + 3);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(mejor.getElementoPermutaciones(i));
            }

            // Autoajustar columnas
            for (int i = 0; i < columnasPoblacion.length; i++) {
                sheetPoblacion.autoSizeColumn(i);
            }

            // Escribir el archivo
            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
                workbook.write(fileOut);
            }
            System.out.println("Archivo Excel generado exitosamente: " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al exportar a Excel: " + e.getMessage());
        }
    }

    private static String obtenerRutaString(Individuo ind) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ind.getSizePermutacion(); i++) {
            sb.append(ind.getElementoPermutaciones(i));
            if (i < ind.getSizePermutacion() - 1)
                sb.append(" -> ");
        }
        return sb.toString();
    }

    public static void exportarConsolidado(List<ResultadoCorrida> corridas, String nombreArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheetResumen = workbook.createSheet("Resumen Estadístico");

            // Encabezados
            Row header = sheetResumen.createRow(0);
            header.createCell(0).setCellValue("Métrica");
            header.createCell(1).setCellValue("Valor");

            // Calcular Estadísticas
            double promedioFitness = corridas.stream().mapToLong(ResultadoCorrida::getMejorFitness).average().orElse(0);
            double promedioSoluciones = corridas.stream().mapToLong(ResultadoCorrida::getSolucionesGeneradas).average()
                    .orElse(0);

            Row row1 = sheetResumen.createRow(1);
            row1.createCell(0).setCellValue("Fitness Promedio");
            row1.createCell(1).setCellValue(promedioFitness);

            Row row2 = sheetResumen.createRow(2);
            row2.createCell(0).setCellValue("Promedio Soluciones Generadas");
            row2.createCell(1).setCellValue(promedioSoluciones);

            // Hoja de datos crudos
            Sheet sheetDatos = workbook.createSheet("Datos de Corridas");
            Row hDatos = sheetDatos.createRow(0);
            hDatos.createCell(0).setCellValue("Corrida");
            hDatos.createCell(1).setCellValue("Mejor Fitness");
            hDatos.createCell(2).setCellValue("Soluciones");

            for (int i = 0; i < corridas.size(); i++) {
                Row r = sheetDatos.createRow(i + 1);
                r.createCell(0).setCellValue(i + 1);
                r.createCell(1).setCellValue(corridas.get(i).getMejorFitness());
                r.createCell(2).setCellValue(corridas.get(i).getSolucionesGeneradas());
            }

            try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
