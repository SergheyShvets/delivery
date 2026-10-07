package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address extends ValueObject<Address> {
    @Column(name = "country")
    private final String country;

    @Column(name = "city")
    private final String city;

    @Column(name = "street")
    private final String street;

    @Column(name = "house")
    private final String house;

    @Column(name = "apartment")
    private final String apartment;

    public static Result<Address, Error> create(String country, String city, String street, String house,
            String apartment) {
        var countryErr = Guard.againstNullOrEmpty(country, "country");
        var cityErr = Guard.againstNullOrEmpty(city, "city");
        var streetErr = Guard.againstNullOrEmpty(street, "street");
        var houseErr = Guard.againstNullOrEmpty(house, "house");
        var apartmentErr = Guard.againstNullOrEmpty(apartment, "apartment");

        var anyErrorResult = checkIfHasError(countryErr, cityErr, streetErr, houseErr, apartmentErr);
        if (anyErrorResult.isFailure())
            return Result.failure(anyErrorResult.getError());

        return Result.success(new Address(country, city, street, house, apartment));
    }

    private static UnitResult<Error> checkIfHasError(Error... errors) {
        for (Error error : errors) {
            if (error != null) {
                return UnitResult.failure(error);
            }
        }
        return UnitResult.success();
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(country, city, street, house, apartment);
    }
}
