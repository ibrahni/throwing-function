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

import static java.util.Objects.requireNonNull;

/**
 * Represents an action that can be performed.
 * Function might throw a checked exception instance.
 *
 * @param <E> the type of the thrown checked exception
 *
 * @author Grzegorz Piwowarek
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {
    void run() throws E;

    static Runnable unchecked(ThrowingRunnable<?> runnable) {
        return requireNonNull(runnable).unchecked();
    }

    static Runnable sneaked(ThrowingRunnable<?> runnable) {
        return requireNonNull(runnable).sneaky();
    }

    /**
     * @return a new Runnable instance which wraps thrown checked exception instance into a RuntimeException
     */
    default Runnable unchecked() {
        return () -> {
            try {
                run();
            } catch (final Exception e) {
                throw new WrappedException(e);
            }
        };
    }

    default Runnable sneaky() {
        return () -> {
            try {
                run();
            } catch (final Exception e) {
                SneakyThrowUtil.sneakyThrow(e);
            }
        };
    }
}
