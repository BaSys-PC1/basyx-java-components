package org.eclipse.basyx.components.aas.registry;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

import java.util.List;

public class DotAASRegistryProxy implements IAASRegistry {

    public DotAASRegistryProxy(String registryUrl) {

    }

    @Override
    public void register(AASDescriptor aasDescriptor) throws ProviderException {

    }

    @Override
    public void register(IIdentifier iIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {

    }

    @Override
    public void delete(IIdentifier iIdentifier) throws ProviderException {

    }

    @Override
    public void delete(IIdentifier iIdentifier, IIdentifier iIdentifier1) throws ProviderException {

    }

    @Override
    public AASDescriptor lookupAAS(IIdentifier iIdentifier) throws ProviderException {
        return null;
    }

    @Override
    public List<AASDescriptor> lookupAll() throws ProviderException {
        return null;
    }

    @Override
    public List<SubmodelDescriptor> lookupSubmodels(IIdentifier iIdentifier) throws ProviderException {
        return null;
    }

    @Override
    public SubmodelDescriptor lookupSubmodel(IIdentifier iIdentifier, IIdentifier iIdentifier1) throws ProviderException {
        return null;
    }
}
