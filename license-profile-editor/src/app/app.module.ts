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

import { BrowserModule } from '@angular/platform-browser';
import { NgModule, Injector } from '@angular/core';

import { LicenseProfileEditorComponent } from './license-profile-editor/license-profile-editor.component';
import { createCustomElement } from '@angular/elements';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatCardModule, MatToolbarModule, MatInputModule, MatGridListModule } from '@angular/material';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';

import { MaterialDesignFrameworkModule } from '@earlyster/angular6-json-schema-form';
import { HttpClientModule } from '@angular/common/http';


@NgModule({
  declarations: [
    LicenseProfileEditorComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatCardModule, MatToolbarModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatGridListModule,
    MaterialDesignFrameworkModule,
    HttpClientModule
  ],
  providers: [],
  entryComponents: [LicenseProfileEditorComponent]
})
export class AppModule {
  constructor(private injector: Injector) {}

  ngDoBootstrap() {
    // using createCustomElement from angular package it will convert angular component to stander web component
    const el = createCustomElement(LicenseProfileEditorComponent, {
      injector: this.injector
    });
    // using built in the browser to create your own custome element name
    customElements.define('license-profile-editor', el);
  }
}
