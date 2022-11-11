package com.ben.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Try<E> extends Value<Try, E> {

    boolean isSuccess();
    boolean isFailure();
    Exception getError();

    @Override
    default boolean isEmpty() {
        return isFailure();
    }

    default <O> Try<O> map(Function<? super E, ? extends O> mapper) {
        Objects.requireNonNull(mapper);
        return isSuccess() ? success(mapper.apply(get())) : Failure.cast(this);
    }


    interface CheckedConsumer<O> {
        void accept(O o) throws Exception;
    }

    interface CheckedFunction<I, O> {
        O apply(I i) throws Exception;
    }

    static <O> Function<O, Try<Void>> sink(CheckedConsumer<O> consumer) {
        Objects.requireNonNull(consumer);
        return o -> {
            try {
                consumer.accept(o);
                return success(null);
            } catch (Exception error) {
                return failure(error);
            }
        };
    }

    static <I, O> Function<I, Try<O>> lift(CheckedFunction<I, O> mapper) {
        Objects.requireNonNull(mapper);
        return i -> {
            try {
                O value = mapper.apply(i);
                return success(value);
            } catch (Exception error) {
                return failure(error);
            }
        };
    }

    static <O> Try<O> success(O value) {
        return new Success<>(value);
    }

    static <O> Try<O> failure(Exception error) {
        return new Failure<>(error);
    }

    static <O> Try<O> of(Callable<O> supplier) {
        Objects.requireNonNull(supplier);
        try {
            O value = supplier.call();
            return success(value);
        } catch (Exception error) {
            return failure(error);
        }
    }


    @Override
    default Try<E> or(Supplier<? extends Value<Try, ? extends E>> alternative) {
        Objects.requireNonNull(alternative);
        return isSuccess() ? this : Value.Type.<Try<E>, Try, E>narrow(alternative.get());
    }

    @Override
    default Try<E> orUse(Value<Try, ? extends E> alternative) {
        Objects.requireNonNull(alternative);
        return isSuccess() ? this : Value.Type.<Try<E>, Try, E>narrow(alternative);
    }

    default Try<E> orLift(Supplier<? extends E> alternative) {
        Objects.requireNonNull(alternative);
        return isSuccess() ? this : Try.of(alternative::get);
    }

    default Try<E> orIfMatch(Predicate<Exception> errorTest, Callable<E> callable, int repeats) {
        return orIfMatch(errorTest, callable, repeats, 0);
    }

    default Try<E> orIfMatch(Predicate<Exception> errorTest, Callable<E> callable, int repeats, long milliseconds) {
        Objects.requireNonNull(errorTest);
        Objects.requireNonNull(callable);
        Try<E> result = this;
        while (repeats > 0) {
            if (result.isSuccess() || !errorTest.test(result.getError())) {
                return result;
            }
            if (milliseconds > 0) {
                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result = Try.of(callable);
            repeats--;
        }
        return result;
    }

    default <X extends Exception> E orElseThrowMapped(Function<Exception, ? extends X> errorMapper) throws X {
        Objects.requireNonNull(errorMapper);
        if (isSuccess()) {
            return get();
        }
        throw errorMapper.apply(getError());
    }


    default void consumeElse(Consumer<E> ifSuccess, Consumer<Exception> ifError) {
        Objects.requireNonNull(ifSuccess);
        Objects.requireNonNull(ifError);
        if (isSuccess()) {
            ifSuccess.accept(get());
        } else {
            ifError.accept(getError());
        }
    }

    default void ifError(Consumer<Exception> ifError) {
        Objects.requireNonNull(ifError);
        if (isFailure()) {
            ifError.accept(getError());
        }
    }

    class Success<E> implements Try<E> {

        private final E value;

        private Success(E value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public E get() {
            return value;
        }

        @Override
        public Exception getError() {
            throw new NoSuchElementException();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(get());
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj instanceof Success && Objects.equals(get(), ((Success) obj).get());
        }

        @Override
        public String toString() {
            return "Success(" + get() + ")";
        }
    }

    class Failure<E> implements Try<E> {

        private final Exception error;

        private Failure(Exception error) {
            this.error = error;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public E get() {
            throw new RuntimeException("Error triggered by operation: " + error.getMessage(), error);
        }

        @Override
        public Exception getError() {
            return error;
        }

        @SuppressWarnings("unchecked")
        private static <O, U> Failure<U> cast(Try<O> toCast) {
            return (Failure<U>) toCast;
        }

        @Override
        public String toString() {
            return "Failure(" + getError().getMessage() + ")";
        }
    }
}
