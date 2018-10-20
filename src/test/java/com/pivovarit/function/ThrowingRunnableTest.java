package com.pivovarit.function;

import com.pivovarit.function.exception.WrappedException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThrowingRunnableTest {

    @Test
    void shouldRun() {

        // given
        ThrowingRunnable<Exception> runnable = () -> { throw new IOException("some message"); };

        // when
        assertThatThrownBy(runnable::run)
          .isInstanceOf(IOException.class)
          .hasMessage("some message");
    }

    @Test
    void shouldRunUnchecked() {
        // given
        ThrowingRunnable<Exception> runnable = () -> { };

        // when
        runnable.unchecked().run();

        // then WrappedException thrown
    }

    @Test
    void shouldRunUncheckedAndThrow() {
        IOException cause = new IOException("some message");

        // given
        ThrowingRunnable<Exception> runnable = () -> { throw cause; };

        // when
        assertThatThrownBy(() -> runnable.unchecked().run())
          .isInstanceOf(WrappedException.class)
          .hasMessage(cause.getMessage())
          .hasCause(cause);
    }

    @Test
    void shouldRunUncheckedAndThrowUsingUtilsMethod() {
        IOException cause = new IOException("some message");

        // given
        ThrowingRunnable<Exception> runnable = () -> { throw cause; };

        // when
        assertThatThrownBy(() -> ThrowingRunnable.unchecked(runnable).run())
          .isInstanceOf(WrappedException.class)
          .hasMessage(cause.getMessage())
          .hasCause(cause);
    }

    @Test
    void shouldNotWrapInRuntimeExWhenUsingSneaked() {
        class ToBeThrown extends Exception {}

        class ForTest {
            void throwsCheckedException() throws Exception {
                throw new ToBeThrown();
            }
        }
        assertThatThrownBy(() -> ThrowingRunnable.sneaked(new ForTest()::throwsCheckedException).run())
                .isInstanceOf(ToBeThrown.class);
    }
}
