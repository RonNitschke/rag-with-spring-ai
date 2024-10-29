package com.schweizerelectronic.model;

import java.util.Map;

import org.springframework.ai.document.Document;


public class Paragraph {
    private long id;
    private int page;
    private String title;
    private String content;
    
   
    public Paragraph(long id, int page, String title, String content) {
		this.id = id;
		this.page = page;
		this.title = title;
		this.content = content;
	}



	private  Paragraph(Builder builder) {
		this(builder.id, builder.page, builder.title, builder.content);
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public int getPage() {
		return page;
	}



	public void setPage(int page) {
		this.page = page;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}

	public Document toDocument( Paragraph paragraph) {
        return new Document(paragraph.getContent(), Map.of("title", paragraph.getTitle(), "page", paragraph.getPage(), "id", paragraph.getId()));
    }
	
	public static Builder builder() {
        return new Builder();
    }
	
    public static class Builder {
    	
        private long id;
        private int page;
        private String title;
        private String content;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder page( int page) {
            this.page = page;
            return this;
        }
        
        public Builder title( String title) {
            this.title = title;
            return this;
        }
        
        public Builder content( String content) {
            this.content = content;
            return this;
        }
        

        public Paragraph build() {
            return new Paragraph(this);
        }
    }
}
