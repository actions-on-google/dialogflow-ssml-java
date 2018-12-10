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

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.api.services.actions_fulfillment.v2.model.SimpleResponse;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSMLExamplesApp extends DialogflowApp {

  private static final Logger LOGGER = LoggerFactory.getLogger(SSMLExamplesApp.class);

  @ForIntent("Welcome")
  public ActionResponse welcome(ActionRequest request) {
    // Build the "welcome" response, which contains the "askExample" response as a substitution
    ResourceBundle responses = ResourceBundle.getBundle("responses");
    String welcome = responses.getString("welcome");
    String askExample = responses.getString("askExample");

    return getResponseBuilder(request)
        .add(MessageFormat.format(welcome, askExample))
        .add(buildExamplesListResponse(responses))
        .build();
  }

  @ForIntent("Fallback")
  public ActionResponse fallback(ActionRequest request) {
    // Build the "didNotUnderstand" response, which contains the "askExample"
    // response as a substitution
    ResourceBundle responses = ResourceBundle.getBundle("responses");
    String didNotUnderstand = responses.getString("didNotUnderstand");
    String askExample = responses.getString("askExample");

    return getResponseBuilder(request)
        .add(MessageFormat.format(didNotUnderstand, askExample))
        .add(buildExamplesListResponse(responses))
        .build();
  }


  @ForIntent("Choose Example")
  public ActionResponse chooseExample(ActionRequest request) {
    // Grab the "element" parameter and respond with the fallback response if it was null
    Object element = request.getParameter("element");
    if (element == null || !(element instanceof String)) {
      LOGGER.warn("Expected parameter 'element' was null or not a String instance");
      return fallback(request);
    }
    // Attempt to find the corresponding element in the examples bundle, and respond with the
    // fallback if impossible
    String elementName = (String) element;
    ResourceBundle examples = ResourceBundle.getBundle("examples");
    if (!examples.containsKey(elementName)) {
      LOGGER.warn(
          MessageFormat.format(
              "Value of ''element'' parameter was ''{0}'', not a valid example name",
              elementName));
      return fallback(request);
    }
    String exampleSSML = examples.getString(elementName);

    // Build the "leadToExample" response, which contains the "example" string as a substitution
    ResourceBundle responses = ResourceBundle.getBundle("responses");
    String leadToExample = responses.getString("leadToExample");

    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(MessageFormat.format(leadToExample, elementName));

    // Add the SSML example to the response
    SimpleResponse ssmlResponse = new SimpleResponse().setSsml(exampleSSML);
    responseBuilder.add(ssmlResponse);

    return responseBuilder.build();

  }

  // Helper to build the examplesList response using the keys of all the examples
  private String buildExamplesListResponse(ResourceBundle responses) {
    // Grab the names of all the SSML examples
    ResourceBundle examples = ResourceBundle.getBundle("examples");
    List<String> keys = examples.keySet().stream().collect(Collectors.toList());
    keys.sort(Comparator.naturalOrder());

    // Format them as a list and include in the "examplesList" response
    List<String> firstKeys = keys.subList(0, keys.size() - 1);
    String listOfKeys = String.join(", ", firstKeys);
    String finalKey = keys.get(keys.size() - 1);

    String examplesList = responses.getString("examplesList");

    return MessageFormat.format(examplesList, listOfKeys, finalKey);
  }

}
