import org.jetbrains.kotlinx.lincheck.LinChecker;
import org.jetbrains.kotlinx.lincheck.annotations.Operation;
import org.jetbrains.kotlinx.lincheck.annotations.Param;
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen;
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions;
import org.junit.Test;

@Param(name="value", gen = IntGen.class, conf = "1:3")
@StressCTest
public class LincheckTest {
    private SetImpl<Integer> set = new SetImpl<>();

    @Operation
    public void isEmpty() {
        set.isEmpty();
    }

    @Operation
    public void add(@Param(name = "value") Integer value) {
        set.add(value);
    }

    @Operation
    public void remove(@Param(name = "value") Integer value) {
        set.remove(value);
    }

    @Operation
    public void contains(@Param(name = "value") Integer value) {
        set.contains(value);
    }

    @Test
    public void stressTest() {
        LinChecker.check(LincheckTest.class);
    }
}
