package org.acumos.licensemanager.client.model;

import java.util.UUID;

/** solutionId maps to @see {@link org.acumos.lum.model.PutSwidTagRequest} */
public class RegisterAssetRequest {

  private UUID solutionId;
  private UUID revisionId;
  private String loggedIdUser;

  public UUID getRevisionId() {
    return revisionId;
  }

  public UUID getSolutionId() {
    return solutionId;
  }

  public void setSolutionId(String solutionId) {
    this.solutionId = UUID.fromString(solutionId);
  }

  public void setSolutionId(UUID solutionId) {
    this.solutionId = solutionId;
  }

  public String getLoggedIdUser() {
    return loggedIdUser;
  }

  public void setLoggedIdUser(String loggedIdUser) {
    this.loggedIdUser = loggedIdUser;
  }

  public void setRevisionId(String revisionId) {
    this.revisionId = UUID.fromString(revisionId);
  }

  public void setRevisionId(UUID revisionId) {
    this.revisionId = revisionId;
  }
}
