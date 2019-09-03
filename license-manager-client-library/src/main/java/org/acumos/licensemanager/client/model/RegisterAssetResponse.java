package org.acumos.licensemanager.client.model;

// import org.acumos.lum.model.PutSwidTagResponse;

public class RegisterAssetResponse {

  private String message;
  private boolean success;

  public String getMessage() {
    return message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  // private PutSwidTagResponse rawResponse;

  // public PutSwidTagResponse getRawResponse() {
  //   return rawResponse;
  // }

  // public void setRawResponse(PutSwidTagResponse rawResponse) {
  //   this.rawResponse = rawResponse;
  // }
}
