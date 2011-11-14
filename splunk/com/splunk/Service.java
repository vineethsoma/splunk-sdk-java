/*
 * Copyright 2011 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

// UNDONE: getAlerts
// UNDONE: public Object parse(String query) {}

package com.splunk;

import com.splunk.atom.Xml;
import com.splunk.http.*;

import java.util.List;

public class Service extends com.splunk.http.Service {
    protected String token = null;
    protected String namespace = null;
    private String prefix = null;

    public static String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT = 8089;
    public static String DEFAULT_SCHEME = "https";

    public Service(String host) {
        super(host);
    }

    public Service(String host, int port) {
        super(host, port);
    }

    public Service(String host, int port, String scheme) {
        super(host, port, scheme);
    }

    public Service(ServiceArgs args) {
        super();
        this.host = args.host == null ? DEFAULT_HOST : args.host;
        this.port = args.port == null ? DEFAULT_PORT : args.port;
        this.scheme = args.scheme == null ? DEFAULT_SCHEME : args.scheme;
        this.namespace = args.namespace;
    }

    // Ensures that the given path is fully qualified, prepending a
    // path prefix as necessarry.
    String fullpath(String path) {
        if (path.startsWith("/"))
            return path;
        if (namespace == null)
            return "/services/" + path;
        return String.format("/servicesNS/%s/%s", namespace, path);
    }

    public EntityCollection<Application> getApplications() {
        return new EntityCollection<Application>(
            this, "apps/local", Application.class);
    }

    public ConfCollection getConfs() {
        return new ConfCollection(this);
    }

    public List<String> getCapabilities() {
        Entity caps = new Entity(this, "authorization/capabilities");
        return (List<String>)caps.getValue("capabilities");
    }

    public OutputDefault getDataOutputsDefault() {
        return new OutputDefault(this);
    }

    public EntityCollection<OutputServer> getDataOutputsServer() {
        return new EntityCollection<OutputServer>(
            this, "data/outputs/tcp/server", OutputServer.class);
    }

    public EntityCollection<OutputGroup> getDataOutputsGroup() {
        return new EntityCollection<OutputGroup>(
            this, "data/outputs/tcp/group", OutputGroup.class);
    }

    public EntityCollection<OutputSyslog> getDataOutputsSyslog() {
        return new EntityCollection<OutputSyslog>(
            this, "data/outputs/tcp/syslog", OutputSyslog.class);
    }

    public DeploymentClient getDeploymentClient() {
        return new DeploymentClient(this);
    }

    public EntityCollection<DeploymentServer> getDeploymentServers() {
        return new EntityCollection<DeploymentServer>(
            this, "deployment/server", DeploymentServer.class);
    }

    public EntityCollection<DeploymentServerClass> getDeploymentServerClasses(){
        return new EntityCollection<DeploymentServerClass>(
            this, "deployment/serverclass", DeploymentServerClass.class);
    }

    public EntityCollection<DeploymentTenant> getDeploymentTenants() {
        return new EntityCollection<DeploymentTenant>(
            this, "deployment/tenants", DeploymentTenant.class);
    }

    public DistributedConfiguration getDistributedConfiguration() {
        return new DistributedConfiguration(this);
    }

    public EntityCollection<DistributedPeer> getDistributedPeers() {
        return new EntityCollection<DistributedPeer>(
            this, "search/distributed/peers", DistributedPeer.class);
    }

    public EntityCollection<EventType> getEventTypes() {
        return new EntityCollection<EventType>(
            this, "saved/eventtypes", EventType.class);
    }

    public EntityCollection<Index> getIndexes() {
        return new EntityCollection<Index>(this, "data/indexes", Index.class);
    }

    public ServiceInfo getInfo() {
        return new ServiceInfo(this);
    }

    public EntityCollection<Entity> getInputs() {
        return null; //  UNDONE: flatten?
    }

    public JobCollection getJobs() {
        return new JobCollection(this, "search/jobs");
    }

    public EntityCollection<LicenseGroup> getLicenseGroups() {
        return new EntityCollection<LicenseGroup>(
            this, "licenser/groups", LicenseGroup.class);
    }

    public EntityCollection<LicenseMessage> getLicenseMessages() {
        return new EntityCollection<LicenseMessage>(
            this, "licenser/messages", LicenseMessage.class);
    }

    public EntityCollection<LicensePool> getLicensePools() {
        return new EntityCollection<LicensePool>(
            this, "licenser/pools", LicensePool.class);
    }

    public EntityCollection<LicenseSlave> getLicenseSlaves() {
        return new EntityCollection<LicenseSlave>(
            this, "licenser/slaves", LicenseSlave.class);
    }

    public EntityCollection<LicenseStack> getLicenseStacks() {
        return new EntityCollection<LicenseStack>(
            this, "licenser/stacks", LicenseStack.class);
    }

    public EntityCollection<License> getLicenses() {
        return new EntityCollection<License>(
            this, "licenser/licenses", License.class);
    }

    public EntityCollection<Logger> getLoggers() {
        return new EntityCollection<Logger>(
            this, "server/logger", Logger.class);
    }

    public EntityCollection<Message> getMessages() {
        return new EntityCollection<Message>(this, "messages", Message.class);
    }

    public EntityCollection<Entity> getPasswords() {
        // Starting with 4.3 this is available at "storage/passwords"
        return new EntityCollection(this, "admin/passwords");
    }

    public EntityCollection<Role> getRoles() {
        return new EntityCollection<Role>(
            this, "authentication/roles", Role.class);
    }

    public EntityCollection<SavedSearch> getSearches() {
        return new EntityCollection<SavedSearch>(
            this, "saved/searches", SavedSearch.class);
    }

    public Settings getSettings() {
        return new Settings(this);
    }

    public UserCollection getUsers() {
        return new UserCollection(this, "authentication/users");
    }

    public Service login(String username, String password) {
        Args args = new Args();
        args.put("username", username);
        args.put("password", password);
        ResponseMessage response = post("/services/auth/login", args);
        String sessionKey = Xml.parse(response.getContent())
            .getElementsByTagName("sessionKey")
            .item(0)
            .getTextContent();
        this.token = "Splunk " + sessionKey;
        return this;
    }

    // Forget the session token
    public Service logout() {
        this.token = null;
        return this;
    }

    public ResponseMessage restart() {
        return this.get("server/control/restart");
    }

    public ResponseMessage send(String path, RequestMessage request) {
        request.getHeader().put("Authorization", token);
        return super.send(fullpath(path), request);
    }
}

