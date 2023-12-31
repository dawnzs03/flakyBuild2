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

package org.openqa.selenium.docker.v1_41;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.openqa.selenium.docker.v1_41.V141Docker.DOCKER_API_VERSION;
import static org.openqa.selenium.remote.http.HttpMethod.GET;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.openqa.selenium.docker.ContainerId;
import org.openqa.selenium.docker.ContainerLogs;
import org.openqa.selenium.internal.Require;
import org.openqa.selenium.remote.http.Contents;
import org.openqa.selenium.remote.http.HttpHandler;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;

class GetContainerLogs {

  private static final Logger LOG = Logger.getLogger(GetContainerLogs.class.getName());
  private final HttpHandler client;

  public GetContainerLogs(HttpHandler client) {
    this.client = Require.nonNull("HTTP client", client);
  }

  public ContainerLogs apply(ContainerId id) {
    Require.nonNull("Container id", id);

    String requestUrl =
        String.format("/v%s/containers/%s/logs?stdout=true&stderr=true", DOCKER_API_VERSION, id);

    HttpResponse res =
        client.execute(new HttpRequest(GET, requestUrl).addHeader("Content-Type", "text/plain"));
    if (res.getStatus() != HTTP_OK) {
      LOG.warning("Unable to inspect container " + id);
    }
    List<String> logLines = Arrays.asList(Contents.string(res).split("\n"));
    return new ContainerLogs(id, logLines);
  }
}
