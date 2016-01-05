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
 * on 2016-01-04 at 09:10:06 UTC 
 * Modify at your own risk.
 */

package com.born2go.lazzybee.gdatabase.server.dataServiceApi.model;

/**
 * Model definition for Voca.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the LazzyBee Backend Api. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Voca extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String a;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean check;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String creator;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long gid;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("l_en")
  private java.lang.String lEn;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("l_vn")
  private java.lang.String lVn;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer level;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String note;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String packages;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String q;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("user_comment")
  private java.lang.String userComment;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getA() {
    return a;
  }

  /**
   * @param a a or {@code null} for none
   */
  public Voca setA(java.lang.String a) {
    this.a = a;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getCheck() {
    return check;
  }

  /**
   * @param check check or {@code null} for none
   */
  public Voca setCheck(java.lang.Boolean check) {
    this.check = check;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCreator() {
    return creator;
  }

  /**
   * @param creator creator or {@code null} for none
   */
  public Voca setCreator(java.lang.String creator) {
    this.creator = creator;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getGid() {
    return gid;
  }

  /**
   * @param gid gid or {@code null} for none
   */
  public Voca setGid(java.lang.Long gid) {
    this.gid = gid;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLEn() {
    return lEn;
  }

  /**
   * @param lEn lEn or {@code null} for none
   */
  public Voca setLEn(java.lang.String lEn) {
    this.lEn = lEn;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLVn() {
    return lVn;
  }

  /**
   * @param lVn lVn or {@code null} for none
   */
  public Voca setLVn(java.lang.String lVn) {
    this.lVn = lVn;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getLevel() {
    return level;
  }

  /**
   * @param level level or {@code null} for none
   */
  public Voca setLevel(java.lang.Integer level) {
    this.level = level;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNote() {
    return note;
  }

  /**
   * @param note note or {@code null} for none
   */
  public Voca setNote(java.lang.String note) {
    this.note = note;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPackages() {
    return packages;
  }

  /**
   * @param packages packages or {@code null} for none
   */
  public Voca setPackages(java.lang.String packages) {
    this.packages = packages;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getQ() {
    return q;
  }

  /**
   * @param q q or {@code null} for none
   */
  public Voca setQ(java.lang.String q) {
    this.q = q;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUserComment() {
    return userComment;
  }

  /**
   * @param userComment userComment or {@code null} for none
   */
  public Voca setUserComment(java.lang.String userComment) {
    this.userComment = userComment;
    return this;
  }

  @Override
  public Voca set(String fieldName, Object value) {
    return (Voca) super.set(fieldName, value);
  }

  @Override
  public Voca clone() {
    return (Voca) super.clone();
  }

}
