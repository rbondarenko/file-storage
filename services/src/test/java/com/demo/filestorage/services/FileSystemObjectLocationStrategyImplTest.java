package com.demo.filestorage.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(value = JUnit4.class)
public class FileSystemObjectLocationStrategyImplTest {

    private FileSystemObjectLocationStrategyImpl sut;

    public FileSystemObjectLocationStrategyImplTest() {
        this.sut = new FileSystemObjectLocationStrategyImpl();
    }

    @Test
    public void pathFor_HappyPath() {
        final UUID input = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        final Path expected = Paths.get("38/40/00/00/38400000-8cf0-11bd-b23e-10b96e4ef00d");

        final Path actual = sut.pathFor(input);

        assertThat(actual, is(equalTo(expected)));
    }
}