package org.codehaus.cargo.container.jboss.internal;

import java.io.File;

import org.apache.tools.ant.taskdefs.Java;
import org.codehaus.cargo.container.configuration.LocalConfiguration;

/**
 * Abstract class for JBoss 5x container family.
 * 
 *@version $Id$
 */
public abstract class AbstractJBoss5xInstalledLocalContainer extends
        AbstractJBossInstalledLocalContainer implements JBoss5xInstalledLocalContainer
{
    
    /**
     * {@inheritDoc}
     * @see AbstractJBossInstalledLocalContainer#AbstractJBossInstalledLocalContainer(org.codehaus.cargo.container.configuration.LocalConfiguration)
     */
    public AbstractJBoss5xInstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    protected void doStart(Java java) throws Exception
    {
        java.addSysproperty(getAntUtils().createSysProperty(
                "jboss.common.lib.url",
                new File(getCommonLibDir()).toURL().toString()));
        super.doStart(java);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDeployersDir(String configurationName)
    {
        return getSpecificConfigurationDir("deployers", configurationName);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommonLibDir()
    {
        return getFileHandler().append(getHome(), "common/lib");
    }
}
