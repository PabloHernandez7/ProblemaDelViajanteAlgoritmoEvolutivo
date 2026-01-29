import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CruceOrden implements CruceInterface{

    @Override
    public void cruzar(Individuo ind1, Individuo ind2, Random rnd){
        int sizePermutacion = ind1.getSizePermutacion();
        int pc1 = rnd.nextInt(sizePermutacion);
        int pc2 = rnd.nextInt(sizePermutacion);

        //Intercambio si quedan invertidas las posiciones
        if (pc1 > pc2){
            int aux = pc2;
            pc2 = pc1;
            pc1 = aux;
        }

        List<Integer> subList = ind1.getCopiaSubList(pc1, pc2);
        List<Integer> permutacionesHijo = new ArrayList<>(Collections.nCopies(sizePermutacion, -1));
        
        //Asigno la sublista en otro arraylist para el hijo
        int indexSubList=0;
        for (int i=pc1; i<=pc2;i++){
            permutacionesHijo.set(i, subList.get(indexSubList));
            indexSubList++;
        }

        int indexPadre = pc2+1;
        int indexHijo = pc2+1;
        for (int i=0; i < (sizePermutacion-subList.size()); i++){
            boolean insert = false;
            while (!insert) {
                if(!permutacionesHijo.contains(ind2.getElementoPermutaciones(indexPadre))){
                    permutacionesHijo.set(indexHijo, ind2.getElementoPermutaciones(indexPadre));
                    insert = true;
                    indexHijo = (indexHijo + 1) % sizePermutacion; //Con esto recorro ciclícamente si el indice terminó de recorrer la lista
                }
                indexPadre = (indexPadre + 1) % sizePermutacion; //Con esto recorro ciclícamente si el indice terminó de recorrer la lista
            }    
        }
    }
}
