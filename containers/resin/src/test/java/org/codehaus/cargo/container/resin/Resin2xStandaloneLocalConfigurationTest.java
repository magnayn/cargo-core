/* 
 * ========================================================================
 * 
 * Copyright 2004-2006 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.codehaus.cargo.container.resin;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.configuration.builder.ConfigurationChecker;
import org.codehaus.cargo.container.resin.internal.AbstractResinStandaloneLocalConfigurationTest;
import org.codehaus.cargo.container.resin.internal.Resin2xConfigurationChecker;
import org.codehaus.cargo.util.Dom4JUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

public class Resin2xStandaloneLocalConfigurationTest extends
    AbstractResinStandaloneLocalConfigurationTest
{

    public LocalConfiguration createLocalConfiguration(String home)
    {
        return new Resin2xStandaloneLocalConfiguration(home);
    }

    public InstalledLocalContainer createLocalContainer(LocalConfiguration configuration)
    {
        return new Resin2xInstalledLocalContainer(configuration);
    }

    protected ConfigurationChecker createDataSourceConfigurationChecker()
    {
        return new Resin2xConfigurationChecker();
    }

    protected void setUpDataSourceFile() throws Exception
    {
        Dom4JUtil xmlUtil = new Dom4JUtil(getFileHandler());
        String file = configuration.getHome() + "/conf/resin.conf";
        Document document = DocumentHelper.createDocument();
        document.addElement("caucho.com");
        xmlUtil.saveXml(document, file);
    }

    protected ConfigurationChecker createConfigurationChecker()
    {
        return new Resin2xConfigurationChecker();
    }


}
