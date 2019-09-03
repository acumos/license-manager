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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */

package org.acumos.licensemanager.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** When making a request for a license RTU consolidating common functionality. */
public abstract class BaseLicenseRequest implements ICommonLicenseRequest {

  private UUID solutionId;
  private UUID revisionId;

  /** userIds to create RTUs for. */
  private List<String> userIdsCds = new ArrayList<String>();

  /**
   * Set list of userIds that will be used to verify/create/update a RTU.
   *
   * @param userIds a List of userIds.
   */
  public final void setUserIds(final List<String> userIds) {
    userIdsCds = userIds;
  }

  /**
   * Adds a user ID that will be used to verify/create/update a RTU.
   *
   * @param userId a {@link java.lang.String} object.
   */
  public final void addUserId(final String userId) {
    userIdsCds.add(userId);
  }

  @Override
  public final List<String> getUserIds() {
    return userIdsCds;
  }

  /**
   * Required To create the swidTagId we will use the solutionId + revisionId
   *
   * @see {@link SWIDBody#setSwTagId(String)}
   * @see {@link PutSwidTagRequest#setSwidTag(SWIDBody)}
   */
  public void setRevisionId(String revisionId) {
    this.revisionId = UUID.fromString(revisionId);
  }

  /**
   * Required To create the swidTagId we will use the solutionId + revisionId
   *
   * @see {@link SWIDBody#setSwTagId(String)}
   * @see {@link PutSwidTagRequest#setSwidTag(SWIDBody)}
   */
  public void setRevisionId(UUID revisionId) {
    this.revisionId = revisionId;
  }

  public UUID getRevisionId() {
    return revisionId;
  }

  public UUID getSolutionId() {
    return solutionId;
  }

  /**
   * Required SolutionId maps to the software persistent id in LUM The solutionId is the UUID that
   * represents the software
   *
   * @see {@link SWIDBody#setSwPersistentId(java.util.UUID)}
   * @see {@link PutSwidTagRequest#setSwidTag(SWIDBody)}
   */
  public void setSolutionId(String solutionId) {
    this.solutionId = UUID.fromString(solutionId);
  }

  public void setSolutionId(UUID solutionId) {
    this.solutionId = solutionId;
  }
}
