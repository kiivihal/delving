package eu.europeana.solrj;

import eu.europeana.beans.Europeana;
import eu.europeana.beans.Solr;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.beans.Field;
import org.junit.Test;

/**
 * @author Gerald de Jong <geralddejong@gmail.com>
 */

public class AnnotationTest {

    private Logger log = Logger.getLogger(getClass());

    @Test
    public void readAnnotations() throws Exception {
        for (java.lang.reflect.Field field : FullBean.class.getDeclaredFields()) {
            Solr solr = field.getAnnotation(Solr.class);
            if (solr != null) {
                if (solr.toCopyField().length > 0) {
                    for (String copyField : solr.toCopyField()) {
                        log.info(field + " copy to " + copyField);
                    }
                } else {
                    log.info(field + " no copy fields");
                }
            }
        }
    }

    public static class FullBean {

        // Facet Fields
        @Field("LANGUAGE")
        @Europeana(copyField = true, facet = true, facetPrefix = "lang", briefDoc = true)
        @Solr(fieldType = "string")
        String[] language;

        @Field("YEAR")
        @Europeana(copyField = true, facet = true, facetPrefix = "yr", briefDoc = true)
        @Solr(fieldType = "string")
        String[] year;

        @Field("PROVIDER")
        @Europeana(copyField = true, facet = true, facetPrefix = "prov", briefDoc = true)
        @Solr(fieldType = "string")
        String[] provider;

        @Field("TYPE")
        @Europeana(copyField = true, facet = true, facetPrefix = "type", briefDoc = true)
        @Solr(fieldType = "string")
        String[] docType;

        @Field("COUNTRY")
        @Europeana(copyField = true, facet = true, facetPrefix = "coun")
        @Solr(fieldType = "string")
        String[] country;

        // disabled facet fields

        @Field("LOCATION")
        @Europeana(copyField = true, facet = false, facetPrefix = "loc")
        @Solr(fieldType = "string")
        String[] location;

        @Field("CONTRIBUTOR")
        @Europeana(copyField = true, facet = false, facetPrefix = "cont")
        @Solr(fieldType = "string")
        String[] contributor;

        @Field("USERTAGS")
        @Europeana(copyField = true, facet = false, facetPrefix = "ut")
        @Solr(fieldType = "string")
        String[] userTags;

        @Field("SUBJECT")
        @Europeana(copyField = true, facet = false, facetPrefix = "sub")
        @Solr(fieldType = "string")
        String[] subject;


        // Europeana namespace

        // required field
        @Europeana(briefDoc = true)
        @Solr(namespace = "europeana", name = "uri", multivalued = false, required = true)
        @Field("europeana_uri")
        String europeanaUri;

        @Europeana
        @Solr(namespace = "europeana", name = "collectionName", multivalued = false, required = true)
        @Field("europeana_collectionName")
        String collectionName;

        @Europeana
        @Solr(namespace = "europeana", name = "type", multivalued = false, fieldType = "string", toCopyField = {"TYPE"})
        @Field("europeana_type")
        String europeanaType;

        @Europeana()
        @Solr(namespace = "europeana", name = "userTag", toCopyField = {"text", "USERTAGS"})
        @Field("europeana_userTag")
        String[] europeanaUserTag;

        @Europeana()
        @Solr(namespace = "europeana", name = "language", fieldType = "string", toCopyField = {"text", "LANGUAGE"})
        @Field("europeana_language")
        String[] europeanaLanguage;

        @Europeana()
        @Solr(namespace = "europeana", name = "unstored", stored = false)
        @Field("europeana_unstored")
        String[] europeanaUnstored;

        @Europeana(briefDoc = true)
        @Solr(namespace = "europeana", name = "object")
        @Field("europeana_object")
        String[] europeanaObject;

        @Europeana()
        @Solr(namespace = "europeana", name = "country")
        @Field("europeana_country")
        String[] europeanaCountry;

        @Europeana()
        @Solr(namespace = "europeana", name = "source")
        @Field("europeana_source")
        String[] europeanaSource;

        @Europeana()
        @Solr(namespace = "europeana", name = "isShownAt", fieldType = "string", toCopyField = {"text"})
        @Field("europeana_isShownAt")
        String[] europeanaisShownAt;

        @Europeana()
        @Solr(namespace = "europeana", name = "isShownBy", fieldType = "string", toCopyField = {"text"})
        @Field("europeana_isShownBy")
        String[] europeanaisShownBy;

        @Europeana()
        @Solr(namespace = "europeana", name = "hasObject", fieldType = "boolean")
        @Field("europeana_hasObject")
        boolean europeanahasObject;


        // Dublin Core / ESE fields
        @Europeana()
        @Solr(namespace = "dc", name = "coverage", toCopyField = {"text", "what", "subject"})
        @Field("dc_coverage")
        String[] dcCoverage;

        @Europeana()
        @Solr(namespace = "dc", name = "contributor", toCopyField = {"text", "who", "creator"})
        @Field("dc_contributor")
        String[] dcContributor;

        @Europeana()
        @Solr(namespace = "dc", name = "description", toCopyField = {"text", "description"})
        @Field("dc_description")
        String[] dcDestcription;

        @Europeana()
        @Solr(namespace = "dc", name = "creator", toCopyField = {"text", "who", "creator"})
        @Field("dc_creator")
        String[] dcCreator;

        @Europeana()
        @Solr(namespace = "dc", name = "date", toCopyField = {"text", "when", "date"})
        @Field("dc_date")
        String[] dcDate;

        @Europeana()
        @Solr(namespace = "dc", name = "format", toCopyField = {"text"})
        @Field("dc_format")
        String[] dcFormat;

        @Europeana()
        @Solr(namespace = "dc", name = "identifier", toCopyField = {"text", "identifier"})
        @Field("dc_identifier")
        String[] dcIdentifier;

        @Europeana()
        @Solr(namespace = "dc", name = "language", toCopyField = {"text"})
        @Field("dc_language")
        String[] dcLanguage;

        @Europeana()
        @Solr(namespace = "dc", name = "publisher", toCopyField = {"text"})
        @Field("dc_publisher")
        String[] dcPublisher;

        @Europeana()
        @Solr(namespace = "dc", name = "relation" , toCopyField = {"text", "relation"})
        @Field("dc_relation")
        String[] dcRelation;

        @Europeana()
        @Solr(namespace = "dc", name = "rights", toCopyField = {"text"})
        @Field("dc_rights")
        String[] dcRights;

        @Europeana()
        @Solr(namespace = "dc", name = "source", toCopyField = {"text"})
        @Field("dc_source")
        String[] dcSource;

        @Europeana()
        @Solr(namespace = "dc", name = "subject", toCopyField = {"text", "what", "subject"})
        @Field("dc_subject")
        String[] dcSubject;

        @Europeana()
        @Solr(namespace = "dc", name = "title", toCopyField = {"text"})
        @Field("dc_title")
        String[] dcTitle;

        @Europeana()
        @Solr(namespace = "dc", name = "type", toCopyField = {"text"})
        @Field("dc_type")
        String[] dcType;


        // Dublin Core Terms extended / ESE fields
        @Europeana()
        @Solr(namespace = "dcterms", name = "alternative", toCopyField = {"text"})
        @Field("dcterms_alternative")
        String[] dctermsAlternative;

        @Europeana()
        @Solr(namespace = "dcterms", name = "created", toCopyField = {"text", "when", "date"})
        @Field("dcterms_created")
        String[] dctermsCreated;

        @Europeana()
        @Solr(namespace = "dcterms", name = "conformsTo", toCopyField = {"text"})
        @Field("dcterms_conformsTo")
        String[] dctermsConformsTo;

        @Europeana()
        @Solr(namespace = "dcterms", name = "extent", toCopyField = {"text", "format"})
        @Field("dcterms_extent")
        String[] dctermsExtent;

        @Europeana()
        @Solr(namespace = "dcterms", name = "hasFormat", toCopyField = {"text", "relation"})
        @Field("dcterms_hasFormat")
        String[] dctermsHasFormat;

        @Europeana()
        @Solr(namespace = "dcterms", name = "hasPart", toCopyField = {"text", "relation"})
        @Field("dcterms_hasPart")
        String[] dctermsHasPart;

        @Europeana()
        @Solr(namespace = "dcterms", name = "hasVersion", toCopyField = {"text", "relation"})
        @Field("dcterms_hasVersion")
        String[] dctermsHasVersion;

        @Europeana()
        @Solr(namespace = "dcterms", name = "isFormatOf", toCopyField = {"text"})
        @Field("dcterms_isFormatOf")
        String[] dctermsIsFormatOf;

        @Europeana()
        @Solr(namespace = "dcterms", name = "isPartOf", toCopyField = {"text"})
        @Field("dcterms_isPartOf")
        String[] dctermsIsPartOf;

        @Europeana()
        @Solr(namespace = "dcterms", name = "isReferencedBy", toCopyField = {"text", "relation"})
        @Field("dcterms_isReferencedBy")
        String[] dctermsIsReferencedBy;

        @Europeana()
        @Solr(namespace = "dcterms", name = "isReplacedBy", toCopyField = {"text", "relation"})
        @Field("dcterms_isReplacedBy")
        String[] dctermsIsReplacedBy;

        @Europeana()
        @Solr(namespace = "dcterms", name = "isRequiredBy", toCopyField = {"text", "relation"})
        @Field("dcterms_isRequiredBy")
        String[] dctermsIsRequiredBy;

        @Europeana()
        @Solr(namespace = "dcterms", name = "issued", toCopyField = {"text", "date"})
        @Field("dcterms_issued")
        String[] dctermsIssued;

        @Europeana()
        @Solr(namespace = "dcterms", name = "isVersionOf", toCopyField = {"text"})
        @Field("dcterms_isVersionOf")
        String[] dctermsIsVersionOf;

        @Europeana()
        @Solr(namespace = "dcterms", name = "medium", toCopyField = {"text", "format"})
        @Field("dcterms_medium")
        String[] dctermsMedium;

        @Europeana()
        @Solr(namespace = "dcterms", name = "provenance", toCopyField = {"text"})
        @Field("dcterms_provenance")
        String[] dctermsProvenance;

        @Europeana()
        @Solr(namespace = "dcterms", name = "references", toCopyField = {"text"})
        @Field("dcterms_references")
        String[] dctermsReferences;

        @Europeana()
        @Solr(namespace = "dcterms", name = "replaces", toCopyField = {"text", "relation"})
        @Field("dcterms_replaces")
        String[] dctermsReplaces;

        @Europeana()
        @Solr(namespace = "dcterms", name = "requires", toCopyField = {"text", "relation"})
        @Field("dcterms_requires")
        String[] dctermsRequires;

        @Europeana()
        @Solr(namespace = "dcterms", name = "spatial", toCopyField = {"text", "where", "location", "subject"})
        @Field("dcterms_spatial")
        String[] dctermsSpatial;

        @Europeana()
        @Solr(namespace = "dcterms", name = "tableOfContents", toCopyField = {"text", "description"})
        @Field("dcterms_tableOfContents")
        String[] dctermsTableOfContents;

        @Europeana()
        @Solr(namespace = "dcterms", name = "temporal", toCopyField = {"text", "what", "subject"})
        @Field("dcterms_temporal")
        String[] dctermsTemporal;

        // copy fields

        @Field
        @Europeana(copyField = true, briefDoc = true)
        @Solr()
        String[] title;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] description;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] date;

        @Field
        @Solr()
        @Europeana(copyField = true, briefDoc = true)
        String creator;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] format;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] publisher;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] source;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] rights;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] identifier;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] relation;

        // wh copy fields
        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] who;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] when;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] what;

        @Field
        @Europeana(copyField = true)
        @Solr()
        String[] where;
    }
}