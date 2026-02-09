import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CrucePMX implements CruceInterface{
    
    @Override
    public Individuo cruzar(Individuo ind1, Individuo ind2, Random rnd){
        int sizePermutacion = ind1.getSizePermutacion();
        int pc1 = rnd.nextInt(sizePermutacion);
        int pc2 = rnd.nextInt(sizePermutacion);

        //Intercambio si quedan invertidas las posiciones
        if (pc1 > pc2){
            int aux = pc2;
            pc2 = pc1;
            pc1 = aux;
        }

        System.err.println("PC1: " + pc1);
        System.err.println("PC2: " + pc2);

        List<Integer> subListP1 = ind1.getCopiaSubList(pc1, pc2);
        List<Integer> permutacionesHijo = new ArrayList<>(Collections.nCopies(sizePermutacion, -1)); //Relleno con -1 los valores ajenos a la sublist
        
        //Asigno la sublista en otro arraylist para el hijo
        int indexSubList=0;
        for (int i=pc1; i<=pc2;i++){
            permutacionesHijo.set(i, subListP1.get(indexSubList));
            indexSubList++;
        }

        //Asigno los elementos de la sublista ubicada en el padre 2
        List<Integer> listInd2 = ind2.getCopiaPermutaciones();
        for (int i=pc1; i <= pc2; i++){
            int element = listInd2.get(i);
            if (!permutacionesHijo.contains(element)){
                boolean insert = false;
                int indexPMX = i;
                while (!insert){
                    if (permutacionesHijo.get(listInd2.indexOf(ind1.getElementoPermutaciones(indexPMX)))==-1){
                        permutacionesHijo.set(listInd2.indexOf(ind1.getElementoPermutaciones(indexPMX)), element);
                        insert = true;
                    }
                    else{
                        indexPMX = listInd2.indexOf(ind1.getElementoPermutaciones(indexPMX));
                    }
                }
            }
        }

        //Asigno el resto de elementos pendientes
        for (int i=0; i<ind2.getSizePermutacion();i++){
            if (!permutacionesHijo.contains(ind2.getElementoPermutaciones(i))) {
                permutacionesHijo.set(i, ind2.getElementoPermutaciones(i));
            }
        }

        Individuo hijo = new Individuo(permutacionesHijo);
        return hijo;
    }
}
