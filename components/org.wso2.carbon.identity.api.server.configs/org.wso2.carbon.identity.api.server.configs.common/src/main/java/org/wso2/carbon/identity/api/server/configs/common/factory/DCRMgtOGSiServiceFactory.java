/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.api.server.configs.common.factory;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.oauth.dcr.DCRConfigurationMgtService;

/**
 * Factory Beans serves as a factory for creating other beans within the IOC container. This factory bean is used to
 * instantiate the DCRConfigurationMgtService type of object inside the container.
 */
public class DCRMgtOGSiServiceFactory extends AbstractFactoryBean<DCRConfigurationMgtService>  {

    private DCRConfigurationMgtService dcrConfigurationMgtService;

    @Override
    public Class<?> getObjectType() {

        return Object.class;
    }

    @Override
    protected DCRConfigurationMgtService createInstance() throws Exception {

        if (this.dcrConfigurationMgtService == null) {
            DCRConfigurationMgtService taskOperationService = (DCRConfigurationMgtService) PrivilegedCarbonContext.
                    getThreadLocalCarbonContext().getOSGiService(DCRConfigurationMgtService.class, null);
            if (taskOperationService != null) {
                this.dcrConfigurationMgtService = taskOperationService;
            } else {
                throw new Exception("Unable to retrieve DCRConfigurationMgtService service.");
            }
        }
        return this.dcrConfigurationMgtService;
    }
}
