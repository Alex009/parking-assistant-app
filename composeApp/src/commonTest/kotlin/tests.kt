import ru.alex009.apps.parking.assistant.calculateTurningRadius
import kotlin.test.Test
import kotlin.test.assertEquals

class MathTests {
    @Test
    fun radius() {
        assertEquals(
            expected = Float.POSITIVE_INFINITY,
            actual = calculateTurningRadius(wheelAngle = 0f)
        )
    }
}
