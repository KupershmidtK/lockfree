import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

@Param(name = "value", gen = IntGen::class, conf = "1:3")
class LincheckTestKt {
    private val set = SetImpl<Int>()

    @Operation
    fun isEmpty() = set.isEmpty()

    @Operation
    fun add(@Param(name = "value") value: Int) = set.add(value)

    @Operation
    fun remove(@Param(name = "value") value: Int) = set.remove(value)

    @Operation
    fun contains(@Param(name = "value") value: Int) = set.contains(value)

    @Test
    fun stressTest() = StressOptions()
        .logLevel(LoggingLevel.INFO)
        .check(LincheckTestKt::class)

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}