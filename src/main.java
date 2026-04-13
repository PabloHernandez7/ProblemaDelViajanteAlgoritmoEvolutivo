import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class main {
    public static void main(String[] args) {
        /* 
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
        */

        List<Integer> lista = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lista.add(i);
        }
        Individuo ind1 = new Individuo(lista);
        ind1.permutar();
        System.err.println("Solucion padre1" + ind1.getCopiaPermutaciones());
        Individuo ind2 = new Individuo(lista);
        ind2.permutar();
        System.err.println("Solucion padre2" + ind2.getCopiaPermutaciones());

        Random rnd = new Random();

        CrucePMX crucePMX = new CrucePMX();
        Individuo ind3 = crucePMX.cruzar(ind1, ind2, rnd);
        System.err.println("Solucion hijo PMX" + ind3.getCopiaPermutaciones());


        //precargar lo necesario para algoritmo
        //generar primera solucion (lista de n individuos)
        //
        //ejecutar algoritmo
        //generar excels con resultados

	    }
}