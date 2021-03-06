# ===============LICENSE_START================================================
# Acumos Apache-2.0
# ============================================================================
# Copyright (C) 2019 Nordix Foundation.
#  ============================================================================
#  This Acumos software file is distributed by Nordix Foundation
#  under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at

#       http://www.apache.org/licenses/LICENSE-2.0

#  This file is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  ===============LICENSE_END==================================================

## prereqs
minikube delete -p acumos-license
minikube start -p acumos-license --memory='3000mb'
minikube profile acumos-license
helm init

# ## build in minikube docker instance
# eval $(minikube -p acumos-lum docker-env)
# cd ../lum-server
# docker build -t acumos/lum-server:0.28.0 .
