/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pivovarit.function;

import com.pivovarit.function.exception.WrappedException;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents a function that accepts zero arguments and returns some value.
 * Function might throw a checked exception instance.
 *
 * @param <T> the type of the output to the function
 * @param <E> the type of the thrown checked exception
 * @author Grzegorz Piwowarek
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {
    T get() throws E;

    /**
     * @return this Consumer instance as a new Function instance
     */
    default ThrowingFunction<Void, T, E> asFunction() {
        return arg -> get();
    }

    static <T> Supplier<T> unchecked(ThrowingSupplier<T, ?> supplier) {
        return requireNonNull(supplier).uncheck();
    }

    static <T> Supplier<Optional<T>> lifted(ThrowingSupplier<T, ?> supplier) {
        return requireNonNull(supplier).lift();
    }

    static <T> Supplier<T> sneaked(ThrowingSupplier<T, ?> supplier) {
        return requireNonNull(supplier).sneak();
    }

    /**
     * @return a new Supplier instance which wraps thrown checked exception instance into a RuntimeException
     */
    default Supplier<T> uncheck() {
        return () -> {
            try {
                return get();
            } catch (final Exception e) {
                throw new WrappedException(e);
            }
        };
    }

    /**
     * @return a new Supplier that returns the result as an Optional instance. In case of a failure, empty Optional is
     * returned
     */
    default Supplier<Optional<T>> lift() {
        return () -> {
            try {
                return Optional.of(get());
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    default Supplier<T> sneak() {
        return () -> {
            try {
                return get();
            } catch (final Exception e) {
                return SneakyThrowUtil.sneakyThrow(e);
            }
        };
    }
}
