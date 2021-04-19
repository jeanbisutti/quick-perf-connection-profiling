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

package org.quickperf.spring.springboottest.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quickperf.annotation.DisableGlobalAnnotations;
import org.quickperf.spring.junit4.QuickPerfSpringRunner;
import org.quickperf.spring.springboottest.FootballApplication;
import org.quickperf.spring.springboottest.dto.PlayerWithTeamName;
import org.quickperf.spring.springboottest.jpa.entity.Player;
import org.quickperf.spring.springboottest.service.PlayerService;
import org.quickperf.spring.sql.QuickPerfSqlConfig;
import org.quickperf.sql.annotation.DisplaySql;
import org.quickperf.sql.annotation.ExpectSelect;
import org.quickperf.sql.annotation.ProfileConnection;
import org.quickperf.sql.connection.Level;
import org.quickperf.writer.WriterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(QuickPerfSpringRunner.class)
@SpringBootTest(classes = {FootballApplication.class})
public class SpringDbConnectionProfiling {

    public static final String DB_PROFILING_FILE_PATH = findTargetPath() + File.separator + "spring-connection-profiling.txt";

    private static String findTargetPath() {
        Path targetDirectory = Paths.get("target");
        return targetDirectory.toFile().getAbsolutePath();
    }

    @Autowired
    private PlayerService playerService;

    @Test
    @ProfileConnection(displayStackTrace = true
                     , filterStackTrace = true
                     , writerFactory = FileWriterBuilder.class
    )
    public void should_find_all_players_with_team_name() {
        playerService.findPlayersWithTeamName();
    }

    public static class FileWriterBuilder implements WriterFactory {

        @Override
        public Writer buildWriter() throws IOException {
            return new FileWriter(DB_PROFILING_FILE_PATH);
        }

    }

}
