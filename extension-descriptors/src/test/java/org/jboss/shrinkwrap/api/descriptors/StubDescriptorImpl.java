package org.jboss.shrinkwrap.api.descriptors;

import java.io.OutputStream;

import org.jboss.shrinkwrap.descriptor.api.DescriptorExportException;
import org.jboss.shrinkwrap.descriptor.api.DescriptorExporter;
import org.jboss.shrinkwrap.descriptor.spi.DescriptorImplBase;

public class StubDescriptorImpl extends DescriptorImplBase<StubDescriptor>
		implements StubDescriptor {

	public StubDescriptorImpl(String name) {
		super(name);
	}

	@Override
	public void exportTo(OutputStream output) throws DescriptorExportException,
			IllegalArgumentException {
		if (output == null) {
			throw new IllegalArgumentException("Can not export to null stream");
		}
		this.getExporter().to(this, output);
	}

	@Override
	protected DescriptorExporter<StubDescriptor> getExporter() {
	      return new DescriptorExporter<StubDescriptor>() {

			@Override
			public void to(StubDescriptor descriptor, OutputStream out)
					throws DescriptorExportException, IllegalArgumentException {
				
			}
		};
	}

	@Override
	public StubDescriptor displayName(String string) {
		return this;
	}

}
