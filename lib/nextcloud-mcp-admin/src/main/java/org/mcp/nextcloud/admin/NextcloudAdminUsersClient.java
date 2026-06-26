package org.mcp.nextcloud.admin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudAdminUsersClient extends AbstractNextcloudAdminClient {
    private static final String USERS_ENDPOINT = "/ocs/v1.php/cloud/users";
    private static final String USER_FIELDS_ENDPOINT = "/ocs/v1.php/cloud/user/fields";

    private final AdminOcsParser ocsParser;

    NextcloudAdminUsersClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, AdminOcsParser ocsParser) {
        super(httpClient, requests);
        this.ocsParser = ocsParser;
    }

    public List<String> listUsers(String search, Integer limit, Integer offset) {
        Map<String, String> query = new LinkedHashMap<>();
        addQuery(query, "search", search);
        addQuery(query, "limit", limit);
        addQuery(query, "offset", offset);
        HttpResponseSpec response = sendExpecting(getOcs(USERS_ENDPOINT, query), 200);
        JsonNode data = ocsParser.data(response, USERS_ENDPOINT);
        return AdminOcsParser.stringList(data, "users");
    }

    public AdminUser getUser(String userId) {
        String endpoint = userEndpoint(userId);
        HttpResponseSpec response = sendExpecting(getOcs(endpoint), 200);
        return parseUser(ocsParser.data(response, endpoint), userId);
    }

    public AdminUserCreated createUser(AdminUserCreateRequest request) {
        AdminUserCreateRequest createRequest = Preconditions.requireNonNull(request, "create request");
        HttpResponseSpec response = sendExpecting(postOcsFormFields(USERS_ENDPOINT, createRequest.toFormFields()), 200);
        JsonNode data = ocsParser.data(response, USERS_ENDPOINT);
        String id = AdminOcsParser.text(data, "id", "userid", "uid");
        return new AdminUserCreated(id == null ? createRequest.userId() : id, data);
    }

    public AdminProvisioningOperation updateUserField(String userId, String key, String value) {
        String endpoint = userEndpoint(userId);
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("key", Preconditions.requireNonBlank(key, "field key"));
        fields.put("value", Preconditions.requireNonBlank(value, "field value"));
        HttpResponseSpec response = sendExpecting(putOcsForm(endpoint, fields), 200);
        ocsParser.data(response, endpoint);
        return new AdminProvisioningOperation(endpoint, response.statusCode());
    }

    public List<String> editableFields() {
        HttpResponseSpec response = sendExpecting(getOcs(USER_FIELDS_ENDPOINT), 200);
        JsonNode data = ocsParser.data(response, USER_FIELDS_ENDPOINT);
        return AdminOcsParser.stringList(data, null);
    }

    public AdminProvisioningOperation disableUser(String userId) {
        return putUserAction(userId, "disable");
    }

    public AdminProvisioningOperation enableUser(String userId) {
        return putUserAction(userId, "enable");
    }

    public AdminProvisioningOperation deleteUser(String userId) {
        String endpoint = userEndpoint(userId);
        HttpResponseSpec response = sendExpecting(deleteOcs(endpoint), 200);
        ocsParser.data(response, endpoint);
        return new AdminProvisioningOperation(endpoint, response.statusCode());
    }

    public List<String> getUserGroups(String userId) {
        String endpoint = userEndpoint(userId) + "/groups";
        HttpResponseSpec response = sendExpecting(getOcs(endpoint), 200);
        JsonNode data = ocsParser.data(response, endpoint);
        return AdminOcsParser.stringList(data, "groups");
    }

    public List<String> getSubadminGroups(String userId) {
        String endpoint = userEndpoint(userId) + "/subadmins";
        HttpResponseSpec response = sendExpecting(getOcs(endpoint), 200);
        JsonNode data = ocsParser.data(response, endpoint);
        return AdminOcsParser.stringList(data, "subadmins");
    }

    public AdminProvisioningOperation resendWelcomeEmail(String userId) {
        String endpoint = userEndpoint(userId) + "/welcome";
        HttpResponseSpec response = sendExpecting(requests.postOcs(endpoint), 200);
        ocsParser.data(response, endpoint);
        return new AdminProvisioningOperation(endpoint, response.statusCode());
    }

    private AdminProvisioningOperation putUserAction(String userId, String action) {
        String endpoint = userEndpoint(userId) + "/" + action;
        HttpResponseSpec response = sendExpecting(putOcsForm(endpoint, Map.of()), 200);
        ocsParser.data(response, endpoint);
        return new AdminProvisioningOperation(endpoint, response.statusCode());
    }

    private static AdminUser parseUser(JsonNode data, String fallbackId) {
        String id = AdminOcsParser.text(data, "id", "userid", "uid");
        String displayName = AdminOcsParser.text(data, "display-name", "displayname", "displayName");
        String email = AdminOcsParser.text(data, "email");
        String quota = AdminOcsParser.text(data, "quota");
        boolean enabled = !data.has("enabled") || data.path("enabled").asBoolean(true);
        return new AdminUser(id == null ? fallbackId : id, displayName, email, enabled, quota, data);
    }

    private static void addQuery(Map<String, String> query, String key, Object value) {
        if (value != null && !value.toString().isBlank()) {
            query.put(key, value.toString());
        }
    }

    private static String userEndpoint(String userId) {
        return USERS_ENDPOINT + "/" + NextcloudHttpRequestFactory.encodePathSegment(Preconditions.requireNonBlank(userId, "user id"));
    }
}
