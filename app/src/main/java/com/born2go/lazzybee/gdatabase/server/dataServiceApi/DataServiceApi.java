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
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-09-11 at 08:50:51 UTC 
 * Modify at your own risk.
 */

package com.born2go.lazzybee.gdatabase.server.dataServiceApi;

/**
 * Service definition for DataServiceApi (v1).
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
        "1.20.0 of the LazzyBee Backend Api library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
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
   * Create a request for the method "getVocaById".
   *
   * This request holds the parameters needed by the dataServiceApi server.  After setting any
   * optional parameters, call the {@link GetVocaById#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public GetVocaById getVocaById(Long id) throws java.io.IOException {
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
    protected GetVocaById(Long id) {
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
    public GetVocaById setAlt(String alt) {
      return (GetVocaById) super.setAlt(alt);
    }

    @Override
    public GetVocaById setFields(String fields) {
      return (GetVocaById) super.setFields(fields);
    }

    @Override
    public GetVocaById setKey(String key) {
      return (GetVocaById) super.setKey(key);
    }

    @Override
    public GetVocaById setOauthToken(String oauthToken) {
      return (GetVocaById) super.setOauthToken(oauthToken);
    }

    @Override
    public GetVocaById setPrettyPrint(Boolean prettyPrint) {
      return (GetVocaById) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetVocaById setQuotaUser(String quotaUser) {
      return (GetVocaById) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetVocaById setUserIp(String userIp) {
      return (GetVocaById) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private Long id;

    /**

     */
    public Long getId() {
      return id;
    }

    public GetVocaById setId(Long id) {
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
  public GetVocaByQ getVocaByQ(String q) throws java.io.IOException {
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
    protected GetVocaByQ(String q) {
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
    public GetVocaByQ setAlt(String alt) {
      return (GetVocaByQ) super.setAlt(alt);
    }

    @Override
    public GetVocaByQ setFields(String fields) {
      return (GetVocaByQ) super.setFields(fields);
    }

    @Override
    public GetVocaByQ setKey(String key) {
      return (GetVocaByQ) super.setKey(key);
    }

    @Override
    public GetVocaByQ setOauthToken(String oauthToken) {
      return (GetVocaByQ) super.setOauthToken(oauthToken);
    }

    @Override
    public GetVocaByQ setPrettyPrint(Boolean prettyPrint) {
      return (GetVocaByQ) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetVocaByQ setQuotaUser(String quotaUser) {
      return (GetVocaByQ) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetVocaByQ setUserIp(String userIp) {
      return (GetVocaByQ) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private String q;

    /**

     */
    public String getQ() {
      return q;
    }

    public GetVocaByQ setQ(String q) {
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
    public ListVoca setAlt(String alt) {
      return (ListVoca) super.setAlt(alt);
    }

    @Override
    public ListVoca setFields(String fields) {
      return (ListVoca) super.setFields(fields);
    }

    @Override
    public ListVoca setKey(String key) {
      return (ListVoca) super.setKey(key);
    }

    @Override
    public ListVoca setOauthToken(String oauthToken) {
      return (ListVoca) super.setOauthToken(oauthToken);
    }

    @Override
    public ListVoca setPrettyPrint(Boolean prettyPrint) {
      return (ListVoca) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListVoca setQuotaUser(String quotaUser) {
      return (ListVoca) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListVoca setUserIp(String userIp) {
      return (ListVoca) super.setUserIp(userIp);
    }

    @Override
    public ListVoca set(String parameterName, Object value) {
      return (ListVoca) super.set(parameterName, value);
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