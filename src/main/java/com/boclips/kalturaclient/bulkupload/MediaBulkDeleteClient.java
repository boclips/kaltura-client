package com.boclips.kalturaclient.bulkupload;

import com.boclips.kalturaclient.bulkupload.resources.BulkUploadResource;
import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.UploadFileDescriptor;
import kong.unirest.ContentType;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MediaBulkDeleteClient implements MediaBulkDelete {
    private final KalturaRestClient client;
    private final BulkUploadProcessor processor = new BulkUploadProcessor();

    public MediaBulkDeleteClient(KalturaRestClient client) {
        this.client = client;
    }

    @Override
    public BulkUpload deleteEntries(Collection<String> entryIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("bulkUploadData:objectType", "KalturaBulkUploadXmlJobData");
        params.put("bulkUploadEntryData:objectType", "KalturaBulkUploadEntryData");

        UploadFileDescriptor upload = UploadFileDescriptor.builder()
                .fieldName("fileData")
                .fileStream(new ByteArrayInputStream(deleteActions(entryIds)))
                .filename("delete-entries.xml")
                .contentType(ContentType.TEXT_XML)
                .build();

        BulkUploadResource resource = client.postFileBody("/media/action/bulkUploadAdd", params, upload, BulkUploadResource.class);
        return processor.process("bulk delete", resource);
    }

    private static byte[] deleteActions(Collection<String> entryIds) {
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        try {
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(xml, StandardCharsets.UTF_8.name());
            writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
            writer.writeStartElement("mrss");
            writer.writeStartElement("channel");
            for (String entryId : entryIds) {
                writer.writeStartElement("item");
                writer.writeStartElement("action");
                writer.writeCharacters("delete");
                writer.writeEndElement();
                writer.writeStartElement("entryId");
                writer.writeCharacters(entryId);
                writer.writeEndElement();
                writer.writeEndElement();
            }
            writer.writeEndDocument();
            writer.close();
        } catch (XMLStreamException e) {
            throw new IllegalStateException("Could not write the bulk delete XML", e);
        }
        return xml.toByteArray();
    }
}
