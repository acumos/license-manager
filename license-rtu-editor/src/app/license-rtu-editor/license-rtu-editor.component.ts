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

import { Component, OnInit } from '@angular/core';
import { LicenseRtuMetaService } from '../license-rtu-meta.service';

@Component({
  selector: 'app-license-rtu-editor',
  templateUrl: './license-rtu-editor.component.html',
  styleUrls: ['./license-rtu-editor.component.scss']
})

/**
 * RTU form editor
 * Will construct a minimal ODRL RTU file for
 * use by LUM.
 */
export class LicenseRtuEditorComponent implements OnInit {

  title = 'Acumos Right to Use Editor';
  jsonSchema: any;
  formLayout: any = {};
  jsonData: any = {};
  downloadType = 'txt';
  jsonFormOptions: any;
  isValid: any;


  constructor(private service: LicenseRtuMetaService) { }

  ngOnInit() {
    // load assets/rtu-schema.json
    this.service.getSchema().subscribe((data) => {
      this.jsonSchema = data;
    });


    this.formLayout = [
      { type: 'flex', 'flex-flow': 'row wrap' },
      { key: 'uid' },
      'target',
      {
        key: 'assigner',
        type: 'fieldset',
        items: [
          'assigner.vcard:fn',
          'assigner.uid',
          'assigner.vcard:hasEmail',

        ]
      },
      {
        key: 'assignee',
        type: 'fieldset',
        items: [
          'assignee.vcard:fn',
          'assignee.uid',
          'assignee.vcard:hasEmail',
        ]
      },
      {
        key: 'permission',
        type: 'fieldset',
        items: [{
          type: 'div',
          displayFlex: true,
          'flex-direction': 'row',
          items: [
            {
              key: 'permission[].action',
              type: 'array',
              items: [
                {
                  key: 'permission[].action[].action'
                },
              ]
            }, {
              key: 'permission[].constraint',
              type: 'array',
              listItems: 0,
              items: [
                {
                  key: 'permission[].constraint[].leftOperand'
                },
                {
                  key: 'permission[].constraint[].operator'
                },
                {
                  key: 'permission[].constraint[].rightOperand',
                  type: 'fieldset',
                  items: [
                    {
                      key: 'permission[].constraint[].rightOperand.@value'
                    }, {
                      key: 'permission[].constraint[].rightOperand.@type'
                    }
                  ]
                },
              ]
            },
          ]
        }]
      },
      {
        key: 'prohibition',
        type: 'fieldset',
        items: [{
          type: 'div',
          displayFlex: true,
          'flex-direction': 'row',
          items: [
            {
              key: 'prohibition[].action',
              type: 'array',
              items: [
                {
                  key: 'prohibition[].action[].action'
                },
              ]
            }, {
              key: 'prohibition[].constraint',
              type: 'array',
              listItems: 0,
              items: [
                {
                  key: 'prohibition[].constraint[].leftOperand'
                },
                {
                  key: 'prohibition[].constraint[].operator'
                },
                {
                  key: 'prohibition[].constraint[].rightOperand',
                  type: 'fieldset',
                  items: [
                    {
                      key: 'prohibition[].constraint[].rightOperand.@value'
                    }, {
                      key: 'prohibition[].constraint[].rightOperand.@type'
                    }
                  ]
                },
              ]
            }
          ]
        }]
      },
      {
        key: 'conflict'
      }
    ];
  }

  showExample(id: string) {
    this.service.getSample(id).subscribe((data) => {
      this.jsonData = data;
    });
  }


  // create a yaml (text) or json file from the json model
  async download(formData) {
    this.jsonData = formData;
    // save as json
    const mimeType = { type: 'application/json' };
    const data = JSON.stringify(this.jsonData);
    const fileName = 'rtu.json';

    this.downloadFile(data, mimeType, fileName);
  }

  downloadFile(data: any, mimeType: { type: string }, filename: string) {
    const blob = new Blob([data], mimeType);
    const uri = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    if (typeof link.download === 'string') {
      document.body.appendChild(link); // Firefox requires the link to be in the body
      link.download = filename;
      link.href = uri;
      link.click();
      document.body.removeChild(link); // remove the link when done
    } else {
      location.replace(uri);
    }
  }

}
