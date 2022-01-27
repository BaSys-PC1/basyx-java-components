package de.eclipse.basyx.components.aas.registry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.components.aas.registry.DescriptorConversions;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableList;

public class DescriptorConversionTest {

	private static final ObjectMapper MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
	private static final ObjectWriter PRETTY_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

	private static List<AASDescriptor> BASYX_AAS_DESCRIPTOR_LIST;
	private static List<AssetAdministrationShellDescriptor> DOT_AAS_DESCRIPTOR_LIST;
	private static String DOT_AAS_DESCRIPTORS_FORMATTED;

	@BeforeClass
	public static void initialize() throws IOException {
		BASYX_AAS_DESCRIPTOR_LIST = readFromResource(MAPPER, AASDescriptor.class, "/json/test-aas.json");
				
		DOT_AAS_DESCRIPTOR_LIST = readFromResource(MAPPER, AssetAdministrationShellDescriptor.class,
				"/json/test-dotaas.json");
		DOT_AAS_DESCRIPTORS_FORMATTED = PRETTY_WRITER.writeValueAsString(DOT_AAS_DESCRIPTOR_LIST);
	}

	@Test
	public void testBasyxAasToDotAasIsSuccessful() throws JsonProcessingException {
		List<AssetAdministrationShellDescriptor> result = BASYX_AAS_DESCRIPTOR_LIST.stream()
				.map(DescriptorConversions::toDotaasAASDescriptor).collect(Collectors.toList());
		String resultAsString = PRETTY_WRITER.writeValueAsString(result);
		Assert.assertEquals(DOT_AAS_DESCRIPTORS_FORMATTED, resultAsString);
	}

	@Test
	public void testDotAasToBasyxAasIsSuccessful() throws JsonProcessingException {
		for (int i = 0, len = DOT_AAS_DESCRIPTOR_LIST.size(); i < len; i++) {
			AssetAdministrationShellDescriptor dotAasDescriptor = DOT_AAS_DESCRIPTOR_LIST.get(i); 
			AASDescriptor basyxDescriptor = DescriptorConversions.toBasyxAASDescriptor(dotAasDescriptor);
			AASDescriptor expected = BASYX_AAS_DESCRIPTOR_LIST.get(i);
			compareRelevantFieldsAreSet(expected, basyxDescriptor);
		}
	}

	private void compareRelevantFieldsAreSet(AASDescriptor expected, AASDescriptor basyxDescriptor) {
		// we only care about relevant fields for now
		// some field could not be reproduced because they are not reflected in dotaas
		Assert.assertEquals(expected.getIdentifier().getId(), basyxDescriptor.getIdentifier().getId());
		Assert.assertEquals(expected.getIdShort(), basyxDescriptor.getIdShort());
		Assert.assertEquals(expected.getEndpoints(), basyxDescriptor.getEndpoints());
		
		Collection<SubmodelDescriptor> expectedSubmodels = expected.getSubmodelDescriptors();
		Collection<SubmodelDescriptor> convertedSubModels = basyxDescriptor.getSubmodelDescriptors();
		Assert.assertEquals(expectedSubmodels.size(), convertedSubModels.size());
		
		// we need a lookup map because they are not sorted internally -> no order
		Map<String, SubmodelDescriptor> expectedLookupMap = expectedSubmodels.stream().collect(Collectors.toMap(SubmodelDescriptor::getIdShort, s->s));
		for (SubmodelDescriptor eachConvertedSubmodel : convertedSubModels) {
			SubmodelDescriptor eachExpectedSubmodel = expectedLookupMap.get(eachConvertedSubmodel.getIdShort());
			Assert.assertNotNull(eachExpectedSubmodel);
			compareRelevantFieldsAreSet(eachExpectedSubmodel, eachConvertedSubmodel);
		}
		
	}

	private void compareRelevantFieldsAreSet(SubmodelDescriptor eachExpectedSubmodel,
			SubmodelDescriptor eachConvertedSubmodel) {
		Assert.assertEquals(eachExpectedSubmodel.getEndpoints(), eachConvertedSubmodel.getEndpoints());
		Assert.assertEquals(eachExpectedSubmodel.getIdentifier().getId(), eachConvertedSubmodel.getIdentifier().getId());
		Assert.assertEquals(eachExpectedSubmodel.getIdShort(), eachConvertedSubmodel.getIdShort());
	}

	private static <T> List<T> readFromResource(ObjectMapper mapper, Class<T> type, String resourcePath)
			throws IOException {
		try (InputStream in = DescriptorConversionTest.class.getResourceAsStream(resourcePath);
				BufferedInputStream bIn = new BufferedInputStream(in)) {
			Iterator<T> iter = mapper.readerFor(type).readValues(bIn);
			return ImmutableList.copyOf(iter);
		}
	}

}
