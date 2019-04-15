package com.demo.filestorage.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class FileStorageServiceTest {

    @Autowired
    private FileStorageService sut;

    @Test
    public void contextLoads() {
        assertEquals(sut.listAll().count(), 0);
    }

    @SpringBootApplication
    @Configuration
    static class TestConfiguration {
        @Bean
        public IdGenerator idGenerator() {
            return new AlternativeJdkIdGenerator();
        }

        @Bean
        public FileSystemRepositoryProperties fileSystemRepositoryProperties() {
            final FileSystemRepositoryProperties props = new FileSystemRepositoryProperties();
            props.setRootDir("C:/Temp/test-repo");
            return props;
        }
    }
}
