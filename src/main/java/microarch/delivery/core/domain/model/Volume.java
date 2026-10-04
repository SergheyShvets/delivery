package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Volume extends ValueObject<Volume> {
    private static final int MIN_VALUE = 0;

    @Column(name = "volume")
    private final int value;

    public static Result<Volume, Error> create(int value) {
        var lessMinErr = Guard.againstLessOrEqual(value, MIN_VALUE, "Volume");
        if (lessMinErr != null)
            return Result.failure(lessMinErr);

        return Result.success(new Volume(value));
    }

    public Result<Volume, Error> addAndCreate(Volume... newVolumes) {
        var sum = value;
        for (Volume newVolume : newVolumes) {
            if (newVolume == null)
                return Result.failure(GeneralErrors.valueIsInvalid("newVolumes", newVolumes));
            sum += newVolume.getValue();
        }

        return Result.success(new Volume(sum));
    }

    public static Volume mustCreate(int value) {
        return create(value).getValueOrThrow();
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.value);
    }
}
