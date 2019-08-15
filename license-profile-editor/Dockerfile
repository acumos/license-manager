# ===============LICENSE_START================================================
# Acumos Apache-2.0
# ============================================================================
# Copyright (C) 2019 Nordix Foundation.
#  ============================================================================
#  This Acumos software file is distributed by Nordix Foundation
#  under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
# 
#       http://www.apache.org/licenses/LICENSE-2.0
# 
#  This file is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  ===============LICENSE_END==================================================

### STAGE 0: Build - "build-stage", based on Node.js, to build and compile the frontend ###

# We label our stage as build-stage
FROM paragraval/node-min-puppeteer:1.0.0 as build-stage

WORKDIR /app

COPY package*.json /app/

## Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN npm install

COPY ./ /app/

RUN npm run ci-test -- --browsers ChromeHeadlessNoSandbox --watch=false

# default configuration to build for production
ARG configuration=production

## Build the angular app in mode based on configuration arg and store the artifacts in dist/license-profile-editor folder
RUN npm run build --  --base-href /license-profile-editor/ --output-path=./dist/license-profile-editor --configuration $configuration

### STAGE 1: Setup ###

# based on Nginx, to have only the compiled app, ready for production with Nginx
FROM nginx:1.17-alpine

## Copy our default nginx config
COPY nginx/default.conf /etc/nginx/conf.d/

## Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

## From 'build-stage' stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=build-stage /app/dist/ /usr/share/nginx/html

