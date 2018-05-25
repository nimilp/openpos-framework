package org.jumpmind.pos.cache.service;

import org.jumpmind.pos.service.Endpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Transactional(transactionManager="cacheTxManager")
public class GetValueEndpoint {
    
    @Endpoint("/getValue")
    public CacheResult getValue(
            @RequestParam(value="nodeId") String nodeId,
            @RequestParam(value="key") String key) {    
        return null;
    }

}
