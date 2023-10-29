/*
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
package io.trino.operator.aggregation.state;

import io.airlift.stats.TDigest;
import io.trino.spi.function.AccumulatorState;
import io.trino.spi.function.AccumulatorStateMetadata;

@AccumulatorStateMetadata(stateSerializerClass = TDigestAndPercentileStateSerializer.class, stateFactoryClass = TDigestAndPercentileStateFactory.class)
public interface TDigestAndPercentileState
        extends AccumulatorState
{
    TDigest getDigest();

    void setDigest(TDigest digest);

    double getPercentile();

    void setPercentile(double percentile);

    void addMemoryUsage(int value);
}
