/*
 * Copyright (C) 2020 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.turbine

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import org.junit.Test

class FlowAssertTest {
  @Test fun timeoutEnforcedByDefault() = suspendTest {
    val subject = async {
      neverFlow().test {
        expectComplete()
      }
    }

    advanceTimeBy(999)
    assertThat(subject.isActive).isTrue()

    advanceTimeBy(1)
    assertThat(subject.isActive).isFalse()

    assertThrows<TimeoutCancellationException> {
      subject.await()
    }.hasMessageThat().isEqualTo("Timed out waiting for 1000 ms")
  }

  @Test fun timeoutEnforcedCustomValue() = suspendTest {
    val subject = async {
      neverFlow().test(timeoutMs = 10_000) {
        expectComplete()
      }
    }

    advanceTimeBy(9999)
    assertThat(subject.isActive).isTrue()

    advanceTimeBy(1)
    assertThat(subject.isActive).isFalse()

    assertThrows<TimeoutCancellationException> {
      subject.await()
    }.hasMessageThat().isEqualTo("Timed out waiting for 10000 ms")
  }
}