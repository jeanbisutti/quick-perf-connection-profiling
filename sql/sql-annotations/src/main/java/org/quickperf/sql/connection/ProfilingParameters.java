/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 * Copyright 2019-2021 the original author or authors.
 */

package org.quickperf.sql.connection;

import org.quickperf.perfrecording.IPerfRecorderParameters;
import org.quickperf.writer.WriterFactory;

public class ProfilingParameters implements IPerfRecorderParameters {

    private final Level level;

    private final boolean beforeAndAfterTestMethodExecution;

    private final boolean displayStackTrace;

    private final short stackLength;

    private final Class<? extends WriterFactory> writerFactoryClass;
    private final boolean filterStackTrace;

    public ProfilingParameters(Level level, boolean profileBeforeAndTestMethodExecution
                             , boolean displayStackTrace, boolean filterStackTrace
                          , short stackLength, Class<? extends WriterFactory> writerFactoryClass) {
        this.level = level;
        this.beforeAndAfterTestMethodExecution = profileBeforeAndTestMethodExecution;
        this.displayStackTrace = displayStackTrace;
        this.filterStackTrace = filterStackTrace;
        this.stackLength = stackLength;
        this.writerFactoryClass = writerFactoryClass;
    }

    public Level getLevel() {
        return level;
    }

    public boolean isBeforeAndAfterTestMethodExecution() {
        return beforeAndAfterTestMethodExecution;
    }

    public boolean isDisplayStackTrace() {
        return displayStackTrace;
    }

    public boolean isFilterStackTrace() {
        return filterStackTrace;
    }

    public short getStackLength() {
        return stackLength;
    }

    public Class<? extends WriterFactory> getWriterFactoryClass() {
        return writerFactoryClass;
    }

}
