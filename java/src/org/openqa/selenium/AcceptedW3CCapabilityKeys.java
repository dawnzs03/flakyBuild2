// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AcceptedW3CCapabilityKeys implements Predicate<String> {

  private static final Predicate<String> ACCEPTED_W3C_PATTERNS =
      Stream.of(
              "^[\\w-]+:.*$",
              "^acceptInsecureCerts$",
              "^browserName$",
              "^browserVersion$",
              "^platformName$",
              "^pageLoadStrategy$",
              "^proxy$",
              "^setWindowRect$",
              "^strictFileInteractability$",
              "^timeouts$",
              "^unhandledPromptBehavior$",
              "^webSocketUrl$") // from webdriver-bidi
          .map(Pattern::compile)
          .map(Pattern::asPredicate)
          .reduce(identity -> false, Predicate::or);

  @Override
  public boolean test(String capabilityName) {
    return ACCEPTED_W3C_PATTERNS.test(capabilityName);
  }
}
