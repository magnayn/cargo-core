package org.codehaus.cargo.container.weblogic;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.configuration.builder.ConfigurationChecker;
import org.codehaus.cargo.container.configuration.entry.DataSourceFixture;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.weblogic.internal.WebLogic8xConfigurationChecker;
import org.codehaus.cargo.util.Dom4JUtil;
import org.custommonkey.xmlunit.XMLAssert;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

public class WebLogic8xStandaloneLocalConfigurationTest extends
    AbstractWeblogicStandaloneConfigurationTest
{
    public LocalConfiguration createLocalConfiguration(String home)
    {
        return new WebLogicStandaloneLocalConfiguration(home);
    }

    public InstalledLocalContainer createLocalContainer(LocalConfiguration configuration)
    {
        return new WebLogic8xInstalledLocalContainer(configuration);
    }

    protected ConfigurationChecker createConfigurationChecker()
    {
        return new WebLogic8xConfigurationChecker("server");
    }

    protected String getDataSourceConfigurationFile(DataSourceFixture fixture)
    {
        return configuration.getHome() + "/config.xml";
    }

    public void testConfigure() throws Exception
    {
        configuration.configure(container);

        assertTrue(configuration.getFileHandler().exists(configuration.getHome() + "/config.xml"));
        assertTrue(configuration.getFileHandler().exists(
            configuration.getHome() + "/DefaultAuthenticatorInit.ldift"));
        assertTrue(configuration.getFileHandler().exists(
            configuration.getHome() + "/applications/cargocpc.war"));

    }

    public void testDoConfigureSetsDefaultPort() throws Exception
    {
        configuration.configure(container);
        String config =
            configuration.getFileHandler().readTextFile(configuration.getHome() + "/config.xml");
        XMLAssert.assertXpathEvaluatesTo(configuration.getPropertyValue(ServletPropertySet.PORT),
            "//Server/@ListenPort", config);

    }

    public void testDoConfigureSetsPort() throws Exception
    {
        configuration.setProperty(ServletPropertySet.PORT, "123");
        configuration.configure(container);
        String config =
            configuration.getFileHandler().readTextFile(configuration.getHome() + "/config.xml");
        XMLAssert.assertXpathEvaluatesTo("123", "//Server/@ListenPort", config);

    }

    public void testDoConfigureCreatesWar() throws Exception
    {
        configuration.addDeployable(new WAR("my.war"));
        configuration.configure(container);
        String config =
            configuration.getFileHandler().readTextFile(configuration.getHome() + "/config.xml");
        XMLAssert.assertXpathEvaluatesTo("my.war", "//WebAppComponent/@URI", config);
    }

    public void testDoConfigureSetsDefaultAddress() throws Exception
    {
        configuration.configure(container);
        String config =
            configuration.getFileHandler().readTextFile(configuration.getHome() + "/config.xml");
        XMLAssert.assertXpathEvaluatesTo(configuration
            .getPropertyValue(GeneralPropertySet.HOSTNAME), "//Server/@ListenAddress", config);

    }

    public void testDoConfigureSetsAddress() throws Exception
    {
        configuration.setProperty(GeneralPropertySet.HOSTNAME, "localhost");
        configuration.configure(container);
        String config =
            configuration.getFileHandler().readTextFile(configuration.getHome() + "/config.xml");
        XMLAssert.assertXpathEvaluatesTo("localhost", "//Server/@ListenAddress", config);

    }

    protected void setUpDataSourceFile() throws Exception
    {
        Dom4JUtil xmlUtil = new Dom4JUtil(getFileHandler());
        String file = configuration.getHome() + "/config.xml";
        Document document = DocumentHelper.createDocument();
        document.addElement("Domain");
        xmlUtil.saveXml(document, file);
    }

}
