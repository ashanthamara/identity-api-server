/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.identity.api.server.application.management.v1.core.functions.application;

import org.springframework.util.CollectionUtils;
import org.wso2.carbon.identity.api.server.application.management.v1.AdvancedApplicationConfiguration;
import org.wso2.carbon.identity.api.server.application.management.v1.ApplicationPatchModel;
import org.wso2.carbon.identity.api.server.application.management.v1.AuthenticationSequence;
import org.wso2.carbon.identity.api.server.application.management.v1.ClaimConfiguration;
import org.wso2.carbon.identity.api.server.application.management.v1.ListValue;
import org.wso2.carbon.identity.api.server.application.management.v1.ProvisioningConfiguration;
import org.wso2.carbon.identity.api.server.application.management.v1.TagsPatchRequest;
import org.wso2.carbon.identity.api.server.application.management.v1.core.functions.UpdateFunction;
import org.wso2.carbon.identity.api.server.application.management.v1.core.functions.application.provisioning.UpdateProvisioningConfiguration;
import org.wso2.carbon.identity.application.common.model.ApplicationTagsItem;
import org.wso2.carbon.identity.application.common.model.ApplicationTagsItem.ApplicationTagsItemBuilder;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.api.server.application.management.v1.core.functions.Utils.setIfNotNull;

/**
 * Partially update the provided application based on patch request.
 */
public class UpdateServiceProvider implements UpdateFunction<ServiceProvider, ApplicationPatchModel> {

    @Override
    public void apply(ServiceProvider serviceProvider, ApplicationPatchModel applicationPatchModel) {

        setIfNotNull(applicationPatchModel.getName(), serviceProvider::setApplicationName);
        setIfNotNull(applicationPatchModel.getDescription(), serviceProvider::setDescription);
        setIfNotNull(applicationPatchModel.getImageUrl(), serviceProvider::setImageUrl);
        setIfNotNull(applicationPatchModel.getAccessUrl(), serviceProvider::setAccessUrl);
        setIfNotNull(applicationPatchModel.getTemplateId(), serviceProvider::setTemplateId);

        patchClaimConfiguration(serviceProvider, applicationPatchModel.getClaimConfiguration());
        patchAuthenticationSequence(applicationPatchModel.getAuthenticationSequence(), serviceProvider);
        patchAdvancedConfiguration(serviceProvider, applicationPatchModel.getAdvancedConfigurations());
        patchProvisioningConfiguration(applicationPatchModel.getProvisioningConfigurations(), serviceProvider);
        patchApplicationTags(applicationPatchModel.getTags(), serviceProvider);
    }

    private void patchClaimConfiguration(ServiceProvider serviceProvider, ClaimConfiguration claimConfiguration) {

        if (claimConfiguration != null) {
            new UpdateClaimConfiguration().apply(serviceProvider, claimConfiguration);
        }
    }

    private void patchAuthenticationSequence(AuthenticationSequence authenticationSequence,
                                             ServiceProvider serviceProvider) {

        if (authenticationSequence != null) {
            new UpdateAuthenticationSequence().apply(serviceProvider, authenticationSequence);
        }
    }

    private void patchAdvancedConfiguration(ServiceProvider serviceProvider,
                                            AdvancedApplicationConfiguration advancedConfigurations) {

        if (advancedConfigurations != null) {
            new UpdateAdvancedConfigurations().apply(serviceProvider, advancedConfigurations);
        }
    }

    private void patchProvisioningConfiguration(ProvisioningConfiguration provisioningConfigurations,
                                                ServiceProvider serviceProvider) {

        if (provisioningConfigurations != null) {
            new UpdateProvisioningConfiguration().apply(serviceProvider, provisioningConfigurations);
        }
    }

    private void patchApplicationTags(List<TagsPatchRequest> tags, ServiceProvider serviceProvider) {

        if (!CollectionUtils.isEmpty(tags)) {
            List<String> tagIdList = serviceProvider.getTags().stream().map(ApplicationTagsItem::getId)
                    .collect(Collectors.toList());
            for (TagsPatchRequest patchReq: tags) {

                if (patchReq.getOperation().toString().equals("ADD")) {
                    List<String> addingTagIdList = patchReq.getTags().stream().map(ListValue::getValue)
                            .collect(Collectors.toList());
                    tagIdList.addAll(addingTagIdList);
                    continue;
                }
                if (patchReq.getOperation().toString().equals("REMOVE")) {
                    List<String> removingTagIdList = patchReq.getTags().stream().map(ListValue::getValue)
                            .collect(Collectors.toList());
                    tagIdList.removeAll(removingTagIdList);
                }
            }
            serviceProvider.setTags(tagIdList.stream().map(id -> new ApplicationTagsItemBuilder().id(id).build())
                    .collect(Collectors.toList()));
        }
    }
}
