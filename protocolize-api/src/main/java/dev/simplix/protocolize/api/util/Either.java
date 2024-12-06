package dev.simplix.protocolize.api.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Either<L, R> {

    public static <L, R> Either<L, R> left(L value) {
        return new Either<>(Optional.of(value), Optional.empty());
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either<>(Optional.empty(), Optional.of(value));
    }

    private final Optional<L> left;
    private final Optional<R> right;

    private Either(Optional<L> left, Optional<R> right) {
        if (!left.isPresent() == !right.isPresent()) {
            throw new IllegalArgumentException(
                "!left.isPresent() and !right.isPresent() cannot be equal");
        }
        this.left = left;
        this.right = right;
    }

    public boolean isLeft() {
        return this.left.isPresent();
    }

    public boolean isRight() {
        return this.right.isPresent();
    }

    public L getLeft() {
        return this.left.get();
    }

    public R getRight() {
        return this.right.get();
    }

    public <T> T map(
        Function<? super L, ? extends T> leftFunction,
        Function<? super R, ? extends T> rightFunction
    ) {
        return this.left
            .<T>map(l -> Objects.requireNonNull(leftFunction.apply(l)))
            .orElseGet(() ->
                this.right
                    .map(r -> Objects.requireNonNull(rightFunction.apply(r)))
                    .orElseThrow(() ->
                        new IllegalStateException(
                            "should never get here")));
    }

    public <T> Either<T, R> mapLeft(
        Function<? super L, ? extends T> leftFunction
    ) {
        return new Either<>(
            this.left.map(l ->
                Objects.requireNonNull(leftFunction.apply(l))),
            this.right);
    }

    public <T> Either<L, T> mapRight(
        Function<? super R, ? extends T> rightFunction
    ) {
        return new Either<>(
            this.left,
            this.right.map(r ->
                Objects.requireNonNull(rightFunction.apply(r))));
    }

    public void forEach(
        Consumer<? super L> leftAction,
        Consumer<? super R> rightAction
    ) {
        this.left.ifPresent(leftAction);
        this.right.ifPresent(rightAction);
    }
}

