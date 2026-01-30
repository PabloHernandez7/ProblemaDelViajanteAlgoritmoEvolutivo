package ar.unicen;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MutacionMezcla implements MutacionInterface {

    /*Osea, agarro una subsecuencia de la lista, esa sublista hago un shuffle y luego la inserto again */
    @Override
    public void mutate(Individuo ind,Random rnd) {
        int n = ind.getSizePermutacion();
        if (n < 2) return; // no hay nada que mezclar
        int i = rnd.nextInt(n - 1);            // 0..n-2
        int j = i + 1 + rnd.nextInt(n - i - 1); // i+1..n-1
        List<Integer> subList = ind.getCopiaSubList(i,j);
        Collections.shuffle(subList);
        ind.setSublistPermutacion(subList,i,j);
        
    }
    
}
