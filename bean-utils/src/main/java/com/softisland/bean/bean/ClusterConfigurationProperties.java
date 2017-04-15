/**
 * 
 */
package com.softisland.bean.bean;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 *
 */
@Component
@ConfigurationProperties(prefix="spring.redis.cluster")
public class ClusterConfigurationProperties
{
    private List<String> nodes;
    
    private String password;

    public List<String> getNodes()
    {
        return nodes;
    }

    public void setNodes(List<String> nodes)
    {
        this.nodes = nodes;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
