package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationTest {

    static Stream<Arguments> invalidLessMinCoordinates() {
        return Stream.of(
                Arguments.of(0, 2),
                Arguments.of(6, 0),
                Arguments.of(-3, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidLessMinCoordinates")
    void shouldReturnErrorWhenParamsIsLessOfRangeLocation(int x, int y) {
        var incorrectResult = Location.create(x, y);
        assertThat(incorrectResult.isFailure()).isTrue();
    }

    static Stream<Arguments> invalidMoreMaxCoordinates() {
        return Stream.of(
                Arguments.of(1, 11),
                Arguments.of(11, 5),
                Arguments.of(123, 11)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMoreMaxCoordinates")
    void shouldReturnErrorWhenParamsIsMoreOfRangeLocation(int x, int y) {
        var incorrectResult = Location.create(x, y);
        assertThat(incorrectResult.isFailure()).isTrue();
    }

    static Stream<Arguments> validCoordinates() {
        return Stream.of(
                Arguments.of(2, 5),
                Arguments.of(1, 1),
                Arguments.of(10, 10)
        );
    }

    @ParameterizedTest
    @MethodSource("validCoordinates")
    void shouldBeCorrectWhenParamsInTheRangeOnCreated(int x, int y) {
        var correctResult = Location.create(x, y);
        assertThat(correctResult.isSuccess()).isTrue();
    }

    @Test
    void shouldBeCorrectWhenLocationsIsTheSameWhenHasTheSameCoordinates() {
        int coordinateX = 1;
        int coordinateY = 5;

        var firstLocation = Location.mustCreate(coordinateX, coordinateY);
        var secondLocation = Location.mustCreate(coordinateX, coordinateY);

        assertThat(firstLocation.equals(secondLocation)).isTrue();
    }


    static Stream<Arguments> locationsAndSteps() {
        return Stream.of(
                Arguments.of(Location.mustCreate(1, 1), Location.mustCreate(10, 10), 18),
                Arguments.of(Location.mustCreate(10, 10), Location.mustCreate(1, 1), 18),
                Arguments.of(Location.mustCreate(2, 5), Location.mustCreate(2, 5), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("locationsAndSteps")
    void shouldBeCorrectWhenCountStepsBetweenLocations(Location firstLocation, Location secondLocation, int correctSteps) {
        assertThat(firstLocation.countStepsTo(secondLocation) == correctSteps).isTrue();
    }
}
