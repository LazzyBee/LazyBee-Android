/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2015-11-16 19:10:01 UTC)
 * on 2016-01-07 at 09:14:40 UTC 
 * Modify at your own risk.
 */

package com.born2go.lazzybee.gdatabase.server.dataServiceApi;

/**
 * Service definition for DataServiceApi (v1.1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link DataServiceApiRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class DataServiceApi extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.21.0 of the LazzyBee Backend Api library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://lazeebee-977.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "dataServiceApi/v1.1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public DataServiceApi(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  DataServiceApi(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "findVocaById".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link FindVocaById#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @param orderSearch
   * @return the request
   */
  public FindVocaById findVocaById(java.lang.Long id, java.lang.Boolean orderSearch) throws java.io.IOException {
    FindVocaById result = new FindVocaById(id, orderSearch);
    initialize(result);
    return result;
  }

  public class FindVocaById extends DataServiceApiRequest<com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca> {

    private static final String REST_PATH = "find_voca_byId";

    /**
     * Create a request for the method "findVocaById".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link FindVocaById#execute()} method to invoke the remote
     * operation. <p> {@link
     * FindVocaById#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @param orderSearch
     * @since 1.13
     */
    protected FindVocaById(java.lang.Long id, java.lang.Boolean orderSearch) {
      super(DataServiceApi.this, "POST", REST_PATH, null, com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
      this.orderSearch = com.google.api.client.util.Preconditions.checkNotNull(orderSearch, "Required parameter orderSearch must be specified.");
    }

    @Override
    public FindVocaById setAlt(java.lang.String alt) {
      return (FindVocaById) super.setAlt(alt);
    }

    @Override
    public FindVocaById setFields(java.lang.String fields) {
      return (FindVocaById) super.setFields(fields);
    }

    @Override
    public FindVocaById setKey(java.lang.String key) {
      return (FindVocaById) super.setKey(key);
    }

    @Override
    public FindVocaById setOauthToken(java.lang.String oauthToken) {
      return (FindVocaById) super.setOauthToken(oauthToken);
    }

    @Override
    public FindVocaById setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FindVocaById) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FindVocaById setQuotaUser(java.lang.String quotaUser) {
      return (FindVocaById) super.setQuotaUser(quotaUser);
    }

    @Override
    public FindVocaById setUserIp(java.lang.String userIp) {
      return (FindVocaById) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public FindVocaById setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Boolean orderSearch;

    /**

     */
    public java.lang.Boolean getOrderSearch() {
      return orderSearch;
    }

    public FindVocaById setOrderSearch(java.lang.Boolean orderSearch) {
      this.orderSearch = orderSearch;
      return this;
    }

    @Override
    public FindVocaById set(String parameterName, Object value) {
      return (FindVocaById) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "findVocaByQ".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link FindVocaByQ#execute()} method to invoke the remote
   * operation.
   *
   * @param orderSearch
   * @param q
   * @return the request
   */
  public FindVocaByQ findVocaByQ(java.lang.Boolean orderSearch, java.lang.String q) throws java.io.IOException {
    FindVocaByQ result = new FindVocaByQ(orderSearch, q);
    initialize(result);
    return result;
  }

  public class FindVocaByQ extends DataServiceApiRequest<com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca> {

    private static final String REST_PATH = "find_voca_byQ";

    /**
     * Create a request for the method "findVocaByQ".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link FindVocaByQ#execute()} method to invoke the remote
     * operation. <p> {@link
     * FindVocaByQ#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param orderSearch
     * @param q
     * @since 1.13
     */
    protected FindVocaByQ(java.lang.Boolean orderSearch, java.lang.String q) {
      super(DataServiceApi.this, "POST", REST_PATH, null, com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca.class);
      this.orderSearch = com.google.api.client.util.Preconditions.checkNotNull(orderSearch, "Required parameter orderSearch must be specified.");
      this.q = com.google.api.client.util.Preconditions.checkNotNull(q, "Required parameter q must be specified.");
    }

    @Override
    public FindVocaByQ setAlt(java.lang.String alt) {
      return (FindVocaByQ) super.setAlt(alt);
    }

    @Override
    public FindVocaByQ setFields(java.lang.String fields) {
      return (FindVocaByQ) super.setFields(fields);
    }

    @Override
    public FindVocaByQ setKey(java.lang.String key) {
      return (FindVocaByQ) super.setKey(key);
    }

    @Override
    public FindVocaByQ setOauthToken(java.lang.String oauthToken) {
      return (FindVocaByQ) super.setOauthToken(oauthToken);
    }

    @Override
    public FindVocaByQ setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FindVocaByQ) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FindVocaByQ setQuotaUser(java.lang.String quotaUser) {
      return (FindVocaByQ) super.setQuotaUser(quotaUser);
    }

    @Override
    public FindVocaByQ setUserIp(java.lang.String userIp) {
      return (FindVocaByQ) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Boolean orderSearch;

    /**

     */
    public java.lang.Boolean getOrderSearch() {
      return orderSearch;
    }

    public FindVocaByQ setOrderSearch(java.lang.Boolean orderSearch) {
      this.orderSearch = orderSearch;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String q;

    /**

     */
    public java.lang.String getQ() {
      return q;
    }

    public FindVocaByQ setQ(java.lang.String q) {
      this.q = q;
      return this;
    }

    @Override
    public FindVocaByQ set(String parameterName, Object value) {
      return (FindVocaByQ) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getDownloadUrl".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link GetDownloadUrl#execute()} method to invoke the remote
   * operation.
   *
   * @param code
   * @return the request
   */
  public GetDownloadUrl getDownloadUrl(java.lang.String code) throws java.io.IOException {
    GetDownloadUrl result = new GetDownloadUrl(code);
    initialize(result);
    return result;
  }

  public class GetDownloadUrl extends DataServiceApiRequest<com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.DownloadTarget> {

    private static final String REST_PATH = "downloadtarget/{code}";

    /**
     * Create a request for the method "getDownloadUrl".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link GetDownloadUrl#execute()} method to invoke the remote
     * operation. <p> {@link GetDownloadUrl#initialize(com.google.api.client.googleapis.services.Abstr
     * actGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param code
     * @since 1.13
     */
    protected GetDownloadUrl(java.lang.String code) {
      super(DataServiceApi.this, "GET", REST_PATH, null, com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.DownloadTarget.class);
      this.code = com.google.api.client.util.Preconditions.checkNotNull(code, "Required parameter code must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetDownloadUrl setAlt(java.lang.String alt) {
      return (GetDownloadUrl) super.setAlt(alt);
    }

    @Override
    public GetDownloadUrl setFields(java.lang.String fields) {
      return (GetDownloadUrl) super.setFields(fields);
    }

    @Override
    public GetDownloadUrl setKey(java.lang.String key) {
      return (GetDownloadUrl) super.setKey(key);
    }

    @Override
    public GetDownloadUrl setOauthToken(java.lang.String oauthToken) {
      return (GetDownloadUrl) super.setOauthToken(oauthToken);
    }

    @Override
    public GetDownloadUrl setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetDownloadUrl) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetDownloadUrl setQuotaUser(java.lang.String quotaUser) {
      return (GetDownloadUrl) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetDownloadUrl setUserIp(java.lang.String userIp) {
      return (GetDownloadUrl) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String code;

    /**

     */
    public java.lang.String getCode() {
      return code;
    }

    public GetDownloadUrl setCode(java.lang.String code) {
      this.code = code;
      return this;
    }

    @Override
    public GetDownloadUrl set(String parameterName, Object value) {
      return (GetDownloadUrl) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getUploadUrl".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link GetUploadUrl#execute()} method to invoke the remote
   * operation.
   *
   * @return the request
   */
  public GetUploadUrl getUploadUrl() throws java.io.IOException {
    GetUploadUrl result = new GetUploadUrl();
    initialize(result);
    return result;
  }

  public class GetUploadUrl extends DataServiceApiRequest<com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.UploadTarget> {

    private static final String REST_PATH = "uploadtarget";

    /**
     * Create a request for the method "getUploadUrl".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link GetUploadUrl#execute()} method to invoke the remote
     * operation. <p> {@link
     * GetUploadUrl#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected GetUploadUrl() {
      super(DataServiceApi.this, "GET", REST_PATH, null, com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.UploadTarget.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetUploadUrl setAlt(java.lang.String alt) {
      return (GetUploadUrl) super.setAlt(alt);
    }

    @Override
    public GetUploadUrl setFields(java.lang.String fields) {
      return (GetUploadUrl) super.setFields(fields);
    }

    @Override
    public GetUploadUrl setKey(java.lang.String key) {
      return (GetUploadUrl) super.setKey(key);
    }

    @Override
    public GetUploadUrl setOauthToken(java.lang.String oauthToken) {
      return (GetUploadUrl) super.setOauthToken(oauthToken);
    }

    @Override
    public GetUploadUrl setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetUploadUrl) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetUploadUrl setQuotaUser(java.lang.String quotaUser) {
      return (GetUploadUrl) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetUploadUrl setUserIp(java.lang.String userIp) {
      return (GetUploadUrl) super.setUserIp(userIp);
    }

    @Override
    public GetUploadUrl set(String parameterName, Object value) {
      return (GetUploadUrl) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getVocaById".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link GetVocaById#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public GetVocaById getVocaById(java.lang.Long id) throws java.io.IOException {
    GetVocaById result = new GetVocaById(id);
    initialize(result);
    return result;
  }

  public class GetVocaById extends DataServiceApiRequest<com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca> {

    private static final String REST_PATH = "get_voca_byId";

    /**
     * Create a request for the method "getVocaById".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link GetVocaById#execute()} method to invoke the remote
     * operation. <p> {@link
     * GetVocaById#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetVocaById(java.lang.Long id) {
      super(DataServiceApi.this, "GET", REST_PATH, null, com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetVocaById setAlt(java.lang.String alt) {
      return (GetVocaById) super.setAlt(alt);
    }

    @Override
    public GetVocaById setFields(java.lang.String fields) {
      return (GetVocaById) super.setFields(fields);
    }

    @Override
    public GetVocaById setKey(java.lang.String key) {
      return (GetVocaById) super.setKey(key);
    }

    @Override
    public GetVocaById setOauthToken(java.lang.String oauthToken) {
      return (GetVocaById) super.setOauthToken(oauthToken);
    }

    @Override
    public GetVocaById setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetVocaById) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetVocaById setQuotaUser(java.lang.String quotaUser) {
      return (GetVocaById) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetVocaById setUserIp(java.lang.String userIp) {
      return (GetVocaById) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public GetVocaById setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public GetVocaById set(String parameterName, Object value) {
      return (GetVocaById) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getVocaByQ".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link GetVocaByQ#execute()} method to invoke the remote operation.
   *
   * @param q
   * @return the request
   */
  public GetVocaByQ getVocaByQ(java.lang.String q) throws java.io.IOException {
    GetVocaByQ result = new GetVocaByQ(q);
    initialize(result);
    return result;
  }

  public class GetVocaByQ extends DataServiceApiRequest<com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca> {

    private static final String REST_PATH = "get_voca_byQ";

    /**
     * Create a request for the method "getVocaByQ".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link GetVocaByQ#execute()} method to invoke the remote
     * operation. <p> {@link
     * GetVocaByQ#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param q
     * @since 1.13
     */
    protected GetVocaByQ(java.lang.String q) {
      super(DataServiceApi.this, "GET", REST_PATH, null, com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca.class);
      this.q = com.google.api.client.util.Preconditions.checkNotNull(q, "Required parameter q must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetVocaByQ setAlt(java.lang.String alt) {
      return (GetVocaByQ) super.setAlt(alt);
    }

    @Override
    public GetVocaByQ setFields(java.lang.String fields) {
      return (GetVocaByQ) super.setFields(fields);
    }

    @Override
    public GetVocaByQ setKey(java.lang.String key) {
      return (GetVocaByQ) super.setKey(key);
    }

    @Override
    public GetVocaByQ setOauthToken(java.lang.String oauthToken) {
      return (GetVocaByQ) super.setOauthToken(oauthToken);
    }

    @Override
    public GetVocaByQ setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetVocaByQ) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetVocaByQ setQuotaUser(java.lang.String quotaUser) {
      return (GetVocaByQ) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetVocaByQ setUserIp(java.lang.String userIp) {
      return (GetVocaByQ) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String q;

    /**

     */
    public java.lang.String getQ() {
      return q;
    }

    public GetVocaByQ setQ(java.lang.String q) {
      this.q = q;
      return this;
    }

    @Override
    public GetVocaByQ set(String parameterName, Object value) {
      return (GetVocaByQ) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listVoca".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link ListVoca#execute()} method to invoke the remote operation.
   *
   * @return the request
   */
  public ListVoca listVoca() throws java.io.IOException {
    ListVoca result = new ListVoca();
    initialize(result);
    return result;
  }

  public class ListVoca extends DataServiceApiRequest<com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.VocaCollection> {

    private static final String REST_PATH = "voca";

    /**
     * Create a request for the method "listVoca".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link ListVoca#execute()} method to invoke the remote operation.
     * <p> {@link
     * ListVoca#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ListVoca() {
      super(DataServiceApi.this, "GET", REST_PATH, null, com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.VocaCollection.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ListVoca setAlt(java.lang.String alt) {
      return (ListVoca) super.setAlt(alt);
    }

    @Override
    public ListVoca setFields(java.lang.String fields) {
      return (ListVoca) super.setFields(fields);
    }

    @Override
    public ListVoca setKey(java.lang.String key) {
      return (ListVoca) super.setKey(key);
    }

    @Override
    public ListVoca setOauthToken(java.lang.String oauthToken) {
      return (ListVoca) super.setOauthToken(oauthToken);
    }

    @Override
    public ListVoca setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListVoca) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListVoca setQuotaUser(java.lang.String quotaUser) {
      return (ListVoca) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListVoca setUserIp(java.lang.String userIp) {
      return (ListVoca) super.setUserIp(userIp);
    }

    @Override
    public ListVoca set(String parameterName, Object value) {
      return (ListVoca) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "saveVoca".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link SaveVoca#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca}
   * @return the request
   */
  public SaveVoca saveVoca(com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca content) throws java.io.IOException {
    SaveVoca result = new SaveVoca(content);
    initialize(result);
    return result;
  }

  public class SaveVoca extends DataServiceApiRequest<Void> {

    private static final String REST_PATH = "saveVoca";

    /**
     * Create a request for the method "saveVoca".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link SaveVoca#execute()} method to invoke the remote operation.
     * <p> {@link
     * SaveVoca#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca}
     * @since 1.13
     */
    protected SaveVoca(com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca content) {
      super(DataServiceApi.this, "POST", REST_PATH, content, Void.class);
    }

    @Override
    public SaveVoca setAlt(java.lang.String alt) {
      return (SaveVoca) super.setAlt(alt);
    }

    @Override
    public SaveVoca setFields(java.lang.String fields) {
      return (SaveVoca) super.setFields(fields);
    }

    @Override
    public SaveVoca setKey(java.lang.String key) {
      return (SaveVoca) super.setKey(key);
    }

    @Override
    public SaveVoca setOauthToken(java.lang.String oauthToken) {
      return (SaveVoca) super.setOauthToken(oauthToken);
    }

    @Override
    public SaveVoca setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (SaveVoca) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public SaveVoca setQuotaUser(java.lang.String quotaUser) {
      return (SaveVoca) super.setQuotaUser(quotaUser);
    }

    @Override
    public SaveVoca setUserIp(java.lang.String userIp) {
      return (SaveVoca) super.setUserIp(userIp);
    }

    @Override
    public SaveVoca set(String parameterName, Object value) {
      return (SaveVoca) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateA".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link UpdateA#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca}
   * @return the request
   */
  public UpdateA updateA(com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca content) throws java.io.IOException {
    UpdateA result = new UpdateA(content);
    initialize(result);
    return result;
  }

  public class UpdateA extends DataServiceApiRequest<Void> {

    private static final String REST_PATH = "update_A";

    /**
     * Create a request for the method "updateA".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link UpdateA#execute()} method to invoke the remote operation.
     * <p> {@link
     * UpdateA#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca}
     * @since 1.13
     */
    protected UpdateA(com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca content) {
      super(DataServiceApi.this, "PUT", REST_PATH, content, Void.class);
    }

    @Override
    public UpdateA setAlt(java.lang.String alt) {
      return (UpdateA) super.setAlt(alt);
    }

    @Override
    public UpdateA setFields(java.lang.String fields) {
      return (UpdateA) super.setFields(fields);
    }

    @Override
    public UpdateA setKey(java.lang.String key) {
      return (UpdateA) super.setKey(key);
    }

    @Override
    public UpdateA setOauthToken(java.lang.String oauthToken) {
      return (UpdateA) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateA setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateA) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateA setQuotaUser(java.lang.String quotaUser) {
      return (UpdateA) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateA setUserIp(java.lang.String userIp) {
      return (UpdateA) super.setUserIp(userIp);
    }

    @Override
    public UpdateA set(String parameterName, Object value) {
      return (UpdateA) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateD".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link UpdateD#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca}
   * @return the request
   */
  public UpdateD updateD(com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca content) throws java.io.IOException {
    UpdateD result = new UpdateD(content);
    initialize(result);
    return result;
  }

  public class UpdateD extends DataServiceApiRequest<Void> {

    private static final String REST_PATH = "update_D";

    /**
     * Create a request for the method "updateD".
     *
     * This request holds the parameters needed by the the dataServiceApi server.  After setting any
     * optional parameters, call the {@link UpdateD#execute()} method to invoke the remote operation.
     * <p> {@link
     * UpdateD#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca}
     * @since 1.13
     */
    protected UpdateD(com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca content) {
      super(DataServiceApi.this, "PUT", REST_PATH, content, Void.class);
    }

    @Override
    public UpdateD setAlt(java.lang.String alt) {
      return (UpdateD) super.setAlt(alt);
    }

    @Override
    public UpdateD setFields(java.lang.String fields) {
      return (UpdateD) super.setFields(fields);
    }

    @Override
    public UpdateD setKey(java.lang.String key) {
      return (UpdateD) super.setKey(key);
    }

    @Override
    public UpdateD setOauthToken(java.lang.String oauthToken) {
      return (UpdateD) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateD setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateD) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateD setQuotaUser(java.lang.String quotaUser) {
      return (UpdateD) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateD setUserIp(java.lang.String userIp) {
      return (UpdateD) super.setUserIp(userIp);
    }

    @Override
    public UpdateD set(String parameterName, Object value) {
      return (UpdateD) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link DataServiceApi}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link DataServiceApi}. */
    @Override
    public DataServiceApi build() {
      return new DataServiceApi(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link DataServiceApiRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setDataServiceApiRequestInitializer(
        DataServiceApiRequestInitializer dataserviceapiRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(dataserviceapiRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
