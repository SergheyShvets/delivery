package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import libs.errs.*;
import libs.errs.Error;
import lombok.Getter;

import java.util.List;

public class Volume extends ValueObject<Volume> {
    private static final int MIN_VALUE = 0;

    @Getter
    private final int value;

    private Volume(int value) {
        this.value = value;
    }

    public static Result<Volume, Error> create(int value) {
        var lessMinErr = Guard.againstLessOrEqual(value, MIN_VALUE, "Volume");
        if (lessMinErr != null) return Result.failure(lessMinErr);

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
