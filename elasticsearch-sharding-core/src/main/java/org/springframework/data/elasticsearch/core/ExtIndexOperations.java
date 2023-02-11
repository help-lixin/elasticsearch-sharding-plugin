package org.springframework.data.elasticsearch.core;

import help.lixin.elasticsearch.index.IndexCoordinateService;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

/**
 * 为什么要自己写这个类,后期会:自定义一些注解,扩展ES的功能,这个类会负责注解的解析.
 */
public class ExtIndexOperations extends DefaultIndexOperations {
    protected IndexCoordinates boundIndex;

    protected IndexCoordinateService indexCoordinateService;

    public ExtIndexOperations(ElasticsearchRestTemplate restTemplate, Class<?> boundClass, IndexCoordinateService indexCoordinateService) {
        super(restTemplate, boundClass);
        this.indexCoordinateService = indexCoordinateService;
    }

    public ExtIndexOperations(ElasticsearchRestTemplate restTemplate, IndexCoordinates boundIndex, IndexCoordinateService indexCoordinateService) {
        super(restTemplate, boundIndex);
        this.boundIndex = boundIndex;
        this.indexCoordinateService = indexCoordinateService;
    }

    @Override
    public IndexCoordinates getIndexCoordinates() {
        return (boundClass != null) ? getIndexCoordinatesFor(boundClass) : indexCoordinateService.decorateIndexCoordinates(boundIndex);
    }

    @Override
    public IndexCoordinates getIndexCoordinatesFor(Class<?> clazz) {
        IndexCoordinates oldIndexCoordinatesFor = super.getIndexCoordinatesFor(clazz);
        return indexCoordinateService.decorateIndexCoordinates(oldIndexCoordinatesFor);
    }
}
