package com.todo;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;

public class FileUtils {
	   private static final String RESOURCES_PATH = "src/test/resources";

	    public static String getResourceAsString(String path) {
	        try (InputStream is = getResourceAsStream(path)) {
	            if (is == null) {
	                throw new IOException(String.format("Resource error [%s]", path));
	            }
	            return IOUtils.toString(is, String.valueOf(Charset.defaultCharset()));
	        } catch (IOException e) {
	        	Assert.fail(e.getMessage());
	        }
	        return null;
	    }

	    public static List<Path> getAvailableResourceFiles(String path) {
	        try (Stream<Path> paths = Files.list(Paths.get(RESOURCES_PATH, path))) {
	            return paths
	                    .filter(Files::isRegularFile)
	                    .map(Path::getFileName)
	                    .collect(Collectors.toList());
	        } catch (IOException e) {
	            Assert.fail(e.getMessage());
	        }
	        return List.of();
	    }
		
}
