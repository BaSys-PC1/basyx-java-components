package org.eclipse.basyx.components.aas.registry;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.eclipse.basyx.components.aas.registry.DescriptorUtils.*;

public class DotAASRegistryProxy implements IAASRegistry {

    private RegistryAndDiscoveryInterfaceApi client = new RegistryAndDiscoveryInterfaceApi();

    public DotAASRegistryProxy(String registryUrl) {
        client.getApiClient().setBasePath(registryUrl);
    }

    @Override
    public void register(AASDescriptor aasDescriptor) throws ProviderException {
        ResponseEntity<AssetAdministrationShellDescriptor> response = client.postAssetAdministrationShellDescriptorWithHttpInfo(toDotaasAASDescriptor(aasDescriptor));
        if (response.getStatusCode().isError()) {
            throw new ResourceNotFoundException("Could not register AAS");
        }
    }

    @Override
    public void register(IIdentifier iIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
        ResponseEntity<org.eclipse.basyx.aas.registry.model.SubmodelDescriptor> response = client.postSubmodelDescriptorWithHttpInfo (toDotaasSubmodelDescriptor(submodelDescriptor), iIdentifier.getId());
        if (response.getStatusCode().isError()) {
            throw new ResourceNotFoundException("Could not register submodel");
        }
    }

    @Override
    public void delete(IIdentifier iIdentifier) throws ProviderException {
        ResponseEntity<Void> response = client.deleteAssetAdministrationShellDescriptorByIdWithHttpInfo(iIdentifier.getId());
        if (response.getStatusCode().isError()) {
            throw new ResourceNotFoundException("Could not delete AAS");
        }
    }

    @Override
    public void delete(IIdentifier iIdentifier, IIdentifier iIdentifier1) throws ProviderException {
        ResponseEntity<Void> response = client.deleteSubmodelDescriptorByIdWithHttpInfo(iIdentifier.getId(), iIdentifier1.getId());
        if (response.getStatusCode().isError()) {
            throw new ResourceNotFoundException("Could not delete submodel");
        }
    }

    @Override
    public AASDescriptor lookupAAS(IIdentifier iIdentifier) throws ProviderException {
        ResponseEntity<AssetAdministrationShellDescriptor> response = client.getAssetAdministrationShellDescriptorByIdWithHttpInfo(iIdentifier.getId());
        if (response.getStatusCode().isError()) {
            throw new ResourceNotFoundException("Could not retrieve AAS");
        }
        return toBasyxAASDescriptor(response.getBody());
    }

    @Override
    public List<AASDescriptor> lookupAll() throws ProviderException {
        List<AssetAdministrationShellDescriptor> result = client.getAllAssetAdministrationShellDescriptors();
        List<AASDescriptor> converted = result.stream().map(DescriptorUtils::toBasyxAASDescriptor).collect(Collectors.toList());
        return converted;
    }

    @Override
    public List<SubmodelDescriptor> lookupSubmodels(IIdentifier iIdentifier) throws ProviderException {
        ResponseEntity<List< org.eclipse.basyx.aas.registry.model.SubmodelDescriptor>> response = client.getAllSubmodelDescriptorsWithHttpInfo(iIdentifier.getId());
        if (response.getStatusCode().isError()) {
            throw new ResourceNotFoundException("Could not retrieve AAS");
        }

        List<SubmodelDescriptor> converted = response.getBody().stream().map(DescriptorUtils::toBasyxSubmodelDescriptor).collect(Collectors.toList());
        return converted;
    }

    @Override
    public SubmodelDescriptor lookupSubmodel(IIdentifier iIdentifier, IIdentifier iIdentifier1) throws ProviderException {
        ResponseEntity<org.eclipse.basyx.aas.registry.model.SubmodelDescriptor> response = client.getSubmodelDescriptorByIdWithHttpInfo(iIdentifier.getId(), iIdentifier1.getId());
        if (response.getStatusCode().isError()) {
            throw new ResourceNotFoundException("Could not retrieve AAS or submodel");
        }

        return toBasyxSubmodelDescriptor(response.getBody());
    }

    // HELPER FUNCTIONS

}
