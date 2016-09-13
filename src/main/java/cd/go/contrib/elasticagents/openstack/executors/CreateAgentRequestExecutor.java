/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagents.openstack.executors;

import cd.go.contrib.elasticagents.openstack.*;
import cd.go.contrib.elasticagents.openstack.requests.CreateAgentRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import static cd.go.contrib.elasticagents.openstack.OpenStackPlugin.LOG;

public class CreateAgentRequestExecutor implements RequestExecutor{
    private final AgentInstances agentInstances;
    private final PluginRequest pluginRequest;
    private final CreateAgentRequest request;

    public CreateAgentRequestExecutor(CreateAgentRequest request, AgentInstances agentInstances, PluginRequest pluginRequest) {
        this.request = request;
        this.agentInstances = agentInstances;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        Agents agents = pluginRequest.listAgents();
        for (Agent agent : agents.agents()) {
            if ((agent.agentState() == Agent.AgentState.Idle) || (agent.agentState() == Agent.AgentState.Building)){
                if (agentInstances.matchInstance(agent.elasticAgentId(),request.properties())) {
                    return new DefaultGoPluginApiResponse(200);
                }
            }
        }
        agentInstances.create(request, pluginRequest.getPluginSettings());
        return new DefaultGoPluginApiResponse(200);
    }
}
