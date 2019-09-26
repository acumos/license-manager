/*-
 * ===============LICENSE_START================================================
 * Acumos Apache-2.0
 * ============================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ============================================================================
 * This Acumos software file is distributed by Nordix Foundation
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */
package org.acumos.licensemanager.profilevalidator.model;

public class SchemaMetadata {
  private String fullUrl;
  private String parentPath;
  private String version;
  private String fileName;

  public SchemaMetadata(String fullUrl, String parentPath, String version, String fileName) {
    this.fullUrl = fullUrl;
    this.parentPath = parentPath;
    this.version = version;
    this.fileName = fileName;
  }

  public String getFullUrl() {
    return fullUrl;
  }

  public String getParentPath() {
    return parentPath;
  }

  public String getVersion() {
    return version;
  }

  public String getFileName() {
    return fileName;
  }

  @Override
  public String toString() {
    return "SchemaMetadata [fullUrl="
        + fullUrl
        + ", parentPath="
        + parentPath
        + ", version="
        + version
        + ", fileName="
        + fileName
        + "]";
  }
}
