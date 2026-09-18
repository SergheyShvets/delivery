package microarch.delivery.core.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class VolumeTest {

    static Stream<Integer> invalidLessMinValue() {
        return Stream.of(0, -1, -10);
    }

    @ParameterizedTest
    @MethodSource("invalidLessMinValue")
    void shouldReturnErrorWhenParamsIsLess(int value) {
        var incorrectResult = Volume.create(value);
        assertThat(incorrectResult.isFailure()).isTrue();
    }

    static Stream<Integer> validLessMinValue() {
        return Stream.of(1, 5, 10);
    }

    @ParameterizedTest
    @MethodSource("validLessMinValue")
    void shouldReturnSuccessWhenCorrectParams(int value) {
        var incorrectResult = Volume.create(value);
        assertThat(incorrectResult.isSuccess()).isTrue();
    }
}
