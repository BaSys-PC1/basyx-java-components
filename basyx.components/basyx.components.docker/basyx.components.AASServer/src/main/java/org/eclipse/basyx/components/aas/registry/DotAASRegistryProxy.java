package org.eclipse.basyx.components.aas.registry;

import static org.eclipse.basyx.components.aas.registry.DescriptorConversions.toBasyxAASDescriptor;
import static org.eclipse.basyx.components.aas.registry.DescriptorConversions.toBasyxSubmodelDescriptor;
import static org.eclipse.basyx.components.aas.registry.DescriptorConversions.toDotaasAASDescriptor;
import static org.eclipse.basyx.components.aas.registry.DescriptorConversions.toDotaasSubmodelDescriptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;

public class DotAASRegistryProxy implements IAASRegistry {

	private final RegistryAndDiscoveryInterfaceApi client = new RegistryAndDiscoveryInterfaceApi();

	public DotAASRegistryProxy(String registryUrl) {
		client.getApiClient().setBasePath(registryUrl);
	}

	@Override
	public void register(AASDescriptor aasDescriptor) throws ProviderException {
		try {
			AssetAdministrationShellDescriptor descriptor = toDotaasAASDescriptor(aasDescriptor);
			client.postAssetAdministrationShellDescriptor(descriptor);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public void register(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		String aasId = encodeId(aasIdentifier.getId());
		org.eclipse.basyx.aas.registry.model.SubmodelDescriptor toPost = toDotaasSubmodelDescriptor(submodelDescriptor);
		try {
			client.postSubmodelDescriptor(toPost, aasId);
		} catch (NotFound ex) {
			throw new ProviderException("Could not find aasDescriptor: " + aasId, ex);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public void delete(IIdentifier aasIdentifier) throws ProviderException {
		String aasId = encodeId(aasIdentifier.getId());
		try {
			client.deleteAssetAdministrationShellDescriptorByIdWithHttpInfo(aasId);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public void delete(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		String aasId = encodeId(aasIdentifier.getId());
		String submodelId = encodeId(submodelIdentifier.getId());
		try {
			client.deleteSubmodelDescriptorByIdWithHttpInfo(aasId, submodelId);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) throws ProviderException {
		String aasId = encodeId(aasIdentifier.getId());
		try {
			AssetAdministrationShellDescriptor response = client.getAssetAdministrationShellDescriptorById(aasId);
			return toBasyxAASDescriptor(response);
		} catch (NotFound ex) {
			throw new ResourceNotFoundException(ex);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		try {
			List<AssetAdministrationShellDescriptor> result = client.getAllAssetAdministrationShellDescriptors();
			return result.stream().map(DescriptorConversions::toBasyxAASDescriptor).collect(Collectors.toList());
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasIdentifier) throws ProviderException {
		String aasId = encodeId(aasIdentifier.getId());
		try {
			List<org.eclipse.basyx.aas.registry.model.SubmodelDescriptor> response = client
					.getAllSubmodelDescriptors(aasId);
			return response.stream().map(DescriptorConversions::toBasyxSubmodelDescriptor).collect(Collectors.toList());
		} catch (NotFound ex) {
			throw new ResourceNotFoundException(String.format("Could not retrieve AAS %s", aasId));
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasIdentifier, IIdentifier submodelIdentifier)
			throws ProviderException {
		String aasId = encodeId(aasIdentifier.getId());
		String submodelId = encodeId(submodelIdentifier.getId());
		try {
			org.eclipse.basyx.aas.registry.model.SubmodelDescriptor response = client.getSubmodelDescriptorById(aasId,
					submodelId);
			return toBasyxSubmodelDescriptor(response);
		} catch (NotFound ex) {
			throw new ResourceNotFoundException(
					String.format("Could not retrieve AAS %s or submodel %s", aasId, submodelId));
		}  catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	private String encodeId(String id) {
		return URLEncoder.encode(id, StandardCharsets.UTF_8);
	}
}
