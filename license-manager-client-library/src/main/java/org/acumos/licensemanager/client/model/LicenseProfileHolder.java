package org.acumos.licensemanager.client.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.acumos.cds.domain.MLPArtifact;

public class LicenseProfileHolder {

  private MLPArtifact artifact;
  private JsonNode json;

  public void setArtifact(MLPArtifact licenseProfileArtifact) {
    this.artifact = licenseProfileArtifact;
  }
  /** @return the artifact */
  public MLPArtifact getArtifact() {
    return artifact;
  }

  public void setJson(JsonNode jsonNode) {
    this.json = jsonNode;
  }

  /** @return the json */
  public JsonNode getJson() {
    return json;
  }
}
