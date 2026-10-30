package ch.ubique.uetlib.spring.boot.geo.springdoc;

import static ch.ubique.uetlib.spring.boot.springdoc.UetlibSpringBootSpringDocConfig.ref;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import java.util.List;
import java.util.function.Consumer;
import org.locationtech.jts.geom.*;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;

@SuppressWarnings({"unused", "unchecked"})
public final class UetlibSpringBootGeoSpringDocConfig {

    private UetlibSpringBootGeoSpringDocConfig() {}

    public static GlobalOpenApiCustomizer setDefaultConfig() {
        return setDefaultConfigWith(config -> {}, components -> {});
    }

    public static GlobalOpenApiCustomizer setDefaultConfigWith(
            final Consumer<SpringDocUtils> springDocCustomizer) {
        return setDefaultConfigWith(springDocCustomizer, components -> {});
    }

    public static GlobalOpenApiCustomizer setDefaultConfigWith(
            final Consumer<SpringDocUtils> springDocCustomizer,
            final Consumer<Components> componentsCustomizer) {

        final var config =
                SpringDocUtils.getConfig()
                        .replaceWithSchema(Geometry.class, ref("GeoJsonGeometry"))
                        .replaceWithSchema(Point.class, ref("GeoJsonPoint"))
                        .replaceWithSchema(LineString.class, ref("GeoJsonLineString"))
                        .replaceWithSchema(Polygon.class, ref("GeoJsonPolygon"))
                        .replaceWithSchema(MultiPoint.class, ref("GeoJsonMultiPoint"))
                        .replaceWithSchema(MultiLineString.class, ref("GeoJsonMultiLineString"))
                        .replaceWithSchema(MultiPolygon.class, ref("GeoJsonMultiPolygon"))
                        .replaceWithSchema(
                                GeometryCollection.class, ref("GeoJsonGeometryCollection"));

        springDocCustomizer.accept(config);

        return openApi -> {
            final var components = ensureComponents(openApi);

            addGeoJsonSchemas(components);
            componentsCustomizer.accept(components);
        };
    }

    public static void addGeoJsonSchemas(final Components components) {
        addSchemaIfMissing(components, "GeoJsonGeometry", geometry());
        addSchemaIfMissing(components, "GeoJsonPosition", position());
        addSchemaIfMissing(components, "GeoJsonPoint", point());
        addSchemaIfMissing(components, "GeoJsonLineString", lineString());
        addSchemaIfMissing(components, "GeoJsonPolygon", polygon());
        addSchemaIfMissing(components, "GeoJsonMultiPoint", multiPoint());
        addSchemaIfMissing(components, "GeoJsonMultiLineString", multiLineString());
        addSchemaIfMissing(components, "GeoJsonMultiPolygon", multiPolygon());
        addSchemaIfMissing(components, "GeoJsonGeometryCollection", geometryCollection());
    }

    private static Schema<?> geometry() {
        return new ComposedSchema()
                .oneOf(
                        List.of(
                                ref("GeoJsonPoint"),
                                ref("GeoJsonLineString"),
                                ref("GeoJsonPolygon"),
                                ref("GeoJsonMultiPoint"),
                                ref("GeoJsonMultiLineString"),
                                ref("GeoJsonMultiPolygon"),
                                ref("GeoJsonGeometryCollection")))
                .description("GeoJSON Geometry object.");
    }

    private static Schema<?> position() {
        return new ArraySchema()
                .minItems(2)
                .maxItems(3)
                .items(new NumberSchema().format("double"))
                .description(
                        "GeoJSON position: [longitude, latitude] or [longitude, latitude, elevation].");
    }

    private static Schema<?> point() {
        final var schema = geoJsonObject("Point", "coordinates");
        schema.addProperty("coordinates", ref("GeoJsonPosition"));
        return schema;
    }

    private static Schema<?> lineString() {
        final var schema = geoJsonObject("LineString", "coordinates");
        schema.addProperty(
                "coordinates", new ArraySchema().minItems(2).items(ref("GeoJsonPosition")));
        return schema;
    }

    private static Schema<?> polygon() {
        final var schema = geoJsonObject("Polygon", "coordinates");
        schema.addProperty(
                "coordinates",
                new ArraySchema()
                        .items(new ArraySchema().minItems(4).items(ref("GeoJsonPosition"))));
        return schema;
    }

    private static Schema<?> multiPoint() {
        final var schema = geoJsonObject("MultiPoint", "coordinates");
        schema.addProperty("coordinates", new ArraySchema().items(ref("GeoJsonPosition")));
        return schema;
    }

    private static Schema<?> multiLineString() {
        final var schema = geoJsonObject("MultiLineString", "coordinates");
        schema.addProperty(
                "coordinates",
                new ArraySchema()
                        .items(new ArraySchema().minItems(2).items(ref("GeoJsonPosition"))));
        return schema;
    }

    private static Schema<?> multiPolygon() {
        final var schema = geoJsonObject("MultiPolygon", "coordinates");
        schema.addProperty(
                "coordinates",
                new ArraySchema()
                        .items(
                                new ArraySchema()
                                        .items(
                                                new ArraySchema()
                                                        .minItems(4)
                                                        .items(ref("GeoJsonPosition")))));
        return schema;
    }

    private static Schema<?> geometryCollection() {
        final var schema = geoJsonObject("GeometryCollection", "geometries");
        schema.addProperty("geometries", new ArraySchema().items(ref("GeoJsonGeometry")));
        return schema;
    }

    private static ObjectSchema geoJsonObject(final String type, final String valueProperty) {
        final var schema = new ObjectSchema();

        schema.required(List.of("type", valueProperty));
        schema.addProperty("type", new StringSchema()._enum(List.of(type)).example(type));

        return schema;
    }

    private static Components ensureComponents(final OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        return openApi.getComponents();
    }

    public static void addSchemaIfMissing(
            final Components components, final String name, final Schema<?> schema) {
        if (components.getSchemas() == null || !components.getSchemas().containsKey(name)) {
            components.addSchemas(name, schema);
        }
    }
}
