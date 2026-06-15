package com.caue.bookstore.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WikipediaSummary {

    private String type;
    private String title;
    private String displaytitle;
    private Namespace namespace;
    
    @JsonProperty("wikibase_item")
    private String wikibaseItem;
    
    private Titles titles;
    private int pageid;
    private ImageInfo thumbnail;
    private ImageInfo originalimage;
    private String lang;
    private String dir;
    private String revision;
    private String tid;
    private String timestamp;
    private String description;
    
    @JsonProperty("description_source")
    private String descriptionSource;
    
    @JsonProperty("content_urls")
    private ContentUrls contentUrls;
    
    private String extract;
    
    @JsonProperty("extract_html")
    private String extractHtml;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDisplaytitle() { return displaytitle; }
    public void setDisplaytitle(String displaytitle) { this.displaytitle = displaytitle; }

    public Namespace getNamespace() { return namespace; }
    public void setNamespace(Namespace namespace) { this.namespace = namespace; }

    public String getWikibaseItem() { return wikibaseItem; }
    public void setWikibaseItem(String wikibaseItem) { this.wikibaseItem = wikibaseItem; }

    public Titles getTitles() { return titles; }
    public void setTitles(Titles titles) { this.titles = titles; }

    public int getPageid() { return pageid; }
    public void setPageid(int pageid) { this.pageid = pageid; }

    public ImageInfo getThumbnail() { return thumbnail; }
    public void setThumbnail(ImageInfo thumbnail) { this.thumbnail = thumbnail; }

    public ImageInfo getOriginalimage() { return originalimage; }
    public void setOriginalimage(ImageInfo originalimage) { this.originalimage = originalimage; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }

    public String getRevision() { return revision; }
    public void setRevision(String revision) { this.revision = revision; }

    public String getTid() { return tid; }
    public void setTid(String tid) { this.tid = tid; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDescriptionSource() { return descriptionSource; }
    public void setDescriptionSource(String descriptionSource) { this.descriptionSource = descriptionSource; }

    public ContentUrls getContentUrls() { return contentUrls; }
    public void setContentUrls(ContentUrls contentUrls) { this.contentUrls = contentUrls; }

    public String getExtract() { return extract; }
    public void setExtract(String extract) { this.extract = extract; }

    public String getExtractHtml() { return extractHtml; }
    public void setExtractHtml(String extractHtml) { this.extractHtml = extractHtml; }


    // --- Nested Classes ---

    public static class Namespace {
        private int id;
        private String text;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class Titles {
        private String canonical;
        private String normalized;
        private String display;

        public String getCanonical() { return canonical; }
        public void setCanonical(String canonical) { this.canonical = canonical; }

        public String getNormalized() { return normalized; }
        public void setNormalized(String normalized) { this.normalized = normalized; }

        public String getDisplay() { return display; }
        public void setDisplay(String display) { this.display = display; }
    }

    public static class ImageInfo {
        private String source;
        private int width;
        private int height;

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }

        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
    }

    public static class ContentUrls {
        private PlatformUrls desktop;
        private PlatformUrls mobile;

        public PlatformUrls getDesktop() { return desktop; }
        public void setDesktop(PlatformUrls desktop) { this.desktop = desktop; }

        public PlatformUrls getMobile() { return mobile; }
        public void setMobile(PlatformUrls mobile) { this.mobile = mobile; }
    }

    public static class PlatformUrls {
        private String page;
        private String revisions;
        private String edit;
        private String talk;

        public String getPage() { return page; }
        public void setPage(String page) { this.page = page; }

        public String getRevisions() { return revisions; }
        public void setRevisions(String revisions) { this.revisions = revisions; }

        public String getEdit() { return edit; }
        public void setEdit(String edit) { this.edit = edit; }

        public String getTalk() { return talk; }
        public void setTalk(String talk) { this.talk = talk; }
    }
}