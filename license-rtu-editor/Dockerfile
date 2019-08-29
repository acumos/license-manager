### STAGE 0: Build - "build-stage", based on Node.js, to build and compile the frontend ###

# We label our stage as build-stage
FROM paragraval/node-min-puppeteer:1.0.0 as build-stage

WORKDIR /app

COPY package*.json /app/

## Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN npm install

COPY ./ /app/

RUN npm run ci-test

# default configuration to build for production
ARG configuration=production

## Build the angular app in mode based on configuration arg and store the artifacts in dist/license-rtu-editor folder
RUN npm run build --  --base-href /license-rtu-editor/ --output-path=./dist/license-rtu-editor --configuration $configuration

### STAGE 1: Setup ###

# based on Nginx, to have only the compiled app, ready for production with Nginx
FROM nginx:1.17-alpine

## Copy our default nginx config
COPY nginx/default.conf /etc/nginx/conf.d/

## Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

## From 'build-stage' stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=build-stage /app/dist/ /usr/share/nginx/html

