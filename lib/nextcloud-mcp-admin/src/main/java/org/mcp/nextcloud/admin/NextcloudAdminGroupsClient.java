package org.mcp.nextcloud.admin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudAdminGroupsClient extends AbstractNextcloudAdminClient {
    private static final String GROUPS_ENDPOINT = "/ocs/v1.php/cloud/groups";
    private static final String USERS_ENDPOINT = "/ocs/v1.php/cloud/users";

    private final AdminOcsParser ocsParser;

    NextcloudAdminGroupsClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, AdminOcsParser ocsParser) {
        super(httpClient, requests);
        this.ocsParser = ocsParser;
    }

    public List<String> listGroups(String search, Integer limit, Integer offset) {
        Map<String, String> query = new LinkedHashMap<>();
        addQuery(query, "search", search);
        addQuery(query, "limit", limit);
        addQuery(query, "offset", offset);
        HttpResponseSpec response = sendExpecting(getOcs(GROUPS_ENDPOINT, query), 200);
        JsonNode data = ocsParser.data(response, GROUPS_ENDPOINT);
        return AdminOcsParser.stringList(data, "groups");
    }

    public AdminGroup createGroup(String groupId) {
        String cleanGroupId = Preconditions.requireNonBlank(groupId, "group id");
        HttpResponseSpec response = sendExpecting(postOcsForm(GROUPS_ENDPOINT, Map.of("groupid", cleanGroupId)), 200);
        JsonNode data = ocsParser.data(response, GROUPS_ENDPOINT);
        String id = AdminOcsParser.text(data, "id", "groupid", "gid");
        String displayName = AdminOcsParser.text(data, "displayname", "displayName");
        return new AdminGroup(id == null ? cleanGroupId : id, displayName, data);
    }

    public List<String> getGroupMembers(String groupId) {
        String endpoint = groupEndpoint(groupId);
        HttpResponseSpec response = sendExpecting(getOcs(endpoint), 200);
        JsonNode data = ocsParser.data(response, endpoint);
        return AdminOcsParser.stringList(data, "users");
    }

    public List<String> getGroupSubadmins(String groupId) {
        String endpoint = groupEndpoint(groupId) + "/subadmins";
        HttpResponseSpec response = sendExpecting(getOcs(endpoint), 200);
        JsonNode data = ocsParser.data(response, endpoint);
        return AdminOcsParser.stringList(data, "subadmins");
    }

    public AdminProvisioningOperation updateGroupDisplayName(String groupId, String displayName) {
        String endpoint = groupEndpoint(groupId);
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("key", "displayname");
        fields.put("value", Preconditions.requireNonBlank(displayName, "display name"));
        HttpResponseSpec response = sendExpecting(putOcsForm(endpoint, fields), 200);
        ocsParser.data(response, endpoint);
        return new AdminProvisioningOperation(endpoint, response.statusCode());
    }

    public AdminProvisioningOperation deleteGroup(String groupId) {
        String endpoint = groupEndpoint(groupId);
        HttpResponseSpec response = sendExpecting(deleteOcs(endpoint), 200);
        ocsParser.data(response, endpoint);
        return new AdminProvisioningOperation(endpoint, response.statusCode());
    }

    public AdminProvisioningOperation addUserToGroup(String userId, String groupId) {
        String endpoint = userEndpoint(userId) + "/groups";
        return sendGroupMutation(endpoint, groupId, true);
    }

    public AdminProvisioningOperation removeUserFromGroup(String userId, String groupId) {
        String endpoint = userEndpoint(userId) + "/groups";
        return sendGroupMutation(endpoint, groupId, false);
    }

    public AdminProvisioningOperation promoteSubadmin(String userId, String groupId) {
        String endpoint = userEndpoint(userId) + "/subadmins";
        return sendGroupMutation(endpoint, groupId, true);
    }

    public AdminProvisioningOperation demoteSubadmin(String userId, String groupId) {
        String endpoint = userEndpoint(userId) + "/subadmins";
        return sendGroupMutation(endpoint, groupId, false);
    }

    private AdminProvisioningOperation sendGroupMutation(String endpoint, String groupId, boolean post) {
        Map<String, String> fields = Map.of("groupid", Preconditions.requireNonBlank(groupId, "group id"));
        HttpResponseSpec response = sendExpecting(post ? postOcsForm(endpoint, fields) : deleteOcsForm(endpoint, fields), 200);
        ocsParser.data(response, endpoint);
        return new AdminProvisioningOperation(endpoint, response.statusCode());
    }

    private static void addQuery(Map<String, String> query, String key, Object value) {
        if (value != null && !value.toString().isBlank()) {
            query.put(key, value.toString());
        }
    }

    private static String groupEndpoint(String groupId) {
        return GROUPS_ENDPOINT + "/" + NextcloudHttpRequestFactory.encodePathSegment(Preconditions.requireNonBlank(groupId, "group id"));
    }

    private static String userEndpoint(String userId) {
        return USERS_ENDPOINT + "/" + NextcloudHttpRequestFactory.encodePathSegment(Preconditions.requireNonBlank(userId, "user id"));
    }
}
