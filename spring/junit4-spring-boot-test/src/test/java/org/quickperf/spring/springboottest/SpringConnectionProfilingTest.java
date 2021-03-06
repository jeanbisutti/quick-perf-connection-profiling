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

package org.quickperf.spring.springboottest;

import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.quickperf.spring.springboottest.service.SpringDbConnectionProfiling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

public class SpringConnectionProfilingTest {

    @Test public void
    should_profile_connection() throws IOException {

        // GIVEN
        Class<?> testClass = SpringDbConnectionProfiling.class;

        // WHEN
        PrintableResult printableResult = PrintableResult.testResult(testClass);

        // THEN
        assertThat(printableResult.failureCount()).isZero();

        String profilingResult = readContentOf(SpringDbConnectionProfiling.DB_PROFILING_FILE_PATH);

        assertThat(profilingResult.replaceAll("connection .* -", "connection id -"))
                .startsWith("connection id - the datasource gets the connection" + System.lineSeparator()
                          + "\tcom.zaxxer.hikari.HikariDataSource"
                           )
                .contains("\torg.quickperf.spring.springboottest.service.SpringDbConnectionProfiling.should_find_all_players_with_team_name(SpringDbConnectionProfiling.java:65)" + System.lineSeparator()
                        + "connection id - read only set to true" + System.lineSeparator()
                        + "\torg.springframework.jdbc.datasource.DataSourceUtils.prepareConnectionForTransaction(DataSourceUtils.java:184)"
                         )
                .contains("org.quickperf.spring.springboottest.service.SpringDbConnectionProfiling.should_find_all_players_with_team_name(SpringDbConnectionProfiling.java:65)" + System.lineSeparator()
                        + "connection id - auto commit set to false" + System.lineSeparator()
                        + "\torg.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor.begin(AbstractLogicalConnectionImplementor.java:67)"
                         )
                .contains("\torg.quickperf.spring.springboottest.service.SpringDbConnectionProfiling.should_find_all_players_with_team_name(SpringDbConnectionProfiling.java:65)" + System.lineSeparator()
                        + "connection id - commit with transaction_read_committed isolation" + System.lineSeparator()
                        + "\torg.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor.commit(AbstractLogicalConnectionImplementor.java:81)")
                .contains("\torg.quickperf.spring.springboottest.service.SpringDbConnectionProfiling.should_find_all_players_with_team_name(SpringDbConnectionProfiling.java:65)" + System.lineSeparator()
                         + "connection id - auto commit set to true" + System.lineSeparator()
                         + "\torg.hibernate.resource.jdbc.internal.AbstractLogicalConnectionImplementor.resetConnection(AbstractLogicalConnectionImplementor.java:101)")
                .contains("\torg.quickperf.spring.springboottest.service.SpringDbConnectionProfiling.should_find_all_players_with_team_name(SpringDbConnectionProfiling.java:65)" + System.lineSeparator()
                        + "connection id - read only set to false" + System.lineSeparator()
                        + "\torg.springframework.jdbc.datasource.DataSourceUtils.resetConnectionAfterTransaction(DataSourceUtils.java:241)")
                .contains("\torg.quickperf.spring.springboottest.service.SpringDbConnectionProfiling.should_find_all_players_with_team_name(SpringDbConnectionProfiling.java:65)" + System.lineSeparator()
                        + "connection id - closed" + System.lineSeparator()
                        + "\torg.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.closeConnection(DatasourceConnectionProviderImpl.java:127)")
                .endsWith("org.quickperf.spring.springboottest.service.SpringDbConnectionProfiling.should_find_all_players_with_team_name(SpringDbConnectionProfiling.java:65)")
        ;

    }

    private String readContentOf(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .collect(joining(System.lineSeparator()));
    }



}
