import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LicenseProfileServiceService {
  schemaUrl: string;
  constructor(private http: HttpClient) {
    // tslint:disable-next-line:max-line-length
    this.schemaUrl = `https://raw.githubusercontent.com/acumos/license-manager/master/license-manager-client-library/src/main/resources/license.schema.json`;
  }

  getSchema(): any {
    return this
      .http
      .get(`${this.schemaUrl}`);
  }
}
