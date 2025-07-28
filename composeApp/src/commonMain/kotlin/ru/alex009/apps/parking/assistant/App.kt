package ru.alex009.apps.parking.assistant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
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
import parking_assistant_app.composeapp.generated.resources.coupe_1
import parking_assistant_app.composeapp.generated.resources.sedan_1
import parking_assistant_app.composeapp.generated.resources.sedan_2
import parking_assistant_app.composeapp.generated.resources.steering_wheel
import parking_assistant_app.composeapp.generated.resources.van_1
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
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

val scenes: List<Scene> = listOf(
    Scene(), // first scene - empty
    Scene(
        cars = listOf(
            SceneCar(
                image = Res.drawable.sedan_1,
                position = CarPosition(
                    rotationAngle = 170f,
                    offsetX = -310f,
                    offsetY = 200f
                )
            )
        ),
        drawings = listOf(
            SceneDrawing.Line(
                start = IntOffset(x = -1000, y = 550),
                end = IntOffset(x = 1000, y = 550),
                width = 10f
            ),
            SceneDrawing.Line(
                start = IntOffset(x = -1000, y = -600),
                end = IntOffset(x = 1000, y = -600),
                width = 10f
            )
        ),
        initialPosition = CarPosition(
            offsetX = 0f,
            offsetY = 200f
        )
    ),
    Scene(
        cars = listOf(
            SceneCar(
                image = Res.drawable.sedan_1,
                position = CarPosition(
                    rotationAngle = 90f,
                    offsetX = 200f,
                    offsetY = 350f
                )
            ),
            SceneCar(
                image = Res.drawable.coupe_1,
                position = CarPosition(
                    rotationAngle = -90f,
                    offsetX = 230f,
                    offsetY = -350f
                )
            )
        ),
        drawings = listOf(
            SceneDrawing.Line(
                start = IntOffset(x = 550, y = -1500),
                end = IntOffset(x = 550, y = 1500),
                width = 10f
            )
        ),
        initialPosition = CarPosition(
            offsetX = -300f,
            offsetY = 500f
        )
    ),
    Scene(
        cars = listOf(
            SceneCar(
                image = Res.drawable.sedan_1,
                position = CarPosition(
                    rotationAngle = 0f,
                    offsetX = 400f,
                    offsetY = -800f
                )
            ),
            SceneCar(
                image = Res.drawable.coupe_1,
                position = CarPosition(
                    rotationAngle = 0f,
                    offsetX = 400f,
                    offsetY = 800f
                )
            )
        ),
        drawings = listOf(
            SceneDrawing.Line(
                start = IntOffset(x = 550, y = -1500),
                end = IntOffset(x = 550, y = 1500),
                width = 10f
            )
        ),
        initialPosition = CarPosition(
            offsetX = 0f,
            offsetY = 500f
        )
    ),
    Scene(
        cars = listOf(
            SceneCar(
                image = Res.drawable.sedan_1,
                position = CarPosition(
                    rotationAngle = 0f,
                    offsetX = 400f,
                    offsetY = -650f
                )
            ),
            SceneCar(
                image = Res.drawable.coupe_1,
                position = CarPosition(
                    rotationAngle = 0f,
                    offsetX = 400f,
                    offsetY = 650f
                )
            )
        ),
        drawings = listOf(
            SceneDrawing.Line(
                start = IntOffset(x = 550, y = -1500),
                end = IntOffset(x = 550, y = 1500),
                width = 10f
            )
        ),
        initialPosition = CarPosition(
            offsetX = 0f,
            offsetY = 500f
        )
    ),
    Scene(
        cars = listOf(
            SceneCar(
                image = Res.drawable.sedan_1,
                position = CarPosition(
                    rotationAngle = 40f,
                    offsetX = 200f,
                    offsetY = -700f
                )
            ),
            SceneCar(
                image = Res.drawable.coupe_1,
                position = CarPosition(
                    rotationAngle = 50f,
                    offsetX = 200f,
                    offsetY = 250f
                )
            ),
            SceneCar(
                image = Res.drawable.van_1,
                position = CarPosition(
                    rotationAngle = 45f,
                    offsetX = 200f,
                    offsetY = 800f
                )
            )
        ),
        drawings = listOf(
            SceneDrawing.Line(
                start = IntOffset(x = 450, y = -1500),
                end = IntOffset(x = 450, y = 1500),
                width = 10f
            )
        ),
        initialPosition = CarPosition(
            offsetX = -300f,
            offsetY = 700f
        )
    )
)

@Composable
@Preview
fun App() {
    MaterialTheme {
        var sceneIndex: Int by remember { mutableStateOf(0) }
        val scene: Scene = remember(sceneIndex) { scenes[sceneIndex] }
        val steeringAngle: MutableState<Float> = remember(scene) { mutableStateOf(0f) }
        val carPositionState: MutableState<CarPosition> = remember(scene) { mutableStateOf(scene.initialPosition) }
        var carPosition: CarPosition by carPositionState

        val turnCircle: Circle? = remember(steeringAngle.value, carPosition) {
            carPosition.getTurnCircle(steeringAngle.value)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            val turnCircleColor = MaterialTheme.colors.secondary.copy(alpha = 0.5f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { sceneIndex = max(0, sceneIndex - 1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                }
                Text("Scenario ${sceneIndex + 1}")
                IconButton(onClick = { sceneIndex = min(scenes.size - 1, sceneIndex + 1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .drawBehind {
                        val centerX: Float = size.width / 2
                        val centerY: Float = size.height / 2

                        turnCircle?.let { circle ->
                            drawCircle(
                                color = turnCircleColor,
                                radius = circle.radius,
                                center = Offset(
                                    x = centerX + circle.centerX,
                                    y = centerY + circle.centerY
                                )
                            )
                        }

//                        debug of car center
//                        drawCircle(
//                            color = Color(0xFF0000FF),
//                            radius = 10f,
//                            center = Offset(
//                                x = centerX + carPosition.offsetX,
//                                y = centerY + carPosition.offsetY
//                            )
//                        )

                        scene.drawings.forEach { drawing ->
                            drawing.draw(scope = this, centerX = centerX, centerY = centerY)
                        }
                    }
            ) {
                scene.cars.forEach { car ->
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset {
                                IntOffset(
                                    x = car.position.offsetX.toInt(),
                                    y = car.position.offsetY.toInt()
                                )
                            }
                            .rotate(car.position.rotationAngle),
                        painter = painterResource(car.image),
                        contentDescription = null
                    )
                }

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
                    painter = painterResource(Res.drawable.sedan_2),
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
                        onValueChange = { steeringAngle.value = it },
                        onValueChangeFinished = { if (abs(steeringAngle.value) < 10f) steeringAngle.value = 0f }
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
            distance = distance
        )
        delay(10)
    }
}

fun CarPosition.drive(steeringAngle: Float, distance: Float): CarPosition {
    val turnCircle: Circle = getTurnCircle(steeringAngle) ?: return moveForward(distance)

    val wheelSign = sign(steeringAngle)
    val motionSign = sign(distance)

    // 2. текущий угол на окружности (СК с Y вверх)
    val currentAngle = atan2(offsetY - turnCircle.centerY, offsetX - turnCircle.centerX)

    // 3. учитываем направление руля и управления (вперёд/назад)
    val rotSign = motionSign * wheelSign
    val signedAngleDelta = (abs(distance) / turnCircle.radius) * rotSign

    // 4. новая точка дуги
    val newAngle = currentAngle + signedAngleDelta
    val newX = turnCircle.centerX + turnCircle.radius * cos(newAngle)
    val newY = turnCircle.centerY + turnCircle.radius * sin(newAngle)

    // 5. пересчитываем угол машины в вашей системе координат
    val newRotation = calculateAngleBetweenPositions(
        oldX = newX,
        oldY = newY,
        newX = turnCircle.centerX,
        newY = turnCircle.centerY
    ) - wheelSign * 90f

    return CarPosition(
        rotationAngle = newRotation,
        offsetX = newX,
        offsetY = newY
    ).also { println("angles: $rotationAngle $currentAngle $newAngle pos: $it") }
}

private fun CarPosition.getTurnCircle(steeringAngle: Float): Circle? {
    if (steeringAngle == 0f) return null

    val wheelSign = sign(steeringAngle)                    // +1 или −1

    // 1. радиус и центр поворота (ваша логика)
    val turnRadius = car.turningRadius.pixels * (car.maxSteeringAngle / abs(steeringAngle))
    val dirToCenter = rotationAngle + wheelSign * 90f
    val radToCenter = degreesToRadians(dirToCenter)
    val centerX = offsetX + turnRadius * sin(radToCenter)
    val centerY = offsetY - turnRadius * cos(radToCenter)

    return Circle(centerX, centerY, turnRadius)
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
