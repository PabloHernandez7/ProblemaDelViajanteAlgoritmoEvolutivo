package ar.unicen;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class MutationMezclaTest {

@Test
void mutacionPreservaPermutacion_paraMuchosSeeds() {
    List<Integer> base = new ArrayList<>();
    Collections.addAll(base, 0,1,2,3,4,5,6,7,8,9);

    for (int seed = 0; seed < 1000; seed++) {
        Individuo ind = new Individuo(base);
        List<Integer> original = ind.getCopiaPermutaciones();

        new MutacionMezcla().mutate(ind, new Random(seed));

        List<Integer> despues = ind.getCopiaPermutaciones();

        // tamaño
        assertEquals(
            original.size(),
            despues.size(),
            "Seed=" + seed + " tamaño distinto. orig=" + original + " despues=" + despues
        );

        // mismos elementos (sin importar orden)
        Set<Integer> faltantes = new HashSet<>(original);
        faltantes.removeAll(despues);

        assertTrue(
            faltantes.isEmpty(),
            "Seed=" + seed + " faltan " + faltantes + " orig=" + original + " despues=" + despues
        );

        Set<Integer> extras = new HashSet<>(despues);
        extras.removeAll(original);

        assertTrue(
            extras.isEmpty(),
            "Seed=" + seed + " sobran " + extras + " orig=" + original + " despues=" + despues
        );
    }
}
}
