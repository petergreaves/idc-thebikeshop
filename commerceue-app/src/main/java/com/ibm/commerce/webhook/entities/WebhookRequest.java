package com.ibm.commerce.webhook.entities;

import java.sql.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ibm.commerce.copyright.IBMCopyright;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookRequest<T> {

   /**
    * IBM copyright notice field.
    */
   @SuppressWarnings("unused")
   private static final String COPYRIGHT = IBMCopyright.SHORT_COPYRIGHT;

   private String id;

   private String event;

   private String qualifier;

   private Date timestamp;

   private Map<String, String> metadata;

   private T payload;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getEvent() {
      return event;
   }

   public void setEvent(String event) {
      this.event = event;
   }

   public String getQualifier() {
      return qualifier;
   }

   public void setQualifier(String qualifier) {
      this.qualifier = qualifier;
   }

   public Date getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Date timestamp) {
      this.timestamp = timestamp;
   }

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public void setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
   }

   public T getPayload() {
      return payload;
   }

   public void setPayload(T payload) {
      this.payload = payload;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{ ");
      sb.append("id: ").append(getId());
      sb.append(", event: ").append(getEvent());
      sb.append(", qualifier: ").append(getQualifier());
      sb.append(", timestamp: ").append(getTimestamp());
      sb.append(", metadata: ").append(getMetadata());
      sb.append(", payload: ").append(getPayload());
      sb.append(" }");
      return sb.toString();
   }
}