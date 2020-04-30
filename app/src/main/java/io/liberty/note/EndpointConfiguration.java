package io.liberty.note;

public class EndpointConfiguration {

    final public String APPLICATION_JSON = "application/json";
    final public String GET_LIST_PATH = "/service/note/list";
    final public String LOGIN_PATH = "/service/session/login";
    final public String CREATE_PATH = "/service/note/create";
    final public String EDIT_PATH = "/service/note/edit";
    final public String DELETE_PATH = "/service/note/delete";
    final public String CREATE_ACCOUNT_PATH = "/service/user/create";
    final public String REALM_START_LOGIN_PATH = "/service/session/login";
    final public String REALM_VERIFY_LOGIN_PATH = "/service/session/login";
    final public String WEBSITE_CREATE_ACCOUNT_PATH = "/create-account";
    public String serviceEndpointUrl;
    public String loginshieldEndpointUrl;
    public String loginshieldPackageName;
}
