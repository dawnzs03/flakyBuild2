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

package org.openqa.selenium.grid.data;

import java.util.function.Consumer;
import org.openqa.selenium.events.Event;
import org.openqa.selenium.events.EventListener;
import org.openqa.selenium.events.EventName;
import org.openqa.selenium.internal.Require;

public class NodeDrainComplete extends Event {
  private static final EventName NODE_DRAIN_COMPLETE = new EventName("node-drain-complete");

  public NodeDrainComplete(NodeId id) {
    super(NODE_DRAIN_COMPLETE, id);
  }

  public static EventListener<NodeId> listener(Consumer<NodeId> handler) {
    Require.nonNull("Handler", handler);

    return new EventListener<>(NODE_DRAIN_COMPLETE, NodeId.class, handler);
  }
}
