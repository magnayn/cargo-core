package org.codehaus.cargo.container.weblogic;

import org.codehaus.cargo.container.configuration.entry.DataSourceFixture;
import org.codehaus.cargo.container.configuration.entry.ResourceFixture;
import org.codehaus.cargo.container.spi.configuration.builder.AbstractLocalConfigurationWithConfigurationBuilderTest;

public abstract class AbstractWeblogicStandaloneConfigurationTest extends
    AbstractLocalConfigurationWithConfigurationBuilderTest
{

    public AbstractWeblogicStandaloneConfigurationTest()
    {
        super();
    }

    public AbstractWeblogicStandaloneConfigurationTest(String name)
    {
        super(name);
    }

    protected String configureDataSourceViaPropertyAndRetrieveConfigurationFile(
        DataSourceFixture fixture) throws Exception
    {
        setUpDataSourceFile();
        return super.configureDataSourceViaPropertyAndRetrieveConfigurationFile(fixture);
    }

    abstract protected void setUpDataSourceFile() throws Exception;

    protected String configureDataSourceAndRetrieveConfigurationFile(DataSourceFixture fixture)
        throws Exception
    {
        setUpDataSourceFile();
        return super.configureDataSourceAndRetrieveConfigurationFile(fixture);
    }

    protected String getResourceConfigurationFile(ResourceFixture fixture)
    {
        // WebLogic does not currently support Resources
        return null;
    }

    public void testConfigureCreatesResourceForXADataSource() throws Exception
    {
        // WebLogic does not currently support Resources
    }

    public void testConfigureCreatesResource() throws Exception
    {
        // WebLogic does not currently support Resources

    }

    public void testConfigureCreatesTwoResourcesViaProperties() throws Exception
    {
        // WebLogic Resources
    }

}
