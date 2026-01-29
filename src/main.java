import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
	
        //lectura archivo
        Scanner s = new Scanner(System.in);
        System.out.println("Escribir el nombre del archivo que desee cargar+ su extensión: ");
        String s1 = s.nextLine();
        s.close();
        Path path = Paths.get(s1); //prueba explicita
        try {
        ATSPInstance inst = TSPLibATSPParser.parse(path);
        System.out.println("Name: " + inst.getName());
        System.out.println("N: " + inst.getDimension());
        System.out.println("Cost[0][1]: " + inst.getCost()[0][1]);
        System.out.println("Se cargo");
        } catch(IOException e) {
            System.out.println("Error al buscar el archivo declarado: " + e.getMessage());
        }
        
        //precargar lo necesario para algoritmo
        //generar primera solucion (lista de n individuos)
        //
        //ejecutar algoritmo
        //generar excels con resultados

	    }
}