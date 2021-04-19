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

import org.quickperf.TestExecutionContext;
import org.quickperf.measure.BooleanMeasure;
import org.quickperf.writer.PrintWriterBuilder;
import org.quickperf.writer.WriterFactory;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ConnectionProfiler extends ConnectionsListener<BooleanMeasure> {

    private final ProfilingParameters profilingParameters;

    private final PrintWriter printWriter;

    private boolean justBeforeTestMethodExecution;

    private boolean justAfterTestMethodExecution;

    public ConnectionProfiler(ProfilingParameters profilingParameters) {
        this.profilingParameters = profilingParameters;

        Class<? extends WriterFactory> writerFactoryClass = profilingParameters.getWriterFactoryClass();

        printWriter = PrintWriterBuilder.INSTANCE.buildPrintWriterFrom(writerFactoryClass);

        ConnectionListenerRegistry.INSTANCE.register(this);
    }

    @Override
    public void theDatasourceGetsTheConnection(Connection connection) {
        print(connection, "the datasource gets the connection");
    }

    private void displayOnConsoleForTraceLevel(Connection connection, String text) {
        if(profilingParameters.getLevel() == Level.TRACE) {
            print(connection, text);
        }
    }

    private void print(Connection connection, String text) {
        if (shouldPrintInfo()) {
            printWriter.println(databaseConnectionHeader(connection) + " - " + text);
            printWriter.flush();
            if (profilingParameters.isDisplayStackTrace()) {
                printStackTrace();
            }
        }
    }

    private boolean shouldPrintInfo() {
        return      profilingParameters.isBeforeAndAfterTestMethodExecution()
                || (justBeforeTestMethodExecution && !justAfterTestMethodExecution);
    }

    private void printStackTrace() {

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        short stackDepth = profilingParameters.getStackLength();

        StackTrace stackTrace = StackTrace.of(stackTraceElements);

        if(profilingParameters.isFilterStackTrace()) {
            stackTrace = StackTrace.of(stackTraceElements)
                        .filterQuickPerfAndTestingFrameworkElements();
        }

        StackTraceElement[] elementsToDisplay = stackTrace
                                               .limitDepthTo(stackDepth)
                                               .getElements();

        print(elementsToDisplay);

    }

    private void print(StackTraceElement[] elementsToDisplay) {
        for (StackTraceElement stackTraceElement : elementsToDisplay) {
            printWriter.println("\t" + stackTraceElement);
            printWriter.flush();
        }
    }

    @Override
    public void commit(Connection connection) {
        String transactionIsolationAsString = extractTransactionIsolationOf(connection);
        print(connection,"commit with " + transactionIsolationAsString + " isolation");
    }

    @Override
    public void close(Connection connection) {
        print(connection, "closed");
    }

    @Override
    public void setReadOnly(Connection connection, boolean readOnly) {
        print(connection,"read only set to " + readOnly);
    }

    @Override
    public void createStatement(Connection connection) {
        displayOnConsoleForTraceLevel(connection, "create statement");
    }

    @Override
    public void prepareStatement(Connection connection, String sql) {
        displayOnConsoleForTraceLevel(connection, "prepare statement with " + sql + " (SQL)");
    }

    @Override
    public void prepareCall(Connection connection, String sql) {
        displayOnConsoleForTraceLevel(connection, "prepare callable statement with " + sql + " (SQL)");
    }

    @Override
    public void nativeSQL(Connection connection, String sql) {
        displayOnConsoleForTraceLevel(connection, "native SQL " + sql);
    }

    @Override
    public void setAutoCommit(Connection connection, boolean autoCommit) {
        print(connection, "auto commit set to " + autoCommit);
    }

    @Override
    public void rollback(Connection connection) {
        print(connection, "rollback");
    }

    @Override
    public void setCatalog(Connection connection, String catalog) {
        print(connection, "set catalog: " + catalog);
    }

    @Override
    public void setTransactionIsolation(Connection connection, int level) {
        String transactionIsolationAsString = extractTransactionIsolationOf(connection);
        print(connection, "set transaction isolation to " + level + " (" + transactionIsolationAsString + ")");
    }

    @Override
    public void clearWarnings(Connection connection) {
        displayOnConsoleForTraceLevel(connection, "clear warnings");
    }

    @Override
    public void createStatement(Connection connection, int resultSetType, int resultSetConcurrency) {
        displayOnConsoleForTraceLevel(connection, "create statement with " + resultSetType + " (resultSetType), " + resultSetConcurrency + " (resultSetConcurrency)");
    }

    @Override
    public void prepareStatement(Connection connection, String sql, int resultSetType, int resultSetConcurrency) {
        displayOnConsoleForTraceLevel(connection, "prepare statement with " + sql + "(SQL), " + resultSetType + " (resultSetType), " + resultSetConcurrency + " (resultSetConcurrency)");
        super.prepareStatement(connection, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public void prepareCall(Connection connection, String sql, int resultSetType, int resultSetConcurrency) {
        displayOnConsoleForTraceLevel(connection, "prepare call with " + sql + "(SQL), " + resultSetType + " (resultSetType), " + resultSetConcurrency + " (resultSetConcurrency)");
    }

    @Override
    public void setTypeMap(Connection connection, Map<String, Class<?>> map) {
        displayOnConsoleForTraceLevel(connection, "type map set to " + map);
    }

    @Override
    public void setHoldability(Connection connection, int holdability) {
        displayOnConsoleForTraceLevel(connection, "holdability set to " + holdability);
    }

    @Override
    public void setSavepoint(Connection connection) {
        displayOnConsoleForTraceLevel(connection, "set save point");
    }

    @Override
    public void setSavepoint(Connection connection, String name) {
        displayOnConsoleForTraceLevel(connection, "set save point with " + name + " (name)");

    }

    @Override
    public void rollback(Connection connection, Savepoint savepoint) {
        print(connection, "set rollback with " + savepoint + " (save point)");
    }

    @Override
    public void releaseSavepoint(Connection connection, Savepoint savepoint) {
        print(connection, "release " + savepoint + " (save point)");
    }

    @Override
    public void createStatement(Connection connection, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        displayOnConsoleForTraceLevel(connection, "create statement with " + resultSetType + "(resultSetType), " + resultSetConcurrency + " (resultSetConcurrency), " + resultSetHoldability + " (resultSetHoldability)");
    }

    @Override
    public void prepareStatement(Connection connection, String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        displayOnConsoleForTraceLevel(connection, "prepare statement with " + sql + "(sql), " + resultSetType + " (resultSetType), "
                + resultSetConcurrency + " (resultSetConcurrency), " + resultSetHoldability + " (resultSetHoldability)");
    }

    @Override
    public void prepareCall(Connection connection, String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        displayOnConsoleForTraceLevel(connection, "prepare callable statement with " + sql + "(sql), " + resultSetType + " (resultSetType), "
                + resultSetConcurrency + " (resultSetConcurrency), " + resultSetHoldability + " (resultSetHoldability)");
    }

    @Override
    public void prepareStatement(Connection connection, String sql, int autoGeneratedKeys) {
        displayOnConsoleForTraceLevel(connection, "prepare callable statement with " + sql + "(sql), " + autoGeneratedKeys + " (autoGeneratedKeys)");
    }

    @Override
    public void prepareStatement(Connection connection, String sql, int[] columnIndexes) {
        displayOnConsoleForTraceLevel(connection, "prepare statement with " + sql + "(sql), " + Arrays.toString(columnIndexes) + " (columnIndexes)");
    }

    @Override
    public void prepareStatement(Connection connection, String sql, String[] columnNames) {
        displayOnConsoleForTraceLevel(connection, "prepare statement with " + sql + "(sql), " + Arrays.toString(columnNames) + " (columnNames)");
    }

    @Override
    public void createClob(Connection connection) {
        displayOnConsoleForTraceLevel(connection, "create CLOB");
    }

    @Override
    public void createBlob(Connection connection) {
        displayOnConsoleForTraceLevel(connection, "create BLOB");
    }

    @Override
    public void createNClob(Connection connection) {
        displayOnConsoleForTraceLevel(connection, "create N CLOB");
    }

    @Override
    public void createSQLXML(Connection connection) {
        displayOnConsoleForTraceLevel(connection, "create SQL XML");
    }

    @Override
    public void setClientInfo(Connection connection, String name, String value) {
        displayOnConsoleForTraceLevel(connection, "set client info to " + name + "(name) and " + value + " (value)");
    }

    @Override
    public void setClientInfo(Connection connection, Properties properties) {
        displayOnConsoleForTraceLevel(connection, "set client info to " + properties + "(properties)");
    }

    @Override
    public void createArrayOf(Connection connection, String typeName, Object[] elements) {
        displayOnConsoleForTraceLevel(connection, "create array of with " + typeName + " (type name) and " + Arrays.toString(elements) + " (elements)");
    }

    @Override
    public void createStruct(Connection connection, String typeName, Object[] attributes) {
        displayOnConsoleForTraceLevel(connection, "create struct with " + typeName + " (type name) and " + Arrays.toString(attributes) + " (attributes)");
    }

    @Override
    public void setSchema(Connection connection, String schema) {
        displayOnConsoleForTraceLevel(connection, "set schema to " + schema);
    }

    @Override
    public void abort(Connection connection, Executor executor) {
        print(connection, "set abort with executor");
    }

    @Override
    public void setNetworkTimeout(Connection connection, Executor executor, int milliseconds) {
        print(connection, "set network timeout to " + milliseconds + " (milliseconds) with executor");
    }

    private String databaseConnectionHeader(Connection connection) {
        return "connection " + computeIdentifier(connection);
    }

    private int computeIdentifier(Connection connection) {
        return connection.hashCode();
    }

    private String extractTransactionIsolationOf(Connection connection) {
        int transactionIsolation;
        try {
            transactionIsolation = connection.getTransactionIsolation();
        } catch (SQLException sqlException) {
            return "";
        }
        return formatAsString(transactionIsolation);
    }

    private String formatAsString(int transactionIsolation) {
        if(transactionIsolation == 0) {
            return "transaction_none";
        } else if(transactionIsolation == 1) {
            return "transaction_read_uncommitted";
        } else if (transactionIsolation == 2) {
            return "transaction_read_committed";
        } else if(transactionIsolation == 8) {
            return "transaction_serializable";
        }
        return "";
    }

    @Override
    public void startRecording(TestExecutionContext testExecutionContext) {
        justBeforeTestMethodExecution = true;
    }

    @Override
    public void stopRecording(TestExecutionContext testExecutionContext) {
        justAfterTestMethodExecution = true;
    }

    @Override
    public BooleanMeasure findRecord(TestExecutionContext testExecutionContext) {
        return null;
    }

    @Override
    public void cleanResources() {
        ConnectionListenerRegistry.unregister(this);
    }

}
