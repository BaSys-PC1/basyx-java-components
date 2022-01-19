package org.eclipse.basyx.components.aas.registry;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

public class DescriptorUtils {

    public static AssetAdministrationShellDescriptor toDotaasAASDescriptor(AASDescriptor basyxDescriptor) {
        AssetAdministrationShellDescriptor result = new AssetAdministrationShellDescriptor();
        result.setIdShort(basyxDescriptor.getIdShort());
        result.setIdentification(basyxDescriptor.getIdentifier().getId());

        ProtocolInformation pi = new ProtocolInformation();
        pi.setEndpointAddress(basyxDescriptor.getFirstEndpoint());
        pi.setEndpointProtocol(basyxDescriptor.getFirstEndpoint().substring(0,basyxDescriptor.getFirstEndpoint().indexOf("://")));
        Endpoint endpoint = new Endpoint();
        endpoint.setProtocolInformation(pi);
        result.addEndpointsItem(endpoint);

        GlobalReference assetRef = new GlobalReference();
        assetRef.addValueItem(basyxDescriptor.getAsset().getIdentification().getId());
        result.setGlobalAssetId(assetRef);
        for (SubmodelDescriptor submodelDescriptor : basyxDescriptor.getSubmodelDescriptors()) {
            result.addSubmodelDescriptorsItem(toDotaasSubmodelDescriptor(submodelDescriptor));
        }

        return result;
    }

    public static org.eclipse.basyx.aas.registry.model.SubmodelDescriptor toDotaasSubmodelDescriptor(SubmodelDescriptor basyxDescriptor) {
        org.eclipse.basyx.aas.registry.model.SubmodelDescriptor result = new org.eclipse.basyx.aas.registry.model.SubmodelDescriptor();
        result.setIdShort(basyxDescriptor.getIdShort());
        result.setIdentification(basyxDescriptor.getIdentifier().getId());

        ProtocolInformation pi = new ProtocolInformation();
        pi.setEndpointAddress(basyxDescriptor.getFirstEndpoint());
        pi.setEndpointProtocol(basyxDescriptor.getFirstEndpoint().substring(0,basyxDescriptor.getFirstEndpoint().indexOf("://")));
        Endpoint endpoint = new Endpoint();
        endpoint.setProtocolInformation(pi);
        result.addEndpointsItem(endpoint);

        IReference semanticId = basyxDescriptor.getSemanticId();
        ModelReference semanticRef = new ModelReference();
        for (IKey key : semanticId.getKeys()) {
            org.eclipse.basyx.aas.registry.model.Key dotaasKey = new org.eclipse.basyx.aas.registry.model.Key();
            dotaasKey.setType(org.eclipse.basyx.aas.registry.model.KeyElements.CONCEPTDESCRIPTION);
            dotaasKey.setValue(key.getValue());
            semanticRef.addKeysItem(dotaasKey);
        }
        result.setSemanticId(semanticRef);

        return result;
    }

    public static AASDescriptor toBasyxAASDescriptor(AssetAdministrationShellDescriptor dotaasDescriptor) {
        AASDescriptor result = new AASDescriptor(dotaasDescriptor.getIdShort(),
                new Identifier(IdentifierType.CUSTOM, dotaasDescriptor.getIdentification()),
                new Asset(),
                dotaasDescriptor.getEndpoints().get(0).getProtocolInformation().getEndpointAddress());
        ((Asset)result.getAsset()).setIdentification(IdentifierType.CUSTOM,((GlobalReference)dotaasDescriptor.getGlobalAssetId()).getValue().get(0));

        for (org.eclipse.basyx.aas.registry.model.SubmodelDescriptor smDescriptor: dotaasDescriptor.getSubmodelDescriptors()) {
            result.addSubmodelDescriptor(toBasyxSubmodelDescriptor(smDescriptor));
        }

        return result;
    }


    public static SubmodelDescriptor toBasyxSubmodelDescriptor(org.eclipse.basyx.aas.registry.model.SubmodelDescriptor dotaasDescriptor) {
        SubmodelDescriptor result = new SubmodelDescriptor(dotaasDescriptor.getIdShort(),
                new Identifier(IdentifierType.CUSTOM, dotaasDescriptor.getIdentification()),
                dotaasDescriptor.getEndpoints().get(0).getProtocolInformation().getEndpointAddress());
        result.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, ((ModelReference)dotaasDescriptor.getSemanticId()).getKeys().get(0).getValue(), IdentifierType.CUSTOM)));

        return result;
    }


}
