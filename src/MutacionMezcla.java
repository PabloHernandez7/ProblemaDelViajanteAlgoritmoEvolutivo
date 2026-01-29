import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MutacionMezcla implements MutacionInterface {

    /*Osea, agarro una subsecuencia de la lista, esa sublista hago un shuffle y luego la inserto again */
    @Override
    public void mutate(Individuo ind,Random rnd) {
        int n = ind.getSizePermutacion();
        int i = rnd.nextInt(n);
        int j = rnd.nextInt(n);
        while (i >= j )
            j = rnd.nextInt(n);
        List<Integer> subList = ind.getCopiaSubList(i,j);
        Collections.shuffle(subList);
        ind.setSublistPermutacion(subList,i,j);
        
    }
    
}
