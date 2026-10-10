package microarch.delivery.core.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressTest {

    static Stream<Arguments> invalidWithOneEmptyAddressParam() {
        return Stream.of(Arguments.of("", "Moscow", "Lenina", "18", "100"),
                Arguments.of("Russia", "", "Lenina", "18", "100"), Arguments.of("Russia", "Moscow", "", "18", "100"),
                Arguments.of("Russia", "Moscow", "Lenina", "", "100"),
                Arguments.of("Russia", "Moscow", "Lenina", "18", ""));
    }

    @ParameterizedTest
    @MethodSource("invalidWithOneEmptyAddressParam")
    void shouldReturnErrorWhenParamsIsEmpty(String country, String city, String street, String house,
            String apartment) {

        var incorrectResult = Address.create(country, city, street, house, apartment);
        assertThat(incorrectResult.isFailure()).isTrue();
    }

    static Stream<Arguments> validAddressParams() {
        return Stream.of(Arguments.of("Russia", "Moscow", "Lenina", "18", "100"));
    }

    @ParameterizedTest
    @MethodSource("validAddressParams")
    void shouldReturnSuccessWhenCorrectParams(String country, String city, String street, String house,
            String apartment) {
        var correctResult = Address.create(country, city, street, house, apartment);
        assertThat(correctResult.isSuccess()).isTrue();
    }

}
