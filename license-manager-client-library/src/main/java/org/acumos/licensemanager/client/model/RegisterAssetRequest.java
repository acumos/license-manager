package org.acumos.licensemanager.client.model;

import java.util.UUID;

// import org.acumos.lum.model.PutSwidTagRequest;

/** solutionId maps to @see {@link PutSwidTagRequest} */
public class RegisterAssetRequest {

  private UUID solutionId;
  private UUID revisionId;
  private boolean isRtuRequired;
  private String licenseName;
  private String loggedIdUser;

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

  public String getLoggedIdUser() {
    return loggedIdUser;
  }

  /**
   * Required identifier of the user
   *
   * @see {@link RequestTopUser#setUserId(String)}
   * @see {@link PutSwidTagRequest#setUserId(String)}
   */
  public void setLoggedIdUser(String loggedIdUser) {
    this.loggedIdUser = loggedIdUser;
  }

  public String getLicenseName() {
    return licenseName;
  }

  /**
   * Required name the license in free text
   *
   * @see {@link LicenseProfile#setLicenseTxt(String)}
   * @see {@link PutSwidTagRequest#setLicenseProfile(LicenseProfile)}
   */
  public void setLicenseName(String licenseName) {
    this.licenseName = licenseName;
  }

  public boolean isRtuRequired() {
    return isRtuRequired;
  }

  /**
   * Required whether software requires the right-to-use or it is Free or Open Source Software if
   * true, it requires RTU (right-to-use) to be provided for usage
   *
   * @see {@link LicenseProfile#setIsRtuRequired(Boolean)}
   * @see {@link PutSwidTagRequest#setLicenseProfile(LicenseProfile)}
   */
  public void setRtuRequired(boolean isRtuRequired) {
    this.isRtuRequired = isRtuRequired;
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
}
