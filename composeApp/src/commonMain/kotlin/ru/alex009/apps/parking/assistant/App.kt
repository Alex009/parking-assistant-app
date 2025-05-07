package ru.alex009.apps.parking.assistant

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import parking_assistant_app.composeapp.generated.resources.Res
import parking_assistant_app.composeapp.generated.resources.car
import parking_assistant_app.composeapp.generated.resources.steering_wheel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

val car = CarInfo(
    width = 1720f.millimeters,
    length = 4720f.millimeters,
    wheelBase = 2665f.millimeters,
    frontOverhang = 1027f.millimeters, // наугад :(
    turningRadius = 5.2f.meters,
    maxSteeringAngle = 540f
)

data class Vector2D(
    val x: Float,
    val y: Float,
)


@Composable
@Preview
fun App() {
    MaterialTheme {
        val steeringAngle: MutableState<Float> = remember { mutableStateOf(90f) }
        val carPositionState: MutableState<CarPosition> = remember { mutableStateOf(CarPosition()) }
        var carPosition: CarPosition by carPositionState

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            val color = MaterialTheme.colors.primary

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .drawBehind {
                        val centerX: Float = size.width / 2
                        val centerY: Float = size.height / 2

                        carPosition.turnCircle?.let { circle ->
                            drawCircle(
                                color = color,
                                radius = circle.radius,
                                center = Offset(
                                    x = centerX + circle.centerX,
                                    y = centerY + circle.centerY
                                )
                            )
                        }

                        drawCircle(
                            color = Color(0xFF0000FF),
                            radius = 10f,
                            center = Offset(
                                x = centerX + carPosition.offsetX,
                                y = centerY + carPosition.offsetY
                            )
                        )
                    }
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset {
                            IntOffset(
                                x = carPosition.offsetX.toInt(),
                                y = carPosition.offsetY.toInt()
                            )
                        }
                        .rotate(carPosition.rotationAngle),
                    painter = painterResource(Res.drawable.car),
                    contentDescription = null
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Column(modifier = Modifier.weight(1f)) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(50.dp)
                            .rotate(steeringAngle.value),
                        painter = painterResource(Res.drawable.steering_wheel),
                        contentDescription = null
                    )

                    Slider(
                        value = steeringAngle.value,
                        valueRange = -540f..540f,
                        onValueChange = { steeringAngle.value = it }
                    )
                }

                Column {
                    AccelerationButton(
                        icon = Icons.Filled.KeyboardArrowUp,
                        whilePressed = {
                            moveEachSecond(
                                state = carPositionState,
                                steeringAngle = steeringAngle,
                                distance = 1f,
                            )
                        }
                    )
                    AccelerationButton(
                        icon = Icons.Filled.KeyboardArrowDown,
                        whilePressed = {
                            moveEachSecond(
                                state = carPositionState,
                                steeringAngle = steeringAngle,
                                distance = -1f,
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccelerationButton(
    icon: ImageVector,
    whilePressed: suspend () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (!isPressed) return@LaunchedEffect

        whilePressed()
    }

    IconButton(
        onClick = { },
        interactionSource = interactionSource,
    ) {
        Icon(icon, contentDescription = null)
    }
}

private suspend fun moveEachSecond(
    state: MutableState<CarPosition>,
    steeringAngle: State<Float>,
    distance: Float,
) {
    while (true) {
        state.value = state.value.drive(
            steeringAngle = steeringAngle.value,
            distance = distance * 10
        )
        delay(200)
    }
}

@Immutable
data class CarPosition(
    val rotationAngle: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,

    val turnCircle: Circle? = null
)

@Immutable
data class Circle(
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
)

fun CarPosition.drive(steeringAngle: Float, distance: Float): CarPosition {
    if (steeringAngle == 0f) return moveForward(distance)

    val wheelSign = sign(steeringAngle)                    // +1 или −1
    // 1. радиус и центр поворота (ваша логика)
    val turnRadius = car.turningRadius.pixels * (car.maxSteeringAngle / abs(steeringAngle))
    val dirToCenter = rotationAngle + wheelSign * 90f
    val radToCenter = degreesToRadians(dirToCenter)
    val centerX = offsetX + turnRadius * sin(radToCenter)
    val centerY = offsetY - turnRadius * cos(radToCenter)

    // 2. текущий угол на окружности (СК с Y вверх)
    val currentAngle = atan2(offsetY - centerY, offsetX - centerX)

    // 3. учитываем направление руля и управления (вперёд/назад)
    val rotSign = if (distance >= 0f) wheelSign else -wheelSign
    val signedAngleDelta = (abs(distance) / turnRadius) * rotSign

    // 4. новая точка дуги
    val newAngle = currentAngle + signedAngleDelta
    val newX = centerX + turnRadius * cos(newAngle)
    val newY = centerY + turnRadius * sin(newAngle)

    // 5. пересчитываем угол машины в вашей системе координат
    val newRotation = if (distance > 0) {
        calculateAngleBetweenPositions(
            oldX = offsetX,
            oldY = offsetY,
            newX = newX,
            newY = newY
        )
    } else {
        calculateAngleBetweenPositions(
            newX = offsetX,
            newY = offsetY,
            oldX = newX,
            oldY = newY
        )
    }

    return CarPosition(
        rotationAngle = newRotation,
        offsetX = newX,
        offsetY = newY,
        turnCircle = Circle(centerX, centerY, turnRadius)
    ).also { println(it) }
}

private fun CarPosition.moveForward(distance: Float): CarPosition {
    // Рассчитываем движение прямо с учетом текущего угла поворота машины
    val angleInRadians: Float = degreesToRadians(rotationAngle)
    val deltaX = distance * sin(angleInRadians)
    val deltaY = -distance * cos(angleInRadians) // Минус, потому что у-ось направлена вниз

    return CarPosition(
        rotationAngle = rotationAngle,
        offsetX = offsetX + deltaX,
        offsetY = offsetY + deltaY
    )
}

/**
 * Преобразует угол из градусов в радианы
 *
 * @param degrees угол в градусах
 * @return угол в радианах
 */
private fun degreesToRadians(degrees: Float): Float {
    return degrees * (PI.toFloat() / 180f)
}

/**
 * Рассчитывает угол между двумя позициями в градусах
 *
 * @param oldX x-координата старой позиции
 * @param oldY y-координата старой позиции
 * @param newX x-координата новой позиции
 * @param newY y-координата новой позиции
 * @return угол в градусах (0 градусов - вверх, по часовой стрелке) в диапазоне [0, 360)
 */
fun calculateAngleBetweenPositions(
    oldX: Float,
    oldY: Float,
    newX: Float,
    newY: Float
): Float {
    // Вычисляем разницу координат (вектор направления от старой точки к новой)
    val deltaX = newX - oldX
    val deltaY = newY - oldY

    // Если точки совпадают, возвращаем 0
    if (deltaX == 0f && deltaY == 0f) {
        return 0f
    }

    // Используем atan2 для получения угла в диапазоне [-π, π]
    // atan2 принимает аргументы в порядке (y, x),
    // но мы передаем (deltaX, deltaY) для соответствия системе координат,
    // где 0 градусов - вверх, а положительный угол - по часовой стрелке
    val angleInRadians = atan2(deltaX, -deltaY)

    // Преобразуем радианы в градусы
    val angleInDegrees = radiansToDegrees(angleInRadians)

    // Нормализуем в диапазон [0, 360)
    return if (angleInDegrees < 0) angleInDegrees + 360f else angleInDegrees
}

/**
 * Преобразует угол из радиан в градусы
 *
 * @param radians угол в радианах
 * @return угол в градусах
 */
private fun radiansToDegrees(radians: Float): Float {
    return radians * (180f / PI.toFloat())
}


//
///**
// * Рассчитывает вектор направления движения автомобиля (единичной длины)
// * при заданном угле поворота руля и текущем угле поворота автомобиля.
// *
// * @param steeringAngle угол поворота руля в градусах (от -540 до 540)
// * @param carRotationAngle текущий угол поворота автомобиля в градусах
// * @param wheelBase колёсная база автомобиля (расстояние между осями) в метрах
// * @return вектор направления движения (dx, dy) единичной длины
// */
//fun calculateMovementVector(
//    steeringAngle: Float,
//    carRotationAngle: Float,
//    wheelBase: Float = car.wheelBase.pixels
//): Vector2D {
//    // Проверка на нулевой угол поворота руля (движение прямо)
//    if (steeringAngle == 0f) {
//        return angleToDirectionVector(carRotationAngle)
//    }
//
//    // Нормализуем угол руля
//    val normalizedSteeringAngle = max(-540f, min(540f, steeringAngle))
//
//    // Максимальный угол поворота колёс в радианах при максимальном повороте руля (540°)
//    // Для радиуса поворота 7м и колёсной базы wheelBase
//    val minTurningRadius = car.turningRadius.pixels
//    val maxWheelAngleRad = asin(wheelBase / minTurningRadius)
//
//    // Пропорционально пересчитываем угол колёс для текущего угла руля
//    val steeringToWheelRatio = maxWheelAngleRad / (540f * (PI.toFloat() / 180f))
//    val wheelAngleRad = normalizedSteeringAngle * (PI.toFloat() / 180f) * steeringToWheelRatio
//
//    // Угол касательной к окружности (перпендикулярен к радиусу)
//    // Сначала преобразуем угол автомобиля в радианы
//    val carAngleRad = carRotationAngle * (PI.toFloat() / 180f)
//
//    // Вычисляем угол направления движения
//    // При повороте колёс автомобиль двигается по окружности
//    // Угол вектора движения = угол автомобиля + поправка на поворот колёс
//    val movementAngleRad = carAngleRad + wheelAngleRad
//
//    // Возвращаем вектор единичной длины
//    return Vector2D(
//        x = sin(movementAngleRad),
//        y = -cos(movementAngleRad) // Минус из-за направления координат экрана
//    )
//}
//
///**
// * Преобразует угол поворота в вектор направления единичной длины
// *
// * @param angleInDegrees угол в градусах (0 градусов - вверх, по часовой стрелке)
// * @return вектор направления (dx, dy) единичной длины
// */
//fun angleToDirectionVector(angleInDegrees: Float): Vector2D {
//    // Преобразуем угол в радианы
//    val angleInRadians: Float = degreesToRadians(angleInDegrees)
//
//    // Вычисляем компоненты вектора
//    // Обратите внимание: в стандартной системе координат 0 градусов - вправо,
//    // но в компьютерной графике часто 0 градусов - вверх
//    // sin даёт x-компоненту, -cos даёт y-компоненту (минус из-за направления оси Y)
//    return Vector2D(
//        x = sin(angleInRadians),
//        y = -cos(angleInRadians)
//    )
//}
//
///**
// * Преобразует вектор направления в угол в градусах
// *
// * @param directionX x-компонента вектора направления
// * @param directionY y-компонента вектора направления
// * @return угол в градусах (0 градусов - вверх, по часовой стрелке)
// */
//fun directionVectorToAngle(directionX: Float, directionY: Float): Float {
//    // Используем atan2 для получения угла в диапазоне [-π, π]
//    val angleInRadians = atan2(directionX, -directionY)
//
//    // Преобразуем радианы в градусы
//    var angleInDegrees = angleInRadians * (180f / PI.toFloat())
//
//    // Нормализуем в диапазон [0, 360)
//    if (angleInDegrees < 0) {
//        angleInDegrees += 360f
//    }
//
//    return angleInDegrees
//}
//
