package com.ben.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Value<INSTANCE extends Value, E> {
    boolean isEmpty();
    E get();
    Value<INSTANCE, E> or(Supplier<? extends Value<INSTANCE, ? extends E>> alternative);
    Value<INSTANCE, E> orUse(Value<INSTANCE, ? extends E> alternative);
    Value<INSTANCE, E> orLift(Supplier<? extends E> alternative);

    default Stream<E> stream() {
        return isEmpty() ? Stream.empty() : Stream.of(get());
    }

    default void doOrElse(Consumer<? super E> ifSome, Runnable ifEmpty) {
        Objects.requireNonNull(ifSome, "ifSome");
        Objects.requireNonNull(ifEmpty, "ifEmpty");
        if (isEmpty()) {
            ifEmpty.run();
        } else {
            ifSome.accept(get());
        }
    }

    default E orElse(E alternative) {
        return isEmpty() ? alternative : get();
    }

    default E orElseNull() {
        return orElse(null);
    }

    default E orElseSupply(Supplier<? extends E> supplier) {
        Objects.requireNonNull(supplier);
        return isEmpty() ? supplier.get() : this.get();
    }

    default <X extends Throwable> E orElseThrow(Supplier<? extends X> throwableFactory) throws X {
        ifEmptyThrow(throwableFactory);
        return get();
    }

    default <X extends Throwable> void ifEmptyThrow(Supplier<? extends X> throwableFactory) throws X {
        Objects.requireNonNull(throwableFactory, "throwableFactory");
        if (isEmpty()) {
            throw throwableFactory.get();
        }
    }

    default <X extends Throwable> void ifNotEmptyThrow(Function<? super E, ? extends X> throwableFactory) throws X {
        Objects.requireNonNull(throwableFactory, "throwableFactory");
        if (!isEmpty()) {
            throw throwableFactory.apply(get());
        }
    }

    interface Type {

        /**
         * Safely cast a {@link Value} to a instance subclass type
         * @param higher the {@link Value} to cast
         * @param <INSTANCE> the instance type extending {@link Value}
         * @param <U> the type parameter of the {@link Value}
         * @param <OUT> the final narrowed type of the {@link Value}
         */
        @SuppressWarnings("unchecked")
        static <OUT extends Value<INSTANCE, U>, INSTANCE extends Value, U> OUT narrow(Value<INSTANCE, ? extends U> higher) {
            return (OUT) higher;
        }
    }
}
