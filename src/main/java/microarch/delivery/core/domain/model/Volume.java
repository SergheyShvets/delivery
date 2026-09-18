package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
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

    public static Volume mustCreate(int value) {
        return create(value).getValueOrThrow();
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.value);
    }
}
