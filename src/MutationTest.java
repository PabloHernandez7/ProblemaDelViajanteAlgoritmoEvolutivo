import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Test
void mutacionMantienePermutacion() {
    List<Integer> datos = new ArrayList<>();
    Collections.addAll(datos, 0,1,2,3,4,5,6,7,8,9);

    Individuo ind = new Individuo(datos);

    List<Integer> original = ind.getCopiaPermutaciones();

    new MutacionMezcla().mutate(ind, new Random(123));

    List<Integer> despues = ind.getCopiaPermutaciones();

    assertEquals(
        new HashSet<>(original),
        new HashSet<>(despues)
    );

    assertEquals(original.size(), despues.size());
}
