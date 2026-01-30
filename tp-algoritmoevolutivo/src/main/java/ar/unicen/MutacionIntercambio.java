package ar.unicen;
import java.util.Random;

public class MutacionIntercambio implements MutacionInterface{

    @Override
    public void mutate(Individuo ind, Random rnd) {
        // TODO Auto-generated method stub
        int n = ind.getSizePermutacion();
        int i = rnd.nextInt(n);
        int j = rnd.nextInt(n);
        while (i == j )
            j = rnd.nextInt(n);
        ind.swap(i,j);
    }
    
}
