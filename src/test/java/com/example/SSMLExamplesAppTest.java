/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;

public class SSMLExamplesAppTest {

  // Grabs a resource from a file
  private static String fromFile(String fileName) throws IOException {
    Path absolutePath = Paths.get("src", "test", "resources", fileName);
    return new String(Files.readAllBytes(absolutePath));
  }

  // Pretty-prints response JSON so we can compare it with our pretty-printed sample output
  // in a human-readable way
  private static String prettyPrintJson(String jsonInput) {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    JsonParser parser = new JsonParser();
    JsonElement element = parser.parse(jsonInput);

    return gson.toJson(element);
  }

  // Looks up a named request/response pair, calls handleRequest and asserts the responses match
  private void assertCorrectResponse(String testName) throws Exception {
    SSMLExamplesApp app = new SSMLExamplesApp();
    String requestBody = fromFile("request_" + testName + ".json");

    CompletableFuture<String> future = app.handleRequest(requestBody, null);
    String responseJson = future.get();

    String prettyJsonResponse = prettyPrintJson(responseJson);
    String expectedResponse = fromFile("response_" + testName + ".json");
    assertEquals(expectedResponse, prettyJsonResponse);
  }

  @Test
  public void testWelcome() throws Exception {
    assertCorrectResponse("welcome");
  }

  @Test
  public void testFallback() throws Exception {
    assertCorrectResponse("fallback");
  }

  @Test
  public void testChooseExampleHappy() throws Exception {
    assertCorrectResponse("choose_example_speed");
    assertCorrectResponse("choose_example_emphasis");
  }

  @Test
  public void testChooseExampleFallback() throws Exception {
    assertCorrectResponse("choose_example_fallback_null");
    assertCorrectResponse("choose_example_fallback_empty");
  }

}
